package com.same.part.assistant.viewmodel.state

import android.app.Application
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import com.same.part.assistant.data.model.CouponInfoModel
import com.same.part.assistant.data.model.CreateOrderResult
import com.same.part.assistant.data.model.Customer
import com.same.part.assistant.data.repository.request.HttpRequestManger
import com.same.part.assistant.fragment.SelectCustomFragment.Companion.CUSTOMER_TYPE_GENERAL
import com.same.part.assistant.fragment.SelectCustomFragment.Companion.CUSTOMER_TYPE_MEMBER
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestTR
import me.hgj.jetpackmvvm.state.ResultState
import okhttp3.ResponseBody

class ProvideCouponViewModel(application: Application) : BaseViewModel(application) {
    /**
     * 优惠券对象
     */
    var couponInfoModel: CouponInfoModel? = null
     val couponSendResultState = MutableLiveData<ResultState<String>>()

    /**
     * 待发放客户列表
     */
    private val _customerListToProvider = MutableLiveData<ArrayList<Customer>>()

    val customerListToProvider: LiveData<ArrayList<Customer>> = _customerListToProvider

    /**
     * 普通客户
     */
    private val generalCustomerResultState = MutableLiveData<ResultState<List<Customer>>>()
    private val _generalCustomerList = arrayListOf<Customer>()

    /**
     * 会员客户
     */
    private val memberCustomerResultState = MutableLiveData<ResultState<List<Customer>>>()
    private val _memberCustomerList = arrayListOf<Customer>()

    private val customerResultStateArray =
        SparseArray<MutableLiveData<ResultState<List<Customer>>>>()
    private val customerListArray = SparseArray<ArrayList<Customer>>()

    init {
        customerResultStateArray.append(CUSTOMER_TYPE_GENERAL, generalCustomerResultState)
        customerResultStateArray.append(CUSTOMER_TYPE_MEMBER, memberCustomerResultState)
        customerListArray.append(CUSTOMER_TYPE_GENERAL, _generalCustomerList)
        customerListArray.append(CUSTOMER_TYPE_MEMBER, _memberCustomerList)
    }

    fun getResultState(customerType: Int): LiveData<ResultState<List<Customer>>> {
        return customerResultStateArray[customerType]
    }

    fun getCustomerList(customerType: Int): ArrayList<Customer> {
        return customerListArray[customerType]
    }

    fun setCustomerList(data:List<Customer>,customerType: Int,isRefresh:Boolean ){
        customerListArray[customerType].apply {
            if (isRefresh){
                this.clear()
                this.addAll(data)
            }else{
                this.addAll(data)
            }

        }?: kotlin.run {
            val list = ArrayList(data)
            customerListArray.put(customerType,list)
        }
    }

    /**
     * 当前客户是否已添加到待发放集合
     */
    fun hasAdd(customer: Customer): Boolean {
        return customerListToProvider.value?.find { it.userId == customer.userId }?.let { true }
            ?: false
    }

    /**
     * 添加客户到待发放集合
     */
    fun addCustomListToProvider(list: List<Customer>) {
        if (list.isNotEmpty()) {
            if (_customerListToProvider.value == null) {
                _customerListToProvider.value = arrayListOf()
            }
            _customerListToProvider.value?.apply {
                addAll(list)
                distinctBy { it.userId }
            }
            _customerListToProvider.value = _customerListToProvider.value
        }
    }

    /**
     * 从待发放集合这种删除客户
     */
    fun delCustomToProvider(customer: Customer) {
        _customerListToProvider.value?.remove(customer)
        _customerListToProvider.value = _customerListToProvider.value
    }

    /**
     * 发放优惠券
     */
    fun sendCoupon() {
        val customerList = customerListToProvider.value
        if (couponInfoModel != null && !customerList.isNullOrEmpty()) {
            request({
                HttpRequestManger.instance.sendCoupon(
                    couponInfoModel!!.couponActivityId,
                    customerList.map { it.userId }
                )
            }, couponSendResultState, true)
        }
    }

    fun requestCustomerList(customerType: Int) {
        when (customerType) {
            CUSTOMER_TYPE_GENERAL -> {
                requestGeneralCustomer()
            }
            CUSTOMER_TYPE_MEMBER -> {
                requestMemberCustomer()
            }
        }
    }

    /**
     * 请求普通客户列表
     */
    private fun requestGeneralCustomer() {
        requestTR(
            { HttpRequestManger.instance.getCustomerList() },
            generalCustomerResultState,
            paresResult = {
                val result = it.string()
                val jsonObject = JSON.parseObject(result)
                val content = jsonObject.getString("content")
                content?.let {
                    val contentJsonObject = JSON.parseArray(content)
                    val list = contentJsonObject.mapIndexed { index, _ ->
                        contentJsonObject.getJSONObject(index)
                    }
                        .map {
                            GsonUtils.fromJson(it.getString("user"), Customer::class.java)
                        }
                    Triple(
                        list, "000", ""
                    )
                } ?: kotlin.run {
                    val code = jsonObject.getString("code")
                    val msg = jsonObject.getString("massage")
                    Triple(
                        null, code, msg
                    )
                }

            },
            isShowDialog = true
        )
    }

    /**
     * 请求会员客户列表
     */
    private fun requestMemberCustomer() {
        request(
            { HttpRequestManger.instance.getMemberCustomer() },
            memberCustomerResultState, isShowDialog = true
        )
    }
}
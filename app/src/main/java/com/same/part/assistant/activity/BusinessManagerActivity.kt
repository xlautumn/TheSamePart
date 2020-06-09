package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.alibaba.fastjson.JSONObject
import com.same.part.assistant.R
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.databinding.ActivityBusinessManagerBinding
import com.same.part.assistant.utils.HttpUtil
import com.same.part.assistant.utils.Util
import com.same.part.assistant.viewmodel.state.BusinessManagerModel
import kotlinx.android.synthetic.main.activity_business_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel

/**
 * 交易管理
 */
class BusinessManagerActivity : AppCompatActivity() {
    val mViewModel by lazy {
        getViewModel<BusinessManagerModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            setContentView<ActivityBusinessManagerBinding>(this, R.layout.activity_business_manager)
        binding.vm = mViewModel
        //标题
        mToolbarTitle.text = "交易管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //交易明细
        businessDetail.setOnClickListener {
            startActivity(Intent(this, BusinessDetailActivity::class.java))
        }
        //加载数据
        loadData()
    }

    private fun loadData() {
        val url = StringBuilder("${ApiService.SERVER_URL}shop-summary/management/2013")
            .append("?year=${Util.getCurrentYear()}")
            .append("&month=${Util.getCurrentMonth()}")
//            .append("&day=${Util.getCurrentDay()}")
            .append("&day=8")
            .append("&week=${Util.getCurrentWeek()}")
        HttpUtil.instance.getUrlWithHeader("WSCX", CacheUtil.getToken(), url.toString(), {
            try {
                val resultJson = JSONObject.parseObject(it)
                resultJson.getJSONObject("msg")?.apply {
                    mViewModel.incomeToday.set(getString("incomeToday") ?: "0")
                    mViewModel.incomeWeek.set(getString("incomeWeek") ?: "0")
                    mViewModel.incomeMonth.set(getString("incomeMonth") ?: "0")
                    mViewModel.incomeYear.set(getString("incomeYear") ?: "0")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }
}
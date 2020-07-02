package com.same.part.assistant.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.data.model.CreateMemberCard
import com.same.part.assistant.data.model.MemberCardModel
import com.same.part.assistant.databinding.ActivityAddVipCardBinding
import com.same.part.assistant.viewmodel.request.RequestVipCardViewModel
import com.same.part.assistant.viewmodel.state.AddVipCardViewModel
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.shaohui.bottomdialog.BottomDialog
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * 添加会员卡
 */
class AddVipCardActivity : BaseActivity<AddVipCardViewModel, ActivityAddVipCardBinding>() {

    /** 跳转来源*/
    private lateinit var mJumpFromType: String

    /** 编辑页面数据*/
    private var mCardModel: MemberCardModel? = null

    private val mRequestVipCardViewModel: RequestVipCardViewModel by lazy { getViewModel<RequestVipCardViewModel>() }

    override fun layoutId(): Int = R.layout.activity_add_vip_card

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        //跳转来源
        mJumpFromType = when (intent?.getStringExtra(JUMP_FORM_TYPE).orEmpty()) {
            JUMP_FORM_EDIT -> {
                mToolbarTitle.text = "编辑会员卡"
                JUMP_FORM_EDIT
            }
            else -> {
                mToolbarTitle.text = "添加会员卡"
                JUMP_FORM_ADD
            }
        }
        //编辑页面数据
        if (mJumpFromType == JUMP_FORM_EDIT) {
            mCardModel = intent?.getSerializableExtra(CARD_DATA) as? MemberCardModel
            mCardModel?.apply {
                mViewModel.name.set(name)
                mViewModel.discount.set(discount.toString())
                mViewModel.description.set(description)
            }
            mDatabind.etDiscount.setFocusable(false)
            mDatabind.etDiscount.isFocusableInTouchMode = false
        }
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
    }

    override fun createObserver() {
        //编辑或者添加成功
        mRequestVipCardViewModel.memberCardResult.observe(this, Observer {
            EventBus.getDefault().post(HAND_CARD_SUCCESS)
            finish()
        })
    }

    inner class ProxyClick {
        fun save() {
            if (mViewModel.name.get().isEmpty()) {
                ToastUtils.showShort("请填写名称")
                return
            }
            if (mViewModel.discount.get().isEmpty()) {
                ToastUtils.showShort("请填写折扣")
                return
            }
            if (mViewModel.description.get().isEmpty()) {
                ToastUtils.showShort("请填写描述")
                return
            }
            val memberCard = CreateMemberCard(
                mViewModel.description.get(),
                mViewModel.discount.get().toDouble(),
                JSONObject().put("type", 1).toString(),
                mViewModel.name.get(),
                mViewModel.receiveWay.get()
            )
            if (mJumpFromType == JUMP_FORM_EDIT) {
                mRequestVipCardViewModel.editMemberCard(mCardModel?.cardId.toString(), memberCard)
            } else {
                mRequestVipCardViewModel.createMemberCard(memberCard)
            }
        }

        fun chooseWay() {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                //选择商品分类列表
                it.findViewById<RecyclerView>(R.id.recyclerview).apply {
                    layoutManager = LinearLayoutManager(this@AddVipCardActivity)
                    adapter = WayAdapter(WAY_METHODS).apply {
                        setOnItemClickListener { _, _, position ->
                            mViewModel.receiveWay.set(position + 1)
                            dialog.dismissAllowingStateLoss()
                        }
                    }
                }

            }.setLayoutRes(R.layout.bottom_dialog_list).setDimAmount(0.4F)
                .setCancelOutside(true).setTag("mChooseUnit").show()
        }

        //卡片有效期
        fun chooseCardPeriodOfValidity() {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                handleCardPeriodOfValidityDialogView(it, dialog)
            }.setLayoutRes(R.layout.dialog_card_period_of_validity).setDimAmount(0.4F)
                .setCancelOutside(true).setTag("mChooseCardPeriodOfValidity").show()
        }
    }

    /**
     * 卡片有效期
     */
    private fun handleCardPeriodOfValidityDialogView(
        it: View,
        dialog: BottomDialog
    ) {
        val ivLifeTime = it.findViewById<ImageView>(R.id.ivLifeTime)
        val ivLimited = it.findViewById<ImageView>(R.id.ivLimited)
        val etLimitedDayNum = it.findViewById<EditText>(R.id.etLimitedDayNum)
        if (mViewModel.cardPeriodOfValidity.get() == "0") {
            ivLifeTime.isSelected = true
            ivLimited.isSelected = false
        } else {
            ivLifeTime.isSelected = false
            ivLimited.isSelected = true
            etLimitedDayNum.setText(mViewModel.cardPeriodOfValidity.get())
        }
        it.findViewById<LinearLayout>(R.id.llLifeTime).setOnClickListener {
            ivLifeTime.isSelected = true
            ivLimited.isSelected = false
        }
        it.findViewById<LinearLayout>(R.id.llLimited).setOnClickListener {
            ivLifeTime.isSelected = false
            ivLimited.isSelected = true
        }
        it.findViewById<TextView>(R.id.tvConfirm).setOnClickListener {
            if (ivLimited.isSelected) {
                val dayNum = etLimitedDayNum.text.toString().toIntOrNull()
                if (dayNum == null) {
                    ToastUtils.showLong("请填写天数")
                    return@setOnClickListener
                } else if (dayNum == 0) {
                    ToastUtils.showLong("天数不能为0")
                    return@setOnClickListener
                }
                mViewModel.cardPeriodOfValidity.set(dayNum.toString())
            } else {
                mViewModel.cardPeriodOfValidity.set("0")
            }
            dialog.dismissAllowingStateLoss()
        }
    }

    inner class WayAdapter(data: ArrayList<String>) :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.choose_list_item, data) {

        override fun convert(holder: BaseViewHolder, item: String) {
            //赋值
            item.run {
                holder.setText(R.id.option, item)
            }
        }
    }

    companion object {
        /** 编辑页面数据*/
        const val CARD_DATA = "CARD_DATA"

        /** 跳转来源*/
        const val JUMP_FORM_TYPE = "JUMP_FORM_TYPE"

        /** 编辑页面*/
        const val JUMP_FORM_EDIT = "JUMP_FORM_EDIT"

        /** 添加页面*/
        const val JUMP_FORM_ADD = "JUMP_FORM_ADD"

        /** 编辑或者添加成功标记*/
        const val HAND_CARD_SUCCESS = "ADD_CARD_SUCCESS"

        /** 领取方式*/
        val WAY_METHODS = arrayListOf("直接领取", "付费领取", "满足条件自动领取")

    }

}
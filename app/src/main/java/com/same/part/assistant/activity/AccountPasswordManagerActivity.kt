package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.activity.ChangePasswordActivity.Companion.USER_ID_KEY
import com.same.part.assistant.data.model.AccountModel
import com.same.part.assistant.viewmodel.request.RequsetAccountsViewmodel
import kotlinx.android.synthetic.main.activity_account_password.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel


/**
 * 账号密码管理
 */
class AccountPasswordManagerActivity : AppCompatActivity() {

    //适配器
    private val mAccountAdapter: AccountAdapter by lazy { AccountAdapter(arrayListOf()) }

    //店铺账号列表
    private val mRequsetAccountsViewmodel: RequsetAccountsViewmodel by lazy { getViewModel<RequsetAccountsViewmodel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_password)
        //标题
        mToolbarTitle.text = "账号密码"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //列表数据
        mAccountRecyclerView.apply {
            adapter = mAccountAdapter
            layoutManager = LinearLayoutManager(context)
        }
        mAccountAdapter.apply {
            addChildClickViewIds(R.id.accountOperation)
            setOnItemChildClickListener { adapter, view, position ->
                startActivity(
                    Intent(
                        this@AccountPasswordManagerActivity,
                        ChangePasswordActivity::class.java
                    ).apply {
                        putExtra(USER_ID_KEY, mAccountAdapter.data[position].id)
                    }
                )
            }
        }
        mRequsetAccountsViewmodel.queryAccounts()
        mRequsetAccountsViewmodel.accountsResult.observe(this, Observer {
            mAccountAdapter.setNewInstance(it)
        })
    }

    inner class AccountAdapter(data: ArrayList<AccountModel>) :
        BaseQuickAdapter<AccountModel, BaseViewHolder>(R.layout.account_list_item, data) {

        override fun convert(holder: BaseViewHolder, item: AccountModel) {
            //赋值
            item.run {
                holder.setText(R.id.accountName, name)
                holder.setText(R.id.accountMobile, mobile)
                holder.setText(R.id.accountRole, role)
            }
        }
    }

}
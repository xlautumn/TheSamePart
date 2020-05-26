package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.data.model.AccountModel
import kotlinx.android.synthetic.main.activity_account_password.*
import kotlinx.android.synthetic.main.toolbar_title.*


/**
 * 账号密码管理
 */
class AccountPasswordManagerActivity : AppCompatActivity() {
    private val mAccountList = arrayListOf<AccountModel>().apply {
        add(
            AccountModel(
                "DUODUO",
                "13666666666",
                "店主"
            )
        )
        add(
            AccountModel(
                "HUAHUA",
                "13888888888",
                "收银员"
            )
        )
        add(
            AccountModel(
                "HEHE",
                "13999999999",
                "收银员"
            )
        )
    }

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
            adapter = CustomAdapter(mAccountList)
            layoutManager = LinearLayoutManager(context)
        }
    }


    inner class CustomAdapter(var dataList: ArrayList<AccountModel>) :
        RecyclerView.Adapter<AccountItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountItemHolder =
            AccountItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.account_list_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: AccountItemHolder, position: Int) {
            val model = dataList[position]
            holder.accountName.text = model.name
            holder.accountMobile.text = model.mobile
            holder.accountRole.text = model.role
            holder.accountOperation.apply {
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@AccountPasswordManagerActivity,
                            ChangePasswordActivity::class.java
                        )
                    )
                }
            }
        }

    }

    class AccountItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var accountName: TextView = itemView.findViewById(R.id.accountName)
        var accountMobile: TextView = itemView.findViewById(R.id.accountMobile)
        var accountRole: TextView = itemView.findViewById(R.id.accountRole)
        var accountOperation: TextView = itemView.findViewById(R.id.accountOperation)
    }

}
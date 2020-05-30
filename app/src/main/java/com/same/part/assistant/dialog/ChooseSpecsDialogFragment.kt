package com.same.part.assistant.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.adapter.ChooseSpecsDialogAdapter
import com.same.part.assistant.utils.Util
import kotlinx.android.synthetic.main.layout_dialog_choose_specs.*

class ChooseSpecsDialogFragment : DialogFragment() {

    companion object {
        private val TAG = ChooseSpecsDialogFragment::class.java.simpleName

        const val PARAMS_TITLE = "params_title"

        fun create(title: String): ChooseSpecsDialogFragment {
            return ChooseSpecsDialogFragment().apply {
                arguments = Bundle().also {
                    //Bundle中添加数据
                    it.putString(PARAMS_TITLE, title)
                }
            }
        }
    }

    private var mTitleContent: String? = null
    private var mContext: Context? = null
    private var mAdapter: ChooseSpecsDialogAdapter? = null
    private lateinit var mDataView: RecyclerView

    private var mData: List<String> = ArrayList<String>().apply {
        add("10kg")
        add("20kg")
        add("30kg")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            mTitleContent = getString(PARAMS_TITLE, " test dialog fragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_dialog_choose_specs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //findViewById
        mDataView = view.findViewById(R.id.product_attribute_value)
        view.findViewById<ImageView>(R.id.dialog_close).setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mContext = activity
        mContext?.let {
            dialog?.window?.decorView?.setPadding(Util.dip2px(it, 25f), 0,
                Util.dip2px(it, 25f), 0)
        }
        addCart.setOnClickListener {
            Toast.makeText(mContext,"加入购物车了~~",Toast.LENGTH_LONG).show()
            //TODO 加入购物车，调用加入购物车的接口
            dismiss()
        }
        //设置监听等动作
        mDataView.apply {
            layoutManager = GridLayoutManager(mContext, 2)
            //adapter需要重新处理
            mAdapter = mContext?.let { ChooseSpecsDialogAdapter(it) }
            mAdapter?.setChooseSpecsDialogListener {
                onItemClick {position ->
                    mAdapter?.notifyDataSetChanged()
                    //填充已选规格
                    var specs = mData[position]
                    selectedSpecs.text = "已选规格：$specs"
                    //TODO 根据价格及所选规格计算 总价
                    selectedPrice.text = "￥$specs"
                }
            }
            adapter = mAdapter
        }
        mAdapter?.setData(mData)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            decorView.setPadding(0,0,0,0)
            setBackgroundDrawableResource(android.R.color.transparent)
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            setCanceledOnTouchOutside(false)
        }
    }

    fun showDialog(manager: FragmentManager) {
        show(manager, TAG)
    }

}
package com.same.part.assistant.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.commonsware.cwac.merge.MergeAdapter
import com.same.part.assistant.R
import com.same.part.assistant.adapter.ChooseSpecsDialogAdapter
import com.same.part.assistant.data.model.ProductSku
import com.same.part.assistant.data.model.PropertyData
import com.same.part.assistant.utils.Util
import kotlinx.android.synthetic.main.layout_dialog_choose_specs.*

class ChooseSpecsDialogFragment private constructor(private var mContext: Context) : DialogFragment() {

    companion object {
        private val TAG = ChooseSpecsDialogFragment::class.java.simpleName

        const val PARAMS_TITLE = "params_title"

        fun create(title: String, context: Context): ChooseSpecsDialogFragment {
            return ChooseSpecsDialogFragment(context).apply {
                arguments = Bundle().also {
                    //Bundle中添加数据
                    it.putString(PARAMS_TITLE, title)
                }
            }
        }
    }

    private var mTitleContent: String? = null
    private var mPropertyList = LinkedHashMap<String, MutableSet<PropertyData>>()
    private var mProductSkuList = LinkedHashSet<ProductSku>()
    private lateinit var mDataView: ListView
    private lateinit var mMergeAdapter: MergeAdapter
    private var mSelectProperties = hashMapOf<String,String>()

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
        dialog?.window?.decorView?.setPadding(
            Util.dip2px(mContext, 25f), 0,
            Util.dip2px(mContext, 25f), 0
        )
        dialogProductName.text=mTitleContent
        addCart.setOnClickListener {
            Toast.makeText(mContext, "加入购物车了~~", Toast.LENGTH_LONG).show()
            //TODO 加入购物车，调用加入购物车的接口
            dismiss()
        }
        mergeAdapter()
        mDataView.adapter = mMergeAdapter
    }

    private fun mergeAdapter() {
        mMergeAdapter = MergeAdapter()
        mPropertyList.forEach {
            val titleView = createRightTitleView(it.key)
            mMergeAdapter.addView(titleView)
            //添加adapter
            val adapter = ChooseSpecsDialogAdapter(mContext, it.value) { position, propertyData ->
                mSelectProperties[propertyData.project] = propertyData.name
                mProductSkuList.forEach { productSku ->
                    var matchCount=0
                    var selectStr="已选规格:"
                    val selectSize = mSelectProperties.size
                    productSku.properties.takeIf { properties -> properties.size == selectSize }
                        ?.also { properties ->
                            mSelectProperties.forEach { selected ->
                                if (properties.contains(selected.value)) {
                                    selectStr += if (selectStr.endsWith(":")) selected.value else "、${selected.value}"
                                    matchCount++
                                }
                            }
                        }

                    if (matchCount == selectSize) {
                        selectedSpecs.text = selectStr
                        selectedPrice.text = "￥${productSku.price}"
                    }
                }
            }
            mMergeAdapter.addAdapter(adapter)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            decorView.setPadding(0, 0, 0, 0)
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

    fun setData(
        propertyList: LinkedHashMap<String, MutableSet<PropertyData>>?,
        propertyPriceList: LinkedHashSet<ProductSku>?
    ) {
        propertyList?.let { properties ->
            propertyPriceList?.let { propertyPrice ->
                mPropertyList.clear()
                mProductSkuList.clear()
                mPropertyList.putAll(properties)
                mProductSkuList.addAll(propertyPrice)
            }
        }
    }

    private fun createRightTitleView(title: String?): View {
        var view = LayoutInflater.from(mContext).inflate(R.layout.layout_property_title_item, null)
        var propertyTitle: TextView = view.findViewById(R.id.tv_property_title)
        propertyTitle.text = title ?: ""
        return view
    }
}
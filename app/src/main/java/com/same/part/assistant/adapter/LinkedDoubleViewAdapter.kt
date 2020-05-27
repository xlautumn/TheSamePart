package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.same.part.assistant.R
import com.same.part.assistant.dialog.ChooseSpecsDialogFragment
import com.same.part.assistant.listener.CartDataChangedListener
import com.same.part.assistant.listener.OnCartDataChangedListener
import com.same.part.assistant.manager.GoodPurchaseDataManager
import com.same.part.assistant.model.GoodClassModel
import java.util.*

class LinkedDoubleViewAdapter {

    private var mGoodClassModel: ArrayList<GoodClassModel>? = null
    private var mCartGoodModels: ArrayList<GoodClassModel.GoodModel> = ArrayList()

    private var mLeftAdapter: LeftViewAdapter? = null
    private var mMergeAdapter: StrengthenedMergeAdapter? = null
    private var mRightTitleViews: ArrayList<View> = ArrayList()
    private var mCartDataChangeListener: CartDataChangedListener<String>? = null

    private lateinit var mContext: FragmentActivity

    constructor(context: FragmentActivity) {
        this.mContext = context
        this.mGoodClassModel = GoodPurchaseDataManager.getInstance().goodPurchaseModels
        this.mCartGoodModels = GoodPurchaseDataManager.getInstance().cartGoodModels
        initData()
        mergeAdapter()
    }

    private fun initData() {
        mGoodClassModel?.let { dataList ->
            for (dataModel in dataList) {
                dataModel.selectNum = 0
                for (goodMode in dataModel.contentModels) {
                    goodMode?.let { goodMode ->
                        //将用户的购物车里得数据填充到选中列表里
                        mCartGoodModels.takeIf { !(it.isNullOrEmpty()) }?.let { cartGoodModels ->
                            //有数据
                            for (model in cartGoodModels) {
                                //还需要判断其他保证唯一性得字段
                                if (model.name == goodMode.name) {
                                    goodMode.inCart = true
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
        //todo 监听刷新数据
        mCartDataChangeListener?.onChanged()
    }


    private fun mergeAdapter() {
        mLeftAdapter = LeftViewAdapter()
        mMergeAdapter = StrengthenedMergeAdapter()
        //当前被选中得数据
        var cartGoodModels = ArrayList<GoodClassModel.GoodModel>(mCartGoodModels)
        //添加标题、遍历数据
        mGoodClassModel?.let { dataList ->
            for (dataModel in dataList) {
                val view = createRightTitleView(dataModel.title)
                mMergeAdapter?.addView(view)
                mRightTitleViews.clear()
                mRightTitleViews.add(view)
                //此处更改数据，方便显示 购物车还是选规格
                //最终要显示在右边得数据
                var goodModels: ArrayList<GoodClassModel.GoodModel> = ArrayList()
                //遍历数据源的产品
                for (i in dataModel.contentModels.indices) {
                    var model = dataModel.contentModels[i]
                    //遍历已选中的产品
                    for (j in cartGoodModels.indices) {
                        val cartGoodModel = cartGoodModels[j]
                        //产品名称相等，TODO 根据返回可能需要判断 一级分类、二级分类
                        if (cartGoodModel.name == model.name) {
                            dataModel.selectNum = dataModel.selectNum + 1
                            model = cartGoodModel
                            cartGoodModels.remove(cartGoodModel)
                        }
                    }
                    goodModels.add(model)
                }
                mMergeAdapter?.addAdapter(RightViewAdapter(goodModels))
            }
        }
    }

    internal fun notifyDataSetChanged() {
        mLeftAdapter?.notifyDataSetChanged()
        mMergeAdapter?.notifyDataSetChanged()
        //刷新右上二级分类数据
        //监听事件刷新
        mCartDataChangeListener?.onChanged()
    }

    private fun createRightTitleView(title: String?): View {
        var view = LayoutInflater.from(mContext)
            .inflate(R.layout.layout_double_linked_view_right_title_item, null)
        var rightTitleView: TextView = view.findViewById(R.id.tv_right_title)
        rightTitleView.text = title ?: ""
        return view
    }

    private fun updateCardList(goodModel: GoodClassModel.GoodModel) {
        if (goodModel.inCart) {
            if (!mCartGoodModels.contains(goodModel)) {
                mCartGoodModels.add(goodModel)
                mCartDataChangeListener?.handleAddToCartModel(true, goodModel)
            }
        } else {
            mCartGoodModels.remove(goodModel)
            mCartDataChangeListener?.handleAddToCartModel(false, goodModel)
        }
        mCartDataChangeListener?.onChanged()
    }

    inner class LeftViewAdapter() :
        BaseAdapter() {
        override fun getCount(): Int {
            return mGoodClassModel?.size ?: 0
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long = position.toLong()


        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            var view: View
            var viewHolder: ViewHolder
            if (convertView == null) {
                view = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_double_linked_view_left_item, null)
                viewHolder = ViewHolder()
                viewHolder.leftName = view.findViewById(R.id.tv_left_name)
                viewHolder.selectedBar = view.findViewById(R.id.selected_bar)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
            mGoodClassModel?.get(position)?.let {
                viewHolder.leftName?.text = it.title
            }
            return view
        }
    }

    class ViewHolder {
        var leftName: TextView? = null
        var selectedBar: ImageView? = null
    }


    inner class RightViewAdapter(
        private val goodModels: ArrayList<GoodClassModel.GoodModel>
    ) :
        BaseAdapter() {

        override fun getCount(): Int = goodModels.size

        override fun getItem(position: Int): Any? = goodModels[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            var view: View
            var goodViewHolder: GoodViewHolder
            if (convertView == null || convertView.tag == null
                || convertView.tag !is GoodViewHolder
            ) {
                view = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_double_linked_right_order_item, null)
                goodViewHolder = GoodViewHolder()
                goodViewHolder.apply {
                    goodAvatar = view.findViewById(R.id.goodAvatar)
                    goodName = view.findViewById(R.id.goodName)
                    goodNameExplain = view.findViewById(R.id.goodNameExplain)
                    goodTag = view.findViewById(R.id.goodTag)
                    price = view.findViewById(R.id.tv_price)
                    subPrice = view.findViewById(R.id.tv_sub_price)
                    subTag = view.findViewById(R.id.tv_sub_tag)
                    goodShoppingCartRoot = view.findViewById(R.id.goodShoppingCartRoot)
                    cartReduce = view.findViewById(R.id.cart_reduce)
                    cartNum = view.findViewById(R.id.tv_cart_num)
                    cartIncrease = view.findViewById(R.id.cart_increase)
                    chooseSpecs = view.findViewById(R.id.chooseSpecs)
                }
                view.tag = goodViewHolder
            } else {
                view = convertView
                goodViewHolder = view.tag as GoodViewHolder
            }
            //填充View
            goodModels[position].apply {
                goodViewHolder.goodName?.text = name ?: ""
                goodViewHolder.goodNameExplain?.text = desc ?: ""
                goodViewHolder.price?.text = price ?: ""

                if (inCart) {
                    goodViewHolder.chooseSpecs?.visibility = View.GONE
                    goodViewHolder.goodShoppingCartRoot?.visibility = View.VISIBLE
                    goodViewHolder.cartIncrease?.setOnClickListener(View.OnClickListener {
                        //TODO 购买数量 增加

                    })
                    goodViewHolder.cartReduce?.setOnClickListener(View.OnClickListener {
                        //TODO 购买数量减少

                    })
                } else {
                    goodViewHolder.goodShoppingCartRoot?.visibility = View.GONE
                    goodViewHolder.chooseSpecs?.visibility = View.VISIBLE
                    goodViewHolder.chooseSpecs?.setOnClickListener(View.OnClickListener {
                        val dialogFragment = ChooseSpecsDialogFragment()
                        if (mContext != null) {
                            dialogFragment.showDialog(mContext.supportFragmentManager)
                        }
                    })
                }
            }
            return view
        }
    }

    class GoodViewHolder {
        var goodAvatar: ImageView? = null
        var goodName: TextView? = null
        var goodNameExplain: TextView? = null
        var goodTag: TextView? = null
        var price: TextView? = null
        var subPrice: TextView? = null
        var subTag: TextView? = null
        var goodShoppingCartRoot: LinearLayout? = null
        var cartNum: TextView? = null
        var cartReduce: LinearLayout? = null
        var cartIncrease: LinearLayout? = null
        var chooseSpecs: TextView? = null
    }

    fun getLeftViewAdapter(): LeftViewAdapter? = mLeftAdapter
    fun getMergeAdapter(): StrengthenedMergeAdapter? = mMergeAdapter
    internal fun getRightTitleViews(): ArrayList<View>? = mRightTitleViews

    //todo 添加监听事件 -被选入购物车数据发生变化

    fun setCartDataChangedListener(listener: (OnCartDataChangedListener<String>.() -> Unit)) {
        val changedListener = OnCartDataChangedListener<String>()
        changedListener.listener()
        mCartDataChangeListener = changedListener
    }

}
package com.same.part.assistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.same.part.assistant.Helper.DoubleLinkedViewHelper.Companion.RIGHT_CONTENT_TYPE
import com.same.part.assistant.Helper.DoubleLinkedViewHelper.Companion.RIGHT_TITLE_TYPE
import com.same.part.assistant.R
import com.zhihu.matisse.internal.ui.widget.RoundedRectangleImageView

class DoubleLinkedViewAdapter {




    internal class LeftViewAdapter(private val mContext: Context) :
        BaseAdapter() {
        override fun getCount(): Int {
            return 0
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
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            viewHolder.leftName?.text = ""
            return view
        }

        internal inner class ViewHolder {
            var leftName: TextView? = null
        }

    }


    internal class RightViewAdapter(private val mContext: Context) :
        BaseAdapter() {
        private var mRightType: String? = null
        override fun getCount(): Int {
            return 0
        }

        override fun getItem(position: Int): Any? = null

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getItemViewType(position: Int): Int =
            if (mRightType == "0") RIGHT_TITLE_TYPE else RIGHT_CONTENT_TYPE

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            var view: View
            var titleViewHolder: TitleViewHolder
            var goodViewHolder: GoodViewHolder
            var itemViewType = getItemViewType(position)

            if (convertView == null) {
                if (itemViewType == RIGHT_TITLE_TYPE) {
                    view = LayoutInflater.from(mContext)
                        .inflate(R.layout.layout_double_linked_view_right_title_item, null)
                    titleViewHolder = TitleViewHolder()
                    titleViewHolder.rightTitle = view.findViewById(R.id.tv_right_title)
                    view.tag = titleViewHolder
                } else {
                    view = LayoutInflater.from(mContext)
                        .inflate(R.layout.layout_double_linked_right_order_item, null)
                    goodViewHolder = GoodViewHolder()
                    goodViewHolder.goodAvatar = view.findViewById(R.id.goodAvatar)
                    goodViewHolder.goodName = view.findViewById(R.id.goodName)
                    goodViewHolder.goodNameExplain = view.findViewById(R.id.goodNameExplain)
                    goodViewHolder.goodTag = view.findViewById(R.id.goodTag)
                    goodViewHolder.price = view.findViewById(R.id.tv_price)
                    goodViewHolder.subPrice = view.findViewById(R.id.tv_sub_price)
                    goodViewHolder.subTag = view.findViewById(R.id.tv_sub_tag)
                    goodViewHolder.goodShoppingCart = view.findViewById(R.id.goodShoppingCart)
                    goodViewHolder.chooseSpecs = view.findViewById(R.id.chooseSpecs)
                }
            } else {
                if (itemViewType == RIGHT_TITLE_TYPE) {
                    view = convertView
                    titleViewHolder = view.tag as TitleViewHolder
                    //填充view
                    titleViewHolder.rightTitle?.text = ""
                } else {
                    view = convertView
                    goodViewHolder = view.tag as GoodViewHolder
                    //填充View

                }
            }
            return view
        }

        internal inner class TitleViewHolder {
            var rightTitle: TextView? = null
        }

        internal inner class GoodViewHolder {
            var goodAvatar: RoundedRectangleImageView? = null
            var goodName: TextView? = null
            var goodNameExplain: TextView? = null
            var goodTag: TextView? = null
            var price: TextView? = null
            var subPrice: TextView? = null
            var subTag: TextView? = null
            var goodShoppingCart: ImageView? = null
            var chooseSpecs: TextView? = null
        }

    }
}
package com.same.part.assistant.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.adapter.PayWayDialogAdapter
import com.same.part.assistant.model.PayWayModel
import kotlinx.android.synthetic.main.dialog_pay_way.*

class PayWayDialogFragment(var mContext: Context) : DialogFragment() {

    private var mAdapter: PayWayDialogAdapter? = null
    private var mListener: ((String) -> Unit)? = null

    private val mPayWayData = arrayListOf<PayWayModel>().apply {
        add(PayWayModel("在线付款", false))
        add(PayWayModel("货到付款", true))
    }

    private lateinit var payWayListView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            LayoutInflater.from(mContext).inflate(R.layout.dialog_pay_way, container, false)
        payWayListView = view.findViewById(R.id.payWayListView)
        payWayListView.apply {
            mAdapter = PayWayDialogAdapter(mContext, mPayWayData)
            mAdapter?.setOnItemClickListener {

            }
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(mContext)
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            decorView.setPadding(0, 0, 0, 0)
            attributes.gravity = Gravity.BOTTOM
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        dialog.apply {
            setCanceledOnTouchOutside(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelButton.setOnClickListener {
            dialog?.takeIf { it.isShowing }?.apply {
                dismiss()
            }
        }
    }

    fun show(manager: FragmentManager) {
        show(manager, "PayWayDialogFragment")
    }

    fun setChooseCallBack(listener: (String)->Unit) {
        mListener = listener
    }
}
package com.uploadcare.android.example.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.uploadcare.android.example.R
import com.uploadcare.android.library.urls.Order

class OrderDialogFragment : DialogFragment() {

    private lateinit var listener: OrderDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = try {
            if (parentFragment != null) {
                parentFragment as OrderDialogListener
            } else {
                context as OrderDialogListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Parent must implement OrderDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!).apply {
            setItems(R.array.orders) { dialog, which -> orderSelected(which) }
            setNegativeButton(android.R.string.cancel, null)
        }.create()
    }

    private fun orderSelected(position: Int) {
        val order: Order
        when (position) {
            1 -> order = Order.UPLOAD_TIME_DESC
            2 -> order = Order.SIZE_ASC
            3 -> order = Order.SIZE_DESC
            0 -> order = Order.UPLOAD_TIME_ASC
            else -> order = Order.UPLOAD_TIME_ASC
        }
        listener.onOrderSelected(order)
    }

    companion object {

        fun newInstance(): OrderDialogFragment {
            return OrderDialogFragment()
        }
    }
}

interface OrderDialogListener {
    fun onOrderSelected(order: Order)
}
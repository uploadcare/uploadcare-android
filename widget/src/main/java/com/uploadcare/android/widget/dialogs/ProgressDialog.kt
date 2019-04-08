package com.uploadcare.android.widget.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.uploadcare.android.widget.R

class ProgressDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val message = arguments?.getString(DIALOG_MESSAGE)

        return AlertDialog.Builder(context!!, R.style.UploadcareWidget_AlertDialogStyle)
                .setTitle(message)
                .setView(R.layout.ucw_dialog_progress)
                .setCancelable(false)
                .create()
    }

    companion object {

        private const val DIALOG_MESSAGE = "extras.dialog_message"

        fun newInstance(message: String?): ProgressDialog {
            val arguments = Bundle().apply {
                putString(DIALOG_MESSAGE, message)
            }

            val fragment = ProgressDialog()
            fragment.arguments = arguments
            return fragment
        }
    }
}
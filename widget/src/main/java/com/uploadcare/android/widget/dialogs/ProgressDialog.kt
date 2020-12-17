package com.uploadcare.android.widget.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.databinding.UcwDialogProgressHorizontalBinding
import com.uploadcare.android.widget.viewmodels.ProgressData

class ProgressDialog : DialogFragment() {

    val uploadProgress = MutableLiveData<Int>().apply { value = 0 }

    private lateinit var cancelUploadListener: CancelUploadListener

    private var showProgress: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        cancelUploadListener = try {
            if (parentFragment != null) {
                parentFragment as CancelUploadListener
            } else {
                context as CancelUploadListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Parent must implement CancelUploadListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val message = arguments?.getString(DIALOG_MESSAGE)
        showProgress = arguments?.getBoolean(DIALOG_SHOW_PROGRESS) ?: false
        val cancelable = arguments?.getBoolean(DIALOG_CANCELABLE) ?: false

        val dialogBuilder = AlertDialog.Builder(requireContext(),
                R.style.UploadcareWidget_AlertDialogStyle).apply {
            setTitle(message)
            setCancelable(false)
            if (showProgress) {
                val binding = UcwDialogProgressHorizontalBinding.inflate(LayoutInflater.from(context), null, false)
                binding.lifecycleOwner = activity
                binding.dialog = this@ProgressDialog

                setView(binding.root)
            } else {
                setView(R.layout.ucw_dialog_progress)
            }

            if (cancelable) {
                setNegativeButton(android.R.string.cancel) { dialogInterface, i ->
                    cancelUploadListener.onCancelUpload()
                }
            }
        }

        return dialogBuilder.create()
    }

    fun updateProgress(progress: Int) {
        if (showProgress) {
            uploadProgress.value = progress
        }
    }

    companion object {

        private const val DIALOG_MESSAGE = "extras.dialog_message"
        private const val DIALOG_SHOW_PROGRESS = "extras.dialog_show_progress"
        private const val DIALOG_CANCELABLE = "extras.dialog_cancelable"

        fun newInstance(progressData: ProgressData): ProgressDialog {
            val arguments = Bundle().apply {
                putString(DIALOG_MESSAGE, progressData.message)
                putBoolean(DIALOG_SHOW_PROGRESS, progressData.showProgress)
                putBoolean(DIALOG_CANCELABLE, progressData.cancelable)
            }

            val fragment = ProgressDialog()
            fragment.arguments = arguments
            return fragment
        }
    }
}

internal interface CancelUploadListener {
    fun onCancelUpload()
}
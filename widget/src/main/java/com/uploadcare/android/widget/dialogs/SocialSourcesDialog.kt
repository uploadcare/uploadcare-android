package com.uploadcare.android.widget.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.adapter.SocialNetworksAdapter
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.utils.tryGetParcelableArrayListSafely

class SocialSourcesDialog : DialogFragment() {

    private lateinit var socialSourcesListener: SocialSourcesListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        socialSourcesListener = try {
            if (parentFragment != null) {
                parentFragment as SocialSourcesListener
            } else {
                context as SocialSourcesListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Parent must implement SocialSourcesListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(DIALOG_TITLE)
        val fileType = arguments?.getString(DIALOG_FILE_TYPE)?.let { FileType.valueOf(it) }
                ?: FileType.any
        val sources = arguments?.tryGetParcelableArrayListSafely(
            DIALOG_SOURCES,
            SocialSource::class.java
        ) ?: listOf()

        val dialogBuilder = AlertDialog.Builder(requireContext(),
                R.style.UploadcareWidget_AlertDialogStyle).apply {
            setCancelable(true)
            setTitle(title)
            setNegativeButton(R.string.ucw_cancel) { dialog, which ->
                socialSourcesListener.onSelectSourceCanceled()
            }
            setOnCancelListener { dialog ->
                socialSourcesListener.onSelectSourceCanceled()
            }
            setOnKeyListener { dialog, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    socialSourcesListener.onSelectSourceCanceled()
                    true
                } else {
                    false
                }
            }

            val adapter = SocialNetworksAdapter(context,
                    fileType,
                    sources.toMutableList()) { socialSource ->
                dialog?.dismiss()
                socialSourcesListener.onSocialSourceSelected(socialSource)
            }
            setAdapter(adapter, null)
        }

        return dialogBuilder.create()
    }

    companion object {

        private const val DIALOG_TITLE = "extras.dialog_title"
        private const val DIALOG_FILE_TYPE = "extras.dialog_file_type"
        private const val DIALOG_SOURCES = "extras.dialog_sources"

        fun newInstance(sources: List<SocialSource>,
                        title: String,
                        fileType: FileType): SocialSourcesDialog {
            val arguments = Bundle().apply {
                putString(DIALOG_TITLE, title)
                putString(DIALOG_FILE_TYPE, fileType.name)
                putParcelableArrayList(DIALOG_SOURCES, ArrayList(sources))
            }

            val fragment = SocialSourcesDialog()
            fragment.arguments = arguments
            return fragment
        }
    }
}

interface SocialSourcesListener {
    fun onSocialSourceSelected(socialSource: SocialSource)
    fun onSelectSourceCanceled()
}

package com.uploadcare.android.widget.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uploadcare.android.library.exceptions.UploadcareException
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.controller.UploadcareWidgetResult
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.databinding.UcwFragmentUploadcareBinding
import com.uploadcare.android.widget.dialogs.CancelUploadListener
import com.uploadcare.android.widget.dialogs.SocialSourcesListener
import com.uploadcare.android.widget.utils.CaptureMedia
import com.uploadcare.android.widget.utils.NavigationHelper
import com.uploadcare.android.widget.utils.OpenDocumentLocally
import com.uploadcare.android.widget.viewmodels.MediaType
import com.uploadcare.android.widget.viewmodels.UploadcareViewModel

class UploadcareFragment : Fragment(), SocialSourcesListener, CancelUploadListener {

    private lateinit var binding: UcwFragmentUploadcareBinding
    private lateinit var viewModel: UploadcareViewModel

    private val captureMediaLauncher = registerForActivityResult(CaptureMedia) { success ->
        if (success) {
            viewModel.uploadFile()
        } else {
            activity?.finish()
        }
    }

    private val filePickerLauncher = registerForActivityResult(OpenDocumentLocally) { fileUri ->
        fileUri?.let {
            context?.contentResolver?.takePersistableUriPermission(
                fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            viewModel.uploadFile(fileUri)
        } ?: activity?.finish()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = UcwFragmentUploadcareBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get()
        viewModel.progressDialogCommand.observe(this.viewLifecycleOwner, { progressData ->
            if (progressData.show) {
                NavigationHelper.showProgressDialog(childFragmentManager, progressData)
            } else {
                NavigationHelper.dismissProgressDialog(childFragmentManager)
            }
        })
        viewModel.uploadProgress.observe(this.viewLifecycleOwner, { progress ->
            NavigationHelper.updateProgressDialogProgress(childFragmentManager, progress)
        })

        viewModel.showSocialSourcesCommand.observe(this.viewLifecycleOwner, { pair ->
            NavigationHelper.showSocialSourcesDialog(childFragmentManager,
                    pair.first,
                    getString(R.string.ucw_action_select_network),
                    pair.second)
        })
        viewModel.launchSocialSourceCommand.observe(this.viewLifecycleOwner, { socialData ->
            findNavController().navigate(UploadcareFragmentDirections
                    .actionUploadcareFragmentToUploadcareFilesFragment(
                            socialData.socialSource,
                            socialData.storeUponUpload,
                            socialData.cancelable,
                            socialData.showProgress,
                            socialData.backgroundUpload),
                    NavOptions.Builder().setPopUpTo(R.id.uploadcareFragment, true).build())
        })
        viewModel.launchCamera.observe(this.viewLifecycleOwner, { mediaType ->
            launchCamera(mediaType.first, mediaType.second)
        })
        viewModel.launchFilePicker.observe(this.viewLifecycleOwner, { fileType ->
            launchFilePicker(fileType)
        })
        viewModel.closeWidgetCommand.observe(this.viewLifecycleOwner, { exception ->
            activity?.setResult(RESULT_OK, Intent().apply {
                putExtra("result",
                        UploadcareWidgetResult(exception = UploadcareException(exception?.message)))
            })
            activity?.finish()
        })
        viewModel.uploadCompleteCommand.observe(this.viewLifecycleOwner, { uploadcareFile ->
            activity?.setResult(RESULT_OK, Intent().apply {
                putExtra("result", UploadcareWidgetResult(uploadcareFile = uploadcareFile))
            })
            activity?.finish()
        })
        viewModel.uploadingInBackgroundCommand.observe(this.viewLifecycleOwner, { uuid ->
            activity?.setResult(RESULT_OK, Intent().apply {
                putExtra("result", UploadcareWidgetResult(backgroundUploadUUID = uuid))
            })
            activity?.finish()
        })

        savedInstanceState?.let { viewModel.onRestoreInstanceState(it) }
        activity?.intent?.extras?.let { viewModel.start(it) }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveInstanceState(outState)
    }

    override fun onSocialSourceSelected(socialSource: SocialSource) {
        viewModel.sourceSelected(socialSource)
    }

    override fun onSelectSourceCanceled() {
        activity?.finish()
    }

    override fun onCancelUpload() {
        viewModel.canlcelUpload()
    }

    private fun launchCamera(uri: Uri, mediaType: MediaType) {
        captureMediaLauncher.launch(uri to mediaType)
    }

    private fun launchFilePicker(fileType: FileType) {
        filePickerLauncher.launch(arrayOf(getTypeForFileChooser(fileType)))
    }

    private fun getTypeForFileChooser(fileType: FileType): String {
        return when (fileType) {
            FileType.image -> "image/*"
            FileType.video -> "video/*"
            else -> "*/*"
        }
    }
}

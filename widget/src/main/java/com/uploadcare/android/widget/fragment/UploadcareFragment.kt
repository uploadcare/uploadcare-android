package com.uploadcare.android.widget.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.uploadcare.android.widget.utils.NavigationHelper
import com.uploadcare.android.widget.viewmodels.MediaType
import com.uploadcare.android.widget.viewmodels.UploadcareViewModel

class UploadcareFragment : Fragment(), SocialSourcesListener, CancelUploadListener {

    private lateinit var binding: UcwFragmentUploadcareBinding
    private lateinit var viewModel: UploadcareViewModel

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            activity?.finish()
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        when (requestCode) {
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE,
            CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE -> {
                viewModel.uploadFile()
            }
            CHOOSE_FILE_ACTIVITY_REQUEST_CODE -> {
                val fileUri = data?.data
                fileUri?.let {
                    context?.contentResolver?.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

                fileUri?.let { viewModel.uploadFile(it) } ?: activity?.finish()
            }
        }
    }

    private fun launchCamera(uri: Uri, mediaType: MediaType) {
        when (mediaType) {
            MediaType.IMAGE -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, uri)
                }
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
            else -> {
                val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                }
                startActivityForResult(videoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    private fun launchFilePicker(fileType: FileType) {
        val chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = getTypeForFileChooser(fileType)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }

        val intentFile = Intent.createChooser(chooseFile, getString(R.string.ucw_choose_file))
        startActivityForResult(intentFile, CHOOSE_FILE_ACTIVITY_REQUEST_CODE)
    }

    private fun getTypeForFileChooser(fileType: FileType): String {
        return when (fileType) {
            FileType.image -> "image/*"
            FileType.video -> "video/*"
            else -> "*/*"
        }
    }

    companion object {

        private const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100
        private const val CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200
        private const val CHOOSE_FILE_ACTIVITY_REQUEST_CODE = 300
    }
}
package com.uploadcare.android.example.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.uploadcare.android.example.R
import com.uploadcare.android.example.databinding.FragmentUploadBinding
import com.uploadcare.android.example.viewmodels.UploadViewModel
import com.uploadcare.android.widget.controller.UploadcareWidgetResult

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private lateinit var viewModel: UploadViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get()
        binding.fragment = this
        binding.viewModel = viewModel

        (activity as AppCompatActivity).let {
            it.setSupportActionBar(binding.toolbar)
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.uploadFragment))
            binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        }

        viewModel.launchGetFilesCommand.observe(this.viewLifecycleOwner, Observer {
            findNavController().navigate(UploadFragmentDirections
                    .actionUploadFragmentToFilesFragment())
        })
        viewModel.launchFilePickerCommand.observe(this.viewLifecycleOwner, Observer {
            selectFilesForUpload()
        })

        return binding.root
    }

    /**
     * Handles results from Uploadcare Widget social network file upload, file/files pick intent.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            // Check file/files pick intent results.
            if (requestCode == ACTIVITY_CHOOSE_FILE) {
                if (data?.data != null) {
                    // Upload 1 file.
                    viewModel.uploadFile(data.data!!)
                    return
                } else if (data?.clipData != null) {
                    // Upload multiple files.
                    val count = data.clipData?.itemCount ?: 0
                    val uriList = mutableListOf<Uri>()
                    for (i in 0 until count) {
                        data.clipData?.getItemAt(i)?.uri?.let { uriList.add(it) }
                    }
                    viewModel.uploadFiles(uriList)
                    return
                }
            }

            // Check Uploadcare Widget social network file upload result
            val result = UploadcareWidgetResult.fromIntent(data)
            result?.let { viewModel.onUploadResult(it) }
        }
    }

    /**
     * Launches file/files pick intent.
     */
    private fun selectFilesForUpload() {
        val chooseFile = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        val intent = Intent.createChooser(chooseFile,
                resources.getString(R.string.activity_main_choose_file))
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE)
    }

    companion object {
        private const val ACTIVITY_CHOOSE_FILE = 101
    }
}

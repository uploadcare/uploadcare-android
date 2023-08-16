package com.uploadcare.android.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.uploadcare.android.example.R
import com.uploadcare.android.example.databinding.FragmentUploadBinding
import com.uploadcare.android.example.utils.GetMultipleContentsLocally
import com.uploadcare.android.example.viewmodels.UploadViewModel
import com.uploadcare.android.widget.controller.UploadcareActivityResultContract

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private lateinit var viewModel: UploadViewModel

    private val selectFilesLauncher = registerForActivityResult(
        GetMultipleContentsLocally
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.uploadFiles(uris)
        }
    }

    private val uploadcareLauncher = registerForActivityResult(UploadcareActivityResultContract) { result ->
        result?.let { viewModel.onUploadResult(result) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.uploadcareLauncher = uploadcareLauncher
        binding.viewModel = viewModel

        (activity as AppCompatActivity).let {
            it.setSupportActionBar(binding.toolbar)
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.uploadFragment))
            binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        }

        viewModel.launchGetFilesCommand.observe(this.viewLifecycleOwner, {
            findNavController().navigate(UploadFragmentDirections
                    .actionUploadFragmentToFilesFragment())
        })
        viewModel.launchFilePickerCommand.observe(this.viewLifecycleOwner, {
            selectFilesForUpload()
        })
        viewModel.backgroundUploadResult.observe(this.viewLifecycleOwner, {
            viewModel.onUploadResult(it)
        })

        return binding.root
    }

    /**
     * Launches file/files pick intent.
     */
    private fun selectFilesForUpload() {
        selectFilesLauncher.launch("image/*")
    }
}

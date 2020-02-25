package com.uploadcare.android.widget.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.exceptions.UploadcareException
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.adapter.ToolbarSpinnerAdapter
import com.uploadcare.android.widget.controller.UploadcareWidgetResult
import com.uploadcare.android.widget.data.Chunk
import com.uploadcare.android.widget.databinding.UcwFragmentFilesBinding
import com.uploadcare.android.widget.utils.NavigationHelper
import com.uploadcare.android.widget.viewmodels.UploadcareFilesViewModel

class UploadcareFilesFragment : Fragment(), AdapterView.OnItemSelectedListener,
        OnFileActionsListener, OnAuthListener {

    private lateinit var binding: UcwFragmentFilesBinding
    private lateinit var viewModel: UploadcareFilesViewModel

    private val args: UploadcareFilesFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = UcwFragmentFilesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get()
        binding.viewModel = viewModel

        (activity as AppCompatActivity).let {
            it.setSupportActionBar(binding.ucwToolbar)
            it.supportActionBar?.setDisplayShowTitleEnabled(false)
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        context?.let { context ->
            binding.ucwSpinner.apply {
                adapter = ToolbarSpinnerAdapter(context).apply {
                    updateItems(args.socialsource.rootChunks)
                }
                onItemSelectedListener = this@UploadcareFilesFragment
            }
        }

        viewModel.progressDialogCommand.observe(this.viewLifecycleOwner, Observer { pait ->
            if (pait.first) {
                NavigationHelper.showProgressDialog(childFragmentManager, pait.second)
            } else {
                NavigationHelper.dismissProgressDialog(childFragmentManager)
            }
        })
        viewModel.closeWidgetCommand.observe(this.viewLifecycleOwner, Observer { exception ->
            activity?.setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("result",
                        UploadcareWidgetResult(exception = UploadcareException(exception?.message)))
            })
            activity?.finish()
        })
        viewModel.uploadCompleteCommand.observe(this.viewLifecycleOwner, Observer { uploadcareFile ->
            activity?.setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("result", UploadcareWidgetResult(uploadcareFile = uploadcareFile))
            })
            activity?.finish()
        })

        viewModel.start(args.socialsource, args.store)

        childFragmentManager.addOnBackStackChangedListener {
            val chunkFragment = childFragmentManager.findFragmentByTag(fragmentTag)
                    as UploadcareChunkFragment?
            if (chunkFragment != null && chunkFragment.isAdded) {
                updateTitle(chunkFragment.getTitle())
            }
        }

        if (savedInstanceState == null) {
            val chunkFragment = UploadcareChunkFragment
                    .newInstance(binding.ucwSpinner.selectedItemPosition,
                            args.socialsource,
                            args.socialsource.rootChunks,
                            isRoot = true)
            childFragmentManager.beginTransaction()
                    .add(R.id.chunkHolder, chunkFragment, fragmentTag).commit()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.ucw_social_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                checkBackStack()
                true
            }
            R.id.ucw_action_sign_out -> {
                viewModel.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Ignore.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        updateChunk(position)
    }

    override fun onError(exception: UploadcareApiException) {
        exception.message?.let { showError(it) }
    }

    override fun onFileSelected(fileUrl: String) {
        viewModel.selectFile(fileUrl)
    }

    override fun onAuthorizationNeeded(loginLink: String) {
        val authFragment = UploadcareAuthFragment.newInstance(loginLink)
        childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.ucw_fragment_slide_left_enter, 0, 0,
                        R.anim.ucw_fragment_slide_right_exit)
                .replace(R.id.chunkHolder, authFragment, authFragmentTag)
                .commit()
        updateTitle(getString(R.string.ucw_authorization_fragment_title))
    }

    override fun onChunkSelected(chunks: List<Chunk>, title: String) {
        openChunk(chunks, title)
    }

    override fun onAuthSuccess(cookie: String) {
        viewModel.saveCookie(cookie)

        val chunkFragment = UploadcareChunkFragment
                .newInstance(binding.ucwSpinner.selectedItemPosition,
                        args.socialsource,
                        args.socialsource.rootChunks,
                        isRoot = true)
        childFragmentManager.beginTransaction()
                .replace(R.id.chunkHolder, chunkFragment, fragmentTag)
                .commit()
        updateTitle(null)
    }

    override fun onAuthError() {
        activity?.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("result", UploadcareWidgetResult(exception = UploadcareException("Auth error")))
        })
        activity?.finish()
    }

    private fun updateTitle(title: String?) {
        (activity as AppCompatActivity).let {
            viewModel.isRoot.set(title == null)
            title?.let { updatedTitle ->
                it.supportActionBar?.apply {
                    setTitle(updatedTitle)
                    setDisplayShowTitleEnabled(true)
                    setDisplayHomeAsUpEnabled(true)
                }
            } ?: it.supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    private fun updateChunk(position: Int, chunks: List<Chunk>? = null, title: String? = null) {
        if (chunks == null) {
            val chunkFragment = childFragmentManager.findFragmentByTag(fragmentTag)
                    as UploadcareChunkFragment?
            if (chunkFragment != null && chunkFragment.isAdded) {
                chunkFragment.changeChunk(position)
            }
        } else {
            val chunkFragment = UploadcareChunkFragment
                    .newInstance(position, args.socialsource, chunks, title)
            childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.ucw_fragment_slide_left_enter, 0, 0,
                            R.anim.ucw_fragment_slide_right_exit)
                    .add(R.id.chunkHolder, chunkFragment, fragmentTag)
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun openChunk(chunks: List<Chunk>, title: String) {
        updateChunk(binding.ucwSpinner.selectedItemPosition, chunks, title)
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun checkBackStack() {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
        } else {
            activity?.finish()
        }
    }

    companion object {
        private const val authFragmentTag = "auth"
        private const val fragmentTag = "latest"
    }
}
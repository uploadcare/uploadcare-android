package com.uploadcare.android.widget.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.adapter.FilesAdapter
import com.uploadcare.android.widget.adapter.FilesGridAdapter
import com.uploadcare.android.widget.adapter.FilesLinearAdapter
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.controller.SocialNetwork
import com.uploadcare.android.widget.data.Action
import com.uploadcare.android.widget.data.Chunk
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.data.Thing
import com.uploadcare.android.widget.databinding.UcwFragmentChunkBinding
import com.uploadcare.android.widget.utils.RecyclerViewOnScrollListener
import com.uploadcare.android.widget.viewmodels.UploadcareChunkViewModel

class UploadcareChunkFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: UcwFragmentChunkBinding
    private lateinit var viewModel: UploadcareChunkViewModel

    private lateinit var mOnFileActionsListener: OnFileActionsListener

    private var mAdapter: FilesAdapter<*>? = null
    private var mOnScrollListener: RecyclerViewOnScrollListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mOnFileActionsListener = try {
            if (parentFragment != null) {
                parentFragment as OnFileActionsListener
            } else {
                context as OnFileActionsListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Parent must implement OnFileActionsListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = UcwFragmentChunkBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val socialSource = arguments?.getParcelable("socialSource") as SocialSource?
        binding.ucwRecyclerView.apply {
            if (socialSource?.name == SocialNetwork.SOCIAL_NETWORK_BOX.rawValue
                    || socialSource?.name == SocialNetwork.SOCIAL_NETWORK_DROPBOX.rawValue
                    || socialSource?.name == SocialNetwork.SOCIAL_NETWORK_EVERNOTE.rawValue
                    || socialSource?.name == SocialNetwork.SOCIAL_NETWORK_SKYDRIVE.rawValue
                    || socialSource?.name == SocialNetwork.SOCIAL_NETWORK_GDRIVE.rawValue) {
                layoutManager = LinearLayoutManager(context)
                mAdapter = FilesLinearAdapter(FileType.any) { thing ->
                    itemSelected(thing)
                }
                val pad = resources.getDimensionPixelSize(R.dimen.ucw_list_linear_padding)
                setPadding(0, pad, 0, pad)
            } else {
                layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.columns))
                mAdapter = FilesGridAdapter(FileType.any) { thing ->
                    itemSelected(thing)
                }
                val pad = resources.getDimensionPixelSize(R.dimen.ucw_list_grid_padding)
                setPadding(pad, pad, pad, pad)
            }
            adapter = mAdapter
        }
        binding.ucwSearchView.setOnQueryTextListener(this)

        binding.ucwRecyclerView.layoutManager?.let {
            mOnScrollListener = RecyclerViewOnScrollListener(it) {
                viewModel.loadMore()
            }
        }

        viewModel.things.observe(this.viewLifecycleOwner, Observer { things ->
            mAdapter?.updateItems(things)
        })
        viewModel.allowLoadMore.observe(this.viewLifecycleOwner, Observer { allowLoadMore ->
            if (allowLoadMore) {
                mOnScrollListener?.let {
                    it.clear()
                    binding.ucwRecyclerView.addOnScrollListener(it)
                }
            } else {
                binding.ucwRecyclerView.clearOnScrollListeners()
            }
        })
        viewModel.errorCommand.observe(this.viewLifecycleOwner, Observer { exception ->
            exception?.let { mOnFileActionsListener.onError(it) }
        })
        viewModel.needAuthCommand.observe(this.viewLifecycleOwner, Observer { loginLink ->
            loginLink?.let { mOnFileActionsListener.onAuthorizationNeeded(loginLink) }
        })

        arguments?.let { viewModel.start(it) }

        return binding.root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.search(query)
        return false
    }

    override fun onQueryTextChange(newText: String?) = false

    fun getTitle(): String? {
        return viewModel.title
    }

    fun changeChunk(position: Int) {
        viewModel.changeChunk(position)
    }

    private fun itemSelected(thing: Thing) {
        when (thing.objectType) {
            Thing.TYPE_ALBUM,
            Thing.TYPE_FOLDER,
            Thing.TYPE_FRIEND -> {
                thing.action?.path?.chunks?.let {
                    mOnFileActionsListener.onChunkSelected(it, thing.title ?: "")
                }
            }
            Thing.TYPE_PHOTO,
            Thing.TYPE_FILE -> {
                thing.action?.url?.let {
                    if (thing.action.action == Action.ACTION_SELECT_FILE) {
                        mOnFileActionsListener.onFileSelected(it)
                    }
                }
            }
            else -> {
                Log.d("UploadcareChunkFragment", "Unknown thing type: ${thing.objectType}")
            }
        }
    }

    companion object {

        /**
         * Create a new instance of UploadcareChunkFragment, initialized to
         * show the provided Chunk content.
         */
        fun newInstance(currentRootChunk: Int,
                        socialSource: SocialSource,
                        chunks: List<Chunk>,
                        title: String? = null,
                        isRoot: Boolean = false): UploadcareChunkFragment {
            return UploadcareChunkFragment().apply {
                val args = Bundle().apply {
                    putInt("currentChunk", currentRootChunk)
                    putParcelable("socialSource", socialSource)
                    putParcelableArrayList("chunks", ArrayList(chunks))
                    putString("title", title)
                    putBoolean("isRoot", isRoot)
                }
                arguments = args
            }
        }
    }

}

interface OnFileActionsListener {

    fun onError(exception: UploadcareApiException)

    fun onFileSelected(fileUrl: String)

    fun onAuthorizationNeeded(loginLink: String)

    fun onChunkSelected(chunks: List<Chunk>, title: String)
}
package com.uploadcare.android.widget.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.controller.SocialNetwork
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.data.Urls
import com.uploadcare.android.widget.databinding.UcwDialogNetworkItemBinding

class SocialNetworksAdapter(context: Context,
                            fileType: FileType? = FileType.any,
                            private val sources: MutableList<SocialSource>,
                            private val clickObserver: ((SocialSource) -> Unit)? = null)
    : ArrayAdapter<SocialSource>(context, R.layout.ucw_dialog_network_item) {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    init {
        sources.sortBy { it.name }
        sources.add(0, socialFile)

        val hasCamera = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
        if (hasCamera) {
            when (fileType) {
                FileType.image -> {
                    sources.add(0, socialImages)
                }
                FileType.video -> {
                    sources.add(0, socialVideos)
                }
                else -> {
                    sources.add(0, socialVideos)
                    sources.add(0, socialImages)
                }
            }
        }
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let {
            DataBindingUtil.getBinding<UcwDialogNetworkItemBinding>(convertView)
        } ?: UcwDialogNetworkItemBinding.inflate(inflater, parent, false)
        binding.adapter = this
        binding.socialSource = getItem(position)
        binding.executePendingBindings()
        // Invalidate to make sure view correctly calculates it's size inside DialogFragment.
        binding.invalidateAll()

        return binding.root
    }

    fun onSocialSourceSelected(socialSource: SocialSource) {
        clickObserver?.let { it(socialSource) }
    }

    override fun getItem(position: Int): SocialSource? {
        return sources[position]
    }

    override fun isEmpty(): Boolean {
        return sources.isEmpty()
    }

    override fun getCount(): Int {
        return sources.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun clear() {
        super.clear()
        sources.clear()
    }

    companion object {

        private val socialFile = SocialSource(listOf(),
                SocialNetwork.SOCIAL_NETWORK_FILE.rawValue,
                Urls("", "", ""))

        private val socialImages = SocialSource(listOf(),
                SocialNetwork.SOCIAL_NETWORK_CAMERA.rawValue,
                Urls("", "", ""))

        private val socialVideos = SocialSource(listOf(),
                SocialNetwork.SOCIAL_NETWORK_VIDEOCAM.rawValue,
                Urls("", "", ""))
    }
}
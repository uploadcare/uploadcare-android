package com.uploadcare.android.widget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.squareup.picasso.Picasso
import com.uploadcare.android.widget.BuildConfig
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.data.Thing
import com.uploadcare.android.widget.databinding.UcwFileItemBinding

class FilesGridAdapter(fileType: FileType,
                       private val clickObserver: ((Thing) -> Unit)? = null)
    : FilesAdapter<ThingGridViewHolder>(fileType) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingGridViewHolder {
        return ThingGridViewHolder.build(parent, clickObserver)
    }
}

class ThingGridViewHolder(private val binding: UcwFileItemBinding,
                          private val clickObserver: ((Thing) -> Unit)? = null)
    : BaseViewHolder<Thing>(binding.root) {
    override fun bind(data: Thing) {
        binding.handler = this
        binding.thing = data

        val placeHolderResource = getPlaceHolderResource(data)
        data.thumbnail?.let { thumbnail ->
            val url = if (!thumbnail.startsWith("http", true)
                    || !thumbnail.startsWith("https", true)) {
                BuildConfig.SOCIAL_API_ENDPOINT + thumbnail
            } else {
                thumbnail
            }
            Picasso.with(binding.root.context)
                    .load(url)
                    .placeholder(placeHolderResource)
                    .into(binding.ucwItemTb)
        } ?: Picasso.with(binding.root.context).load(placeHolderResource).into(binding.ucwItemTb)
    }

    override fun getBinding(): ViewDataBinding {
        return binding
    }

    fun select(thing: Thing) {
        clickObserver?.let { it(thing) }
    }

    companion object {
        fun build(parent: ViewGroup,
                  clickObserver: ((Thing) -> Unit)? = null)
                : ThingGridViewHolder {
            return ThingGridViewHolder(UcwFileItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false), clickObserver)
        }
    }
}
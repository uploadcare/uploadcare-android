package com.uploadcare.android.widget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.squareup.picasso.Picasso
import com.uploadcare.android.widget.BuildConfig
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.data.Thing
import com.uploadcare.android.widget.databinding.UcwFileLinearItemBinding

class FilesLinearAdapter(fileType: FileType,
                         private val clickObserver: ((Thing) -> Unit)? = null)
    : FilesAdapter<ThingLinearViewHolder>(fileType) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingLinearViewHolder {
        return ThingLinearViewHolder.build(parent, clickObserver)
    }
}

class ThingLinearViewHolder(private val binding: UcwFileLinearItemBinding,
                            private val clickObserver: ((Thing) -> Unit)? = null)
    : BaseViewHolder<Thing>(binding.root) {
    override fun bind(data: Thing) {
        binding.handler = this
        binding.thing = data

        val placeHolderResource = getLinearPlaceHolderResource(data)
        data.thumbnail?.let { thumbnail ->
            val url = if (!thumbnail.startsWith("http", true)
                    || !thumbnail.startsWith("https", true)) {
                BuildConfig.SOCIAL_API_ENDPOINT + thumbnail
            } else {
                thumbnail
            }
            Picasso.get()
                    .load(url)
                    .placeholder(placeHolderResource)
                    .into(binding.ucwItemTb)
        } ?: Picasso.get().load(placeHolderResource).into(binding.ucwItemTb)
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
                : ThingLinearViewHolder {
            return ThingLinearViewHolder(UcwFileLinearItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false), clickObserver)
        }
    }
}
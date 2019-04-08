package com.uploadcare.android.widget.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.data.Action
import com.uploadcare.android.widget.data.Thing

abstract class FilesAdapter<VH : BaseViewHolder<Thing>>(private val fileType: FileType)
    : RecyclerView.Adapter<VH>() {

    private val dataList = mutableListOf<Thing>()

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = dataList[position]
        holder.bind(data)
        holder.getBinding().executePendingBindings()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getData(): MutableList<Thing> {
        return dataList
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    /**
     * Update existing Dataset.
     */
    fun updateItems(data: List<Thing>?) {
        clear()
        addItems(data)
    }

    private fun addItems(data: List<Thing>?) {
        if (data == null) {
            return
        }

        when (fileType) {
            FileType.any -> {
                dataList.addAll(data)
                notifyDataSetChanged()
            }
            FileType.video,
            FileType.image -> {
                for (thing in data) {
                    if (thing.objectType.equals(Thing.TYPE_ALBUM, true)
                            || thing.objectType.equals(Thing.TYPE_FOLDER, true)) {
                        dataList.add(thing)
                        notifyItemInserted(dataList.size - 1)
                    } else if (thing.mimetype?.startsWith(fileType.name, true) == true) {
                        dataList.add(thing)
                        notifyItemInserted(dataList.size - 1)
                    }
                }
            }
        }
    }
}

abstract class BaseViewHolder<in M>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(data: M)

    abstract fun getBinding(): ViewDataBinding

    companion object {
        fun getPlaceHolderResource(thing: Thing): Int {
            var placeHolderResource = R.drawable.ucw_ic_file_grey_48dp
            if (thing.objectType.equals(Thing.TYPE_ALBUM, true)
                    || thing.objectType.equals(Thing.TYPE_FOLDER, true)) {
                placeHolderResource = R.drawable.ucw_ic_folder_grey_48dp
            } else if (thing.objectType.equals(Thing.TYPE_PHOTO, true)
                    && (thing.thumbnail != null && (thing.thumbnail.contains("http", true)
                            || thing.thumbnail.contains("https", true)))
                    || (thing.action?.action == Action.ACTION_SELECT_FILE
                            && thing.action.url?.matches(Regex.fromLiteral(".*((\\.jpg)|(\\.png)).*")) == true)) {
                placeHolderResource = R.drawable.ucw_ic_photo_grey_48dp
            }

            return placeHolderResource
        }

        fun getLinearPlaceHolderResource(thing: Thing): Int {
            var placeHolderResource = R.drawable.ucw_ic_file_grey_24dp
            if (thing.objectType.equals(Thing.TYPE_ALBUM, true)
                    || thing.objectType.equals(Thing.TYPE_FOLDER, true)) {
                placeHolderResource = R.drawable.ucw_ic_folder_grey_24dp
            } else if (thing.objectType.equals(Thing.TYPE_PHOTO, true)
                    && (thing.thumbnail != null && (thing.thumbnail.contains("http", true)
                            || thing.thumbnail.contains("https", true)))
                    || (thing.action?.action == Action.ACTION_SELECT_FILE
                            && thing.action.url?.matches(Regex.fromLiteral(".*((\\.jpg)|(\\.png)).*")) == true)) {
                placeHolderResource = R.drawable.ucw_ic_photo_grey_24dp
            }

            return placeHolderResource
        }
    }

}
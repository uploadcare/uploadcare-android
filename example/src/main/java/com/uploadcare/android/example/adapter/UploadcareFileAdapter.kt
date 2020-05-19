package com.uploadcare.android.example.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uploadcare.android.example.databinding.FileItemBinding
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.urls.Urls

class UploadcareFileAdapter(private val clickObserver: ((UploadcareFile) -> Unit)? = null)
    : RecyclerView.Adapter<UploadcareFileViewHolder>() {

    private val dataList = mutableListOf<UploadcareFile>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadcareFileViewHolder {
        return UploadcareFileViewHolder.build(parent, clickObserver)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: UploadcareFileViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
        holder.getBinding().executePendingBindings()
    }

    fun updateFiles(data: List<UploadcareFile>?) {
        dataList.clear()
        data?.let { dataList.addAll(it) }
        notifyDataSetChanged()
    }

    fun addFiles(data: List<UploadcareFile>?) {
        val startPos = dataList.size - 1
        data?.let {
            dataList.addAll(it)
            notifyItemRangeInserted(startPos, data.size)
        }
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }
}

class UploadcareFileViewHolder(private val binding: FileItemBinding,
                               private val clickObserver: ((UploadcareFile) -> Unit)? = null)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(uploadcareFile: UploadcareFile) {
        binding.handler = this
        binding.uploadcareFile = uploadcareFile

        val builder = uploadcareFile.cdnPath()
        builder.resizeWidth(250)
        builder.cropCenter(250, 250)
        val url = Urls.cdn(builder)
        Picasso.get().load(url.toString()).into(binding.image)
    }

    fun select(uploadcareFile: UploadcareFile) {
        clickObserver?.let { it(uploadcareFile) }
    }

    fun getBinding(): ViewDataBinding {
        return binding
    }

    companion object {
        fun build(parent: ViewGroup,
                  clickObserver: ((UploadcareFile) -> Unit)? = null)
                : UploadcareFileViewHolder {
            return UploadcareFileViewHolder(FileItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false), clickObserver)
        }
    }
}
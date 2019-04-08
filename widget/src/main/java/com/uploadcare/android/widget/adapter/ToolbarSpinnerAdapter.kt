package com.uploadcare.android.widget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.data.Chunk
import com.uploadcare.android.widget.databinding.UcwSpinnerRowBinding
import com.uploadcare.android.widget.databinding.UcwToolbarSpinnerItemBinding

class ToolbarSpinnerAdapter(context: Context)
    : ArrayAdapter<Chunk>(context, R.layout.ucw_spinner_row) {

    private val chunks = mutableListOf<Chunk>()
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    override fun clear() {
        super.clear()
        chunks.clear()
    }

    fun updateItems(data: List<Chunk>) {
        chunks.clear()
        chunks.addAll(data)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return chunks.size
    }

    override fun getItem(position: Int): Chunk? {
        return chunks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let {
            DataBindingUtil.getBinding<UcwSpinnerRowBinding>(convertView)
        } ?: UcwSpinnerRowBinding.inflate(inflater, parent, false)

        binding.text = getTitle(position)
        binding.executePendingBindings()

        return binding.root
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let {
            DataBindingUtil.getBinding<UcwToolbarSpinnerItemBinding>(convertView)
        } ?: UcwToolbarSpinnerItemBinding.inflate(inflater, parent, false)

        binding.text = getTitle(position)
        binding.executePendingBindings()

        return binding.root
    }

    private fun getTitle(position: Int): String {
        return chunks[position].title
    }
}
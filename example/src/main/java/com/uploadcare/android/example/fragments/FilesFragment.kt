package com.uploadcare.android.example.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.uploadcare.android.example.R
import com.uploadcare.android.example.adapter.UploadcareFileAdapter
import com.uploadcare.android.example.databinding.FragmentFilesBinding
import com.uploadcare.android.example.viewmodels.FilesViewModel
import com.uploadcare.android.library.urls.Order
import com.uploadcare.android.widget.utils.RecyclerViewOnScrollListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment with RecycleView dynamically populated with a list of Uploadcarefile files.
 */
class FilesFragment : Fragment(), OrderDialogListener, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentFilesBinding
    private lateinit var viewModel: FilesViewModel

    private var mAdapter: UploadcareFileAdapter? = null
    private var mOnScrollListener: RecyclerViewOnScrollListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFilesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        (activity as AppCompatActivity).let {
            it.setSupportActionBar(binding.toolbar)
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.uploadFragment))
            binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        }

        binding.recyclerView.apply {
            val manager = LinearLayoutManager(context)
            layoutManager = manager
            mAdapter = UploadcareFileAdapter { uploadcareFile ->
                findNavController().navigate(FilesFragmentDirections
                        .actionFilesFragmentToCdnFragment(uploadcareFile))
            }
            adapter = mAdapter
            mOnScrollListener = RecyclerViewOnScrollListener(manager) {
                viewModel.loadMore()
            }
        }

        viewModel.files.observe(this.viewLifecycleOwner, { files ->
            mAdapter?.updateFiles(files)
        })
        viewModel.allowLoadMore.observe(this.viewLifecycleOwner, { allowLoadMore ->
            if (allowLoadMore) {
                mOnScrollListener?.let {
                    it.clear()
                    binding.recyclerView.addOnScrollListener(it)
                }
            } else {
                binding.recyclerView.clearOnScrollListeners()
            }
        })
        viewModel.errorCommand.observe(this.viewLifecycleOwner, { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        })
        viewModel.launchFromPickerCommand.observe(this.viewLifecycleOwner, {
            showDateDialog()
        })
        viewModel.launchOrderPickerCommand.observe(this.viewLifecycleOwner, {
            showOrderDialog()
        })

        if (savedInstanceState == null) {
            viewModel.apply()
        }

        return binding.root
    }

    override fun onOrderSelected(order: Order) {
        viewModel.filterOrder.value = order
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val fromDate = dateFormat.parse("$year-${(month + 1)}-$dayOfMonth}")
            viewModel.filterFromDate.value = fromDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun showOrderDialog() {
        var dialog = childFragmentManager
                .findFragmentByTag(OrderDialogFragment::class.java.simpleName)
                as OrderDialogFragment?
        if (dialog != null || fragmentManager?.isStateSaved == true) {
            return
        }

        dialog = OrderDialogFragment.newInstance()
        dialog.show(childFragmentManager, OrderDialogFragment::class.java.simpleName)
    }

    private fun showDateDialog() {
        val preSelectedDate = GregorianCalendar()
        val selectedDay = preSelectedDate.get(GregorianCalendar.DAY_OF_MONTH)
        val selectedMonth = preSelectedDate.get(GregorianCalendar.MONTH)
        val selectedYear = preSelectedDate.get(GregorianCalendar.YEAR)

        context?.let {
            DatePickerDialog(it, this, selectedYear, selectedMonth, selectedDay).show()
        }
    }
}
package com.uploadcare.android.example.viewmodels

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFilesCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.urls.Order
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.utils.SingleLiveEvent
import java.net.URI
import java.util.*

class FilesViewModel(application: Application) : AndroidViewModel(application) {

    var stored = ObservableBoolean(false)
    val removed = ObservableBoolean(false)
    val loading = ObservableBoolean(false)
    val isEmpty = ObservableBoolean(true)
    val filterFromDate = ObservableField<Date>()
    val filterOrder = ObservableField<Order>()

    val files = MutableLiveData<List<UploadcareFile>>()
    val allowLoadMore = MutableLiveData<Boolean>(false)

    val launchFromPickerCommand = SingleLiveEvent<Void>()
    val launchOrderPickerCommand = SingleLiveEvent<Void>()
    val errorCommand = SingleLiveEvent<String>()

    private var mNext: URI? = null

    /**
     * Initialize {@link UploadcareClient}
     */
    private val client = UploadcareWidget.getInstance(application).uploadcareClient

    fun launchFromPicker() {
        launchFromPickerCommand.call()
    }

    fun launchOrderPicker() {
        launchOrderPickerCommand.call()
    }

    fun apply() {
        getFiles(null)
    }

    fun loadMore() {
        mNext?.let { getFiles(it) }
    }

    /**
     * Get Uploadcare files data with {@link UploadcareClient}.
     *
     * @param nextItems page offset. if {@code null} will fetch data without offset.
     */
    private fun getFiles(nextItems: URI?) {
        isEmpty.set(false)
        allowLoadMore.postValue(false)

        if (nextItems == null) {
            files.postValue(null)
            loading.set(true)
        }

        val filesQueryBuilder = client.getFiles()
        if (stored.get()) {
            filesQueryBuilder.stored(true)
        } else if (removed.get()) {
            filesQueryBuilder.removed(true)
        }

        filterFromDate.get()?.let { filesQueryBuilder.from(it) }
        filterOrder.get()?.let { filesQueryBuilder.ordering(it) }

        filesQueryBuilder.asListAsync(getContext(),
                ITEMS_PER_PAGE,
                nextItems,
                object : UploadcareFilesCallback {
                    override fun onFailure(e: UploadcareApiException) {
                        loading.set(false)
                        isEmpty.set(files.value?.isEmpty() == true)
                        errorCommand.postValue(e.message)
                    }

                    override fun onSuccess(result: List<UploadcareFile>, next: URI?) {
                        loading.set(false)
                        mNext = next
                        if (files.value != null) {
                            files.value = files.value?.plus(result)
                        } else {
                            files.value = result
                        }
                        mNext?.let { allowLoadMore.postValue(true) }
                        isEmpty.set(files.value?.isEmpty() == true)
                    }
                })
    }

    private fun getContext(): Context {
        return getApplication()
    }

    companion object {
        private const val ITEMS_PER_PAGE = 30
    }
}
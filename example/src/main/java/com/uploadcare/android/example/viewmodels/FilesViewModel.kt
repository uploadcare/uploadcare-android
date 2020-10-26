package com.uploadcare.android.example.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFilesCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.urls.Order
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.utils.SingleLiveEvent
import java.net.URI
import java.util.*

class FilesViewModel(application: Application) : AndroidViewModel(application) {

    val stored = MutableLiveData<Boolean>().apply { value = false }
    val removed = MutableLiveData<Boolean>().apply { value = false }
    val loading = MutableLiveData<Boolean>().apply { value = false }
    val isEmpty = MutableLiveData<Boolean>().apply { value = true }
    val filterFromDate = MutableLiveData<Date>()
    val filterOrder = MutableLiveData<Order>().apply { value = Order.UPLOAD_TIME_DESC }

    val files = MutableLiveData<List<UploadcareFile>>()
    val allowLoadMore = MutableLiveData<Boolean>().apply { value = false }

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
        allowLoadMore.postValue(false)

        if (nextItems == null) {
            files.postValue(null)
            isEmpty.value = true
            loading.value = true
        }

        val filesQueryBuilder = client.getFiles()
        if (stored.value == true) {
            filesQueryBuilder.stored(true)
        } else if (removed.value == true) {
            filesQueryBuilder.removed(true)
        }

        filterFromDate.value?.let { filesQueryBuilder.from(it) }
        filterOrder.value?.let { filesQueryBuilder.ordering(it) }

        filesQueryBuilder.asListAsync(getContext(),
                ITEMS_PER_PAGE,
                nextItems,
                object : UploadcareFilesCallback {
                    override fun onFailure(e: UploadcareApiException) {
                        loading.value = false
                        isEmpty.value = files.value?.isEmpty() == true
                        errorCommand.postValue(e.message)
                    }

                    override fun onSuccess(result: List<UploadcareFile>, next: URI?) {
                        loading.value = false
                        mNext = next
                        if (files.value != null) {
                            files.value = files.value?.plus(result)
                        } else {
                            files.value = result
                        }
                        mNext?.let { allowLoadMore.postValue(true) }
                        isEmpty.value = files.value?.isEmpty() == true
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
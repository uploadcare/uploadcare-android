package com.uploadcare.android.library.upload

import android.os.AsyncTask
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.data.UploadFromUrlData
import com.uploadcare.android.library.data.UploadFromUrlStatusData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.urls.Urls
import java.util.*

/**
 * Uploadcare uploader for multiple URLs.
 */
class MultipleUrlsUploader constructor(private val client: UploadcareClient,
                                       private val sourceUrls: List<String>) : MultipleUploader {

    private var store = "auto"

    /**
     * Synchronously uploads the files to Uploadcare.
     * <p>
     * The calling thread will be busy until the upload is finished.
     * Uploadcare is polled every 500 ms for upload progress.
     *
     * @return A list of Uploadcare files
     */
    override fun upload(): List<UploadcareFile> {
        return upload(500)
    }

    /**
     * Asynchronously uploads the list of files to Uploadcare.
     */
    override fun uploadAsync(callback: UploadFilesCallback) {
        MultipleUrlsUploadTask(this, callback).execute()
    }

    /**
     * Store the files upon uploading.
     *
     * @param store is set true - store the files upon uploading. Requires “automatic file storing”
     *              setting to be enabled.
     *              is set false - do not store files upon uploading.
     */
    override fun store(store: Boolean): MultipleUrlsUploader {
        this.store = if (store) 1.toString() else 0.toString()
        return this
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @param pollingInterval Progress polling interval in ms
     * @return An Uploadcare file
     */
    @Throws(UploadFailureException::class)
    fun upload(pollingInterval: Int): List<UploadcareFile> {
        val results = ArrayList<UploadcareFile>()
        for (sourceUrl in sourceUrls) {
            print("starting upload:$sourceUrl")
            val uploadUrl = Urls.uploadFromUrl(sourceUrl, client.publicKey, store)
            val token = client.requestHelper.executeQuery(RequestHelper.REQUEST_GET,
                    uploadUrl.toString(), false, UploadFromUrlData::class.java).token
            val statusUrl = Urls.uploadFromUrlStatus(token)
            while (true) {
                sleep(pollingInterval.toLong())
                val (status, fileId) = client.requestHelper.executeQuery(RequestHelper.REQUEST_GET,
                        statusUrl.toString(), false, UploadFromUrlStatusData::class.java)
                if (status == "success" && fileId != null) {
                    print(" upload status success:")
                    results.add(if (client.privateKey != null) {
                        client.getFile(fileId)
                    } else {
                        client.getUploadedFile(client.publicKey, fileId)
                    })
                    break
                } else if (status == "error" || status == "failed") {
                    throw UploadFailureException()
                }
            }
        }
        return results
    }
}

private class MultipleUrlsUploadTask(private val uploader: MultipleUrlsUploader,
                                     private val callback: UploadFilesCallback)
    : AsyncTask<Void, Void, List<UploadcareFile>?>() {
    override fun doInBackground(vararg params: Void?): List<UploadcareFile>? {
        return try {
            uploader.upload(500)
        } catch (e: UploadFailureException) {
            null
        }
    }

    override fun onPostExecute(result: List<UploadcareFile>?) {
        if (result != null) {
            callback.onSuccess(result)
        } else {
            callback.onFailure(UploadFailureException())
        }
    }
}
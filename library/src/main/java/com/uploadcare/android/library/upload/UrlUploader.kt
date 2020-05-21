package com.uploadcare.android.library.upload

import android.os.AsyncTask
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.data.UploadFromUrlData
import com.uploadcare.android.library.data.UploadFromUrlStatusData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.urls.Urls

/**
 * Uploadcare uploader for URLs.
 */
class UrlUploader(private val client: UploadcareClient, private val sourceUrl: String) : Uploader {

    private var store = "auto"

    override fun upload(): UploadcareFile {
        return upload(500)
    }

    override fun uploadAsync(callback: UploadcareFileCallback) {
        UrlUploadTask(this, callback).execute()
    }

    /**
     * Store the file upon uploading.
     *
     * @param store is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     * is set false - do not store file upon uploading.
     */
    override fun store(store: Boolean): UrlUploader {
        this.store = if (store) 1.toString() else 0.toString()
        return this
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @param pollingInterval Progress polling interval in ms
     * @return An Uploadcare file
     * @throws UploadFailureException
     */
    @Throws(UploadFailureException::class)
    fun upload(pollingInterval: Int): UploadcareFile {
        val uploadUrl = Urls.uploadFromUrl(sourceUrl, client.publicKey, store)
        val token = client.requestHelper.executeQuery(RequestHelper.REQUEST_GET,
                uploadUrl.toString(), false, UploadFromUrlData::class.java).token
        val statusUrl = Urls.uploadFromUrlStatus(token)
        while (true) {
            sleep(pollingInterval.toLong())
            val (status, fileId) = client.requestHelper.executeQuery(RequestHelper.REQUEST_GET,
                    statusUrl.toString(), false, UploadFromUrlStatusData::class.java)
            if (status == "success" && fileId != null) {
                return if (client.privateKey != null) {
                    client.getFile(fileId)
                } else {
                    client.getUploadedFile(client.publicKey, fileId)
                }
            } else if (status == "error" || status == "failed") {
                throw UploadFailureException(status)
            }
        }
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

}

private class UrlUploadTask(private val uploader: UrlUploader,
                            private val callback: UploadcareFileCallback)
    : AsyncTask<Void, Void, UploadcareFile?>() {

    override fun doInBackground(vararg params: Void?): UploadcareFile? {
        return try {
            uploader.upload(500)
        } catch (e: Exception) {
            null
        }
    }

    override fun onPostExecute(result: UploadcareFile?) {
        if (result != null) {
            callback.onSuccess(result)
        } else {
            callback.onFailure(UploadFailureException())
        }
    }

}
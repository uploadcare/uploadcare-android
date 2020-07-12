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
import kotlin.math.pow

/**
 * Uploadcare uploader for URLs.
 */
class UrlUploader(private val client: UploadcareClient, private val sourceUrl: String) : Uploader {

    private var store = "auto"

    override fun upload(): UploadcareFile {
        return upload(DEFAULT_POLLING_INTERVAL)
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
     * @param pollingInterval Progress polling interval in ms, default is 500ms.
     * @return An Uploadcare file
     * @throws UploadFailureException
     */
    @Throws(UploadFailureException::class)
    fun upload(pollingInterval: Long): UploadcareFile {
        val uploadUrl = Urls.uploadFromUrl(sourceUrl, client.publicKey, store)
        val token = client.requestHelper.executeQuery(RequestHelper.REQUEST_GET,
                uploadUrl.toString(), false, UploadFromUrlData::class.java).token
        val statusUrl = Urls.uploadFromUrlStatus(token)

        var waitTime = pollingInterval
        var retries: Int = 0
        var progress: Long = 0L

        while (true) {
            sleep(waitTime)
            val data: UploadFromUrlStatusData = client.requestHelper.executeQuery(
                    RequestHelper.REQUEST_GET, statusUrl.toString(), false,
                    UploadFromUrlStatusData::class.java)
            if (data.status == "success" && data.fileId != null) {
                // Success
                return if (client.secretKey != null) {
                    client.getFile(data.fileId)
                } else {
                    client.getUploadedFile(data.fileId)
                }
            } else if (data.status == "progress") {
                // Upload is in progress we make sure that progress changing and not stuck, otherwise start exponential
                // backoff and timeout.
                val currentProgress: Long = data.done * 100L / data.total
                if (retries >= MAX_UPLOAD_STATUS_ATTEMPTS) {
                    break
                } else if (currentProgress > progress) {
                    //reset backoff because progress is changing.
                    retries = 0
                    waitTime = pollingInterval
                    progress = currentProgress
                } else {
                    waitTime = calculateTimeToWait(retries)
                    retries++
                }
            } else if (data.status == "waiting" || data.status == "unknown") {
                // Upload is in unknown state, do exponential backoff and timeout.
                if (retries >= MAX_UPLOAD_STATUS_ATTEMPTS) {
                    break
                } else {
                    waitTime = calculateTimeToWait(retries)
                    retries++
                }
            } else if (data.status == "error" || data.status == "failed") {
                throw UploadFailureException(data.status)
            }
        }

        // If upload status not success or error, and we tried MAX_UPLOAD_STATUS_ATTEMPTS with exponential backoff.
        throw UploadFailureException("Timeout")
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    companion object{
        internal const val DEFAULT_POLLING_INTERVAL: Long = 500L

        internal const val MAX_UPLOAD_STATUS_ATTEMPTS: Int = 25

        /*
         * Returns the next wait interval, in milliseconds, using an exponential
         * backoff algorithm.
         */
        internal fun calculateTimeToWait(retryCount: Int): Long {
            return if (0 == retryCount) {
                DEFAULT_POLLING_INTERVAL
            } else 2.0.pow(retryCount).toLong() * DEFAULT_POLLING_INTERVAL
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
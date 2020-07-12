package com.uploadcare.android.library.upload

import android.os.AsyncTask
import android.text.TextUtils
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.data.UploadFromUrlData
import com.uploadcare.android.library.data.UploadFromUrlStatusData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.urls.Urls
import okhttp3.MultipartBody
import kotlin.math.pow

/**
 * Uploadcare uploader for URLs.
 */
class UrlUploader(private val client: UploadcareClient, private val sourceUrl: String) : Uploader {

    private var store = "auto"

    private var filename: String? = null

    private var checkURLDuplicates: String? = null

    private var saveURLDuplicates: String? = null

    private var signature: String? = null

    private var expire: String? = null

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
     * Set to run duplicates check upon file uploading.
     *
     * @param checkDuplicates Runs the duplicate check and provides the immediate-download behavior.
     */
    fun checkDuplicates(checkDuplicates: Boolean): UrlUploader {
        checkURLDuplicates = if (checkDuplicates) 1.toString() else 0.toString()
        return this
    }

    /**
     * Save duplicates upon file uploading.
     *
     * @param saveDuplicates Provides the save/update URL behavior. The parameter can be used if you believe a
     *                       source_url will be used more than once. If you don’t explicitly define "saveDuplicates",
     *                       it is by default set to the value of "checkDuplicates".
     */
    fun saveDuplicates(saveDuplicates: Boolean): UrlUploader {
        saveURLDuplicates = if (saveDuplicates) 1.toString() else 0.toString()
        return this
    }

    /**
     * Sets the name for a file uploaded from URL. If not defined, the filename is obtained from either response headers
     * or a source URL.
     *
     * @param filename name for a file uploaded from URL.
     */
    fun saveDuplicates(filename: String): UrlUploader {
        this.filename = filename
        return this
    }

    /**
     * Signed Upload - let you control who and when can upload files to a specified Uploadcare
     * project.
     *
     * @param signature is a string sent along with your upload request. It requires your Uploadcare
     * project secret key and hence should be crafted on your back end.
     * @param expire sets the time until your signature is valid. It is a Unix time.(ex 1454902434)
     */
    fun signedUpload(signature: String, expire: String): UrlUploader {
        this.signature = signature
        this.expire = expire
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
        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pub_key", client.publicKey)
                .addFormDataPart("source_url", sourceUrl)
                .addFormDataPart("store", store)

        filename?.let {
            multipartBuilder.addFormDataPart("filename", it)
        }

        checkURLDuplicates?.let {
            multipartBuilder.addFormDataPart("check_URL_duplicates", it)
        }

        saveURLDuplicates?.let {
            multipartBuilder.addFormDataPart("save_URL_duplicates", it)
        }

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            multipartBuilder.addFormDataPart("signature", signature!!)
            multipartBuilder.addFormDataPart("expire", expire!!)
        }

        val uploadUrl = Urls.uploadFromUrl()
        val multipartBody = multipartBuilder.build()
        val token = client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadUrl.toString(), false, UploadFromUrlData::class.java, multipartBody).token
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
                throw UploadFailureException(data.error)
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
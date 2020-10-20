package com.uploadcare.android.library.upload

import android.text.TextUtils
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ProgressCallback
import com.uploadcare.android.library.callbacks.UploadFileCallback
import com.uploadcare.android.library.data.UploadFromUrlData
import com.uploadcare.android.library.data.UploadFromUrlStatusData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.urls.Urls
import kotlinx.coroutines.*
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

    private var job: Job? = null

    private var isCanceled: Boolean = false

    /**
     * Synchronously uploads the file from provided URL to Uploadcare.
     *
     * @param progressCallback, progress will be reported on the same thread upload started.
     */
    override fun upload(progressCallback: ProgressCallback?): UploadcareFile {
        return upload(DEFAULT_POLLING_INTERVAL, progressCallback)
    }

    /**
     * Asynchronously uploads the file from provided URL to Uploadcare.
     *
     * @param callback callback {@link UploadFileCallback}, will be executed on Main (UI) thread.
     */
    override fun uploadAsync(callback: UploadFileCallback) {
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val uploadedFiles = upload(500, callback)
                callback.onSuccess(uploadedFiles)
            } catch (e: Exception) {
                callback.onFailure(UploadFailureException(e.message))
            }
        }
    }

    /**
     * Cancel upload of the file.
     */
    override fun cancel(){
        isCanceled = true
        job?.cancel("canceled", UploadFailureException("Canceled"))
        job = null
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
     * The calling thread will be busy until the upload is finished.
     *
     * @param pollingInterval Progress polling interval in ms, default is 500ms.
     * @param progressCallback, progress will be reported on the same thread upload started.
     *
     * @return An Uploadcare file
     * @throws UploadFailureException
     */
    @Throws(UploadFailureException::class)
    fun upload(pollingInterval: Long, progressCallback: ProgressCallback? = null): UploadcareFile {
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
        var progress : Double = 0.0

        while (true) {
            checkUploadCanceled()
            sleep(waitTime)
            val data: UploadFromUrlStatusData = client.requestHelper.executeQuery(
                    RequestHelper.REQUEST_GET, statusUrl.toString(), false,
                    UploadFromUrlStatusData::class.java)
            if (data.status == "success" && data.fileId != null) {
                progress = 1.0

                progressCallback?.onProgressUpdate(data.done, data.total, progress)

                checkUploadCanceled()
                // Success
                return if (client.secretKey != null) {
                    client.getFile(data.fileId)
                } else {
                    client.getUploadedFile(data.fileId)
                }
            } else if (data.status == "progress") {
                // Upload is in progress we make sure that progress changing and not stuck, otherwise start exponential
                // backoff and timeout.
                val currentProgress = 1.0 * data.done / data.total
                if (retries >= MAX_UPLOAD_STATUS_ATTEMPTS) {
                    break
                } else if (currentProgress > progress) {
                    //reset backoff because progress is changing.
                    retries = 0
                    waitTime = pollingInterval
                    progress = currentProgress

                    checkUploadCanceled()
                    progressCallback?.onProgressUpdate(data.done, data.total, progress)
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
                checkUploadCanceled()
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

    private fun checkUploadCanceled() {
        if (isCanceled) {
            throw UploadFailureException("Canceled")
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
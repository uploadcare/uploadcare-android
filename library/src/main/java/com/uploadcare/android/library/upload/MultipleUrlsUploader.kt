package com.uploadcare.android.library.upload

import android.text.TextUtils
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ProgressCallback
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.data.UploadFromUrlData
import com.uploadcare.android.library.data.UploadFromUrlStatusData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.urls.Urls
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import java.util.*

/**
 * Uploadcare uploader for multiple URLs.
 */
class MultipleUrlsUploader constructor(private val client: UploadcareClient,
                                       private val sourceUrls: List<String>) : MultipleUploader {

    private var store = "auto"

    private var filename: String? = null

    private var checkURLDuplicates: String? = null

    private var saveURLDuplicates: String? = null

    private var signature: String? = null

    private var expire: String? = null

    private var job: Job? = null

    private var isCanceled: Boolean = false

    /**
     * Synchronously uploads the files to Uploadcare.
     * <p>
     * The calling thread will be busy until the upload is finished.
     * Uploadcare is polled every 500 ms for upload progress.
     *
     * @return A list of Uploadcare files
     */
    override fun upload(progressCallback: ProgressCallback?): List<UploadcareFile> {
        return upload(UrlUploader.DEFAULT_POLLING_INTERVAL, progressCallback)
    }

    /**
     * Asynchronously uploads the list of files to Uploadcare.
     */
    override fun uploadAsync(callback: UploadFilesCallback) {
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val uploadedFiles = upload(500, callback)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(uploadedFiles)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(UploadFailureException(e.message))
                }
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

    /**
     * Set to run duplicates check upon file uploading.
     *
     * @param checkDuplicates Runs the duplicate check and provides the immediate-download behavior.
     */
    fun checkDuplicates(checkDuplicates: Boolean): MultipleUrlsUploader {
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
    fun saveDuplicates(saveDuplicates: Boolean): MultipleUrlsUploader {
        saveURLDuplicates = if (saveDuplicates) 1.toString() else 0.toString()
        return this
    }

    /**
     * Sets the name for a file uploaded from URL. If not defined, the filename is obtained from either response headers
     * or a source URL.
     *
     * @param filename name for a file uploaded from URL.
     */
    fun saveDuplicates(filename: String): MultipleUrlsUploader {
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
    fun signedUpload(signature: String, expire: String): MultipleUrlsUploader {
        this.signature = signature
        this.expire = expire
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
     * @param pollingInterval Progress polling interval in ms, default is 500ms.
     * @param progressCallback, progress will be reported on the same thread upload started.
     *
     * @return An Uploadcare file
     * @throws UploadFailureException
     */
    @Throws(UploadFailureException::class)
    fun upload(pollingInterval: Long, progressCallback: ProgressCallback? = null)
            : List<UploadcareFile> {
        val results = ArrayList<UploadcareFile>()
        for (sourceUrl in sourceUrls) {
            checkUploadCanceled()
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
            var progress: Double = 0.0

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

                    results.add(if (client.secretKey != null) {
                        client.getFile(data.fileId)
                    } else {
                        client.getUploadedFile(data.fileId)
                    })
                    break
                } else if (data.status == "progress") {
                    // Upload is in progress we make sure that progress changing and not stuck, otherwise start exponential
                    // backoff and timeout.
                    val currentProgress = 1.0 * data.done / data.total
                    if (retries >= UrlUploader.MAX_UPLOAD_STATUS_ATTEMPTS) {
                        break
                    } else if (currentProgress > progress) {
                        //reset backoff because progress is changing.
                        retries = 0
                        waitTime = pollingInterval
                        progress = currentProgress

                        checkUploadCanceled()
                        progressCallback?.onProgressUpdate(data.done, data.total, progress)
                    } else {
                        waitTime = UrlUploader.calculateTimeToWait(retries)
                        retries++
                    }
                }else if (data.status == "waiting" || data.status == "unknown") {
                    // Upload is in unknown state, do exponential backoff and timeout.
                    if (retries >= UrlUploader.MAX_UPLOAD_STATUS_ATTEMPTS) {
                        break
                    } else {
                        waitTime = UrlUploader.calculateTimeToWait(retries)
                        retries++
                    }
                } else if (data.status == "error" || data.status == "failed") {
                    checkUploadCanceled()
                    throw UploadFailureException(data.error)
                }
            }
        }
        return results
    }

    private fun checkUploadCanceled() {
        if (isCanceled) {
            throw UploadFailureException("Canceled")
        }
    }
}
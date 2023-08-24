package com.uploadcare.android.library.upload

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ProgressCallback
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.data.UploadBaseData
import com.uploadcare.android.library.data.UploadMultipartCompleteData
import com.uploadcare.android.library.data.UploadMultipartStartData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.exceptions.UploadPausedException
import com.uploadcare.android.library.upload.UploadUtils.Companion.chunkedSequence
import com.uploadcare.android.library.urls.Urls
import com.uploadcare.android.library.utils.CountingRequestBody
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Uploadcare uploader for multiple files.
 */
@Suppress("unused") @SuppressWarnings("WeakerAccess")
class MultipleFilesUploader : MultipleUploader {

    private val client: UploadcareClient

    private val files: List<File>?

    private val uris: List<Uri>?

    private val context: Context?

    private var store = "auto"

    private var signature: String? = null

    private var expire: String? = null

    private var totalFilesSize: Long = 0L

    private var totalBytesWritten: Long = 0L

    private var job: Job? = null

    private var size: Long = 0L

    private var contentMediaType: MediaType? = null

    private var multipartData: UploadMultipartStartData? = null

    //when uploadFileNumber lower than 0, pause is not possible
    private var uploadFileNumber: Int = -1

    private var uploadChunkNumber: Int = -1

    private var allBytesWritten: Long = 0L

    private var resultCallback: UploadFilesCallback? = null

    private val results = ArrayList<UploadcareFile>()

    private var isAsyncUpload: Boolean = false

    var isCanceled: Boolean = false
        private set

    var isPaused: Boolean = false
        private set

    /**
     * Creates a new uploader from a list of files on disk
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client Uploadcare client
     * @param files  Files on disk
     */
    constructor(client: UploadcareClient, files: List<File>) {
        this.client = client
        this.files = files
        this.uris = null
        this.context = null
    }

    /**
     * Creates a new uploader from a list of [android.net.Uri] objects references.
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client  Uploadcare client
     * @param uris    List of [android.net.Uri] objects references.
     * @param context Application context [android.content.Context]
     */
    constructor(client: UploadcareClient, uris: List<Uri>, context: Context) {
        this.client = client
        this.files = null
        this.uris = uris
        this.context = context
    }

    /**
     * Synchronously uploads the files to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @param progressCallback, progress will be reported on the same thread upload started.
     *
     * @return An list of Uploadcare files
     * @throws UploadFailureException
     */
    @Throws(UploadFailureException::class)
    override fun upload(progressCallback: ProgressCallback?): List<UploadcareFile> {
        totalFilesSize = calculateTotalSize()
        uploadFileNumber = 0
        uploadChunkNumber = 0
        return uploadFiles(progressCallback)
    }

    /**
     * Asynchronously uploads the files to Uploadcare.
     *
     * @param callback callback {@link UploadFilesCallback}
     */
    override fun uploadAsync(callback: UploadFilesCallback) {
        isAsyncUpload = true
        resultCallback = callback
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val uploadedFiles = upload(callback)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(uploadedFiles)
                }
            } catch (e: UploadPausedException) {
                // Ignore.
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(UploadFailureException(e.message))
                }
            } catch (e: RuntimeException) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(UploadFailureException(e.message))
                }
            }
        }
    }

    private fun uploadFiles(progressCallback: ProgressCallback?): List<UploadcareFile> {
        if (files != null) {
            while (uploadFileNumber < files.size) {
                if (isPaused) {
                    uploadChunkNumber = -1
                    throw UploadPausedException("Paused")
                }
                try {
                    val file = files[uploadFileNumber]
                    val name = file.name
                    size = file.length()
                    val contentType = UploadUtils.getMimeType(file)
                            ?: throw UploadFailureException("Cannot get mime type for file: $name")
                    contentMediaType = contentType

                    results.add(if (size > MIN_MULTIPART_SIZE) {
                        multipartUpload(
                                name,
                                contentType,
                                file.chunkedSequence(CHUNK_SIZE),
                                progressCallback
                        )
                    } else {
                        // We can use only direct upload
                        directUpload(name, file.asRequestBody(contentType), progressCallback)
                    })
                } catch (e: UploadFailureException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                uploadFileNumber += 1
            }
        } else if (context != null && uris != null) {
            val cr = context.contentResolver
            while (uploadFileNumber < uris.size) {
                if (isPaused) {
                    uploadChunkNumber = -1
                    throw UploadPausedException("Paused")
                }
                try {
                    val uri = uris[uploadFileNumber]
                    val name = UploadUtils.getFileName(uri, context)
                    size = UploadUtils.getFileSize(uri, context)
                            ?: throw UploadFailureException("Cannot get file size for uri: $uri")
                    val contentType = UploadUtils.getMimeType(context.contentResolver, uri)
                            ?: throw UploadFailureException("Cannot get mime type for uri: $uri")
                    contentMediaType = contentType

                    results.add(if (size > MIN_MULTIPART_SIZE) {
                        // We can use multipart upload
                        multipartUpload(
                            name,
                            contentType,
                            uri.chunkedSequence(context, CHUNK_SIZE),
                            progressCallback)
                    } else {
                        // We can use only direct upload
                        val requestBody = try {
                            val iStream = cr?.openInputStream(uri)
                            UploadUtils.getBytes(iStream)?.toRequestBody(contentType)
                                    ?: throw UploadFailureException("Cannot read file: $name")
                        } catch (e: IOException) {
                            throw UploadFailureException(e)
                        } catch (e: NullPointerException) {
                            throw UploadFailureException(e)
                        } catch (e: Exception) {
                            throw UploadFailureException(e)
                        }

                        directUpload(name, requestBody, progressCallback)
                    })
                } catch (e: UploadFailureException) {
                    e.printStackTrace()
                }
                uploadFileNumber += 1
            }
        }

        return results
    }

    /**
     * Cancel upload of the files.
     */
    override fun cancel(){
        isCanceled = true
        job?.cancel("Canceled", UploadFailureException("Canceled"))
        job = null
    }

    /**
     * Store the files upon uploading.
     *
     * @param store is set true - store the files upon uploading. Requires “automatic file storing”
     *              setting to be enabled.
     *              is set false - do not store files upon uploading.
     */
    override fun store(store: Boolean): MultipleFilesUploader {
        this.store = if (store) 1.toString() else 0.toString()
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
    fun signedUpload(signature: String, expire: String): MultipleFilesUploader {
        this.signature = signature
        this.expire = expire
        return this
    }

    /**
     * Pause upload of the file. Only Async upload
     * Only upload for file with size bigger than 10485760 bytes can be paused/resumed.
     *
     * @return {@code true} when successfully paused upload. {@code false} If pause not supported.
     */
    fun pause(): Boolean {
        if (isPaused) {
            return true
        }

        if (!isPauseResumeSupported()) {
            return false
        }

        if (uploadFileNumber < 0) {
            return false
        }

        isPaused = true
        return true
    }

    /**
     * Resume upload of the file that was previously paused.
     * Only upload for file with size bigger than 10485760 bytes can be paused/resumed.
     *
     * @return {@code true} when successfully resumed upload. {@code false} If resume not supported.
     */
    fun resume(): Boolean {
        if (!isPaused) {
            return true
        }

        if (!isPauseResumeSupported()) {
            return false
        }

        isPaused = false
        resultCallback?.let {
            resumeUpload(it)
            return true
        } ?: return false
    }

    private fun resumeUpload(callback: UploadFilesCallback) {
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val uploadedFiles = uploadFiles(callback)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(uploadedFiles)
                }
            } catch (e: UploadPausedException) {
                // Ignore.
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(UploadFailureException(e.message))
                }
            } catch (e: RuntimeException) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(UploadFailureException(e.message))
                }
            }
        }
    }

    private fun isPauseResumeSupported(): Boolean {
        return isAsyncUpload
    }

    private fun directUpload(
            name: String,
            requestBody: RequestBody,
            progressCallback: ProgressCallback?): UploadcareFile {
        val uploadUrl = Urls.uploadBase()

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)
                .addFormDataPart("UPLOADCARE_STORE", store)

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            multipartBuilder.addFormDataPart("signature", signature!!)
            multipartBuilder.addFormDataPart("expire", expire!!)
        }

        val countingRequestBody = CountingRequestBody(requestBody) { bytesWritten, contentLength ->
            checkUploadCanceled()
            updateProgress(bytesWritten, contentLength, progressCallback)
        }

        multipartBuilder.addFormDataPart("file", name, countingRequestBody)
        val body = multipartBuilder.build()

        val fileId = client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadUrl.toString(), false, UploadBaseData::class.java, body).file

        checkUploadCanceled()

        return if (client.secretKey != null) {
            client.getFile(fileId)
        } else {
            client.getUploadedFile(fileId)
        }
    }

    private fun multipartUpload(name: String,
                                contentType: MediaType,
                                chunkedSequence: Sequence<ByteArray>,
                                progressCallback: ProgressCallback?): UploadcareFile {
        // start multipart upload
        val multipartDataResult = startMultipartUpload(name, size, contentType)
        multipartData = multipartDataResult
        // upload parts
        uploadChunkNumber = 0
        allBytesWritten = 0L
        return uploadChunks(
                multipartDataResult,
                contentType,
                chunkedSequence,
                progressCallback)
    }

    private fun uploadChunks(multipartData: UploadMultipartStartData,
                             contentType: MediaType,
                             chunkedSequence: Sequence<ByteArray>,
                             progressCallback: ProgressCallback?): UploadcareFile {
        // upload parts starting from uploadChunkNumber
        val chunks = chunkedSequence.toList()
        while (uploadChunkNumber < chunks.count()) {
            checkUploadCanceled()
            if (isPaused) {
                throw UploadPausedException("Paused")
            }

            val requestBody = chunks[uploadChunkNumber].toRequestBody(contentType)
            val chunk = CountingRequestBody(requestBody) { bytesWritten, contentLength ->
                checkUploadCanceled()
                allBytesWritten += bytesWritten
                if (allBytesWritten > size) {
                    allBytesWritten = size
                }
                updateProgress(allBytesWritten, size, progressCallback)
            }

            val partUrl = multipartData.parts[uploadChunkNumber]
            client.requestHelper.executeCommand(RequestHelper.REQUEST_PUT, partUrl, false, chunk)
            uploadChunkNumber += 1
        }

        uploadChunkNumber = -1

        val multipartComplete = completeMultipartUpload(multipartData.uuid)

        checkUploadCanceled()
        //complete upload
        return if (client.secretKey != null) {
            client.getFile(multipartComplete.uuid)
        } else {
            client.getUploadedFile(multipartComplete.uuid)
        }
    }

    private fun startMultipartUpload(name: String,
                                     size: Long,
                                     contentType: MediaType): UploadMultipartStartData {
        checkUploadCanceled()

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)
                .addFormDataPart("UPLOADCARE_STORE", store)

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            multipartBuilder.addFormDataPart("signature", signature!!)
            multipartBuilder.addFormDataPart("expire", expire!!)
        }

        multipartBuilder.addFormDataPart("filename", name)
        multipartBuilder.addFormDataPart("size", size.toString())
        multipartBuilder.addFormDataPart("content_type", contentType.toString())

        val uploadUrl = Urls.uploadMultipartStart()
        val multipartBody = multipartBuilder.build()

        return client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadUrl.toString(), false, UploadMultipartStartData::class.java, multipartBody)
    }

    private fun completeMultipartUpload(uuid: String): UploadMultipartCompleteData {
        checkUploadCanceled()

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)

        multipartBuilder.addFormDataPart("uuid", uuid)

        val uploadCompleteUrl = Urls.uploadMultipartComplete()
        val multipartBody = multipartBuilder.build()

        return client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadCompleteUrl.toString(), false, UploadMultipartCompleteData::class.java, multipartBody)
    }

    private fun calculateTotalSize(): Long {
        var totalSize = 0L
        if (files != null) {
            for (file in files) {
                val size = file.length()
                totalSize += size
            }
        } else if (context != null && uris != null) {
            for (uri in uris) {
                val size = UploadUtils.getFileSize(uri, context)
                if (size != null) {
                    totalSize += size
                }
            }
        }

        return totalSize
    }

    private fun updateProgress(
            fileBytesWritten: Long,
            totalFileSize: Long,
            progressCallback: ProgressCallback?) {
        if (progressCallback == null) {
            return
        }

        val bytesWritten = totalBytesWritten + fileBytesWritten

        // Only add file bytes to total, after it uploaded.
        if (fileBytesWritten >= totalFileSize) {
            totalBytesWritten += fileBytesWritten
        }

        if (totalBytesWritten > totalFilesSize) {
            totalBytesWritten = totalFilesSize
        }
        val progress = 1.0 * bytesWritten / totalFilesSize

        progressCallback.onProgressUpdate(totalBytesWritten, totalFilesSize, progress)
    }

    private fun checkUploadCanceled() {
        if (isCanceled) {
            throw UploadFailureException("Canceled")
        }
    }

    companion object {
        private const val MIN_MULTIPART_SIZE: Long = 10485760

        private const val CHUNK_SIZE: Int = 5242880
    }
}

package com.uploadcare.android.library.upload

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.text.TextUtils
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.data.UploadBaseData
import com.uploadcare.android.library.data.UploadMultipartCompleteData
import com.uploadcare.android.library.data.UploadMultipartStartData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.upload.UploadUtils.Companion.chunkedSequence
import com.uploadcare.android.library.urls.Urls
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Uploadcare uploader for files and binary data.
 */
class FileUploader : Uploader {

    private val client: UploadcareClient

    private val file: File?

    private val stream: InputStream?

    private val bytes: ByteArray?

    private val filename: String?

    private val uri: Uri?

    private val content: String?

    private val context: Context?

    private var store = "auto"

    private var signature: String? = null

    private var expire: String? = null

    /**
     * Creates a new uploader from a file on disk
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client Uploadcare client
     * @param file   File on disk
     */
    constructor(client: UploadcareClient, file: File) {
        this.client = client
        this.file = file
        this.stream = null
        this.bytes = null
        this.filename = null
        this.uri = null
        this.context = null
        this.content = null
    }

    /**
     * Creates a new uploader from a [android.net.Uri] object reference.
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client  Uploadcare client
     * @param uri     Object reference [android.net.Uri].
     * @param context Application context [android.content.Context].
     */
    constructor(client: UploadcareClient, uri: Uri, context: Context) {
        this.client = client
        this.file = null
        this.stream = null
        this.bytes = null
        this.filename = null
        this.uri = uri
        this.context = context
        this.content = null
    }

    /**
     * Creates a new uploader from InputStream.
     *
     * InputStream data upload only supported for data less than 10485760 bytes,
     * multipart upload is not supported for InputStream, because we don't know size in advance,
     * consider using different method or Uploader class for such cases.
     *
     * @param client   Uploadcare client
     * @param stream   InputStream
     * @param filename Original filename
     */
    constructor(client: UploadcareClient, stream: InputStream, filename: String = DEFAULT_FILE_NAME) {
        this.client = client
        this.file = null
        this.stream = stream
        this.bytes = null
        this.filename = filename
        this.uri = null
        this.context = null
        this.content = null
    }

    /**
     * Creates a new uploader from binary data.
     *
     * @param client   Uploadcare client
     * @param bytes    File contents as binary data
     * @param filename Original filename
     */
    constructor(client: UploadcareClient, bytes: ByteArray, filename: String = DEFAULT_FILE_NAME) {
        this.client = client
        this.file = null
        this.stream = null
        this.bytes = bytes
        this.filename = filename
        this.uri = null
        this.context = null
        this.content = null
    }

    /**
     * Creates a new uploader from binary data.
     *
     * @param client  Uploadcare client
     * @param content Contents data as String object.
     * @param filename Original filename
     */
    constructor(client: UploadcareClient, content: String, filename: String = DEFAULT_FILE_NAME) {
        this.client = client
        this.file = null
        this.stream = null
        this.bytes = null
        this.filename = filename
        this.uri = null
        this.context = null
        this.content = content
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @return An Uploadcare file
     */
    override fun upload(): UploadcareFile {
        val name: String
        val size: Long
        val contentType: MediaType

        when {
            file != null -> {
                name = file.name
                size = file.length()
                contentType = UploadUtils.getMimeType(file)
                        ?: throw UploadFailureException("Cannot get mime type for file: $name")
            }

            uri != null && context != null -> {
                name = UploadUtils.getFileName(uri, context)
                size = UploadUtils.getFileSize(uri, context)
                        ?: throw UploadFailureException("Cannot get file size for uri: $uri")
                contentType = UploadUtils.getMimeType(context.contentResolver, uri)
                        ?: throw UploadFailureException("Cannot get mime type for uri: $uri")
            }

            content != null && filename != null -> {
                name = filename
                size = content.length.toLong()
                contentType = UploadUtils.getMimeType(filename)
                        ?: throw UploadFailureException("Cannot get mime type for file: $filename")
            }

            stream != null && filename != null -> {
                name = filename
                size = 0L // InputStream upload only supported for data less than MIN_MULTIPART_SIZE, multipart upload is not supported for InputStream.
                contentType = UploadUtils.getMimeType(filename)
                        ?: throw UploadFailureException("Cannot get mime type for file: $filename")
            }

            bytes != null && filename != null -> {
                name = filename
                size = bytes.size.toLong()
                contentType = UploadUtils.getMimeType(filename)
                        ?: throw UploadFailureException("Cannot get mime type for file: $filename")
            }

            else -> throw UploadFailureException(IllegalArgumentException())
        }

        return if (size > MIN_MULTIPART_SIZE) {
            // We can use multipart upload
            multipartUpload(name, size, contentType)
        } else {
            // We can use only direct upload
            directUpload(name, contentType)
        }
    }

    /**
     * Asynchronously uploads the file to Uploadcare.
     *
     * @param callback callback {@link UploadcareFileCallback}
     */
    override fun uploadAsync(callback: UploadcareFileCallback) {
        UploadTask(this, callback).execute()
    }

    /**
     * Store the file upon uploading.
     *
     * @param store is set true - store the file upon uploading. Requires “automatic file storing”
     *              setting to be enabled.
     *              is set false - do not store file upon uploading.
     */
    override fun store(store: Boolean): FileUploader {
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
    fun signedUpload(signature: String, expire: String): FileUploader {
        this.signature = signature
        this.expire = expire
        return this
    }

    private fun directUpload(name: String, contentType: MediaType): UploadcareFile {
        val uploadUrl = Urls.uploadBase()

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)
                .addFormDataPart("UPLOADCARE_STORE", store)

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            multipartBuilder.addFormDataPart("signature", signature!!)
            multipartBuilder.addFormDataPart("expire", expire!!)
        }
        val requestBody = getRequestBody(name, contentType)
                ?: throw UploadFailureException("Cannot read file: $name")

        multipartBuilder.addFormDataPart("file", name, requestBody)

        val body = multipartBuilder.build()

        val fileId = client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadUrl.toString(), false, UploadBaseData::class.java, body).file

        return if (client.secretKey != null) {
            client.getFile(fileId)
        } else {
            client.getUploadedFile(fileId)
        }
    }

    private fun getRequestBody(name: String, contentType: MediaType): RequestBody? {
        return when {
            file != null -> file.asRequestBody(contentType)

            uri != null && context != null -> {
                try {
                    val cr = context.contentResolver
                    val iStream = cr?.openInputStream(uri)
                    UploadUtils.getBytes(iStream)?.toRequestBody(contentType)
                } catch (e: IOException) {
                    throw UploadFailureException(e)
                } catch (e: NullPointerException) {
                    throw UploadFailureException(e)
                } catch (e: Exception) {
                    throw UploadFailureException(e)
                }
            }

            content != null && filename != null -> {
                content.toRequestBody(contentType)
            }

            stream != null && filename != null -> {
                UploadUtils.getBytes(stream)?.toRequestBody(contentType)
            }

            bytes != null && filename != null -> {
                bytes.toRequestBody(contentType)
            }

            else -> throw UploadFailureException(IllegalArgumentException())
        }
    }

    private fun multipartUpload(name: String,
                                size: Long,
                                contentType: MediaType): UploadcareFile {
        // start multipart upload
        val multipartData = startMultipartUpload(name, size, contentType)

        // upload parts
        var i = 0
        getChunkedSequence().forEach {
            val chunk = it.toRequestBody(contentType)
            val partUrl = multipartData.parts[i]
            client.requestHelper.executeCommand(RequestHelper.REQUEST_PUT, partUrl, false, chunk)
            i += 1
        }

        val multipartComplete = completeMultipartUpload(multipartData.uuid)

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
        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)

        multipartBuilder.addFormDataPart("uuid", uuid)

        val uploadCompleteUrl = Urls.uploadMultipartComplete()
        val multipartBody = multipartBuilder.build()

        return client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadCompleteUrl.toString(), false, UploadMultipartCompleteData::class.java, multipartBody)
    }

    private fun getChunkedSequence(): Sequence<ByteArray> {
        return when {
            file != null -> file.chunkedSequence(CHUNK_SIZE)

            uri != null && context != null -> uri.chunkedSequence(context, CHUNK_SIZE)

            content != null && filename != null -> content.chunkedSequence(CHUNK_SIZE)

            stream != null && filename != null -> stream.chunkedSequence(CHUNK_SIZE)

            bytes != null && filename != null -> bytes.chunkedSequence(CHUNK_SIZE)

            else -> throw UploadFailureException(IllegalArgumentException())
        }
    }

    companion object {
        private const val MIN_MULTIPART_SIZE: Long = 10485760

        private const val CHUNK_SIZE: Int = 5242880

        private const val DEFAULT_FILE_NAME = "default_filename"
    }
}

private class UploadTask(private val uploader: FileUploader,
                         private val callback: UploadcareFileCallback)
    : AsyncTask<Void, Void, UploadcareFile?>() {
    override fun doInBackground(vararg params: Void?): UploadcareFile? {
        return try {
            uploader.upload()
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
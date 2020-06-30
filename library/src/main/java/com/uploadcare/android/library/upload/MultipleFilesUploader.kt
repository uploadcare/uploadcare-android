package com.uploadcare.android.library.upload

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.text.TextUtils
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadFilesCallback
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
import java.util.*

/**
 * Uploadcare uploader for multiple files.
 */
class MultipleFilesUploader : MultipleUploader {

    private val client: UploadcareClient

    private val files: List<File>?

    private val uris: List<Uri>?

    private val context: Context?

    private var store = "auto"

    private var signature: String? = null

    private var expire: String? = null

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
     * @return An list of Uploadcare files
     */
    override fun upload(): List<UploadcareFile> {
        val results = ArrayList<UploadcareFile>()
        if (files != null) {
            for (file in files) {
                try {
                    val name = file.name
                    val size = file.length()
                    val contentType = UploadUtils.getMimeType(file)
                            ?: throw UploadFailureException("Cannot get mime type for file: $name")

                    results.add(if (size > MIN_MULTIPART_SIZE) {
                        // We can use multipart upload
                        multipartUpload(name, size, contentType, file.chunkedSequence(CHUNK_SIZE))
                    } else {
                        // We can use only direct upload
                        directUpload(name, file.asRequestBody(contentType))
                    })
                } catch (e: UploadFailureException) {
                    e.printStackTrace()
                }
            }
        } else if (context != null && uris != null) {
            val cr = context.contentResolver
            for (uri in uris) {
                try {
                    val name = UploadUtils.getFileName(uri, context)
                    val size = UploadUtils.getFileSize(uri, context)
                            ?: throw UploadFailureException("Cannot get file size for uri: $uri")
                    val contentType = UploadUtils.getMimeType(context.contentResolver, uri)
                            ?: throw UploadFailureException("Cannot get mime type for uri: $uri")

                    results.add(if (size > MIN_MULTIPART_SIZE) {
                        // We can use multipart upload
                        multipartUpload(name, size, contentType, uri.chunkedSequence(context, CHUNK_SIZE))
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

                        directUpload(name, requestBody)
                    })
                } catch (e: UploadFailureException) {
                    e.printStackTrace()
                }
            }
        }

        return results
    }

    /**
     * Asynchronously uploads the files to Uploadcare.
     *
     * @param callback callback {@link UploadFilesCallback}
     */
    override fun uploadAsync(callback: UploadFilesCallback) {
        MultipleUploadTask(this, callback).execute()
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

    private fun directUpload(name: String, requestBody: RequestBody): UploadcareFile {
        val uploadUrl = Urls.uploadBase()

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)
                .addFormDataPart("UPLOADCARE_STORE", store)

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            multipartBuilder.addFormDataPart("signature", signature!!)
            multipartBuilder.addFormDataPart("expire", expire!!)
        }

        multipartBuilder.addFormDataPart("file", name, requestBody)

        val body = multipartBuilder.build()

        val fileId = client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadUrl.toString(), false, UploadBaseData::class.java, body).file

        return if (client.secretKey != null) {
            client.getFile(fileId)
        } else {
            client.getUploadedFile(client.publicKey, fileId)
        }
    }

    private fun multipartUpload(name: String,
                                size: Long,
                                contentType: MediaType,
                                chunkedSequence: Sequence<ByteArray>): UploadcareFile {
        // start multipart upload
        val multipartData = startMultipartUpload(name, size, contentType)

        // upload parts
        var i = 0
        chunkedSequence.forEach {
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
            client.getUploadedFile(client.publicKey, multipartComplete.uuid)
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

    companion object {
        private const val MIN_MULTIPART_SIZE: Long = 10485760

        private const val CHUNK_SIZE: Int = 5242880
    }
}

private class MultipleUploadTask(private val uploader: MultipleUploader,
                                 private val callback: UploadFilesCallback)
    : AsyncTask<Void, Void, List<UploadcareFile>?>() {
    override fun doInBackground(vararg params: Void?): List<UploadcareFile>? {
        try {
            return uploader.upload()
        } catch (e: Exception) {
            return null
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
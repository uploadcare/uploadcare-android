package com.uploadcare.android.library.upload

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.data.UploadBaseData
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.urls.Urls
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
     * @param client   Uploadcare client
     * @param stream   InputStream
     * @param filename Original filename
     */
    constructor(client: UploadcareClient, stream: InputStream, filename: String) {
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
    constructor(client: UploadcareClient, bytes: ByteArray, filename: String) {
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
     */
    constructor(client: UploadcareClient, content: String) {
        this.client = client
        this.file = null
        this.stream = null
        this.bytes = null
        this.filename = null
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
        val uploadUrl = Urls.uploadBase()

        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)
                .addFormDataPart("UPLOADCARE_STORE", store)

        when {
            file != null -> multipartBuilder.addFormDataPart("file", file.name,
                    RequestBody.create(UploadUtils.getMimeType(file), file))
            uri != null && context != null -> {
                var iStream: InputStream? = null
                try {
                    val cr = context.contentResolver
                    iStream = cr?.openInputStream(uri)
                    val inputData = UploadUtils.getBytes(iStream)
                    multipartBuilder.addFormDataPart("file", UploadUtils.getFileName(uri, context),
                            RequestBody.create(UploadUtils.getMimeType(cr, uri), inputData))
                } catch (e: IOException) {
                    throw UploadFailureException(e)
                } catch (e: NullPointerException) {
                    throw UploadFailureException(e)
                } catch (e: Exception) {
                    throw UploadFailureException(e)
                }
            }
            content != null -> multipartBuilder.addFormDataPart("file", content)
            stream != null -> try {
                val inputData = UploadUtils.getBytes(stream)
                multipartBuilder.addFormDataPart("file", filename,
                        RequestBody.create(UploadUtils.getMimeType(filename), inputData))
            } catch (e: IOException) {
                throw UploadFailureException(e)
            } catch (e: Exception) {
                throw UploadFailureException(e)
            }
            bytes != null -> multipartBuilder.addFormDataPart("file", filename,
                    RequestBody.create(UploadUtils.getMimeType(filename), bytes))
            else -> throw UploadFailureException(IllegalArgumentException())
        }

        val requestBody = multipartBuilder.build()

        val fileId = client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                uploadUrl.toString(), false, UploadBaseData::class.java, requestBody).file
        println("uploaded file id:$fileId")
        return client.getFile(fileId)
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
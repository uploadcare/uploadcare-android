package com.uploadcare.android.library.upload

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.data.UploadBaseData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.urls.Urls
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream
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
        val uploadUrl = Urls.uploadBase()

        val results = ArrayList<UploadcareFile>()

        if (files != null) {
            for (file in files) {

                val multipartBuilder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)
                        .addFormDataPart("UPLOADCARE_STORE", store)

                multipartBuilder.addFormDataPart("file", file.name, RequestBody
                        .create(UploadUtils.getMimeType(file), file))

                val requestBody = multipartBuilder.build()

                val fileId = client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                        uploadUrl.toString(), false, UploadBaseData::class.java, requestBody).file
                results.add(if (client.privateKey != null) {
                    client.getFile(fileId)
                } else {
                    client.getUploadedFile(client.publicKey, fileId)
                })
            }
        } else if (context != null && uris != null) {
            val cr = context.contentResolver
            for (uri in uris) {

                val multipartBuilder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("UPLOADCARE_PUB_KEY", client.publicKey)
                        .addFormDataPart("UPLOADCARE_STORE", store)

                var iStream: InputStream? = null
                try {
                    iStream = cr.openInputStream(uri)
                    val inputData = UploadUtils.getBytes(iStream)
                    inputData?.let {
                        multipartBuilder.addFormDataPart("file",
                                UploadUtils.getFileName(uri, context),
                                it.toRequestBody(UploadUtils.getMimeType(cr, uri)))
                    }
                } catch (e: IOException) {
                    throw UploadFailureException(e)
                } catch (e: NullPointerException) {
                    throw UploadFailureException(e)
                }

                val requestBody = multipartBuilder.build()

                val fileId = client.requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                        uploadUrl.toString(), false, UploadBaseData::class.java, requestBody).file
                results.add(if (client.privateKey != null) {
                    client.getFile(fileId)
                } else {
                    client.getUploadedFile(client.publicKey, fileId)
                })
            }
        }

        return results
    }

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
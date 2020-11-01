package com.uploadcare.android.library.api

import android.content.Context
import android.os.AsyncTask
import com.uploadcare.android.library.callbacks.UploadcareAllFilesCallback
import com.uploadcare.android.library.callbacks.UploadcareFilesCallback
import com.uploadcare.android.library.data.FilePageData
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.urls.*
import java.net.URI
import java.util.*

/**
 * File resource request builder.
 * <p>
 * Allows to specify some file filters and get results.
 * <p>
 * {@link UploadcareFile}
 */
@Suppress("unused")
class FilesQueryBuilder(private val client: UploadcareClient)
    : PaginatedQueryBuilder<UploadcareFile> {

    private val parameters: MutableMap<String, UrlParameter> = mutableMapOf()

    /**
     * Adds a filter for removed files.
     *
     * @param removed If `true`, accepts removed files, otherwise declines them.
     */
    fun removed(removed: Boolean): FilesQueryBuilder {
        parameters["removed"] = FilesRemovedParameter(removed)
        return this
    }

    /**
     * Adds a filter for stored files.
     *
     * @param stored If `true`, accepts stored files, otherwise declines them.
     */
    fun stored(stored: Boolean): FilesQueryBuilder {
        parameters["stored"] = FilesStoredParameter(stored)
        return this
    }

    /**
     * Specifies the way files are sorted.
     *
     * @param order Order in which files are sorted in a returned list
     */
    fun ordering(order: Order): FilesQueryBuilder {
        parameters["ordering"] = FilesOrderParameter(order)
        parameters.remove("from")
        return this
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     * Order {@link Order#UPLOAD_TIME_ASC} will be used.
     *
     * @param fromDate A uploading datetime from which objects will be returned.
     */
    fun from(fromDate: Date): FilesQueryBuilder {
        parameters["ordering"] = FilesOrderParameter(Order.UPLOAD_TIME_ASC)
        parameters["from"] = FilesFromParameter(fromDate)
        return this
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     * Order {@link Order#SIZE_ASC} will be used.
     *
     * @param fromSize File size in bytes.
     */
    fun from(fromSize: Long): FilesQueryBuilder {
        parameters["ordering"] = FilesOrderParameter(Order.SIZE_ASC)
        parameters["from"] = FilesFromParameter(fromSize)
        return this
    }

    /**
     * Adds a filter for datetime to which objects will be returned.
     * Order {@link Order#UPLOAD_TIME_DESC} will be used.
     *
     * @param toDate A uploading datetime to which objects will be returned.
     */
    fun to(toDate: Date): FilesQueryBuilder {
        parameters["ordering"] = FilesOrderParameter(Order.UPLOAD_TIME_DESC)
        parameters["from"] = FilesFromParameter(toDate)
        return this
    }

    /**
     * Adds a filter for datetime to which objects will be returned.
     * Order {@link Order#SIZE_DESC} will be used.
     *
     * @param toSize File size in bytes.
     */
    fun to(toSize: Long): FilesQueryBuilder {
        parameters["ordering"] = FilesOrderParameter(Order.SIZE_DESC)
        parameters["from"] = FilesFromParameter(toSize)
        return this
    }

    /**
     * Add special fields to the file object in the result.
     *
     * @param fields Example: "rekognition_info"
     */
    fun addFields(fields: String): FilesQueryBuilder {
        parameters["add_fields"] = AddFieldsParameter(fields)
        return this
    }

    override fun asIterable(): Iterable<UploadcareFile> {
        val url = Urls.apiFiles()
        return client.requestHelper.executePaginatedQuery(url, parameters.values, true,
                FilePageData::class.java)
    }

    override fun asList(): List<UploadcareFile> {
        val files = ArrayList<UploadcareFile>()
        for (file in asIterable()) {
            files.add(file)
        }
        return files
    }

    /**
     * Returns a limited amount of resources with specified offset Asynchronously.
     *
     * @param context  application context. @link android.content.Context
     * @param limit    amount of resources returned in callback.
     * @param next     amount of resources to skip. if `null` then no offset will be applied.
     * @param callback [UploadcareFilesCallback].
     */
    fun asListAsync(context: Context, limit: Int, next: URI?,
                    callback: UploadcareFilesCallback?) {
        if (next == null) {
            parameters["limit"] = FilesLimitParameter(limit)
        }
        val url = next ?: Urls.apiFiles()

        client.requestHelper.executePaginatedQueryWithOffsetLimitAsync(context, url,
                parameters.values, true, callback)
    }

    /**
     * Iterates through all resources and returns a complete list Asynchronously.
     *
     * @param callback [UploadcareAllFilesCallback].
     */
    fun asListAsync(callback: UploadcareAllFilesCallback?) {
        PaginatedQueryTask(this, callback).execute()
    }

}

private class PaginatedQueryTask(private val queryBuilder: FilesQueryBuilder,
                                 private val callback: UploadcareAllFilesCallback?)
    : AsyncTask<Void, Void, List<UploadcareFile>?>() {

    override fun doInBackground(vararg params: Void?): List<UploadcareFile>? {
        return try {
            queryBuilder.asList()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: List<UploadcareFile>?) {
        result?.let { callback?.onSuccess(result) }
                ?: callback?.onFailure(UploadcareApiException("Unexpected error"))
    }
}

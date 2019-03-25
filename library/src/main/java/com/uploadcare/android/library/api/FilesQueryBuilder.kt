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
import java.util.Arrays.asList

/**
 * File resource request builder.
 * <p>
 * Allows to specify some file filters and get results.
 * <p>
 * {@link UploadcareFile}
 */
class FilesQueryBuilder(private val client: UploadcareClient)
    : PaginatedQueryBuilder<UploadcareFile> {

    private val parameters: MutableList<UrlParameter> = mutableListOf()

    /**
     * Adds a filter for removed files.
     *
     * @param removed If `true`, accepts removed files, otherwise declines them.
     */
    fun removed(removed: Boolean): FilesQueryBuilder {
        parameters.add(FilesRemovedParameter(removed))
        return this
    }

    /**
     * Adds a filter for stored files.
     *
     * @param stored If `true`, accepts stored files, otherwise declines them.
     */
    fun stored(stored: Boolean): FilesQueryBuilder {
        parameters.add(FilesStoredParameter(stored))
        return this
    }

    /**
     * Specifies the way files are sorted.
     *
     * @param order [Order]
     */
    fun ordering(order: Order): FilesQueryBuilder {
        parameters.add(FilesOrderParameter(order))
        return this
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     *
     * @param from A uploading datetime from which objects will be returned.
     */
    fun from(from: Date): FilesQueryBuilder {
        parameters.add(FilesFromParameter(from))
        return this
    }

    override fun asIterable(): Iterable<UploadcareFile> {
        val url = Urls.apiFiles()
        return client.requestHelper.executePaginatedQuery(url, parameters, true,
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
            parameters.add(FilesLimitParameter(limit))
        }
        val url = next ?: Urls.apiFiles()

        client.requestHelper.executePaginatedQueryWithOffsetLimitAsync(context, url, parameters,
                true, callback)
    }

    /**
     * Iterates through all resources and returns a complete list Asynchronously.
     *
     * @param callback [UploadcareAllFilesCallback].
     */
    fun asListAsync(callback: UploadcareAllFilesCallback?) {
        PaginatedQueryTask(callback).execute()
    }

}

private class PaginatedQueryTask(private val callback: UploadcareAllFilesCallback?)
    : AsyncTask<Void, Void, List<UploadcareFile>?>() {

    override fun doInBackground(vararg params: Void?): List<UploadcareFile>? {
        return try {
            asList()
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

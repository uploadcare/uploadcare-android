package com.uploadcare.android.library.api

import android.content.Context
import android.os.AsyncTask
import com.uploadcare.android.library.callbacks.UploadcareAllGroupsCallback
import com.uploadcare.android.library.callbacks.UploadcareGroupsCallback
import com.uploadcare.android.library.data.GroupPageData
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.urls.*
import java.net.URI
import java.util.*

@Suppress("unused")
class GroupsQueryBuilder(private val client: UploadcareClient)
    : PaginatedQueryBuilder<UploadcareGroup> {

    private val parameters: MutableList<UrlParameter> = mutableListOf()

    /**
     * Adds a filter for datetime from objects will be returned.
     *
     * @param from A uploading datetime from which objects will be returned.
     */
    fun from(from: Date): GroupsQueryBuilder {
        parameters.add(FilesFromParameter(from))
        return this
    }

    /**
     * Specifies the way files are sorted.
     *
     * @param order [Order]
     */
    fun ordering(order: Order): GroupsQueryBuilder {
        parameters.add(FilesOrderParameter(order))
        return this
    }

    override fun asIterable(): Iterable<UploadcareGroup> {
        val url = Urls.apiGroups()
        return client.requestHelper.executePaginatedQuery(url, parameters, true,
                GroupPageData::class.java)
    }

    override fun asList(): List<UploadcareGroup> {
        val groups = ArrayList<UploadcareGroup>()
        for (file in asIterable()) {
            groups.add(file)
        }
        return groups
    }

    /**
     * Returns a limited amount of group resources with specified offset Asynchronously.
     *
     * @param context  application context. @link android.content.Context
     * @param limit    amount of resources returned in callback.
     * @param next     amount of resources to skip. if `null` then no offset will be applied.
     * @param callback [UploadcareGroupsCallback].
     */
    fun asListAsync(context: Context, limit: Int, next: URI?,
                    callback: UploadcareGroupsCallback?) {
        if (next == null) {
            parameters.add(FilesLimitParameter(limit))
        }
        val url = next ?: Urls.apiGroups()
        val requestHelper = client.requestHelper

        requestHelper.executeGroupsPaginatedQueryWithOffsetLimitAsync(context, url, parameters,
                true, callback)
    }

    /**
     * Iterates through all resources and returns a complete list Asynchronously.
     *
     * @param callback [UploadcareAllGroupsCallback].
     */
    fun asListAsync(callback: UploadcareAllGroupsCallback?) {
        PaginatedGroupQueryTask(this, callback).execute()
    }

}

private class PaginatedGroupQueryTask(private val queryBuilder: GroupsQueryBuilder,
                                      private val callback: UploadcareAllGroupsCallback?)
    : AsyncTask<Void, Void, List<UploadcareGroup>?>() {

    override fun doInBackground(vararg params: Void?): List<UploadcareGroup>? {
        return try {
            queryBuilder.asList()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: List<UploadcareGroup>?) {
        result?.let { callback?.onSuccess(result) }
                ?: callback?.onFailure(UploadcareApiException("Unexpected error"))
    }
}
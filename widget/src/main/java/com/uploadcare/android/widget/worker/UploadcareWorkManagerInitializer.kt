package com.uploadcare.android.widget.worker

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.annotation.RestrictTo
import androidx.work.Configuration
import androidx.work.WorkManager

/**
 * The {@link ContentProvider} responsible for initializing {@link WorkManagerImpl}.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class UploadcareWorkManagerInitializer : ContentProvider() {

    override fun onCreate(): Boolean {
        // provide custom configuration
        val workManagerConfig = Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()

        // initialize WorkManager
        WorkManager.initialize(context!!, workManagerConfig)

        return true
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun query(p0: Uri, p1: Array<String>?, p2: String?, p3: Array<String>?, p4: String?): Cursor? {
        return null
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<String>?): Int {
        return 0
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<String>?): Int {
        return 0
    }

    override fun getType(p0: Uri): String? {
        return null
    }

}
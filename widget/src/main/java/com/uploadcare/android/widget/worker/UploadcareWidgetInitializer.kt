package com.uploadcare.android.widget.worker

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import androidx.annotation.RestrictTo
import com.uploadcare.android.widget.controller.UploadcareWidget

/**
 * The {@link ContentProvider} responsible for initializing {@link UploadcareWidget}
 * and {@link WorkManagerImpl}.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class UploadcareWidgetInitializer : ContentProvider() {

    override fun onCreate(): Boolean {
        //Initialize UploadcareWidget.
        UploadcareWidget.init(context!!)

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

    override fun attachInfo(context: Context?, info: ProviderInfo?) {
        if (info == null) {
            throw NullPointerException("UploadcareWorkManagerInitializer ProviderInfo cannot be null.")
        }

        // So if the authorities equal the library internal ones, the developer forgot to set his applicationId
        if ("com.uploadcare.android.widget.UploadcareWorkManagerInitializer" == info.authority) {
            throw IllegalStateException("Incorrect provider authority in manifest. Most likely due to a "
                    + "missing applicationId variable in application\'s build.gradle.")
        }

        super.attachInfo(context, info)
    }
}
package com.uploadcare.android.widget.controller

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.uploadcare.android.widget.activity.UploadcareActivity
import com.uploadcare.android.widget.utils.getSupportParcelable

// TODO: @CheK539 add javadoc or update documentation/WIDGET.md
object UploadcareContract :
    ActivityResultContract<UploadcareWidgetParams, UploadcareWidgetResult?>() {

    private const val KEY_RESULT = "result"

    override fun createIntent(context: Context, input: UploadcareWidgetParams): Intent =
        Intent(context, UploadcareActivity::class.java).apply {
            input.network?.let { network -> putExtra("network", network.rawValue) }
            putExtra("fileType", input.fileType.name)
            putExtra("store", input.storeUponUpload)
            putExtra("style", input.style)
            putExtra("signature", input.signature)
            putExtra("expire", input.expire)
            putExtra("cancelable", input.cancelable)
            putExtra("showProgress", input.showProgress)
            putExtra("backgroundUpload", input.backgroundUpload)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): UploadcareWidgetResult? =
        intent?.extras?.getSupportParcelable(KEY_RESULT, UploadcareWidgetResult::class.java)
}

package com.uploadcare.android.widget.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument

object OpenDocumentLocally : OpenDocument() {

    override fun createIntent(context: Context, input: Array<String>): Intent =
        super.createIntent(context, input).apply {
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
}

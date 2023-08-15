package com.uploadcare.android.example.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import com.uploadcare.android.example.R

object GetMultipleContentsLocally : GetMultipleContents() {

    override fun createIntent(context: Context, input: String): Intent =
        super.createIntent(context, input)
            .apply { putExtra(Intent.EXTRA_LOCAL_ONLY, true) }
            .let { intent ->
                Intent.createChooser(intent, context.getString(R.string.activity_main_choose_file))
            }
}

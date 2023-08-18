package com.uploadcare.android.widget.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.uploadcare.android.widget.viewmodels.MediaType

object CaptureMedia : ActivityResultContract<Pair<Uri, MediaType>, Boolean>() {

    override fun createIntent(context: Context, input: Pair<Uri, MediaType>): Intent {
        val (uri, mediaType) = input

        return when (mediaType) {
            MediaType.IMAGE -> Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            else -> Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            }
        }.apply { putExtra(MediaStore.EXTRA_OUTPUT, uri) }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
        resultCode != Activity.RESULT_OK
}

package com.uploadcare.android.widget.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.databinding.UcwActivityUploadcareBinding

class UploadcareActivity : AppCompatActivity() {

    private lateinit var binding: UcwActivityUploadcareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        applyStyle(intent.extras)
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        binding = DataBindingUtil.setContentView(this, R.layout.ucw_activity_uploadcare)
    }

    private fun applyStyle(bundle: Bundle?) {
        val style = bundle?.getInt("style") ?: -1
        if (style != -1) {
            theme.applyStyle(style, true)
        } else {
            theme.applyStyle(R.style.UploadcareStyle, true)
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}
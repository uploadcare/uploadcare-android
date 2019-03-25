package com.uploadcare.android.library.data

import com.uploadcare.android.library.api.UploadcareFile

data class CopyFileData(val detail: String? = null,
                        val type: String? = null,
                        val result: UploadcareFile? = null)
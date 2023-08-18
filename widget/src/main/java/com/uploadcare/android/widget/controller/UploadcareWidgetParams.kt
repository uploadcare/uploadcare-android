package com.uploadcare.android.widget.controller

data class UploadcareWidgetParams(
    var storeUponUpload: Boolean = true,
    var fileType: FileType = FileType.any,
    var signature: String? = null,
    var expire: String? = null,
    var network: SocialNetwork? = null,
    var style: Int = -1,
    var cancelable: Boolean = false,
    var showProgress: Boolean = false,
    var backgroundUpload: Boolean = false
)

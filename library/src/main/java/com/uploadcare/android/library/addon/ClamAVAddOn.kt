package com.uploadcare.android.library.addon

import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.urls.Urls

class ClamAVAddOn(client: UploadcareClient) : AddOnExecutor(
    client = client,
    executeUri = Urls.apiExecuteClamAV(),
    checkUri = Urls.apiClamAVStatus()
)

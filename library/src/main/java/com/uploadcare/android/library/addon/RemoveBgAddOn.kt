package com.uploadcare.android.library.addon

import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.urls.Urls

class RemoveBgAddOn(client: UploadcareClient) : AddOnExecutor(
    client = client,
    executeUri = Urls.apiExecuteRemoveBg(),
    checkUri = Urls.apiRemoveBgStatus()
)

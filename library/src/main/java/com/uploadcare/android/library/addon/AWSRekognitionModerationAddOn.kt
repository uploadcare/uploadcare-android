package com.uploadcare.android.library.addon

import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.urls.Urls

class AWSRekognitionModerationAddOn(client: UploadcareClient) : AddOnExecutor(
    client = client,
    executeUri = Urls.apiExecuteAWSRekognitionModeration(),
    checkUri = Urls.apiAWSRekognitionModerationStatus()
)

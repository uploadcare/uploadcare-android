package com.uploadcare.android.library.addon

import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.urls.Urls

class AWSRekognitionAddOn(client: UploadcareClient) : AddOnExecutor(
    client = client,
    executeUri = Urls.apiExecuteAWSRekognition(),
    checkUri = Urls.apiAWSRekognitionStatus()
)

package com.uploadcare.android.library.conversion

import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.urls.Urls
import java.net.URI

/**
 * Uploadcare converter for VideoConversionJob's.
 */
class VideoConverter(client: UploadcareClient, videoConversionJobs: List<VideoConversionJob>)
    : Converter(client, videoConversionJobs) {

    override fun getConversionUri(): URI {
        return Urls.apiConvertVideo()
    }

    override fun getConversionStatusUri(token: Int): URI {
        return Urls.apiConvertVideoStatus(token)
    }
}
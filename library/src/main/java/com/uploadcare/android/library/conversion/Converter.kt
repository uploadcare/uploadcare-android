package com.uploadcare.android.library.conversion

import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.RequestHelper.Companion.md5
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ConversionFilesCallback
import com.uploadcare.android.library.data.ConvertData
import com.uploadcare.android.library.data.ConvertResultData
import com.uploadcare.android.library.data.ConvertStatusData
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.UrlUploader
import kotlinx.coroutines.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import java.net.URI
import java.util.*

/**
 * Uploadcare converter for ConversionJob's.
 */
@Suppress("unused")
abstract class Converter(private val client: UploadcareClient,
                         private val conversionJobs: List<ConversionJob>) {

    private var store: String? = null

    private var job: Job? = null

    private var isCanceled: Boolean = false

    /**
     * Start conversion process synchronously.
     *
     * @return list with Uploadcare files results of conversion.
     */
    @Throws(UploadcareApiException::class)
    fun convert(): List<UploadcareFile> {
        return convert(UrlUploader.DEFAULT_POLLING_INTERVAL)
    }

    /**
     * Start conversion process asynchronously.
     *
     * @param ConversionFilesCallback that will be called when finished, or when error occurred.
     */
    fun convertAsync(callback: ConversionFilesCallback) {
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val uploadedFiles = convert(UrlUploader.DEFAULT_POLLING_INTERVAL)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(uploadedFiles)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(UploadcareApiException(e.message))
                }
            }
        }
    }

    fun cancel() {
        isCanceled = true
        job?.cancel("canceled", UploadcareApiException("Canceled"))
        job = null
    }

    /**
     * Store transformed file.
     *
     * @param store is set true - indicates your outputs should be permanently available.
     * is set false - the output will only be available for 24 hours.
     */
    fun store(store: Boolean): Converter {
        this.store = if (store) 1.toString() else 0.toString()
        return this
    }


    @Throws(UploadcareApiException::class)
    fun convert(pollingInterval: Long): List<UploadcareFile> {
        val results = ArrayList<UploadcareFile>()

        val convertData = ConvertData(getPaths(), store)
        val requestBodyContent = client.objectMapper.toJson(convertData, ConvertData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
        val url = getConversionUri()

        val jobTokens = client.requestHelper.executeQuery(
                RequestHelper.REQUEST_POST,
                url.toString(),
                true,
                ConvertResultData::class.java,
                body,
                requestBodyContent.md5())

        if (jobTokens.result.isEmpty()) {
            throw UploadcareApiException("Convert Error: " + jobTokens.problems.toString())
        }

        val statusUrls = mutableListOf<URI>()
        jobTokens.result.forEach { convertJobResult ->
            statusUrls.add(getConversionStatusUri(convertJobResult.token))
        }

        for (statusUrl in statusUrls) {
            var waitTime = pollingInterval
            var lastPendingStatus = 0L
            var retries = 0
            val startTime = System.currentTimeMillis()

            while (true) {
                checkConvertCanceled()
                sleep(waitTime)

                val convertStatusData: ConvertStatusData = client.requestHelper.executeQuery(
                        RequestHelper.REQUEST_GET,
                        statusUrl.toString(),
                        true,
                        ConvertStatusData::class.java)

                if (convertStatusData.status == "finished") {
                    checkConvertCanceled()

                    results.add(client.getFile(convertStatusData.result.uuid))
                    break
                } else if (convertStatusData.status == "processing"
                        || convertStatusData.status == "pending") {
                    when {
                        retries >= MAX_CONVERSION_STATUS_ATTEMPTS -> {
                            throw UploadcareApiException(
                                    "Conversion error: Unexpected processing error.")
                        }
                        lastPendingStatus < PROCESSING_TIMEOUT -> {
                            lastPendingStatus = System.currentTimeMillis() - startTime
                            checkConvertCanceled()
                        }
                        else -> {
                            waitTime = UrlUploader.calculateTimeToWait(retries)
                            retries++
                        }
                    }
                } else if (convertStatusData.status == "failed") {
                    checkConvertCanceled()
                    throw UploadcareApiException(convertStatusData.error)
                } else if (convertStatusData.status == "canceled") {
                    throw UploadcareApiException("Canceled")
                } else {
                    //unsupported status.
                    throw UploadcareApiException(
                            "Unsupported conversion status: ${convertStatusData.status}")
                }
            }
        }

        return results
    }

    protected abstract fun getConversionUri(): URI

    protected abstract fun getConversionStatusUri(token: Int): URI

    private fun checkConvertCanceled() {
        if (isCanceled) {
            throw UploadcareApiException("Canceled")
        }
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    private fun getPaths(): List<String> {
        val paths = mutableListOf<String>()
        conversionJobs.forEach { conversionJob ->
            val path = conversionJob.getPath()
            paths.add(path)
        }

        return paths
    }

    companion object {
        private const val PROCESSING_TIMEOUT = 300000L //5 minutes

        private const val MAX_CONVERSION_STATUS_ATTEMPTS: Int = 10
    }
}

@Suppress("unused")
enum class DocumentFormat constructor(val rawValue: String) {
    PDF("pdf"),
    DOC("doc"),
    DOCX("docx"),
    XLS("xls"),
    XLSX("xlsx"),
    ODT("odt"),
    ODS("ods"),
    RTF("rtf"),
    TXT("txt"),
    JPG("jpg"),
    ENHANCED_JPG("enhanced.jpg"),
    PNG("png")
}

@Suppress("unused")
enum class VideoFormat constructor(val rawValue: String) {
    MP4("mp4"),
    WEBM("webm"),
    OGG("ogg")
}

@Suppress("unused")
enum class VideoQuality constructor(val rawValue: String) {
    NORMAL("normal"),
    BETTER("better"),
    BEST("best"),
    LIGHTER("lighter"),
    LIGHTEST("lightest")
}

@Suppress("unused")
enum class VideoResizeMode constructor(val rawValue: String) {
    PRESERVE_RATIO("preserve_ratio"),
    CHANGE_RATIO("change_ratio"),
    SCALE_CROP("scale_crop"),
    LETTERBOX("add_padding")
}
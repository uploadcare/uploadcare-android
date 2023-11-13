package com.uploadcare.android.library.addon

import com.uploadcare.android.library.api.RequestHelper
import com.uploadcare.android.library.api.RequestHelper.Companion.md5
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.data.AddOnExecuteData
import com.uploadcare.android.library.data.AddOnExecuteResult
import com.uploadcare.android.library.data.AddOnStatus
import com.uploadcare.android.library.data.AddOnStatusResult
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.UrlUploader
import com.uploadcare.android.library.urls.RequestIdParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import java.net.URI

abstract class AddOnExecutor(
    private val client: UploadcareClient,
    private val executeUri: URI,
    private val checkUri: URI
) {

    private val scope = MainScope()

    fun execute(fileId: String): AddOnExecuteResult {
        val executeData = AddOnExecuteData(fileId)
        val requestBodyContent = client.objectMapper.toJson(
            executeData,
            AddOnExecuteData::class.java
        )
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)

        return client.requestHelper.executeQuery(
            RequestHelper.REQUEST_POST,
            executeUri.toString(),
            true,
            AddOnExecuteResult::class.java,
            body,
            requestBodyContent.md5()
        )
    }

    fun check(requestId: String): AddOnStatusResult {
        return client.requestHelper.executeQuery(
            RequestHelper.REQUEST_GET,
            checkUri.toString(),
            true,
            AddOnStatusResult::class.java,
            urlParameters = listOf(RequestIdParameter(requestId))
        )
    }

    fun executeAsync(fileId: String, callback: UploadcareFileCallback) {
        scope.launch(Dispatchers.IO) {
            try {
                val file = executeWithResult(fileId)
                withContext(Dispatchers.Main) {
                    callback.onSuccess(file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure(UploadcareApiException(e.message))
                }
            }
        }
    }

    private suspend fun executeWithResult(fileId: String): UploadcareFile {
        val requestId = execute(fileId).requestId
        var retryCount = 0
        var lastPendingStatus = 0L
        val startTime = System.currentTimeMillis()
        var waitTime = UrlUploader.DEFAULT_POLLING_INTERVAL

        while (retryCount < MAX_CHECK_STATUS_ATTEMPTS) {
            val statusResult = check(requestId)

            when (statusResult.status) {
                AddOnStatus.IN_PROGRESS-> {
                    if (lastPendingStatus < PROCESSING_TIMEOUT) {
                        lastPendingStatus = System.currentTimeMillis() - startTime
                    } else {
                        waitTime = UrlUploader.calculateTimeToWait(retryCount)
                        retryCount++
                    }
                }

                AddOnStatus.ERROR -> {
                    throw UploadcareApiException("Execution error: Unexpected processing error.")
                }

                AddOnStatus.UNKNOWN -> {
                    throw UploadcareApiException("Execution error: TTL expired.")
                }

                AddOnStatus.DONE -> {
                    return statusResult.result?.fileId?.let { resultFileId ->
                        client.getFile(resultFileId)
                    } ?: client.getFileWithAppData(fileId)
                }
            }

            delay(waitTime)
        }

        throw UploadcareApiException("Execution error: Unexpected processing error.")
    }

    fun cancel() = scope.coroutineContext.cancelChildren()

    companion object {
        private const val PROCESSING_TIMEOUT = 300000L
        private const val MAX_CHECK_STATUS_ATTEMPTS: Int = 10
    }
}

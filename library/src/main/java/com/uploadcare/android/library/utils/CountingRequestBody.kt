package com.uploadcare.android.library.utils

import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
 * decorate any request body, but is most useful for tracking the upload progress of large
 * multipart requests.
 */
internal class CountingRequestBody(private val requestBody: RequestBody,
                                   private val onProgressUpdate: CountingRequestListener)
    : RequestBody() {

    override fun contentType() = requestBody.contentType()

    @Throws(IOException::class)
    override fun contentLength() = requestBody.contentLength()

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink) { bytesWritten ->
            onProgressUpdate(bytesWritten, requestBody.contentLength())
        }
        val bufferedSink = countingSink.buffer()

        requestBody.writeTo(bufferedSink)

        bufferedSink.flush()
    }
}

internal typealias CountingRequestListener = (bytesWritten: Long, contentLength: Long) -> Unit

internal typealias CountingSinkListener = (bytesWritten: Long) -> Unit

internal class CountingSink(sink: Sink,
                            private val onSinkUpdate: CountingSinkListener)
    : ForwardingSink(sink) {
    private var bytesWritten = 0L

    override fun write(source: Buffer, byteCount: Long) {
        super.write(source, byteCount)

        bytesWritten += byteCount
        onSinkUpdate(bytesWritten)
    }
}
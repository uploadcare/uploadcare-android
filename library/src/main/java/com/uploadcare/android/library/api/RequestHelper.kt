package com.uploadcare.android.library.api

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.uploadcare.android.library.BuildConfig
import com.uploadcare.android.library.callbacks.BaseCallback
import com.uploadcare.android.library.callbacks.RequestCallback
import com.uploadcare.android.library.callbacks.UploadcareFilesCallback
import com.uploadcare.android.library.callbacks.UploadcareGroupsCallback
import com.uploadcare.android.library.data.FilePageData
import com.uploadcare.android.library.data.GroupPageData
import com.uploadcare.android.library.data.PageData
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.exceptions.UploadcareAuthenticationException
import com.uploadcare.android.library.exceptions.UploadcareInvalidRequestException
import com.uploadcare.android.library.urls.UrlParameter
import com.uploadcare.android.library.urls.UrlUtils.Companion.trustedBuild
import okhttp3.*
import java.io.IOException
import java.net.URI
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * A helper class for doing API calls to the Uploadcare API. Supports API version 0.5.
 * <p>
 * TODO Support of throttled requests needs to be added
 */
class RequestHelper(private val client: UploadcareClient) {

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun makeSignature(url: String, date: String, requestType: String): String {
        val sb = StringBuilder()
        sb.append(requestType)
                .append("\n").append(EMPTY_MD5)
                .append("\n").append(JSON_CONTENT_TYPE)
                .append("\n").append(date)
                .append("\n").append(url)

        val privateKeyBytes = client.privateKey.toByteArray()
        val signingKey = SecretKeySpec(privateKeyBytes, "HmacSHA1")
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(signingKey)
        val hmacBytes = mac.doFinal(sb.toString().toByteArray())
        return String(encodeHex(hmacBytes))
    }

    @Throws(UploadcareApiException::class)
    fun setApiHeaders(requestBuilder: Request.Builder,
                      url: String,
                      requestType: String,
                      callback: BaseCallback<out Any>? = null) {
        val calendar = GregorianCalendar(UTC)
        val formattedDate = rfc2822(calendar.time)

        requestBuilder.addHeader("Accept", "application/vnd.uploadcare-v0.5+json")
        requestBuilder.addHeader("Date", formattedDate)
        requestBuilder.addHeader("User-Agent",
                String.format("javauploadcare-android/%s/%s", BuildConfig.VERSION_NAME,
                        client.publicKey))
        var authorization: String? = null
        if (client.simpleAuth) {
            authorization = "Uploadcare.Simple " + client.publicKey + ":" + client.privateKey
        } else {
            try {
                val signature = makeSignature(url, formattedDate, requestType)
                authorization = "Uploadcare " + client.publicKey + ":" + signature
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
                if (callback == null) {
                    throw UploadcareApiException("Error when signing the request")
                } else {
                    callback.onFailure(UploadcareApiException("Error when signing the request", e))
                }
            }

        }

        authorization?.let { requestBuilder.addHeader("Authorization", it) }
    }

    fun <T : Any> executeQuery(requestType: String,
                               url: String,
                               apiHeaders: Boolean,
                               dataClass: Class<T>,
                               requestBody: RequestBody? = null): T {
        val requestBuilder = Request.Builder().url(url)
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBuilder.delete()
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType)
        }
        try {
            val response = client.httpClient.newCall(requestBuilder.build()).execute()

            checkResponseStatus(response)

            response.body()?.string()?.let {
                val result = client.objectMapper.fromJson(it, dataClass)
                result?.let { return result } ?: throw UploadcareApiException("Can't parse result")
            } ?: throw UploadcareApiException("No response")
        } catch (e: RuntimeException) {
            throw UploadcareApiException(e)
        }
    }

    fun <T : Any> executeQueryAsync(context: Context,
                                    requestType: String,
                                    url: String,
                                    apiHeaders: Boolean,
                                    dataClass: Class<T>,
                                    callback: BaseCallback<T>? = null,
                                    requestBody: RequestBody? = null) {
        val requestBuilder = Request.Builder().url(url)
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBuilder.delete()
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, callback)
        }
        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post { callback?.onFailure(UploadcareApiException(e)) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    mainHandler.post {
                        callback?.onFailure(UploadcareApiException("Unexpected code $response"))
                    }
                }

                try {
                    checkResponseStatus(response)

                    response.body()?.string()?.let {
                        val result = client.objectMapper.fromJson(it, dataClass)
                        mainHandler.post {
                            result?.let {
                                callback?.onSuccess(it)
                            } ?: callback?.onFailure(UploadcareApiException("Can't parse result"))
                        }
                    }
                            ?: mainHandler.post { callback?.onFailure(UploadcareApiException("No response")) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mainHandler.post { callback?.onFailure(UploadcareApiException(e)) }
                }

            }
        })
    }

    fun <T : Any> executePaginatedQuery(url: URI,
                                        urlParameters: List<UrlParameter>,
                                        apiHeaders: Boolean,
                                        dataClass: Class<out PageData<T>>)
            : Iterable<T> {
        return object : Iterable<T> {
            override fun iterator(): Iterator<T> {
                return object : Iterator<T> {
                    private var next: URI? = null

                    private var more: Boolean = false

                    private var pageIterator: Iterator<T>? = null

                    init {
                        getNext()
                    }

                    private fun getNext() {
                        val pageUrl: URI? =
                                if (next == null) {
                                    val builder = Uri.parse(url.toString()).buildUpon()

                                    setQueryParameters(builder, urlParameters)
                                    trustedBuild(builder)
                                } else {
                                    next
                                }
                        val pageData = executeQuery(REQUEST_GET,
                                pageUrl!!.toString(),
                                apiHeaders,
                                dataClass,
                                null)
                        more = pageData.hasMore() == true
                        next = pageData.getNextURI()
                        pageIterator = pageData.getResultsData().iterator()
                    }

                    override fun hasNext(): Boolean {
                        return when {
                            pageIterator?.hasNext() == true -> true
                            more -> {
                                getNext()
                                true
                            }
                            else -> false
                        }
                    }

                    override fun next(): T {
                        return pageIterator!!.next()
                    }
                }
            }
        }
    }

    fun executePaginatedQueryWithOffsetLimitAsync(context: Context,
                                                  url: URI,
                                                  urlParameters: List<UrlParameter>,
                                                  apiHeaders: Boolean,
                                                  callback: UploadcareFilesCallback? = null) {

        val builder = Uri.parse(url.toString()).buildUpon()

        setQueryParameters(builder, urlParameters)
        val pageUrl = trustedBuild(builder)

        val requestBuilder = Request.Builder().url(pageUrl.toString())
        requestBuilder.get()

        if (apiHeaders) {
            try {
                setApiHeaders(requestBuilder, pageUrl.toString(), REQUEST_GET, null)
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.onFailure(UploadcareApiException(e))
                return
            }

        }
        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post { callback?.onFailure(UploadcareApiException(e)) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    mainHandler.post {
                        callback?.onFailure(UploadcareApiException("Unexpected code $response"))
                    }
                }
                try {
                    checkResponseStatus(response)

                    response.body()?.string()?.let {
                        val result = client.objectMapper.fromJson(it, FilePageData::class.java)
                        mainHandler.post {
                            result?.let { pageData ->
                                callback?.onSuccess(pageData.getResultsData(), pageData.getNextURI())
                            } ?: callback?.onFailure(UploadcareApiException("Can't parse result"))
                        }
                    }
                            ?: mainHandler.post { callback?.onFailure(UploadcareApiException("No response")) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mainHandler.post { callback?.onFailure(UploadcareApiException(e)) }
                }

            }
        })

    }

    fun executeGroupsPaginatedQueryWithOffsetLimitAsync(context: Context,
                                                        url: URI,
                                                        urlParameters: List<UrlParameter>,
                                                        apiHeaders: Boolean,
                                                        callback: UploadcareGroupsCallback?) {

        val builder = Uri.parse(url.toString()).buildUpon()

        setQueryParameters(builder, urlParameters)
        val pageUrl = trustedBuild(builder)

        val requestBuilder = Request.Builder().url(pageUrl.toString())

        requestBuilder.get()
        if (apiHeaders) {
            try {
                setApiHeaders(requestBuilder, pageUrl.toString(), REQUEST_GET, null)
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.onFailure(UploadcareApiException(e))
                return
            }

        }
        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post { callback?.onFailure(UploadcareApiException(e)) }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    mainHandler.post {
                        callback?.onFailure(UploadcareApiException("Unexpected code $response"))
                    }
                }
                try {
                    checkResponseStatus(response)

                    response.body()?.string()?.let {
                        val result = client.objectMapper.fromJson(it, GroupPageData::class.java)
                        mainHandler.post {
                            result?.let { pageData ->
                                callback?.onSuccess(pageData.getResultsData(), pageData.getNextURI())
                            } ?: callback?.onFailure(UploadcareApiException("Can't parse result"))
                        }
                    }
                            ?: mainHandler.post { callback?.onFailure(UploadcareApiException("No response")) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mainHandler.post { callback?.onFailure(UploadcareApiException(e)) }
                }

            }
        })

    }

    /**
     * Executes the request et the Uploadcare API and return the HTTP Response object.
     *
     *
     * The existence of this method(and it's return type) enables the end user to extend the
     * functionality of the
     * Uploadcare API client by creating a subclass of [com.uploadcare.android.library.api.UploadcareClient].
     *
     * @param requestType request type (ex. "GET", "POST", "DELETE");
     * @param url         request url
     * @param apiHeaders  TRUE if the default API headers should be set
     * @param requestBody body of POST request, used only with request type REQUEST_POST.
     * @return HTTP Response object
     */
    fun executeCommand(requestType: String,
                       url: String,
                       apiHeaders: Boolean,
                       requestBody: RequestBody? = null): Response {
        val requestBuilder = Request.Builder().url(url)
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBuilder.delete()
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType)
        }

        try {
            val response = client.httpClient.newCall(requestBuilder.build()).execute()
            checkResponseStatus(response)
            return response
        } catch (e: IOException) {
            throw UploadcareApiException(e)
        }
    }

    /**
     * Executes the request et the Uploadcare API and return the HTTP Response object.
     *
     *
     * The existence of this method(and it's return type) enables the end user to extend the
     * functionality of the
     * Uploadcare API client by creating a subclass of [com.uploadcare.android.library.api.UploadcareClient].
     *
     * @param context     application context. @link android.content.Context
     * @param requestType request type (ex. "GET", "POST", "DELETE");
     * @param url         request url
     * @param apiHeaders  TRUE if the default API headers should be set
     * @param callback    callback  [RequestCallback]
     * @param requestBody body of POST request, used only with request type REQUEST_POST.
     */
    fun executeCommandAsync(context: Context,
                            requestType: String,
                            url: String,
                            apiHeaders: Boolean,
                            callback: RequestCallback? = null,
                            requestBody: RequestBody? = null) {
        val requestBuilder = Request.Builder().url(url)
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBuilder.delete()
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, callback)
        }

        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                if (callback != null) {
                    mainHandler.post { callback.onFailure(UploadcareApiException(e)) }
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    if (callback != null) {
                        mainHandler.post {
                            callback.onFailure(
                                    UploadcareApiException(
                                            "Unexpected code " + response.body()!!.toString()))
                        }
                    }
                }
                callback?.let {
                    checkResponseStatus(response, it)
                    mainHandler.post { it.onSuccess(response) }
                }
            }
        })
    }

    /**
     * Verifies that the response status codes are within acceptable boundaries and throws
     * corresponding exceptions
     * otherwise.
     *
     * @param response The response object to be checked
     */
    @Throws(IOException::class, UploadcareAuthenticationException::class,
            UploadcareInvalidRequestException::class, UploadcareApiException::class)
    private fun checkResponseStatus(response: Response) {
        val statusCode = response.code()

        if (statusCode in 200..299) {
            return
        } else if (statusCode == 401 || statusCode == 403) {
            throw UploadcareAuthenticationException(response.body()?.string())
        } else if (statusCode == 400 || statusCode == 404) {
            throw UploadcareInvalidRequestException(response.body()?.string())
        } else {
            throw UploadcareApiException(
                    "Unknown exception during an API call, response: ${response.body()?.string()}")
        }
    }

    /**
     * Verifies that the response status codes are within acceptable boundaries and calls
     * corresponding callback method exceptions otherwise.
     * otherwise.
     *
     * @param response The response object to be checked
     * @param callback callback  [BaseCallback]
     */
    private fun checkResponseStatus(response: Response, callback: BaseCallback<Response>?) {

        val statusCode = response.code()
        val requestBody: String? = try {
            response.body()?.string()
        } catch (e: IOException) {
            callback?.onFailure(UploadcareApiException(e))
            return
        }

        if (statusCode in 200..299) {
            return
        } else if (statusCode == 401 || statusCode == 403) {
            callback?.onFailure(UploadcareAuthenticationException(requestBody))
        } else if (statusCode == 400 || statusCode == 404) {
            callback?.onFailure(UploadcareInvalidRequestException(requestBody))
        } else {
            callback?.onFailure(UploadcareApiException(
                    "Unknown exception during an API call, response: $requestBody"))
        }
    }

    companion object {

        const val REQUEST_GET = "GET"

        const val REQUEST_POST = "POST"

        const val REQUEST_DELETE = "DELETE"

        const val REQUEST_PUT = "PUT"

        const val DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z"

        const val DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

        val UTC = TimeZone.getTimeZone("UTC")

        private const val EMPTY_MD5 = "d41d8cd98f00b204e9800998ecf8427e"

        private const val JSON_CONTENT_TYPE = "application/json"

        @JvmStatic
        fun rfc2822(date: Date) = SimpleDateFormat(DATE_FORMAT, Locale.US).apply {
            timeZone = UTC
        }.format(date)

        @JvmStatic
        fun iso8601(date: Date) = SimpleDateFormat(DATE_FORMAT_ISO_8601,
                Locale.US).apply {
            timeZone = UTC
        }.format(date)

        @JvmStatic
        fun setQueryParameters(builder: Uri.Builder, parameters: List<UrlParameter>) {
            for (parameter in parameters) {
                builder.appendQueryParameter(parameter.getParam(), parameter.getValue())
            }
        }

        /**
         * * Converts an array of bytes into an array of characters representing the hexadecimal values
         * of each byte in order.
         * The returned array will be double the length of the passed array, as it takes two characters
         * to represent any
         * given byte.
         *
         * @param data a byte[] to convert to Hex characters
         * @return array of characters the hexadecimal values of each byte in order
         */
        @JvmStatic
        fun encodeHex(data: ByteArray): CharArray {
            val l = data.size
            val out = CharArray(l shl 1)
            // two characters form the hex value.
            var i = 0
            var j = 0
            while (i < l) {
                out[j++] = DIGITS_UPPER[(0xF0 and data[i].toInt()).ushr(4)]
                out[j++] = DIGITS_UPPER[0x0F and data[i].toInt()]
                i++
            }
            return out
        }

        /**
         * Used to build output as Hex
         */
        @JvmStatic
        val DIGITS_UPPER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
                'C', 'D', 'E', 'F')
    }

}
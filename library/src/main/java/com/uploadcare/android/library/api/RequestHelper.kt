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
import com.uploadcare.android.library.urls.Urls
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.net.URI
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * A helper class for doing API calls to the Uploadcare API. Supports API version 0.6.
 * <p>
 * TODO Support of throttled requests needs to be added
 */
class RequestHelper(private val client: UploadcareClient) {

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class, UploadcareApiException::class)
    fun makeSignature(url: String,
                      date: String,
                      requestType: String,
                      requestBodyMD5: String? = null,
                      contentType: String? = null): String {
        client.secretKey?: throw UploadcareApiException("Secret key is required for this request.")

        val uriStartIndex = url.indexOf(Urls.API_BASE) + Urls.API_BASE.length
        val uri = url.substring(uriStartIndex, url.length)
        val sb = StringBuilder()
        sb.append(requestType)
                .append("\n").append(requestBodyMD5?.let { it } ?: "".md5())
                .append("\n").append(contentType?.let { it } ?: JSON_CONTENT_TYPE)
                .append("\n").append(date)
                .append("\n").append(uri)
        val secretKeyBytes = client.secretKey.toByteArray()
        val signingKey = SecretKeySpec(secretKeyBytes, MAC_ALGORITHM)
        val mac = Mac.getInstance(MAC_ALGORITHM)
        mac.init(signingKey)
        val hmacBytes = mac.doFinal(sb.toString().toByteArray())
        return hmacBytes.toHexString()
    }

    @Throws(UploadcareApiException::class)
    fun setApiHeaders(requestBuilder: Request.Builder,
                      url: String,
                      requestType: String,
                      callback: BaseCallback<out Any>? = null,
                      requestBodyMD5: String? = null,
                      contentType: String? = null) {
        val calendar = GregorianCalendar(GMT)
        val formattedDate = rfc2822(calendar.time)

        requestBuilder.addHeader("Content-Type", contentType?.let { it } ?: JSON_CONTENT_TYPE)
        requestBuilder.addHeader("Accept", "application/vnd.uploadcare-v0.6+json")
        requestBuilder.addHeader("Date", formattedDate)
        requestBuilder.addHeader("User-Agent",
                String.format("javauploadcare-android/%s/%s", BuildConfig.VERSION_NAME,
                        client.publicKey))
        var authorization: String? = null
        if (client.simpleAuth) {
            authorization = "Uploadcare.Simple " + client.publicKey + ":" + client.secretKey
        } else {
            try {
                val signature = makeSignature(url, formattedDate, requestType, requestBodyMD5,
                        contentType)
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

    @Throws(UploadcareApiException::class)
    fun <T : Any> executeQuery(requestType: String,
                               url: String,
                               apiHeaders: Boolean,
                               dataClass: Class<T>,
                               requestBody: RequestBody? = null,
                               requestBodyMD5: String? = null,
                               urlParameters: Collection<UrlParameter>? = null): T {
        val builder = Uri.parse(url).buildUpon()
        urlParameters?.let {
            setQueryParameters(builder, it)
        }
        val pageUrl = trustedBuild(builder)

        val requestBuilder = Request.Builder().url(pageUrl.toString())
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBody?.let { requestBuilder.delete(it) }
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, requestBodyMD5 = requestBodyMD5,
                    contentType = requestBody?.contentType().toString())
        }
        try {
            val response = client.httpClient.newCall(requestBuilder.build()).execute()
            checkResponseStatus(response)

            response.body?.string()?.let {
                val result = client.objectMapper.fromJson(it, dataClass)
                result?.let { return result } ?: throw UploadcareApiException("Can't parse result")
            } ?: throw UploadcareApiException("No response")
        } catch (e: RuntimeException) {
            throw UploadcareApiException(e.message)
        } catch (e: IOException) {
            throw UploadcareApiException(e.message)
        }
    }

    @Throws(UploadcareApiException::class)
    fun <T : Any> executeQuery(requestType: String,
                               url: String,
                               apiHeaders: Boolean,
                               dataType: ParameterizedType,
                               requestBody: RequestBody? = null,
                               requestBodyMD5: String? = null,
                               urlParameters: Collection<UrlParameter>? = null): T {
        val builder = Uri.parse(url).buildUpon()
        urlParameters?.let {
            setQueryParameters(builder, it)
        }
        val pageUrl = trustedBuild(builder)

        val requestBuilder = Request.Builder().url(pageUrl.toString())
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBody?.let { requestBuilder.delete(it) }
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, requestBodyMD5 = requestBodyMD5,
                    contentType = requestBody?.contentType().toString())
        }
        try {
            val response = client.httpClient.newCall(requestBuilder.build()).execute()
            checkResponseStatus(response)

            response.body?.string()?.let {
                val result = client.objectMapper.fromJson<T>(it, dataType)
                result?.let { return result } ?: throw UploadcareApiException("Can't parse result")
            } ?: throw UploadcareApiException("No response")
        } catch (e: RuntimeException) {
            throw UploadcareApiException(e.message)
        } catch (e: IOException) {
            throw UploadcareApiException(e.message)
        }
    }

    fun <T : Any> executeQueryAsync(context: Context,
                                    requestType: String,
                                    url: String,
                                    apiHeaders: Boolean,
                                    dataClass: Class<T>,
                                    callback: BaseCallback<T>? = null,
                                    requestBody: RequestBody? = null,
                                    requestBodyMD5: String? = null,
                                    urlParameters: Collection<UrlParameter>? = null) {
        val builder = Uri.parse(url).buildUpon()
        urlParameters?.let {
            setQueryParameters(builder, it)
        }
        val pageUrl = trustedBuild(builder)

        val requestBuilder = Request.Builder().url(pageUrl.toString())
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBody?.let { requestBuilder.delete(it) }
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, callback, requestBodyMD5,
                    requestBody?.contentType().toString())
        }
        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
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

                    response.body?.string()?.let {
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
                    mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
                }
            }
        })
    }

    fun <T : Any> executeQueryAsync(context: Context,
                                    requestType: String,
                                    url: String,
                                    apiHeaders: Boolean,
                                    dataType: ParameterizedType,
                                    callback: BaseCallback<T>? = null,
                                    requestBody: RequestBody? = null,
                                    requestBodyMD5: String? = null,
                                    urlParameters: Collection<UrlParameter>? = null) {
        val builder = Uri.parse(url).buildUpon()
        urlParameters?.let {
            setQueryParameters(builder, it)
        }
        val pageUrl = trustedBuild(builder)

        val requestBuilder = Request.Builder().url(pageUrl.toString())
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBody?.let { requestBuilder.delete(it) }
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, callback, requestBodyMD5,
                    requestBody?.contentType().toString())
        }
        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
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

                    response.body?.string()?.let {
                        val result = client.objectMapper.fromJson<T>(it, dataType)
                        mainHandler.post {
                            result?.let {
                                callback?.onSuccess(it)
                            } ?: callback?.onFailure(UploadcareApiException("Can't parse result"))
                        }
                    }
                            ?: mainHandler.post { callback?.onFailure(UploadcareApiException("No response")) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
                }
            }
        })
    }

    @Throws(UploadcareApiException::class)
    fun <T : Any> executePaginatedQuery(url: URI,
                                        urlParameters: Collection<UrlParameter>,
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
                                                  urlParameters: Collection<UrlParameter>,
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
                callback?.onFailure(UploadcareApiException(e.message))
                return
            }

        }
        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
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

                    response.body?.string()?.let {
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
                    mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
                }

            }
        })
    }

    fun executeGroupsPaginatedQueryWithOffsetLimitAsync(context: Context,
                                                        url: URI,
                                                        urlParameters: Collection<UrlParameter>,
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
                callback?.onFailure(UploadcareApiException(e.message))
                return
            }

        }
        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
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

                    response.body?.string()?.let {
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
                    mainHandler.post { callback?.onFailure(UploadcareApiException(e.message)) }
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
    @Throws(UploadcareApiException::class)
    fun executeCommand(requestType: String,
                       url: String,
                       apiHeaders: Boolean,
                       requestBody: RequestBody? = null,
                       requestBodyMD5: String? = null): Response {
        val requestBuilder = Request.Builder().url(url)
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBody?.let { requestBuilder.delete(it) }
                    ?: requestBuilder.delete()
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, requestBodyMD5 = requestBodyMD5,
                    contentType = requestBody?.contentType().toString())
        }

        try {
            val response = client.httpClient.newCall(requestBuilder.build()).execute()
            checkResponseStatus(response)
            return response
        } catch (e: IOException) {
            throw UploadcareApiException(e.message)
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
                            requestBody: RequestBody? = null,
                            requestBodyMD5: String? = null) {
        val requestBuilder = Request.Builder().url(url)
        when (requestType) {
            REQUEST_GET -> requestBuilder.get()
            REQUEST_POST -> requestBody?.let { requestBuilder.post(it) }
            REQUEST_DELETE -> requestBody?.let { requestBuilder.delete(it) }
                    ?: requestBuilder.delete()
            REQUEST_PUT -> requestBody?.let { requestBuilder.put(it) }
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, callback, requestBodyMD5,
                    requestBody?.contentType().toString())
        }

        client.httpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            val mainHandler = Handler(context.mainLooper)

            override fun onFailure(call: Call, e: IOException) {
                if (callback != null) {
                    mainHandler.post { callback.onFailure(UploadcareApiException(e.message)) }
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    if (callback != null) {
                        mainHandler.post {
                            callback.onFailure(UploadcareApiException("Unexpected code $response"))
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
    internal fun checkResponseStatus(response: Response) {
        val statusCode = response.code

        if (statusCode in 200..299) {
            return
        } else if (statusCode == 401 || statusCode == 403) {
            throw UploadcareAuthenticationException("$response")
        } else if (statusCode == 400 || statusCode == 404) {
            throw UploadcareInvalidRequestException("$response")
        } else {
            throw UploadcareApiException(
                    "Unknown exception during an API call, response: $response")
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

        val statusCode = response.code
        val requestBody: String? = try {
            response.body?.string()
        } catch (e: IOException) {
            callback?.onFailure(UploadcareApiException(e.message))
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

        private const val DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z"

        private const val DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss"

        private val UTC = TimeZone.getTimeZone("UTC")

        private val GMT = TimeZone.getTimeZone("GMT")

        private const val MAC_ALGORITHM = "HmacSHA1"

        private const val JSON_CONTENT_TYPE = "application/json"

        internal val JSON = JSON_CONTENT_TYPE.toMediaTypeOrNull()

        private fun rfc2822(date: Date) = SimpleDateFormat(DATE_FORMAT, Locale.US).apply {
            timeZone = GMT
        }.format(date)

        internal fun iso8601(date: Date) = SimpleDateFormat(DATE_FORMAT_ISO_8601,
                Locale.US).apply {
            timeZone = UTC
        }.format(date)

        private fun setQueryParameters(builder: Uri.Builder, parameters: Collection<UrlParameter>) {
            for (parameter in parameters) {
                builder.appendQueryParameter(parameter.getParam(), parameter.getValue())
            }
        }

        fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

        fun FormBody.md5(): String {
            val sb = StringBuilder()
            for (i in 0 until this.size) {
                if (i > 0) sb.append("&")
                sb.append(this.encodedName(i))
                sb.append("=")
                sb.append(this.encodedValue(i))
            }
            return sb.toString().md5()
        }

        fun String.md5(): String {
            val md = MessageDigest.getInstance("MD5")
            val digested = md.digest(toByteArray())
            return digested.joinToString("") {
                String.format("%02x", it)
            }
        }
    }

}
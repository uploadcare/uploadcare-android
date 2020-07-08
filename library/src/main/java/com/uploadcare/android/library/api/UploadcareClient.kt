package com.uploadcare.android.library.api

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import com.squareup.moshi.Types
import com.uploadcare.android.library.BuildConfig
import com.uploadcare.android.library.api.RequestHelper.Companion.md5
import com.uploadcare.android.library.callbacks.*
import com.uploadcare.android.library.data.CopyOptionsData
import com.uploadcare.android.library.data.ObjectMapper
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.urls.*
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString.Companion.encodeUtf8
import java.util.concurrent.TimeUnit

/**
 * Initializes a client with custom access keys.
 * Can use simple or secure authentication.
 *
 * @param publicKey  Public key
 * @param secretKey Private key, required for any request to Uploadcare REST API.
 * @param simpleAuth If {@code false}, HMAC-based authentication is used, otherwise simple
 * authentication is used.
 */
class UploadcareClient constructor(val publicKey: String,
                                   val secretKey: String? = null,
                                   val simpleAuth: Boolean = false) {

    constructor(publicKey: String) : this(publicKey, null, false)

    constructor(publicKey: String, secretKey: String) : this(publicKey, secretKey, false)

    val httpClient: OkHttpClient
    val requestHelper: RequestHelper
    val objectMapper = ObjectMapper.build()

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(PublicKeyInterceptor(publicKey))
                .callTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

        requestHelper = DefaultRequestHelperProvider().get(this)
    }

    /**
     * Requests project info from the API.
     *
     * @return Project resource
     */
    fun getProject(): Project {
        val url = Urls.apiProject()
        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(), true,
                Project::class.java)
    }

    /**
     * Requests project info from the API Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param callback callback  [ProjectCallback] with either
     * an Project response or a failure exception.
     */
    fun getProjectAsync(context: Context, callback: ProjectCallback? = null) {
        val url = Urls.apiProject()
        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                Project::class.java, callback)
    }

    /**
     * Request file data for uploaded file. Does not require "secretKey" set for UploadcareClient.
     */
    fun getUploadedFile(publicKey: String, fileId: String): UploadcareFile {
        val url = Urls.apiUploadedFile(fileId)

        val parameters: MutableList<UrlParameter> = mutableListOf()
        parameters.add(PublicKeyParameter(publicKey))
        parameters.add(FileIdParameter(fileId))

        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                false, UploadcareFile::class.java, urlParameters = parameters)
    }

    /**
     * Requests file data.
     *
     * @param fileId Resource UUID
     * @return UploadcareFile resource
     */
    fun getFile(fileId: String): UploadcareFile {
        val url = Urls.apiFile(fileId)
        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                true, UploadcareFile::class.java)
    }

    /**
     * Requests file data Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId   Resource UUID
     * @param callback callback  [UploadcareFileCallback] with either
     * an UploadcareFile response or a failure exception.
     */
    fun getFileAsync(context: Context, fileId: String, callback: UploadcareFileCallback? = null) {
        val url = Urls.apiFile(fileId)
        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                UploadcareFile::class.java, callback)
    }

    /**
     * Requests file data, with Rekognition Info if available.
     *
     * @param fileId Resource UUID
     * @return UploadcareFile resource
     */
    fun getFileWithRekognitionInfo(fileId: String): UploadcareFile {
        val url = Urls.apiFile(fileId)
        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                true, UploadcareFile::class.java,
                urlParameters = listOf(AddFieldsParameter("rekognition_info")))
    }

    /**
     * Requests file data, with Rekognition Info if available, Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId   Resource UUID
     * @param callback callback  [UploadcareFileCallback] with either
     * an UploadcareFile response or a failure exception.
     */
    fun getFileWithRekognitionInfoAsync(context: Context, fileId: String, callback: UploadcareFileCallback? = null) {
        val url = Urls.apiFile(fileId)
        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                UploadcareFile::class.java, callback,
                urlParameters = listOf(AddFieldsParameter("rekognition_info")))
    }

    /**
     * Requests group data.
     *
     * @param groupId Group ID
     * @return UploadcareGroup resource
     */
    fun getGroup(groupId: String): UploadcareGroup {
        val url = Urls.apiGroup(groupId)
        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                true, UploadcareGroup::class.java)
    }

    /**
     * Requests file data Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param groupId  Group ID
     * @param callback callback  [UploadcareGroupCallback] with either
     * an UploadcareFile response or a failure exception.
     */
    fun getGroupAsync(context: Context, groupId: String, callback: UploadcareGroupCallback? = null) {
        val url = Urls.apiGroup(groupId)
        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                UploadcareGroup::class.java, callback)
    }

    /**
     * Mark all files in a group as stored (available on CDN).
     *
     * @param groupId Group ID
     */
    fun storeGroup(groupId: String) {
        val requestBody = groupId.toRequestBody("".toMediaTypeOrNull())
        val url = Urls.apiGroupStorage(groupId)
        requestHelper.executeCommand(RequestHelper.REQUEST_PUT, url.toString(), true, requestBody)
    }

    /**
     * Mark all files in a group as stored (available on CDN). Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param groupId  Group ID
     * @param callback callback  [RequestCallback] with either
     * an HTTP response or a failure exception.
     */
    fun storeGroupAsync(context: Context, groupId: String, callback: RequestCallback? = null) {
        val requestBody = groupId.toRequestBody("".toMediaTypeOrNull())
        val url = Urls.apiGroupStorage(groupId)
        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_PUT, url.toString(),
                true, callback, requestBody)
    }

    /**
     * Begins to build a request for uploaded files for the current account.
     *
     * @return UploadcareFile resource request builder
     */
    fun getFiles(): FilesQueryBuilder {
        return FilesQueryBuilder(this)
    }

    /**
     * Begins to build a request for groups for the current account.
     *
     * @return Group resource request builder
     */
    fun getGroups(): GroupsQueryBuilder {
        return GroupsQueryBuilder(this)
    }

    /**
     * Marks a file as deleted.
     *
     * @param fileId Resource UUID
     */
    fun deleteFile(fileId: String) {
        val url = Urls.apiFile(fileId)
        requestHelper.executeCommand(RequestHelper.REQUEST_DELETE, url.toString(), true)
    }

    /**
     * Marks a file as deleted Asynchronously.
     *
     * @param context Application context. [android.content.Context]
     * @param fileId  Resource UUID
     */
    fun deleteFileAsync(context: Context, fileId: String) {
        val url = Urls.apiFile(fileId)
        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_DELETE, url.toString(),
                true)
    }

    /**
     * Marks a file as deleted Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId   Resource UUID
     * @param callback callback  [RequestCallback] with either
     * an HTTP response or a failure exception.
     */
    fun deleteFileAsync(context: Context, fileId: String, callback: RequestCallback? = null) {
        val url = Urls.apiFile(fileId)
        requestHelper
                .executeCommandAsync(context, RequestHelper.REQUEST_DELETE, url.toString(), true,
                        callback)
    }

    /**
     * Marks a files as deleted.
     *
     * @param fileIds  Resource UUIDs
     */
    fun deleteFiles(fileIds: List<String>) {
        val url = Urls.apiFilesBatch()
        if (fileIds.size <= MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            val requestBodyContent = objectMapper.toJson(fileIds,
                    Types.newParameterizedType(List::class.java, String::class.java))
            val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
            requestHelper.executeCommand(RequestHelper.REQUEST_DELETE, url.toString(), true, body,
                    requestBodyContent.md5())
        } else {
            // Make batch requests.
            executeSaveDeleteBatchCommand(RequestHelper.REQUEST_DELETE, fileIds)
        }
    }

    /**
     * Marks multiple files as deleted Asynchronously.
     *
     * @param context Application context. [android.content.Context]
     * @param fileIds  Resource UUIDs
     */
    fun deleteFilesAsync(context: Context, fileIds: List<String>) {
        val url = Urls.apiFilesBatch()
        if (fileIds.size < MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            val requestBodyContent = objectMapper.toJson(fileIds,
                    Types.newParameterizedType(List::class.java, String::class.java))
            val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
            requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_DELETE,
                    url.toString(), true, null, body, requestBodyContent.md5())
        } else {
            // Make batch requests.
            SaveDeleteBatchTask(this, RequestHelper.REQUEST_DELETE, fileIds).execute()
        }
    }

    /**
     * Marks multiple files as deleted Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileIds  Resource UUIDs
     * @param callback callback  [RequestCallback] with either
     * an HTTP response or a failure exception.
     */
    fun deleteFilesAsync(context: Context,
                         fileIds: List<String>,
                         callback: RequestCallback? = null) {
        val url = Urls.apiFilesBatch()
        if (fileIds.size < MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            val requestBodyContent = objectMapper.toJson(fileIds,
                    Types.newParameterizedType(List::class.java, String::class.java))
            val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
            requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_DELETE,
                    url.toString(), true, callback, body, requestBodyContent.md5())
        } else {
            // Make batch requests.
            SaveDeleteBatchTask(this, RequestHelper.REQUEST_DELETE, fileIds, callback).execute()
        }
    }

    /**
     * Marks a file as saved.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param fileId Resource UUID
     */
    fun saveFile(fileId: String) {
        val url = Urls.apiFileStorage(fileId)
        requestHelper.executeCommand(RequestHelper.REQUEST_POST, url.toString(), true)
    }

    /**
     * Marks a file as saved Asynchronously.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param context Application context. [android.content.Context]
     * @param fileId  Resource UUID
     */
    fun saveFileAsync(context: Context, fileId: String) {
        val url = Urls.apiFileStorage(fileId)
        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_POST, url.toString(), true)
    }

    /**
     * Marks a file as saved Asynchronously.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param context  Application context. @link android.content.Context
     * @param fileId   Resource UUID
     * @param callback callback  [RequestCallback] with either
     * an HTTP response or a failure exception.
     */
    fun saveFileAsync(context: Context, fileId: String, callback: RequestCallback? = null) {
        val url = Urls.apiFileStorage(fileId)
        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_POST, url.toString(), true,
                callback)
    }

    /**
     * Marks multiple files as saved.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param fileIds  Resource UUIDs
     */
    fun saveFiles(fileIds: List<String>) {
        val url = Urls.apiFilesBatch()
        if (fileIds.size < MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            val requestBodyContent = objectMapper.toJson(fileIds,
                    Types.newParameterizedType(List::class.java, String::class.java))
            val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
            requestHelper.executeCommand(RequestHelper.REQUEST_PUT, url.toString(), true, body,
                    requestBodyContent.md5())
        } else {
            // Make batch requests.
            executeSaveDeleteBatchCommand(RequestHelper.REQUEST_PUT, fileIds)
        }
    }

    /**
     * Marks multiple files as saved Asynchronously.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param context Application context. [android.content.Context]
     * @param fileIds  Resource UUIDs
     */
    fun saveFilesAsync(context: Context, fileIds: List<String>) {
        val url = Urls.apiFilesBatch()
        if (fileIds.size < MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            val requestBodyContent = objectMapper.toJson(fileIds,
                    Types.newParameterizedType(List::class.java, String::class.java))
            val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
            requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_PUT, url.toString(),
                    true, null, body, requestBodyContent.md5())
        } else {
            // Make batch requests.
            SaveDeleteBatchTask(this, RequestHelper.REQUEST_PUT, fileIds).execute()
        }
    }

    /**
     * Marks multiple files as saved Asynchronously.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param context  Application context. @link android.content.Context
     * @param fileIds  Resource UUIDs
     * @param callback callback  [RequestCallback] with either
     * an HTTP response or a failure exception.
     */
    fun saveFilesAsync(context: Context, fileIds: List<String>, callback: RequestCallback? = null) {
        val url = Urls.apiFilesBatch()
        if (fileIds.size < MAX_SAVE_DELETE_BATCH_SIZE) {
            // Make single request.
            val requestBodyContent = objectMapper.toJson(fileIds,
                    Types.newParameterizedType(List::class.java, String::class.java))
            val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
            requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_PUT, url.toString(),
                    true, callback, body, requestBodyContent.md5())
        } else {
            // Make batch requests.
            SaveDeleteBatchTask(this, RequestHelper.REQUEST_PUT, fileIds, callback).execute()
        }
    }

    /**
     * @deprecated Use {@link #copyFileLocalStorage(String, Boolean)}
     * or {@link #copyFileRemoteStorage(String, String, Boolean, String)} instead.
     *
     * @param source  File Resource UUID or A CDN URL.
     * @param storage Target storage name. If {@code null} local file copy will be executed,
     * else remote file copy will be executed.
     *
     * @return UploadcareCopyFile resource with result of Copy operation
     */
    @Deprecated(message = "Deprecated since v2.3.0", replaceWith = ReplaceWith("copyFileRemoteStorage(source, storage)"))
    fun copyFile(source: String, storage: String? = null): UploadcareCopyFile {
        return if (!TextUtils.isEmpty(storage)) {
            copyFileRemoteStorage(source, storage)
        } else {
            copyFileLocalStorage(source)
        }
    }

    /**
     * Copy file to local storage. Copy original files or their modified versions to default storage. Source files MAY
     * either be stored or just uploaded and MUST NOT be deleted.
     *
     * @param source     File Resource UUID or A CDN URL.
     * @param store      The parameter only applies to the Uploadcare storage and MUST be either true or false.
     *
     * @return An object containing the results of the copy request
     */
    fun copyFileLocalStorage(source: String, store: Boolean = true): UploadcareCopyFile {
        val copyOptionsData = CopyOptionsData(source, store = store)

        val requestBodyContent = objectMapper.toJson(copyOptionsData, CopyOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)

        val url = Urls.apiFileLocalCopy()

        return requestHelper.executeQuery(RequestHelper.REQUEST_POST, url.toString(), true,
                UploadcareCopyFile::class.java, body, requestBodyContent.md5())
    }

    /**
     * Copy file to remote storage. Copy original files or their modified versions to a custom storage. Source files
     * MAY either be stored or just uploaded and MUST NOT be deleted.
     *
     * @param source     File Resource UUID or A CDN URL.
     * @param target     Identifies a custom storage name related to your project. Implies you are copying a file to a
     *                   specified custom storage. Keep in mind you can have multiple storages associated with a single
     *                   S3 bucket.
     * @param makePublic MUST be either true or false. true to make copied files available via public links, false to
     *                   reverse the behavior.
     * @param pattern    The parameter is used to specify file names Uploadcare passes to a custom storage. In case the
     *                   parameter is omitted, we use pattern of your custom storage. Use any combination of allowed
     *                   values.
     *
     * @return An object containing the results of the copy request
     */
    fun copyFileRemoteStorage(source: String,
                              target: String? = null,
                              makePublic: Boolean = true,
                              pattern: String? = null): UploadcareCopyFile {
        val copyOptionsData = CopyOptionsData(
                source,
                target = target,
                makePublic = makePublic,
                pattern = pattern)

        val requestBodyContent = objectMapper.toJson(copyOptionsData, CopyOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)

        val url = Urls.apiFileRemoteCopy()

        return requestHelper.executeQuery(RequestHelper.REQUEST_POST, url.toString(), true,
                UploadcareCopyFile::class.java, body, requestBodyContent.md5())
    }

    /**
     * @deprecated Use {@link #copyFileLocalStorageAsync(Context, String, Boolean, Callback)}
     * or {@link #copyFileRemoteStorageAsync(Context, String, String, Boolean, String, Callback)} instead.
     *
     * @param context  Application context. @link android.content.Context
     * @param source  File Resource UUID or A CDN URL.
     * @param storage Target storage name. If {@code null} local file copy will be executed,
     * else remote file copy will be executed.
     * @param callback callback  [CopyFileCallback] with either
     * an UploadcareCopyFile response or a failure exception.
     */
    @Deprecated(message = "Deprecated since v2.3.0", replaceWith = ReplaceWith("copyFileRemoteStorageAsync(context, source, storage, callback)"))
    fun copyFileAsync(context: Context,
                      source: String,
                      storage: String? = null,
                      callback: CopyFileCallback? = null) {
        if (!TextUtils.isEmpty(storage)) {
            copyFileRemoteStorageAsync(context, source, storage, callback = callback)
        } else {
            copyFileLocalStorageAsync(context, source, callback = callback)
        }
    }

    /**
     * Copy file to local storage. Copy original files or their modified versions to default storage. Source files MAY
     * either be stored or just uploaded and MUST NOT be deleted.
     *
     * @param context  Application context. @link android.content.Context
     * @param source     File Resource UUID or A CDN URL.
     * @param store      The parameter only applies to the Uploadcare storage and MUST be either true or false.
     * @param callback callback  [CopyFileCallback] with either
     * an UploadcareCopyFile response or a failure exception.
     */
    fun copyFileLocalStorageAsync(context: Context,
                                  source: String,
                                  store: Boolean = true,
                                  callback: CopyFileCallback? = null) {
        val copyOptionsData = CopyOptionsData(source, store = store)

        val requestBodyContent = objectMapper.toJson(copyOptionsData, CopyOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)

        val url = Urls.apiFileLocalCopy()

        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_POST, url.toString(), true,
                UploadcareCopyFile::class.java, callback, body, requestBodyContent.md5())
    }

    /**
     * Copy file to remote storage. Copy original files or their modified versions to a custom storage. Source files
     * MAY either be stored or just uploaded and MUST NOT be deleted.
     *
     * @param context  Application context. @link android.content.Context
     * @param source     File Resource UUID or A CDN URL.
     * @param target     Identifies a custom storage name related to your project. Implies you are copying a file to a
     *                   specified custom storage. Keep in mind you can have multiple storages associated with a single
     *                   S3 bucket.
     * @param makePublic MUST be either true or false. true to make copied files available via public links, false to
     *                   reverse the behavior.
     * @param pattern    The parameter is used to specify file names Uploadcare passes to a custom storage. In case the
     *                   parameter is omitted, we use pattern of your custom storage. Use any combination of allowed
     *                   values.
     * @param callback callback  [CopyFileCallback] with either
     * an UploadcareCopyFile response or a failure exception.
     */
    fun copyFileRemoteStorageAsync(context: Context,
                                   source: String,
                                   target: String? = null,
                                   makePublic: Boolean = true,
                                   pattern: String? = null,
                                   callback: CopyFileCallback? = null) {
        val copyOptionsData = CopyOptionsData(
                source,
                target = target,
                makePublic = makePublic,
                pattern = pattern)

        val requestBodyContent = objectMapper.toJson(copyOptionsData, CopyOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)

        val url = Urls.apiFileRemoteCopy()

        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_POST, url.toString(), true,
                UploadcareCopyFile::class.java, callback, body, requestBodyContent.md5())
    }

    internal fun executeSaveDeleteBatchCommand(requestType: String,
                                               fileIds: List<String>): Response? {
        val url = Urls.apiFilesBatch()
        var lastResponse: Response? = null
        for (offset in 0 until fileIds.size step MAX_SAVE_DELETE_BATCH_SIZE) {
            val endIndex = if (offset + MAX_SAVE_DELETE_BATCH_SIZE >= fileIds.size)
                fileIds.size - 1
            else offset + (MAX_SAVE_DELETE_BATCH_SIZE - 1)
            val ids = fileIds.subList(offset, endIndex)

            val requestBodyContent = objectMapper.toJson(ids,
                    Types.newParameterizedType(List::class.java, String::class.java))
            val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
            val response = requestHelper.executeCommand(requestType, url.toString(), true, body,
                    requestBodyContent.md5())
            requestHelper.checkResponseStatus(response)
            lastResponse = response
        }
        return lastResponse
    }

    companion object {

        private const val MAX_SAVE_DELETE_BATCH_SIZE = 100

        @JvmStatic
        fun demoClient(): UploadcareClient {
            return UploadcareClient("demopublickey", "demosecretkey")
        }

        @JvmStatic
        fun demoClientUploadOnly(): UploadcareClient {
            return UploadcareClient("demopublickey")
        }
    }

}

private class PublicKeyInterceptor constructor(private val publicKey: String)
    : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("X-Uploadcare-PublicKey", publicKey)

        return chain.proceed(requestBuilder.build())
    }
}

private class SaveDeleteBatchTask(private val client: UploadcareClient,
                                  private val requestType: String,
                                  private val fileIds: List<String>,
                                  private val callback: RequestCallback? = null)
    : AsyncTask<Void, Void, Response?>() {
    override fun doInBackground(vararg params: Void?): Response? {
        return try {
            client.executeSaveDeleteBatchCommand(requestType, fileIds)
        } catch (e: Exception) {
            null
        }
    }

    override fun onPostExecute(result: Response?) {
        if (result != null) {
            callback?.onSuccess(result)
        } else {
            callback?.onFailure(UploadcareApiException())
        }
    }

}
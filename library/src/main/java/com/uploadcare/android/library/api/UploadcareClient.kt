package com.uploadcare.android.library.api

import android.content.Context
import android.text.TextUtils
import com.squareup.moshi.Types
import com.uploadcare.android.library.BuildConfig
import com.uploadcare.android.library.api.RequestHelper.Companion.md5
import com.uploadcare.android.library.callbacks.*
import com.uploadcare.android.library.data.ConvertStatusData
import com.uploadcare.android.library.data.CopyOptionsData
import com.uploadcare.android.library.data.DocumentInfo
import com.uploadcare.android.library.data.ObjectMapper
import com.uploadcare.android.library.data.UploadFromUrlStatusData
import com.uploadcare.android.library.data.WebhookOptionsData
import com.uploadcare.android.library.exceptions.UploadFailureException
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.urls.IncludeParameter
import com.uploadcare.android.library.urls.Urls
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString.Companion.encodeUtf8
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Initializes a client with custom access keys.
 * Can use simple or secure authentication.
 *
 * @param publicKey  Public key
 * @param secretKey Private key, required for any request to Uploadcare REST API.
 * @param simpleAuth If {@code false}, HMAC-based authentication is used, otherwise simple
 * authentication is used.
 */
@Suppress("unused")
@SuppressWarnings("WeakerAccess")
class UploadcareClient constructor(val publicKey: String,
                                   val secretKey: String? = null,
                                   val simpleAuth: Boolean = false) {

    constructor(publicKey: String) : this(publicKey, null, false)

    constructor(publicKey: String, secretKey: String) : this(publicKey, secretKey, false)

    private val coroutineScope: CoroutineScope = MainScope()

    val httpClient: OkHttpClient
    val requestHelper: RequestHelper
    val objectMapper = ObjectMapper.build()

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
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
     *
     * @param fileId Resource UUID
     * @return UploadcareFile resource
     */
    fun getUploadedFile(fileId: String): UploadcareFile {
        val url = Urls.apiUploadedFile(publicKey, fileId)

        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                false, UploadcareFile::class.java)
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
     * Requests file data, with Appdata if available.
     *
     * @param fileId Resource UUID
     * @return UploadcareFile resource
     */
    fun getFileWithAppData(fileId: String): UploadcareFile {
        val url = Urls.apiFile(fileId)
        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                true, UploadcareFile::class.java,
                urlParameters = listOf(IncludeParameter("appdata")))
    }

    /**
     * Requests file data, with Appdata if available, Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId   Resource UUID
     * @param callback callback  [UploadcareFileCallback] with either
     * an UploadcareFile response or a failure exception.
     */
    fun getFileWithAppDataAsync(context: Context, fileId: String, callback: UploadcareFileCallback? = null) {
        val url = Urls.apiFile(fileId)
        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                UploadcareFile::class.java, callback,
                urlParameters = listOf(IncludeParameter("appdata")))
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
     * Request file data for uploaded file. Does not require "secretKey" set for UploadcareClient.
     *
     * @param groupId Group ID
     */
    fun getUploadedGroup(groupId: String): UploadcareGroup {
        val url = Urls.apiUploadedGroup(publicKey, groupId)

        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                false, UploadcareGroup::class.java)
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
     * Delete a file group by its ID.
     * Note: The operation only removes the group object itself. All the files that were part of
     * the group are left as is.
     *
     * @param groupId Group UUID.
     */
    fun deleteGroup(groupId: String) {
        val url = Urls.apiGroup(groupId)
        requestHelper.executeCommand(RequestHelper.REQUEST_DELETE, url.toString(), true)
    }

    /**
     * Delete a file group by its ID Asynchronously.
     * Note: The operation only removes the group object itself. All the files that were part of
     * the group are left as is.
     *
     * @param context  Application context. [android.content.Context]
     * @param groupId Group UUID.
     */
    fun deleteGroupAsync(context: Context, groupId: String) {
        val url = Urls.apiGroup(groupId)
        requestHelper.executeCommandAsync(
            context = context,
            requestType = RequestHelper.REQUEST_DELETE,
            url = url.toString(),
            apiHeaders = true
        )
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
        val url = Urls.apiFileStorage(fileId)
        requestHelper.executeCommand(RequestHelper.REQUEST_DELETE, url.toString(), true)
    }

    /**
     * Marks a file as deleted Asynchronously.
     *
     * @param context Application context. [android.content.Context]
     * @param fileId  Resource UUID
     */
    fun deleteFileAsync(context: Context, fileId: String) {
        val url = Urls.apiFileStorage(fileId)
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
            executeSaveDeleteBatchCommandAsync(RequestHelper.REQUEST_DELETE, fileIds)
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
            executeSaveDeleteBatchCommandAsync(RequestHelper.REQUEST_DELETE, fileIds, callback)
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
            executeSaveDeleteBatchCommandAsync(RequestHelper.REQUEST_PUT, fileIds)
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
            executeSaveDeleteBatchCommandAsync(RequestHelper.REQUEST_PUT, fileIds, callback)
        }
    }

    private fun executeSaveDeleteBatchCommandAsync(
        requestType: String,
        fileIds: List<String>,
        callback: RequestCallback? = null
    ) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    executeSaveDeleteBatchCommand(requestType, fileIds)
                } catch (e: Exception) {
                    null
                }
            }

            if (result != null) {
                callback?.onSuccess(result)
            } else {
                callback?.onFailure(UploadcareApiException())
            }
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

    /**
     * Create files group from a set of files by using their UUIDs.
     *
     * @param fileIds  That parameter defines a set of files you want to join in a group.
     * @param jsonpCallback Sets the name of your JSONP callback function.
     *
     * @return New created Group resource instance.
     */
    fun createGroup(fileIds: List<String>, jsonpCallback: String? = null): UploadcareGroup {
        return createGroupInternal(fileIds, jsonpCallback = jsonpCallback)
    }

    /**
     * Create files group from a set of files by using their UUIDs. Using Signed Uploads.
     *
     * @param fileIds  That parameter defines a set of files you want to join in a group.
     * @param signature is a string sent along with your upload request. It requires your Uploadcare
     *                  project secret key and hence should be crafted on your back end.
     * @param expire    sets the time until your signature is valid. It is a Unix time.(ex 1454902434)
     * @param jsonpCallback Sets the name of your JSONP callback function.
     *
     * @return New created Group resource instance.
     */
    fun createGroupSigned(fileIds: List<String>,
                          signature: String,
                          expire: String,
                          jsonpCallback: String? = null): UploadcareGroup {
        return createGroupInternal(fileIds, jsonpCallback = jsonpCallback,
                signature = signature, expire = expire)
    }

    /**
     * Create files group from a set of files by using their UUIDs.
     *
     * @param fileIds  That parameter defines a set of files you want to join in a group.
     * @param jsonpCallback Sets the name of your JSONP callback function.
     * @param callback callback  [UploadcareGroupCallback] with either
     * an UploadcareGroup response or a failure exception.
     */
    fun createGroupAsync(fileIds: List<String>,
                         jsonpCallback: String? = null,
                         callback: UploadcareGroupCallback? = null) {
        createGroupInternalAsync(
            fileIds = fileIds,
            jsonpCallback = jsonpCallback,
            callback = callback
        )
    }

    /**
     * Create files group from a set of files by using their UUIDs.
     *
     * @param fileIds  That parameter defines a set of files you want to join in a group.
     * @param signature is a string sent along with your upload request. It requires your Uploadcare
     *                  project secret key and hence should be crafted on your back end.
     * @param expire    sets the time until your signature is valid. It is a Unix time.(ex 1454902434)
     * @param jsonpCallback Sets the name of your JSONP callback function.
     * @param callback callback  [UploadcareGroupCallback] with either
     * an UploadcareGroup response or a failure exception.
     */
    fun createGroupSignedAsync(context: Context,
                               fileIds: List<String>,
                               signature: String,
                               expire: String,
                               jsonpCallback: String? = null,
                               callback: UploadcareGroupCallback? = null) {
        createGroupInternalAsync(
            fileIds = fileIds,
            jsonpCallback = jsonpCallback,
            signature = signature,
            expire = expire,
            callback = callback
        )
    }

    private fun createGroupInternalAsync(
        fileIds: List<String>,
        jsonpCallback: String? = null,
        signature: String? = null,
        expire: String? = null,
        callback: UploadcareGroupCallback? = null
    ) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    createGroupInternal(fileIds, jsonpCallback, signature, expire)
                } catch (e: Exception) {
                    null
                }
            }

            if (result != null) {
                callback?.onSuccess(result)
            } else {
                callback?.onFailure(UploadFailureException())
            }
        }
    }

    /**
     * Requests Webhooks data.
     *
     * @return list of Webhooks resources.
     */
    fun getWebhooks(): List<UploadcareWebhook> {
        val url = Urls.apiWebhooks()

        return requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(), true,
                Types.newParameterizedType(List::class.java, UploadcareWebhook::class.java))
    }

    /**
     * Requests Webhooks data Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param callback callback  [UploadcareWebhooksCallback] with either
     * a response with List of UploadcareWebhook or a failure exception.
     */
    fun getWebhooksAsync(context: Context, callback: UploadcareWebhooksCallback) {
        val url = Urls.apiWebhooks()

        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                Types.newParameterizedType(List::class.java, UploadcareWebhook::class.java), callback)
    }

    /**
     * Create and subscribe to webhook.
     *
     * @param targetUrl A URL that is triggered by an event.
     * @param event An event you subscribe to. Only "file.uploaded" event supported.
     * @param isActive Marks a subscription as either active or not.
     * @param signingSecret Optional HMAC/SHA-256 secret that, if set, will be used to calculate
     * signatures for the webhook payloads sent to the target_url. (Should be <= 32 characters)
     *
     * @return New created webhook resource instance.
     */
    fun createWebhook(
            targetUrl: URI,
            event: EventType,
            isActive: Boolean = true,
            signingSecret: String? = null
    ): UploadcareWebhook {
        val webhookOptionsData = WebhookOptionsData(targetUrl, event, isActive, signingSecret)

        val requestBodyContent = objectMapper.toJson(webhookOptionsData,
                WebhookOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
        val url = Urls.apiWebhooks()

        return requestHelper.executeQuery(RequestHelper.REQUEST_POST, url.toString(), true,
                UploadcareWebhook::class.java, body, requestBodyContent.md5())
    }

    /**
     * Create and subscribe to webhook Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param targetUrl A URL that is triggered by an event.
     * @param event An event you subscribe to. Only "file.uploaded" event supported.
     * @param isActive  Marks a subscription as either active or not. Default value is {@code true}.
     * @param signingSecret Optional HMAC/SHA-256 secret that, if set, will be used to calculate
     * signatures for the webhook payloads sent to the target_url. (Should be <= 32 characters)
     * @param callback callback  [UploadcareWebhookCallback] with either
     * a response with UploadcareWebhook or a failure exception.
     */
    fun createWebhookAsync(context: Context,
                           targetUrl: URI,
                           event: EventType,
                           isActive: Boolean = true,
                           signingSecret: String? = null,
                           callback: UploadcareWebhookCallback) {
        val webhookOptionsData = WebhookOptionsData(targetUrl, event, isActive, signingSecret)

        val requestBodyContent = objectMapper.toJson(webhookOptionsData,
                WebhookOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
        val url = Urls.apiWebhooks()

        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_POST, url.toString(), true,
                UploadcareWebhook::class.java, callback, body, requestBodyContent.md5())
    }

    /**
     * Update webhook attributes.
     *
     * @param webhookId Webhook id. If {@code null} then this field won't be updated.
     * @param targetUrl A URL that is triggered by an event. If {@code null} then this field won't be updated.
     * @param event     An event you subscribe to. Only "file.uploaded" event supported. If {@code null} then this field
     *                  won't be updated.
     * @param isActive  Marks a subscription as either active or not. Default value is {@code true}.
     * @param signingSecret Optional HMAC/SHA-256 secret that, if set, will be used to calculate
     * signatures for the webhook payloads sent to the target_url. (Should be <= 32 characters)
     *
     * @return New webhook resource instance.
     */
    fun updateWebhook(webhookId: Int,
                      targetUrl: URI,
                      event: EventType,
                      isActive: Boolean = true,
                      signingSecret: String? = null
    ): UploadcareWebhook {
        val webhookOptionsData = WebhookOptionsData(targetUrl, event, isActive, signingSecret)

        val requestBodyContent = objectMapper.toJson(webhookOptionsData,
                WebhookOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
        val url = Urls.apiWebhook(webhookId)

        return requestHelper.executeQuery(RequestHelper.REQUEST_PUT, url.toString(), true,
                UploadcareWebhook::class.java, body, requestBodyContent.md5())
    }

    /**
     * Update webhook attributes Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param webhookId Webhook id. If {@code null} then this field won't be updated.
     * @param targetUrl A URL that is triggered by an event. If {@code null} then this field won't be updated.
     * @param event     An event you subscribe to. Only "file.uploaded" event supported. If {@code null} then this field
     *                  won't be updated.
     * @param isActive  Marks a subscription as either active or not. Default value is {@code true}.
     * @param signingSecret Optional HMAC/SHA-256 secret that, if set, will be used to calculate
     * signatures for the webhook payloads sent to the target_url. (Should be <= 32 characters)
     * @param callback callback  [UploadcareWebhookCallback] with either
     * a response with UploadcareWebhook or a failure exception.
     */
    fun updateWebhookAsync(context: Context,
                           webhookId: Int,
                           targetUrl: URI,
                           event: EventType,
                           isActive: Boolean = true,
                           signingSecret: String? = null,
                           callback: UploadcareWebhookCallback) {
        val webhookOptionsData = WebhookOptionsData(targetUrl, event, isActive, signingSecret)

        val requestBodyContent = objectMapper.toJson(webhookOptionsData,
                WebhookOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
        val url = Urls.apiWebhook(webhookId)

        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_PUT, url.toString(), true,
                UploadcareWebhook::class.java, callback, body, requestBodyContent.md5())
    }

    /**
     * Unsubscribe and delete webhook.
     *
     * @param targetUrl A URL that is triggered by an event from Webhook.
     *
     * @return Response with result of the operation or information about failure.
     */
    fun deleteWebhook(targetUrl: URI): Response {
        val webhookOptionsData = WebhookOptionsData(targetUrl = targetUrl)

        val requestBodyContent = objectMapper.toJson(webhookOptionsData,
                WebhookOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
        val url = Urls.apiDeleteWebhook()

        return requestHelper.executeCommand(RequestHelper.REQUEST_DELETE, url.toString(), true, body,
                requestBodyContent.md5())
    }

    /**
     * Unsubscribe and delete webhook Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param targetUrl A URL that is triggered by an event from Webhook.
     * @param callback callback  [RequestCallback] with a response result information.
     */
    fun deleteWebhookAsync(context: Context, targetUrl: URI, callback: RequestCallback? = null) {
        val webhookOptionsData = WebhookOptionsData(targetUrl = targetUrl)

        val requestBodyContent = objectMapper.toJson(webhookOptionsData,
                WebhookOptionsData::class.java)
        val body = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)
        val url = Urls.apiDeleteWebhook()

        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_DELETE, url.toString(),
                true, callback, body, requestBodyContent.md5())
    }

    /**
     * Requests file's metadata.
     *
     * @param fileId Resource UUID.
     */
    fun getFileMetadata(fileId: String): Map<String, String> {
        val url = Urls.apiFileMetadata(fileId)
        val mapType = Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            String::class.java
        )

        return requestHelper.executeQuery(
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataType = mapType
        )
    }

    /**
     * Requests file's metadata Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId Resource UUID.
     * @param callback callback  [UploadcareMetadataCallback] with either
     * a Map<String, String> response or a failure exception.
     */
    fun getFileMetadataAsync(
        context: Context,
        fileId: String,
        callback: UploadcareMetadataCallback
    ) {
        val url = Urls.apiFileMetadata(fileId)
        val mapType = Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            String::class.java
        )

        requestHelper.executeQueryAsync(
            context = context,
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataType = mapType,
            callback = callback
        )
    }

    /**
     * Requests metadata key's value.
     *
     * @param fileId Resource UUID.
     * @param key Metadata key.
     */
    fun getFileMetadataKeyValue(fileId: String, key: String): String {
        val url = Urls.apiFileMetadataKey(fileId, key)

        return requestHelper.executeQuery(
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = String::class.java
        )
    }

    /**
     * Requests metadata key's value Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId Resource UUID.
     * @param key Metadata key.
     * @param callback callback  [UploadcareMetadataKeyValueCallback] with either
     * a String response or a failure exception.
     */
    fun getFileMetadataKeyValueAsync(
        context: Context,
        fileId: String,
        key: String,
        callback: UploadcareMetadataKeyValueCallback
    ) {
        val url = Urls.apiFileMetadataKey(fileId, key)

        requestHelper.executeQueryAsync(
            context = context,
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = String::class.java,
            callback = callback
        )
    }

    /**
     * Update metadata key's value.
     *
     * @param fileId Resource UUID.
     * @param key Metadata key. If the key does not exist, it will be created.
     * @param value Metadata key's value.
     */
    fun updateFileMetadataKeyValue(fileId: String, key: String, value: String): String {
        val url = Urls.apiFileMetadataKey(fileId, key)
        val requestBodyContent = objectMapper.toJson(value, String::class.java)
        val requestBody = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)

        return requestHelper.executeQuery(
            requestType = RequestHelper.REQUEST_PUT,
            url = url.toString(),
            apiHeaders = true,
            dataClass = String::class.java,
            requestBody = requestBody,
            requestBodyMD5 = requestBodyContent.md5()
        )
    }

    /**
     * Update metadata key's value Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId Resource UUID.
     * @param key Metadata key. If the key does not exist, it will be created.
     * @param value Metadata key's value.
     * @param callback callback  [UploadcareMetadataKeyValueCallback] with either
     * a String response or a failure exception.
     */
    fun updateFileMetadataKeyValueAsync(
        context: Context,
        fileId: String,
        key: String,
        value: String,
        callback: UploadcareMetadataKeyValueCallback
    ) {
        val url = Urls.apiFileMetadataKey(fileId, key)
        val requestBodyContent = objectMapper.toJson(value, String::class.java)
        val requestBody = requestBodyContent.encodeUtf8().toRequestBody(RequestHelper.JSON)

        requestHelper.executeQueryAsync(
            context = context,
            requestType = RequestHelper.REQUEST_PUT,
            url = url.toString(),
            apiHeaders = true,
            dataClass = String::class.java,
            requestBody = requestBody,
            requestBodyMD5 = requestBodyContent.md5(),
            callback = callback
        )
    }

    /**
     * Delete metadata key.
     *
     * @param fileId Resource UUID.
     * @param key Metadata key.
     */
    fun deleteFileMetadataKey(fileId: String, key: String) {
        val url = Urls.apiFileMetadataKey(fileId, key)

        requestHelper.executeCommand(
            requestType = RequestHelper.REQUEST_DELETE,
            url = url.toString(),
            apiHeaders = true
        )
    }

    /**
     * Delete metadata key Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId Resource UUID.
     * @param key Metadata key.
     */
    fun deleteFileMetadataKeyAsync(
        context: Context,
        fileId: String,
        key: String
    ) {
        val url = Urls.apiFileMetadataKey(fileId, key)

        requestHelper.executeCommandAsync(
            context = context,
            requestType = RequestHelper.REQUEST_DELETE,
            url = url.toString(),
            apiHeaders = true
        )
    }

    /**
     * Check the status of a task to fetch/upload a file from a URL.
     *
     * @param token Token that identifies a request to fetch/upload a file from a URL.
     */
    fun getFromUrlStatus(token: String): UploadFromUrlStatusData {
        val url = Urls.uploadFromUrlStatus(token)

        return requestHelper.executeQuery(
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = false,
            dataClass = UploadFromUrlStatusData::class.java
        )
    }

    /**
     * Check the status of a task to fetch/upload a file from a URL Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param token Token that identifies a request to fetch/upload a file from a URL.
     * @param callback callback  [UploadFromUrlStatusCallback] with either
     * a UploadFromUrlStatusData response or a failure exception.
     */
    fun getFromUrlStatusAsync(
        context: Context,
        token: String,
        callback: UploadFromUrlStatusCallback
    ) {
        val url = Urls.uploadFromUrlStatus(token)

        return requestHelper.executeQueryAsync(
            context = context,
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = false,
            dataClass = UploadFromUrlStatusData::class.java,
            callback = callback
        )
    }

    /**
     * The method allows you to determine the document format and possible conversion formats.
     *
     * @param fileId Resource UUID.
     * @return DocumentInfo or throws a failure exception.
     */
    fun getDocumentConversionInfo(fileId: String): DocumentInfo {
        val url = Urls.apiDocumentConversionInfo(fileId)

        return requestHelper.executeQuery(
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = DocumentInfo::class.java
        )
    }

    /**
     * Get document format and possible conversion formats Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param fileId Resource UUID.
     * @param callback callback [ConversionInfoCallback] with either
     * a DocumentInfo response or a failure exception.
     */
    fun getDocumentConversionInfoAsync(
        context: Context,
        fileId: String,
        callback: ConversionInfoCallback,
    ) {
        val url = Urls.apiDocumentConversionInfo(fileId)

        return requestHelper.executeQueryAsync(
            context = context,
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = DocumentInfo::class.java,
            callback = callback
        )
    }

    /**
     * Check document conversion job status.
     *
     * @param token Job token.
     */
    fun getDocumentConversionStatus(token: Int): ConvertStatusData {
        val url = Urls.apiConvertDocumentStatus(token)

        return requestHelper.executeQuery(
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = ConvertStatusData::class.java
        )
    }

    /**
     * Check document conversion job status Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param token Job token.
     * @param callback callback  [ConversionStatusCallback] with either
     * a ConvertStatusData response or a failure exception.
     */
    fun getDocumentConversionStatusAsync(
        context: Context,
        token: Int,
        callback: ConversionStatusCallback
    ) {
        val url = Urls.apiConvertDocumentStatus(token)

        return requestHelper.executeQueryAsync(
            context = context,
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = ConvertStatusData::class.java,
            callback = callback
        )
    }

    /**
     * Check video conversion job status.
     *
     * @param token Job token.
     */
    fun getVideoConversionStatus(token: Int): ConvertStatusData {
        val url = Urls.apiConvertVideoStatus(token)

        return requestHelper.executeQuery(
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = ConvertStatusData::class.java
        )
    }

    /**
     * Check video conversion job status Asynchronously.
     *
     * @param context  Application context. [android.content.Context]
     * @param token Job token.
     * @param callback callback  [ConversionStatusCallback] with either
     * a ConvertStatusData response or a failure exception.
     */
    fun getVideoConversionStatusAsync(
        context: Context,
        token: Int,
        callback: ConversionStatusCallback
    ) {
        val url = Urls.apiConvertVideoStatus(token)

        return requestHelper.executeQueryAsync(
            context = context,
            requestType = RequestHelper.REQUEST_GET,
            url = url.toString(),
            apiHeaders = true,
            dataClass = ConvertStatusData::class.java,
            callback = callback
        )
    }

    internal fun createGroupInternal(fileIds: List<String>,
                                     jsonpCallback: String? = null,
                                     signature: String? = null,
                                     expire: String? = null): UploadcareGroup {
        val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pub_key", publicKey)

        jsonpCallback?.let {
            multipartBuilder.addFormDataPart("callback", it)
        }

        if (!TextUtils.isEmpty(signature) && !TextUtils.isEmpty(expire)) {
            multipartBuilder.addFormDataPart("signature", signature!!)
            multipartBuilder.addFormDataPart("expire", expire!!)
        }

        for (i in fileIds.indices) {
            multipartBuilder.addFormDataPart("files[$i]", fileIds[i])
        }

        val url = Urls.apiCreateGroup()
        val body = multipartBuilder.build()

        return requestHelper.executeQuery(RequestHelper.REQUEST_POST,
                url.toString(), false, UploadcareGroup::class.java, body)
    }

    internal fun executeSaveDeleteBatchCommand(requestType: String,
                                               fileIds: List<String>): Response? {
        val url = Urls.apiFilesBatch()
        var lastResponse: Response? = null
        for (offset in fileIds.indices step MAX_SAVE_DELETE_BATCH_SIZE) {
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

package com.uploadcare.android.widget.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.exceptions.UploadcareException
import com.uploadcare.android.widget.BuildConfig
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.activity.UploadcareActivity
import com.uploadcare.android.widget.interfaces.SocialApi
import com.uploadcare.android.widget.utils.SingletonHolder
import com.uploadcare.android.widget.worker.FileUploadWorker
import kotlinx.parcelize.Parcelize
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

/**
 * UploadcareWidget class has multiple options for selecting files from Social networks,
 * uses UploadcareClient internally and provides UploadcareWidgetResult with uploaded file info or
 * error.
 *
 * Replace variables in res/strings.xml file with you public/private keys from Uploadcare console.
 */
@Suppress("unused")
class UploadcareWidget private constructor(context: Context) {

    val uploadcareClient = UploadcareClient(
            context.getString(R.string.uploadcare_public_key),
            // Private Key might not exist if only Upload is used.
            if (context.getString(R.string.uploadcare_private_key).isNotEmpty()) {
                context.getString(R.string.uploadcare_private_key)
            } else null)

    internal val socialApi: SocialApi by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SOCIAL_API_ENDPOINT)
                .client(uploadcareClient.httpClient)
                .addConverterFactory(
                        MoshiConverterFactory.create(uploadcareClient.objectMapper.moshi))
                .build()

        retrofit.create(SocialApi::class.java)
    }

    /**
     * Select and upload file to Uploadcare.
     *
     * @param activity
     *
     * @return Builder where you can specify all required parameters and launch selection
     * and upload.
     */
    fun selectFile(activity: Activity): Builder {
        return Builder(activity)
    }

    /**
     * Select and upload file to Uploadcare.
     *
     * @param fragment
     *
     * @return Builder where you can specify all required parameters and launch selection
     * and upload.
     */
    fun selectFile(fragment: Fragment): Builder {
        return Builder(fragment)
    }

    /**
     * Cancel background upload that is happening.
     *
     * @param context - Context
     * @param uuid - UUID of the background upload, can be get from {@link UploadcareWidgetResult}.
     */
    fun cancelBackgroundUpload(context: Context, uuid: UUID) {
        WorkManager.getInstance(context).cancelWorkById(uuid)
    }

    /**
     * Gets a {@link LiveData} of the {@link UploadcareWidgetResult} for a given background upload.
     *
     * @param context - Context
     * @param uuid - UUID of the background upload, can be get from {@link UploadcareWidgetResult}.
     *
     * @return A {@link LiveData} of the {@link UploadcareWidgetResult} associated with
     * {@code uuid}; note that this {@link UploadcareWidgetResult} may be {@code null}
     * if {@code uuid} is not known to WorkManager.
     */
    fun backgroundUploadResult(context: Context, uuid: UUID): LiveData<UploadcareWidgetResult> {
        val workerLiveData = WorkManager.getInstance(context).getWorkInfoByIdLiveData(uuid)
        val result = MediatorLiveData<UploadcareWidgetResult>()
        result.addSource(workerLiveData) { workInfo ->
            if (workInfo == null) {
                result.value = null
                result.removeSource(workerLiveData)
                return@addSource
            }

            when (workInfo.state) {
                WorkInfo.State.SUCCEEDED -> {
                    val resultJson = workInfo.outputData
                            .getString(FileUploadWorker.KEY_UPLOADCARE_FILE)

                    result.value = resultJson?.let {
                        val uploadcareFile = uploadcareClient.objectMapper
                                .fromJson(it, UploadcareFile::class.java)
                        UploadcareWidgetResult(uploadcareFile = uploadcareFile)
                    } ?: UploadcareWidgetResult(null)
                }
                WorkInfo.State.FAILED -> {
                    val errorMessage = workInfo.outputData.getString(FileUploadWorker.KEY_ERROR)
                    result.value =
                            UploadcareWidgetResult(exception = UploadcareException(errorMessage))
                }
                WorkInfo.State.CANCELLED -> {
                    result.value =
                            UploadcareWidgetResult(exception = UploadcareException("Canceled"))
                }
                // still in progress
                else -> result.value = UploadcareWidgetResult(backgroundUploadUUID = uuid)
            }

            if (workInfo.state.isFinished) {
                result.removeSource(workerLiveData)
            }
        }

        return result
    }

    class Builder private constructor(private val fragment: Fragment? = null,
                                      private val activity: Activity? = null) {

        internal constructor(activity: Activity) : this(null, activity)

        internal constructor(fragment: Fragment) : this(fragment, null)

        private var storeUponUpload: Boolean = true

        private var requestCode: Int = UPLOADCARE_REQUEST_CODE

        private var fileType: FileType = FileType.any

        private var signature: String? = null

        private var expire: String? = null

        private var network: SocialNetwork? = null

        private var style: Int = -1

        private var cancelable: Boolean = false

        private var showProgress: Boolean = false

        private var backgroundUpload: Boolean = false

        /**
         * @param enabled when set true - store the file upon uploading.
         * Requires “automatic file storing” setting to be enabled.
         *              when set false - do not store file upon uploading.
         */
        fun storeUponUpload(enabled: Boolean) = apply { this.storeUponUpload = enabled }

        /**
         * @param requestCode custom Request code that will be used, you can use this code to detect
         *              specific request in onActivityResult().
         */
        fun resultRequestCode(requestCode: Int) = apply { this.requestCode = requestCode }

        /**
         * @param fileType FileType
         */
        fun fileType(fileType: FileType) = apply { this.fileType = fileType }

        /**
         * Signed Upload.
         *
         * Signed Upload will be only used if SocialNetwork.SOCIAL_NETWORK_CAMERA,
         * SocialNetwork.SOCIAL_NETWORK_VIDEOCAM or SocialNetwork.SOCIAL_NETWORK_FILE is selected.
         * Signed upload won't be used if Selected file is from external network like
         * Instagram/Facebook etc. is selected.
         *
         * @param signature is a string sent along with your upload request. It requires your Uploadcare
         * project secret key and hence should be crafted on your back end.
         * @param expire sets the time until your signature is valid. It is a Unix time.(ex 1454902434)
         */
        fun signedUpload(
                signature: String,
                expire: String,
        ) = apply {
            this.signature = signature
            this.expire = expire
        }

        /**
         * Specific source of the file to pick.
         *
         *  @param network SocialNetwork
         */
        fun from(network: SocialNetwork) = apply { this.network = network }

        /**
         * Custom style for Uploadcare widget.
         *
         * @param style - Style resource
         */
        fun style(style: Int) = apply { this.style = style }

        /**
         * @param enabled when set true - upload can be canceled by user,
         * "Cancel" button will be shown. when set false - upload will happen after file selected
         * without ability to cancel.
         */
        fun cancelable(enabled: Boolean) = apply { this.cancelable = enabled }

        /**
         * Show progress percentage during upload process, usually should be enabled for large files.
         *
         * @param enabled when set true - progress percentage will be shown during upload,
         * when set false - will show indeterminate progress bar.
         */
        fun showProgress(enabled: Boolean) = apply { this.showProgress = enabled }

        /**
         * Upload will happen in the background using WorkManager.
         * Notification will be shown to the user with progress/cancel if enabled.
         */
        fun backgroundUpload() = apply { this.backgroundUpload = true }

        /**
         * Launches selection and upload of the file, with specified parameters.
         *
         * To get result with info about uploaded file or error, override onActivityResult() method,
         * and use code below to get result
         * val result = UploadcareWidgetResult.fromIntent(data)
         */
        fun launch() {
            if (fragment != null) {
                fragment.startActivityForResult(Intent(fragment.activity,
                        UploadcareActivity::class.java).apply {
                    network?.let {
                        putExtra("network", it.rawValue)
                    }
                    putExtra("fileType", fileType.name)
                    putExtra("store", storeUponUpload)
                    putExtra("style", style)
                    putExtra("signature", signature)
                    putExtra("expire", expire)
                    putExtra("cancelable", cancelable)
                    putExtra("showProgress", showProgress)
                    putExtra("backgroundUpload", backgroundUpload)
                }, requestCode)
            } else activity?.startActivityForResult(Intent(activity, UploadcareActivity::class.java)
                    .apply {
                        network?.let {
                            putExtra("network", it.rawValue)
                        }
                        putExtra("fileType", fileType.name)
                        putExtra("store", storeUponUpload)
                        putExtra("style", style)
                        putExtra("signature", signature)
                        putExtra("expire", expire)
                        putExtra("cancelable", cancelable)
                        putExtra("showProgress", showProgress)
                        putExtra("backgroundUpload", backgroundUpload)
                    }, requestCode)
        }
    }

    companion object : SingletonHolder<UploadcareWidget, Context>(::UploadcareWidget) {

        @JvmStatic
        val UPLOADCARE_REQUEST_CODE = 431

        @JvmStatic
        override fun init(arg: Context) = super.init(arg)

        @JvmStatic
        override fun getInstance() = super.getInstance()
    }
}

@Suppress("unused")
enum class SocialNetwork constructor(val rawValue: String) {
    SOCIAL_NETWORK_FACEBOOK("facebook"),
    SOCIAL_NETWORK_INSTAGRAM("instagram"),
    SOCIAL_NETWORK_VK("vk"),
    SOCIAL_NETWORK_BOX("box"),
    SOCIAL_NETWORK_HUDDLE("huddle"),
    SOCIAL_NETWORK_FLICKR("flickr"),
    SOCIAL_NETWORK_EVERNOTE("evernote"),
    SOCIAL_NETWORK_SKYDRIVE("skydrive"),
    SOCIAL_NETWORK_ONEDRIVE("onedrive"),
    SOCIAL_NETWORK_DROPBOX("dropbox"),
    SOCIAL_NETWORK_GDRIVE("gdrive"),
    SOCIAL_NETWORK_GPHOTOS("gphotos"),
    SOCIAL_NETWORK_VIDEOCAM("video"),
    SOCIAL_NETWORK_CAMERA("image"),
    SOCIAL_NETWORK_FILE("file")
}

@Suppress("EnumEntryName")
enum class FileType {
    any, image, video
}

/**
 * Data class that holds result/error for select file request.
 */
@Parcelize
@Suppress("unused")
data class UploadcareWidgetResult(val uploadcareFile: UploadcareFile? = null,
                                  val backgroundUploadUUID: UUID? = null,
                                  val exception: UploadcareException? = null) : Parcelable {
    fun isSuccess() = uploadcareFile != null || backgroundUploadUUID != null

    fun hasFile() = uploadcareFile != null

    fun isBackgroundUpload() = backgroundUploadUUID != null

    fun isFailed() = exception != null

    companion object {
        @JvmStatic
        fun fromIntent(intent: Intent?): UploadcareWidgetResult? {
            return intent?.getParcelableExtra("result")
        }
    }
}
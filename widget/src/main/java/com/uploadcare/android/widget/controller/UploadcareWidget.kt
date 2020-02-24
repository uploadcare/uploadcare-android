package com.uploadcare.android.widget.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.exceptions.UploadcareException
import com.uploadcare.android.widget.BuildConfig
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.activity.UploadcareActivity
import com.uploadcare.android.widget.interfaces.SocialApi
import com.uploadcare.android.widget.utils.SingletonHolder
import kotlinx.android.parcel.Parcelize
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * UploadcareWidget class has multiple options for selecting files from Social networks,
 * uses UploadcareClient internally and provides UploadcareWidgetResult with uploaded file info or
 * error.
 *
 * Replace variables in res/strings.xml file with you public/private keys from Uploadcare console.
 */
class UploadcareWidget private constructor(context: Context) {

    val uploadcareClient = UploadcareClient(
            context.getString(R.string.uploadcare_public_key),
            // Private Key might not exist if only Upload is used.
            if (context.getString(R.string.uploadcare_private_key).isNotEmpty()) {
                context.getString(R.string.uploadcare_private_key)
            } else null)

    val socialApi: SocialApi by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SOCIAL_API_ENDPOINT)
                .client(uploadcareClient.httpClient)
                .addConverterFactory(
                        MoshiConverterFactory.create(uploadcareClient.objectMapper.moshi))
                .build()

        retrofit.create(SocialApi::class.java)
    }

    // Custom style for Uploadcare widget.
    var style = -1

    /**
     * Select and upload file to Uploadcare from any social network.
     *
     * @param activity Activity
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFile(activity: Activity, storeUponUpload: Boolean) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload file to Uploadcare from any social network.
     *
     * @param fragment Fragment
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFile(fragment: Fragment, storeUponUpload: Boolean) {
        fragment.startActivityForResult(Intent(fragment.activity, UploadcareActivity::class.java).apply {
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload file to Uploadcare from any social network.
     *
     * @param activity Activity
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFile(activity: Activity,
                   storeUponUpload: Boolean,
                   requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    /**
     * Select and upload file to Uploadcare from any social network.
     *
     * @param fragment Fragment
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFile(fragment: Fragment,
                   storeUponUpload: Boolean,
                   requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        fragment.startActivityForResult(Intent(fragment.activity, UploadcareActivity::class.java).apply {
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    /**
     * Select and upload specific file type to Uploadcare from any social network.
     *
     * @param activity Activity
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFile(activity: Activity,
                   storeUponUpload: Boolean,
                   fileType: FileType) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload specific file type to Uploadcare from any social network.
     *
     * @param fragment Fragment
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFile(fragment: Fragment,
                   storeUponUpload: Boolean,
                   fileType: FileType) {
        fragment.startActivityForResult(Intent(fragment.activity,
                UploadcareActivity::class.java).apply {
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload specific file type to Uploadcare from any social network.
     *
     * @param activity Activity
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFile(activity: Activity,
                   storeUponUpload: Boolean,
                   fileType: FileType,
                   requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    /**
     * Select and upload specific file type to Uploadcare from any social network.
     *
     * @param fragment Fragment
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFile(fragment: Fragment,
                   storeUponUpload: Boolean,
                   fileType: FileType,
                   requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        fragment.startActivityForResult(Intent(fragment.activity,
                UploadcareActivity::class.java).apply {
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param activity Activity
     * @param network SocialNetwork
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFileFrom(activity: Activity,
                       network: SocialNetwork,
                       storeUponUpload: Boolean) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param fragment Fragment
     * @param network SocialNetwork
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFileFrom(fragment: Fragment,
                       network: SocialNetwork,
                       storeUponUpload: Boolean) {
        fragment.startActivityForResult(Intent(fragment.activity,
                UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param activity Activity
     * @param network SocialNetwork
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFileFrom(activity: Activity,
                       network: SocialNetwork,
                       storeUponUpload: Boolean,
                       requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param fragment Fragment
     * @param network SocialNetwork
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFileFrom(fragment: Fragment,
                       network: SocialNetwork,
                       storeUponUpload: Boolean,
                       requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        fragment.startActivityForResult(Intent(fragment.activity,
                UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param activity Activity
     * @param network SocialNetwork
     * @param fileType FileType
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFileFrom(activity: Activity,
                       network: SocialNetwork,
                       fileType: FileType,
                       storeUponUpload: Boolean) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param fragment Fragment
     * @param network SocialNetwork
     * @param fileType FileType
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     */
    fun selectFileFrom(fragment: Fragment,
                       network: SocialNetwork,
                       fileType: FileType,
                       storeUponUpload: Boolean) {
        fragment.startActivityForResult(Intent(fragment.activity,
                UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param activity Activity
     * @param network SocialNetwork
     * @param fileType FileType
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFileFrom(activity: Activity,
                       network: SocialNetwork,
                       fileType: FileType,
                       storeUponUpload: Boolean,
                       requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        activity.startActivityForResult(Intent(activity, UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    /**
     * Select and upload file to Uploadcare from specified network.
     *
     * @param fragment Fragment
     * @param network SocialNetwork
     * @param fileType FileType
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     *
     *              To get result with info about uploaded file or error,
     *              override onActivityResult() method, and use code below to get result
     *              val result = UploadcareWidgetResult.fromIntent(data)
     * @param requestCode custom Request code that will be used, you can use this code to detect
     *              specific request in onActivityResult().
     */
    fun selectFileFrom(fragment: Fragment,
                       network: SocialNetwork,
                       fileType: FileType,
                       storeUponUpload: Boolean,
                       requestCode: Int = UPLOADCARE_REQUEST_CODE) {
        fragment.startActivityForResult(Intent(fragment.activity,
                UploadcareActivity::class.java).apply {
            putExtra("network", network.rawValue)
            putExtra("fileType", fileType.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, requestCode)
    }

    companion object : SingletonHolder<UploadcareWidget, Context>(::UploadcareWidget) {

        @JvmStatic
        val UPLOADCARE_REQUEST_CODE = 431

        @JvmStatic
        override fun getInstance(arg: Context) = super.getInstance(arg)
    }
}

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

enum class FileType {
    any, image, video
}

/**
 * Data class that holds result/error for select file request.
 */
@Parcelize
data class UploadcareWidgetResult(val uploadcareFile: UploadcareFile? = null,
                                  val exception: UploadcareException? = null) : Parcelable {
    fun isSuccess() = uploadcareFile != null
    fun isFailed() = exception != null

    companion object {
        @JvmStatic
        fun fromIntent(intent: Intent?): UploadcareWidgetResult? {
            return intent?.getParcelableExtra("result")
        }
    }
}
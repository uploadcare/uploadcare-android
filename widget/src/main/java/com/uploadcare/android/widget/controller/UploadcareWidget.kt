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

class UploadcareWidget private constructor(context: Context) {

    val uploadcareClient = UploadcareClient(
            context.getString(R.string.uploadcare_public_key),
            context.getString(R.string.uploadcare_private_key))

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
     */
    fun selectFile(fragment: Fragment, storeUponUpload: Boolean) {
        fragment.startActivityForResult(Intent(fragment.activity, UploadcareActivity::class.java).apply {
            putExtra("fileType", FileType.any.name)
            putExtra("store", storeUponUpload)
            putExtra("style", style)
        }, UPLOADCARE_REQUEST_CODE)
    }

    /**
     * Select and upload specific file type to Uploadcare from any social network.
     *
     * @param activity Activity
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
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
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
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
     * Select and upload file to Uploadcare from specified network.
     *
     * @param activity Activity
     * @param network SocialNetwork
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
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
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
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
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
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
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
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

@Parcelize
data class UploadcareWidgetResult(val uploadcareFile: UploadcareFile? = null,
                                  val exception: UploadcareException? = null) : Parcelable {
    fun isSuccess() = uploadcareFile != null
    fun isFailed() = exception != null

    companion object {
        @JvmStatic
        fun fromIntent(intent: Intent): UploadcareWidgetResult? {
            return intent.getParcelableExtra("result") ?: null
        }
    }
}
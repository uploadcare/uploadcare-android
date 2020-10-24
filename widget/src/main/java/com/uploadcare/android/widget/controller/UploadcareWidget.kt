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
                    }, requestCode)
        }
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
package com.uploadcare.android.library.urls

import android.net.Uri
import com.uploadcare.android.library.urls.UrlUtils.Companion.trustedBuild
import java.net.URI

/**
 * Uploadcare API URL factory methods.
 */
@Suppress("unused")
class Urls private constructor() {

    companion object {

        internal const val API_BASE = "https://api.uploadcare.com"
        private const val CDN_BASE = "https://ucarecdn.com"
        private const val UPLOAD_BASE = "https://upload.uploadcare.com"

        /**
         * Creates a URL to a project resource.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiProject(): URI {
            return URI.create("$API_BASE/project/")
        }

        /**
         * Creates a URL to a uploaded file resource.
         *
         * @param publicKey
         * @param fileId File UUID
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiUploadedFile(publicKey: String, fileId: String): URI {
            val builder = Uri.parse(UPLOAD_BASE)
                    .buildUpon()
                    .appendPath("/info/")
                    .appendQueryParameter("pub_key", publicKey)
                    .appendQueryParameter("file_id", fileId)
            return trustedBuild(builder)
        }

        /**
         * Creates a URL to a file resource.
         *
         * @param fileId File UUID
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFile(fileId: String): URI {
            return URI.create("$API_BASE/files/$fileId/")
        }

        /**
         * Creates a URL to a group resource.
         *
         * @param groupId Group ID
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiGroup(groupId: String): URI {
            return URI.create("$API_BASE/groups/$groupId/")
        }

        /**
         * Creates a URL to a group resource with files included.
         *
         * @param publicKey
         * @param groupId   File UUID
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiUploadedGroup(publicKey: String, groupId: String): URI {
            val builder = Uri.parse(UPLOAD_BASE)
                    .buildUpon()
                    .appendPath("/group/info/")
                    .appendQueryParameter("pub_key", publicKey)
                    .appendQueryParameter("group_id", groupId)
            return trustedBuild(builder)
        }

        /**
         * Creates a URL to the storage action for a file (saving the file).
         *
         * @param fileId File UUID
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFileStorage(fileId: String): URI {
            return URI.create("$API_BASE/files/$fileId/storage/")
        }

        /**
         * Creates a URL to the storage action for a group (available on CDN).
         *
         * @param groupId Group ID
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiGroupStorage(groupId: String): URI {
            return URI.create("$API_BASE/groups/$groupId/storage/")
        }

        /**
         * Creates a URL to the file collection resource.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFiles(): URI {
            return URI.create("$API_BASE/files/")
        }

        /**
         * Creates a URL to the file local copy resource.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFileLocalCopy(): URI {
            return URI.create("$API_BASE/files/local_copy/")
        }

        /**
         * Creates a URL to the file remote copy resource.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFileRemoteCopy(): URI {
            return URI.create("$API_BASE/files/remote_copy/")
        }

        /**
         * Creates a URL to the storage action for a multiple files (saving/deleting the files).
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFilesBatch(): URI {
            return URI.create("$API_BASE/files/storage/")
        }

        /**
         * Creates a URL to the group collection resource.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiGroups(): URI {
            return URI.create("$API_BASE/groups/")
        }

        /**
         * Creates a URL to the group resource.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiCreateGroup(): URI {
            return URI.create("$UPLOAD_BASE/group/")
        }

        /**
         * Creates a full CDN URL with a CDN path builder.
         *
         * @param builder Configured CDN path builder
         */
        @JvmStatic
        fun cdn(builder: CdnPathBuilder): URI {
            return URI.create(CDN_BASE + builder.build())
        }

        /**
         * Creates a full CDN URL with a CDN path builder. Using Secure delivery. Akamai
         *
         * @param domain domain that is setup for Secure delivery on your account. (ex. https://cdn.yourdomain.com)
         * @param builder Configured CDN path builder
         * @param token is a generated token used to access your content using a authenticated URL.
         * @param expire is an expiry date provided with the access token.
         */
        @JvmStatic
        fun cdnAkamai(
                domain: String,
                builder: CdnPathBuilder,
                token: String,
                expire: String): URI {
            return URI.create("$domain${builder.build()}?token=exp=$expire~acl=/${builder.getUUID()}/~hmac=$token")
        }

        /**
         * Creates a full CDN URL with a CDN path builder. Using Secure delivery. KeyCDN
         *
         * @param domain domain that is setup for Secure delivery on your account. (ex. https://cdn.yourdomain.com)
         * @param builder Configured CDN path builder
         * @param token is a generated token used to access your content using a authenticated URL.
         * @param expire is an expiry date provided with the access token.
         */
        @JvmStatic
        fun cdnKeyCDN(
                domain: String,
                builder: CdnPathBuilder,
                token: String,
                expire: String): URI {
            return URI.create("$domain${builder.build()}?token=$token&expire=$expire")
        }

        /**
         * Creates a URL to the webhook collection resource.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        fun apiWebhooks(): URI {
            return URI.create("$API_BASE/webhooks/")
        }

        /**
         * Creates a URL for the webhook delete.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        fun apiWebhook(webhookId: Int): URI {
            return URI.create("$API_BASE/webhooks/$webhookId/")
        }

        /**
         * Creates a URL for the webhook delete.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        fun apiDeleteWebhook(): URI {
            return URI.create("$API_BASE/webhooks/unsubscribe/")
        }

        /**
         * Creates a URL to the file upload endpoint.
         *
         * @see com.uploadcare.android.library.upload.FileUploader
         */
        @JvmStatic
        fun uploadBase(): URI {
            return URI.create("$UPLOAD_BASE/base/")
        }

        /**
         * Creates a URL for URL upload.
         *
         * @see com.uploadcare.android.library.upload.UrlUploader
         */
        @JvmStatic
        fun uploadFromUrl(): URI {
            return URI.create("$UPLOAD_BASE/from_url/")
        }

        /**
         * Creates a URL for URL upload status (e.g. progress).
         *
         * @param token Token, received after a URL upload request
         *
         * @see com.uploadcare.android.library.upload.UrlUploader
         */
        @JvmStatic
        fun uploadFromUrlStatus(token: String): URI {
            val builder = Uri.parse(UPLOAD_BASE)
                    .buildUpon()
                    .appendPath("/from_url/status/")
                    .appendQueryParameter("token", token)
            return trustedBuild(builder)
        }

        /**
         * Creates a URL to the file upload using multipart.
         *
         * @see com.uploadcare.android.library.upload.FileUploader
         */
        @JvmStatic
        fun uploadMultipartStart(): URI {
            return URI.create("$UPLOAD_BASE/multipart/start/")
        }

        /**
         * Creates a URL to the file upload using multipart.
         *
         * @see com.uploadcare.android.library.upload.FileUploader
         */
        @JvmStatic
        fun uploadMultipartPart(preSignedPartUrl: String): URI {
            return URI.create("$UPLOAD_BASE$preSignedPartUrl")
        }

        /**
         * Creates a URL for multipart upload complete.
         *
         * @see com.uploadcare.android.library.upload.FileUploader
         */
        @JvmStatic
        fun uploadMultipartComplete(): URI {
            return URI.create("$UPLOAD_BASE/multipart/complete/")
        }

        /**
         * Creates a URL to the document conversion.
         *
         * @see com.uploadcare.android.library.conversion.DocumentConverter
         */
        @JvmStatic
        fun apiConvertDocument(): URI {
            return URI.create("$API_BASE/convert/document/")
        }

        /**
         * Creates a URL to the document conversion status.
         *
         * @param token Token, received after a Convert Document request.
         *
         * @see com.uploadcare.android.library.conversion.DocumentConverter
         */
        @JvmStatic
        fun apiConvertDocumentStatus(token: Int): URI {
            return URI.create("$API_BASE/convert/document/status/$token/")
        }

        /**
         * Creates a URL to the video conversion.
         *
         * @see com.uploadcare.android.library.conversion.VideoConverter
         */
        @JvmStatic
        fun apiConvertVideo(): URI {
            return URI.create("$API_BASE/convert/video/")
        }

        /**
         * Creates a URL to the video conversion status.
         *
         * @param token Token, received after a Convert Video request.
         *
         * @see com.uploadcare.android.library.conversion.VideoConverter
         */
        @JvmStatic
        fun apiConvertVideoStatus(token: Int): URI {
            return URI.create("$API_BASE/convert/video/status/$token/")
        }
    }
}
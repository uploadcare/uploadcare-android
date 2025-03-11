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
         * Allows you to determine the document format and possible conversion formats.
         *
         * @param fileId File UUID.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiDocumentConversionInfo(fileId: String): URI {
            return URI.create("$API_BASE/convert/document/$fileId/")
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

        /**
         * Creates a URL to the AWS Rekognition execution.
         *
         * @see com.uploadcare.android.library.addon.AWSRekognitionAddOn
         */
        @JvmStatic
        fun apiExecuteAWSRekognition(): URI {
            return URI.create("$API_BASE/addons/aws_rekognition_detect_labels/execute/")
        }

        /**
         * Creates a URL to the AWS Rekognition status.
         *
         * @see com.uploadcare.android.library.addon.AWSRekognitionAddOn
         */
        @JvmStatic
        fun apiAWSRekognitionStatus(): URI {
            return URI.create("$API_BASE/addons/aws_rekognition_detect_labels/execute/status/")
        }

        /**
         * Creates a URL to the AWS Rekognition Moderation execution.
         *
         * @see com.uploadcare.android.library.addon.AWSRekognitionModerationAddOn
         */
        @JvmStatic
        fun apiExecuteAWSRekognitionModeration(): URI {
            return URI.create("$API_BASE/addons/aws_rekognition_detect_moderation_labels/execute/")
        }

        /**
         * Creates a URL to the AWS Rekognition Moderation status.
         *
         * @see com.uploadcare.android.library.addon.AWSRekognitionModerationAddOn
         */
        @JvmStatic
        fun apiAWSRekognitionModerationStatus(): URI {
            return URI.create("$API_BASE/addons/aws_rekognition_detect_moderation_labels/execute/status/")
        }

        /**
         * Creates a URL to the ClamAV execution.
         *
         * @see com.uploadcare.android.library.addon.ClamAVAddOn
         */
        @JvmStatic
        fun apiExecuteClamAV(): URI {
            return URI.create("$API_BASE/addons/uc_clamav_virus_scan/execute/")
        }

        /**
         * Creates a URL to the ClamAV status.
         *
         * @see com.uploadcare.android.library.addon.ClamAVAddOn
         */
        @JvmStatic
        fun apiClamAVStatus(): URI {
            return URI.create("$API_BASE/addons/uc_clamav_virus_scan/execute/status/")
        }

        /**
         * Creates a URL to the Remove.bg execution.
         *
         * @see com.uploadcare.android.library.addon.RemoveBgAddOn
         */
        @JvmStatic
        fun apiExecuteRemoveBg(): URI {
            return URI.create("$API_BASE/addons/remove_bg/execute/")
        }

        /**
         * Creates a URL to the Remove.bg status.
         *
         * @see com.uploadcare.android.library.addon.RemoveBgAddOn
         */
        @JvmStatic
        fun apiRemoveBgStatus(): URI {
            return URI.create("$API_BASE/addons/remove_bg/execute/status/")
        }

        /**
         * Creates a URL to the file's metadata.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFileMetadata(fileId: String): URI {
            return URI.create("$API_BASE/files/$fileId/metadata/")
        }

        /**
         * Creates a URL to the file's metadata key.
         *
         * @see com.uploadcare.android.library.api.UploadcareClient
         */
        @JvmStatic
        fun apiFileMetadataKey(fileId: String, key: String): URI {
            return URI.create("$API_BASE/files/$fileId/metadata/$key/")
        }
    }
}

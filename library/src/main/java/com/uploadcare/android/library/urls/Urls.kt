package com.uploadcare.android.library.urls

import android.net.Uri
import com.uploadcare.android.library.urls.UrlUtils.Companion.trustedBuild
import java.net.URI

/**
 * Uploadcare API URL factory methods.
 */
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
         * Creates a full CDN URL with a CDN path builder.
         *
         * @param builder Configured CDN path builder
         */
        @JvmStatic
        fun cdn(builder: CdnPathBuilder): URI {
            return URI.create(CDN_BASE + builder.build())
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
         * @param sourceUrl URL to upload from
         * @param pubKey Public key
         *
         * @see com.uploadcare.android.library.upload.UrlUploader
         */
        @JvmStatic
        fun uploadFromUrl(sourceUrl: String, pubKey: String, store: String): URI {
            val builder = Uri.parse(UPLOAD_BASE)
                    .buildUpon()
                    .appendPath("/from_url/")
                    .appendQueryParameter("source_url", sourceUrl)
                    .appendQueryParameter("pub_key", pubKey)
                    .appendQueryParameter("store", store)
            return trustedBuild(builder)
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
    }
}
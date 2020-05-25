package com.uploadcare.android.library.api

import android.os.Parcelable
import com.squareup.moshi.Json
import com.uploadcare.android.library.urls.CdnPathBuilder
import kotlinx.android.parcel.Parcelize
import java.net.URI
import java.util.*

@Parcelize
data class UploadcareFile(val uuid: String,
                          val url: URI? = null,
                          val size: Int,
                          val source: String? = null,
                          @Json(name = "is_ready") val isReady: Boolean,
                          @Json(name = "is_image") val isImage: Boolean? = false,
                          @Json(name = "mime_type") val mimeType: String? = null,
                          @Json(name = "original_filename") val originalFilename: String? = null,
                          @Json(name = "original_file_url") val originalFileUrl: URI? = null,
                          @Json(name = "datetime_uploaded") val datetimeUploaded: Date? = null,
                          @Json(name = "datetime_stored") val datetimeStored: Date? = null,
                          @Json(name = "datetime_removed") val datetimeRemoved: Date? = null,
                          @Json(name = "image_info") val imageInfo: ImageInfo? = null,
                          @Json(name = "video_info") val videoInfo: VideoInfo? = null,
                          @Json(name = "rekognition_info") val rekognitionInfo: Map<String, Float>? = null,
                          val variations: Map<String, String>? = null)
    : Parcelable {

    fun hasOriginalFileUrl() = originalFileUrl != null

    fun isStored() = datetimeStored != null

    fun isRemoved() = datetimeRemoved != null

    /**
     * Refreshes file data from Uploadcare.
     *
     * This does not mutate the current {@code UploadcareFile} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    fun update(client: UploadcareClient): UploadcareFile? {
        return client.getFile(uuid)
    }

    /**
     * Deletes this file from Uploadcare.
     *
     * This does not mutate the current `UploadcareFile` instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    fun delete(client: UploadcareClient): UploadcareFile? {
        client.deleteFile(uuid)
        return update(client)
    }

    /**
     * Saves this file on Uploadcare (marks it to be kept).
     *
     * This does not mutate the current [UploadcareFile] instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    fun save(client: UploadcareClient): UploadcareFile? {
        client.saveFile(uuid)
        return update(client)
    }

    /**
     * Creates a CDN path builder for this file.
     *
     * @return CDN path builder
     * @see com.uploadcare.android.library.urls.Urls.cdn
     */
    fun cdnPath(): CdnPathBuilder {
        return CdnPathBuilder(this)
    }

    override fun toString(): String {
        val newline = System.getProperty("line.separator")
        return StringBuilder().apply {
            append(UploadcareFile::class.simpleName).append(newline)
            append("File id: $uuid").append(newline)
            append("Url: $url").append(newline)
            append("Size: $size").append(newline)
            append("Mime type: $mimeType").append(newline)
            append("is Ready: $isReady").append(newline)
            append("is Image: $isImage").append(newline)
            append("Original Filename: $originalFilename").append(newline)
            append("Original File Url: $originalFileUrl").append(newline)
            append("Date uploaded: $datetimeUploaded").append(newline)
            append("is Stored: ${isStored()}").append(newline)
            append("Date stored: $datetimeStored").append(newline)
            append("is Removed: ${isRemoved()}").append(newline)
            append("Date removed: $datetimeRemoved").append(newline)
            append("Source: $source").append(newline)
            append("Image Info: $imageInfo").append(newline)
            append("Video Info: $videoInfo").append(newline)
            append("Variations: $variations").append(newline)
            append("Rekognition Info: $rekognitionInfo").append(newline)
        }.toString()
    }
}

@Parcelize
data class ImageInfo(val format: String,
                     val height: Int,
                     val width: Int,
                     val orientation: Int? = null,
                     val sequence: Boolean? = false,
                     @Json(name = "color_mode") val colorMode: ColorMode? = null,
                     @Json(name = "geo_location") val geoLocation: GeoLocation? = null,
                     @Json(name = "datetime_original") val datetimeOriginal: String? = null,
                     val dpi: List<Float>? = null) : Parcelable

@Parcelize
data class VideoInfo(val format: String,
                     val duration: Int,
                     val bitrate: Int,
                     val audio: Audio? = null,
                     val video: Video) : Parcelable

@Parcelize
data class GeoLocation(val latitude: Float, val longitude: Float) : Parcelable

@Parcelize
data class Audio(val bitrate: Int? = null,
                 val codec: String? = null,
                 val channels: String? = null,
                 @Json(name = "sample_rate") val sampleRate: Int? = null) : Parcelable

@Parcelize
data class Video(val bitrate: Int,
                 val codec: String,
                 val height: Int,
                 val width: Int,
                 @Json(name = "frame_rate") val frameRate: Float) : Parcelable

enum class ColorMode {
    RGB, RGBA, RGBa, RGBX, L, LA, La, P, PA, CMYK, YCbCr, HSV, LAB
}
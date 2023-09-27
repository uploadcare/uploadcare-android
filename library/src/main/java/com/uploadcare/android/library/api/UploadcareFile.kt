package com.uploadcare.android.library.api

import android.os.Parcelable
import com.squareup.moshi.Json
import com.uploadcare.android.library.urls.CdnPathBuilder
import kotlinx.parcelize.Parcelize
import java.net.URI
import java.util.*

@Parcelize
@Suppress("unused")
@SuppressWarnings("WeakerAccess")
data class UploadcareFile(
    val uuid: String,
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
    val variations: Map<String, String>? = null,
    val appdata: Appdata? = null,
    @Json(name = "content_info") val contentInfo: ContentInfo? = null,
    val metadata: Map<String, String>? = null
) : Parcelable {

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
            append("Variations: $variations").append(newline)
            append("Appdata: $appdata").append(newline)
            append("Content info: $contentInfo").append(newline)
            append("Metadata: $metadata").append(newline)
        }.toString()
    }
}

@Parcelize
data class ContentInfo(
    val mime: Mime? = null,
    val image: ImageInfo? = null,
    val video: VideoInfo? = null
) : Parcelable

@Parcelize
data class Mime(
    val mime: String,
    val type: String,
    val subtype: String
) : Parcelable

@Parcelize
data class ImageInfo(
    val format: String,
    val height: Int,
    val width: Int,
    val orientation: Int? = null,
    val sequence: Boolean? = false,
    @Json(name = "color_mode") val colorMode: ColorMode? = null,
    @Json(name = "geo_location") val geoLocation: GeoLocation? = null,
    @Json(name = "datetime_original") val datetimeOriginal: String? = null,
    val dpi: List<Float>? = null
) : Parcelable

@Parcelize
data class VideoInfo(
    val format: String,
    val duration: Int,
    val bitrate: Int,
    val audio: List<Audio>,
    val video: List<Video>
) : Parcelable

@Parcelize
data class GeoLocation(val latitude: Float, val longitude: Float) : Parcelable

@Parcelize
data class Audio(
    val bitrate: Int? = null,
    val codec: String? = null,
    val channels: String? = null,
    @Json(name = "sample_rate") val sampleRate: Int? = null
) : Parcelable

@Parcelize
data class Video(
    val bitrate: Int? = null,
    val codec: String? = null,
    val height: Int,
    val width: Int,
    @Json(name = "frame_rate") val frameRate: Float
) : Parcelable

@Suppress("unused")
enum class ColorMode {
    RGB, RGBA, RGBa, RGBX, L, LA, La, P, PA, CMYK, YCbCr, HSV, LAB
}

@Parcelize
data class Appdata(
    @Json(name = "aws_rekognition_detect_labels")
    val awsRekognitionDetectLabels: AwsRekognitionDetectLabels? = null,

    @Json(name = "aws_rekognition_detect_moderation_labels")
    val awsRekognitionDetectModerationLabels: AwsRekognitionDetectModerationLabels? = null,

    @Json(name = "remove_bg")
    val removeBg: RemoveBg? = null,

    @Json(name = "uc_clamav_virus_scan")
    val ucClamavVirusScan: UcClamavVirusScan? = null
) : Parcelable

sealed interface AppdataInfo<Data> {
    val version: String
    val datetimeCreated: Date
    val datetimeUpdated: Date
    val data: Data
}

@Parcelize
data class AwsRekognitionDetectLabels(
    override val version: String,
    @Json(name = "datetime_created") override val datetimeCreated: Date,
    @Json(name = "datetime_updated") override val datetimeUpdated: Date,
    override val data: Data
) : AppdataInfo<AwsRekognitionDetectLabels.Data>, Parcelable {

    @Parcelize
    data class Data(
        @Json(name = "LabelModelVersion") val labelModelVersion: String,
        @Json(name = "Labels") val labels: List<Label>
    ) : Parcelable

    @Parcelize
    data class Label(
        @Json(name = "Confidence") val confidence: Float,
        @Json(name = "Instances") val instances: List<Instance>,
        @Json(name = "Name") val name: String,
        @Json(name = "Parents") val parents: List<Parent>
    ) : Parcelable

    @Parcelize
    data class Instance(
        @Json(name = "BoundingBox") val boundingBox: BoundingBox? = null,
        @Json(name = "Confidence") val confidence: Float? = null
    ) : Parcelable

    @Parcelize
    data class BoundingBox(
        @Json(name = "Height") val height: Float? = null,
        @Json(name = "Left") val left: Float? = null,
        @Json(name = "Top") val top: Float? = null,
        @Json(name = "Width") val width: Float? = null
    ) : Parcelable

    @Parcelize
    data class Parent(@Json(name = "Name") val name: String? = null) : Parcelable
}

@Parcelize
data class AwsRekognitionDetectModerationLabels(
    override val version: String,
    @Json(name = "datetime_created") override val datetimeCreated: Date,
    @Json(name = "datetime_updated") override val datetimeUpdated: Date,
    override val data: Data
) : AppdataInfo<AwsRekognitionDetectModerationLabels.Data>, Parcelable {

    @Parcelize
    data class Data(
        @Json(name = "ModerationModelVersion") val moderationModelVersion: String,
        @Json(name = "ModerationLabels") val moderationLabels: List<ModerationLabel>
    ) : Parcelable

    @Parcelize
    data class ModerationLabel(
        @Json(name = "Confidence") val confidence: String,
        @Json(name = "Name") val name: String,
        @Json(name = "ParentName") val parentName: String
    ) : Parcelable
}

@Parcelize
data class RemoveBg(
    override val version: String,
    @Json(name = "datetime_created") override val datetimeCreated: Date,
    @Json(name = "datetime_updated") override val datetimeUpdated: Date,
    override val data: Data
) : AppdataInfo<RemoveBg.Data>, Parcelable {

    @Parcelize
    data class Data(@Json(name = "foreground_type") val foregroundType: String? = null) : Parcelable
}

@Parcelize
data class UcClamavVirusScan(
    override val version: String,
    @Json(name = "datetime_created") override val datetimeCreated: Date,
    @Json(name = "datetime_updated") override val datetimeUpdated: Date,
    override val data: Data
) : AppdataInfo<UcClamavVirusScan.Data>, Parcelable {

    @Parcelize
    data class Data(
        val infected: Boolean,
        @Json(name = "infected_with") val infectedWith: String? = null
    ) : Parcelable
}

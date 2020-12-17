package com.uploadcare.android.library.conversion

import androidx.annotation.IntRange

/**
 * Base class for Conversion job.
 */
abstract class ConversionJob(val fileId: String) {

    abstract fun getPath(): String
}

class DocumentConversionJob(fileId: String) : ConversionJob(fileId) {

    private var format: DocumentFormat = DocumentFormat.PDF

    private var page: String? = null

    /**
     * The output document format.
     *
     * @param DocumentFormat target format.
     */
    fun setFormat(format: DocumentFormat): DocumentConversionJob {
        this.format = format
        return this
    }

    /**
     * Converts a single page of a multi-paged document to either jpg or png, the output will be a
     * zip archive with one image per page.
     *
     * @param number stands for the one-based number of a page to convert.
     */
    fun page(number: Int): DocumentConversionJob {
        if (format == DocumentFormat.JPG || format == DocumentFormat.PNG) {
            page = number.toString()
        }
        return this
    }

    override fun getPath(): String {
        val builder = StringBuilder()
        builder.append("$fileId/document/-/format/${format.rawValue}/")

        page?.let {
            builder.append("-/page/${it}/")
        }

        return builder.toString()
    }
}

class VideoConversionJob(fileId: String) : ConversionJob(fileId) {

    private var format: VideoFormat = VideoFormat.MP4

    private var videoQuality: VideoQuality = VideoQuality.NORMAL

    private var size: String? = null

    private var cut: String? = null

    private var thumbnails: String? = null

    /**
     * The output video format.
     *
     * @param VideoFormat target format.
     */
    fun setFormat(format: VideoFormat): VideoConversionJob {
        this.format = format
        return this
    }

    /**
     * Sets the level of video quality that affects file sizes and hence loading times and volumes
     * of generated traffic.
     */
    fun quality(quality: VideoQuality): VideoConversionJob {
        videoQuality = quality
        return this
    }

    /**
     * Resizes a video to the specified dimensions. The operation follows the behavior specified
     * by one of the {@link VideoResizeMode} options.
     *
     * @param width should be a non-zero integer divisible by 4.
     * @param height should be a non-zero integer divisible by 4.
     * @param resizeMode VideoResizeMode.
     */
    fun resize(
            width: Int,
            height: Int,
            resizeMode: VideoResizeMode = VideoResizeMode.PRESERVE_RATIO)
            : VideoConversionJob {
        val validatedWidth = if (width > 0 && (width % 4 == 0)) {
            width
        } else {
            null
        }

        val validatedHeight = if (height > 0 && (height % 4 == 0)) {
            height
        } else {
            null
        }

        if (validatedWidth != null && validatedHeight != null) {
            size = "${validatedWidth}x${validatedHeight}/${resizeMode.rawValue}"
        } else if (validatedWidth != null) {
            size = "${validatedWidth}x/${resizeMode.rawValue}"
        } else if (validatedHeight != null) {
            size = "x${validatedHeight}/${resizeMode.rawValue}"
        }

        return this
    }

    /**
     * Cuts out a video fragment.
     *
     * @param startTime defines the starting point of a fragment to cut based on your input file
     * timeline. Format: HHH:MM:SS.sss;
     * HHH are hours ranging from 0 to 999,
     * MM — minutes ranging from 0 to 59,
     * SS.sss are seconds and milliseconds.
     * HHH and MM can be omitted.
     * SSS+.sss, a number of seconds and milliseconds; sss can be omitted.
     * @param length defines the duration of that fragment. Default value is "end" - your video
     * fragment will then include all the duration of your input starting at {@code startTime}.
     */
    fun cut(startTime: String, length: String = "end"): VideoConversionJob {
        cut = "${startTime}/${length}"
        return this
    }

    /**
     * Creates thumbnails for your video. If the operation is omitted,
     * a single thumbnail gets generated from the very middle of your video.
     * If you define another {@code numberOfThumbnails}, thumbnails are generated from the frames
     * evenly distributed along your video timeline. I.e., if you have a 20-second video with
     * {@code numberOfThumbnails} set to 20, you will get a thumbnail per every second of your
     * video.
     *
     *  @param numberOfThumbnails is a non-zero integer ranging from 1 to 50.
     */
    fun thumbnails(@IntRange(from = 1, to = 50) numberOfThumbnails: Int): VideoConversionJob {
        thumbnails = "thumbs~${numberOfThumbnails}"
        return this
    }

    override fun getPath(): String {
        val builder = StringBuilder()
        builder.append("$fileId/video/-/format/${format.rawValue}/")

        videoQuality?.let {
            builder.append("-/quality/${videoQuality.rawValue}/")
        }

        size?.let {
            builder.append("-/size/${it}/")
        }

        cut?.let {
            builder.append("-/cut/${it}/")
        }

        thumbnails?.let {
            builder.append("-/${it}/")
        }

        return builder.toString()
    }
}
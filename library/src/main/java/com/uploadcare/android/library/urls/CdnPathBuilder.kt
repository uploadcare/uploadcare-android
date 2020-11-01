package com.uploadcare.android.library.urls

import com.uploadcare.android.library.api.UploadcareFile

/**
 * Creates a new CDN path builder for some image file.
 *
 * @param file File to be used for the path
 *
 * @see com.uploadcare.android.library.data.UploadcareFile.cdnPath
 */
class CdnPathBuilder internal constructor(file: UploadcareFile) {

    private val sb = StringBuilder("/")

    init {
        sb.append(file.uuid)
    }

    private fun dimensionGuard(dim: Int) {
        if (dim < 1 || dim > 7680) {
            throw IllegalArgumentException("Dimensions must be in the range 1-7680")
        }
    }

    private fun dimensionsGuard(width: Int, height: Int) {
        dimensionGuard(width)
        dimensionGuard(height)
        if (width > 7680 && height > 7680) {
            throw IllegalArgumentException("At least one dimension must be less than 7680")
        }
    }

    /**
     * Adds top-left-aligned crop.
     *
     * @param width Crop width
     * @param height Crop height
     */
    fun crop(width: Int, height: Int): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
        return this
    }

    /**
     * Adds center-aligned crop.
     *
     * @param width Crop width
     * @param height Crop height
     */
    fun cropCenter(width: Int, height: Int): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center")
        return this
    }

    /**
     * Adds top-left-aligned crop with a filled background.
     *
     * @param width Crop width
     * @param height Crop height
     * @param color Background color RGB in Hex format "RRGGBB".
     */
    fun cropColor(width: Int, height: Int, color: String): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/")
                .append(color)
        return this
    }

    /**
     * Adds center-aligned crop with a filled background.
     *
     * @param width Crop width
     * @param height Crop height
     * @param color Background color RGB in Hex format "RRGGBB".
     */
    fun cropCenterColor(width: Int, height: Int, color: String): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center/")
                .append(color)
        return this
    }

    /**
     * Resizes width, keeping the aspect ratio.
     *
     * @param width New width
     */
    fun resizeWidth(width: Int): CdnPathBuilder {
        dimensionGuard(width)
        sb.append("/-/resize/")
                .append(width)
                .append("x")
        return this
    }

    /**
     * Resizes height, keeping the aspect ratio.
     *
     * @param height New height
     */
    fun resizeHeight(height: Int): CdnPathBuilder {
        dimensionGuard(height)
        sb.append("/-/resize/x").append(height)
        return this
    }

    /**
     * Resizes width and height
     *
     * @param width New width
     * @param height New height
     */
    fun resize(width: Int, height: Int): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/resize/")
                .append(width)
                .append("x")
                .append(height)
        return this
    }

    /**
     * Scales the image until one of the dimensions fits,
     * then crops the bottom or right side.
     *
     * @param width New width
     * @param height New height
     */
    fun scaleCrop(width: Int, height: Int): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height)
        return this
    }

    /**
     * Scales the image until one of the dimensions fits,
     * centers it, then crops the rest.
     *
     * @param width New width
     * @param height New height
     */
    fun scaleCropCenter(width: Int, height: Int): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/scale_crop/")
                .append(width)
                .append("x")
                .append(height)
                .append("/center")
        return this
    }

    /**
     * Flips the image.
     */
    fun flip(): CdnPathBuilder {
        sb.append("/-/flip")
        return this
    }

    /**
     * Adds a grayscale effect.
     */
    fun grayscale(): CdnPathBuilder {
        sb.append("/-/grayscale")
        return this
    }

    /**
     * Inverts colors.
     */
    fun invert(): CdnPathBuilder {
        sb.append("/-/invert")
        return this
    }

    /**
     * Horizontally mirror image.
     */
    fun mirror(): CdnPathBuilder {
        sb.append("/-/effect/mirror")
        return this
    }

    /**
     * Performs Gaussian blur on result image.
     */
    fun blur(): CdnPathBuilder {
        sb.append("/-/blur")
        return this
    }

    /**
     * Performs Gaussian blur on result image.
     *
     * @param strength Strength is standard deviation (aka blur radius) multiplied by ten. Strength
     * can be up to 5000. Default is 10.
     */
    fun blur(strength: Int): CdnPathBuilder {
        var strength = strength
        if (strength < 0 || strength > 5000) {
            strength = 10
        }
        sb.append("/-/blur/").append(strength)
        return this
    }

    /**
     * Performs sharpening on result image. This can be useful after scaling down.
     */
    fun sharp(): CdnPathBuilder {
        sb.append("/-/sharp")
        return this
    }

    /**
     * Performs sharpening on result image. This can be useful after scaling down.
     *
     * @param strength Strength can be from 0 to 20. Default is 5.
     */
    fun sharp(strength: Int): CdnPathBuilder {
        var strength = strength
        if (strength < 0 || strength > 20) {
            strength = 5
        }
        sb.append("/-/sharp/").append(strength)
        return this
    }

    /**
     * Reduces an image proportionally in order to fit it into given dimensions.
     *
     * @param width New width
     * @param height New height
     */
    fun preview(width: Int, height: Int): CdnPathBuilder {
        dimensionsGuard(width, height)
        sb.append("/-/preview/")
                .append(width)
                .append("x")
                .append(height)
        return this
    }

    /**
     * Turn an image to one of the following formats: FORMAT_JPEG or FORMAT_PNG.
     *
     * @param format [ImageFormat] either FORMAT_JPEG or FORMAT_PNG.
     */
    fun format(format: ImageFormat): CdnPathBuilder {
        sb.append("/-/format/").append(format.rawValue)
        return this
    }

    /**
     * Image quality affects size of image and loading speed. Has no effect on non-JPEG images, but does not force format to JPEG.
     *
     * @param quality
     * QUALITY_NORMAL – used by default. Fine in most cases.
     * QUALITY_BETTER – can be used on relatively small previews with lots of details. ≈125% file size compared to normal image.
     * QUALITY_BEST – useful if you're a photography god and you want to get perfect quality without paying attention to size. ≈170% file size.
     * QUALITY_LIGHTER – can be used on relatively large images to save traffic without significant quality loss. ≈80% file size.
     * QUALITY_LIGHTEST — useful for retina resolutions, when you don't wory about quality of each pixel. ≈50% file size.
     */
    fun quality(quality: ImageQuality): CdnPathBuilder {
        sb.append("/-/quality/").append(quality.rawValue)
        return this
    }

    /**
     * Returns the current CDN path as a string.
     *
     * Avoid using directly.
     * Instead, pass the configured builder to a URL factory.
     *
     * @return CDN path
     *
     * @see com.uploadcare.android.library.urls.Urls.cdn
     */
    fun build(): String {
        return sb.append("/").toString()
    }
}

enum class ImageFormat constructor(val rawValue: String) {
    FORMAT_JPEG("jpeg"), FORMAT_PNG("png")
}

enum class ImageQuality constructor(val rawValue: String) {
    QUALITY_NORMAL("normal"),
    QUALITY_BETTER("better"),
    QUALITY_BEST("best"),
    QUALITY_LIGHTER("lighter"),
    QUALITY_LIGHTEST("lightest")
}
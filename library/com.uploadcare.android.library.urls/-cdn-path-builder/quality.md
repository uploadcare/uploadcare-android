---
title: quality -
---
//[library](../../index.md)/[com.uploadcare.android.library.urls](../index.md)/[CdnPathBuilder](index.md)/[quality](quality.md)



# quality  
[androidJvm]  
Content  
fun [quality](quality.md)(quality: [ImageQuality](../-image-quality/index.md)): [CdnPathBuilder](index.md)  
More info  


Image quality affects size of image and loading speed. Has no effect on non-JPEG images, but does not force format to JPEG.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.urls/CdnPathBuilder/quality/#com.uploadcare.android.library.urls.ImageQuality/PointingToDeclaration/"></a>quality| <a name="com.uploadcare.android.library.urls/CdnPathBuilder/quality/#com.uploadcare.android.library.urls.ImageQuality/PointingToDeclaration/"></a><br><br>QUALITY_NORMAL – used by default. Fine in most cases. QUALITY_BETTER – can be used on relatively small previews with lots of details. ≈125% file size compared to normal image. QUALITY_BEST – useful if you're a photography god and you want to get perfect quality without paying attention to size. ≈170% file size. QUALITY_LIGHTER – can be used on relatively large images to save traffic without significant quality loss. ≈80% file size. QUALITY_LIGHTEST — useful for retina resolutions, when you don't wory about quality of each pixel. ≈50% file size.<br><br>
  
  




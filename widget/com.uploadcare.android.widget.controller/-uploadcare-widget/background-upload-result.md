---
title: backgroundUploadResult -
---
//[widget](../../index.md)/[com.uploadcare.android.widget.controller](../index.md)/[UploadcareWidget](index.md)/[backgroundUploadResult](background-upload-result.md)



# backgroundUploadResult  
[androidJvm]  
Content  
fun [backgroundUploadResult](background-upload-result.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), uuid: [UUID](https://developer.android.com/reference/kotlin/java/util/UUID.html)): [LiveData](https://developer.android.com/reference/kotlin/androidx/lifecycle/LiveData.html)<[UploadcareWidgetResult](../-uploadcare-widget-result/index.md)>  
More info  


Gets a {@link LiveData} of the {@link UploadcareWidgetResult} for a given background upload.



#### Return  


A {@link LiveData} of the {@link UploadcareWidgetResult} associated with {@code uuid}; note that this {@link UploadcareWidgetResult} may be {@code null} if {@code uuid} is not known to WorkManager.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/backgroundUploadResult/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/backgroundUploadResult/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a><ul><li>Context</li></ul>
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/backgroundUploadResult/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a>uuid| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/backgroundUploadResult/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a><ul><li>UUID of the background upload, can be get from {@link UploadcareWidgetResult}.</li></ul>
  
  




---
title: upload -
---
//[library](../../index.md)/[com.uploadcare.android.library.upload](../index.md)/[UrlUploader](index.md)/[upload](upload.md)



# upload  
[androidJvm]  
Content  
open override fun [upload](upload.md)(progressCallback: [ProgressCallback](../../com.uploadcare.android.library.callbacks/-progress-callback/index.md)?): [UploadcareFile](../../com.uploadcare.android.library.api/-uploadcare-file/index.md)  
More info  


Synchronously uploads the file from provided URL to Uploadcare.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/UrlUploader/upload/#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a>progressCallback| <a name="com.uploadcare.android.library.upload/UrlUploader/upload/#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a><br><br>, progress will be reported on the same thread upload started.<br><br>
  
  


[androidJvm]  
Content  
fun [upload](upload.md)(pollingInterval: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html), progressCallback: [ProgressCallback](../../com.uploadcare.android.library.callbacks/-progress-callback/index.md)? = null): [UploadcareFile](../../com.uploadcare.android.library.api/-uploadcare-file/index.md)  
More info  


Synchronously uploads the file to Uploadcare.



The calling thread will be busy until the upload is finished.



#### Return  


An Uploadcare file



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/UrlUploader/upload/#kotlin.Long#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a>pollingInterval| <a name="com.uploadcare.android.library.upload/UrlUploader/upload/#kotlin.Long#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a><br><br>Progress polling interval in ms, default is 500ms.<br><br>
| <a name="com.uploadcare.android.library.upload/UrlUploader/upload/#kotlin.Long#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a>progressCallback| <a name="com.uploadcare.android.library.upload/UrlUploader/upload/#kotlin.Long#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a><br><br>, progress will be reported on the same thread upload started.<br><br>
  
  




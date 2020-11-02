---
title: upload -
---
//[library](../../index.md)/[com.uploadcare.android.library.upload](../index.md)/[FileUploader](index.md)/[upload](upload.md)



# upload  
[androidJvm]  
Content  
open override fun [upload](upload.md)(progressCallback: [ProgressCallback](../../com.uploadcare.android.library.callbacks/-progress-callback/index.md)?): [UploadcareFile](../../com.uploadcare.android.library.api/-uploadcare-file/index.md)  
More info  


Synchronously uploads the file to Uploadcare.



The calling thread will be busy until the upload is finished.



#### Return  


An Uploadcare file



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/FileUploader/upload/#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a>progressCallback| <a name="com.uploadcare.android.library.upload/FileUploader/upload/#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a><br><br>, progress will be reported on the same thread upload started.<br><br>
  
  




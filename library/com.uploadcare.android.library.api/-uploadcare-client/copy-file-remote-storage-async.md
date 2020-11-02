---
title: copyFileRemoteStorageAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[copyFileRemoteStorageAsync](copy-file-remote-storage-async.md)



# copyFileRemoteStorageAsync  
[androidJvm]  
Content  
fun [copyFileRemoteStorageAsync](copy-file-remote-storage-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), source: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), target: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, makePublic: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, pattern: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, callback: [CopyFileCallback](../../com.uploadcare.android.library.callbacks/-copy-file-callback/index.md)? = null)  
More info  


Copy file to remote storage. Copy original files or their modified versions to a custom storage. Source files MAY either be stored or just uploaded and MUST NOT be deleted.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>callback  [CopyFileCallback](../../com.uploadcare.android.library.callbacks/-copy-file-callback/index.md) with either an UploadcareCopyFile response or a failure exception.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>Application context. @link android.content.Context<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>makePublic| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>MUST be either true or false. true to make copied files available via public links, false to     reverse the behavior.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>pattern| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>The parameter is used to specify file names Uploadcare passes to a custom storage. In case the     parameter is omitted, we use pattern of your custom storage. Use any combination of allowed     values.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>source| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>File Resource UUID or A CDN URL.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>target| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileRemoteStorageAsync/#android.content.Context#kotlin.String#kotlin.String?#kotlin.Boolean#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>Identifies a custom storage name related to your project. Implies you are copying a file to a     specified custom storage. Keep in mind you can have multiple storages associated with a single     S3 bucket.<br><br>
  
  




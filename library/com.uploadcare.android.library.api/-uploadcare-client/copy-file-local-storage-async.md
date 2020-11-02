---
title: copyFileLocalStorageAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[copyFileLocalStorageAsync](copy-file-local-storage-async.md)



# copyFileLocalStorageAsync  
[androidJvm]  
Content  
fun [copyFileLocalStorageAsync](copy-file-local-storage-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), source: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), store: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, callback: [CopyFileCallback](../../com.uploadcare.android.library.callbacks/-copy-file-callback/index.md)? = null)  
More info  


Copy file to local storage. Copy original files or their modified versions to default storage. Source files MAY either be stored or just uploaded and MUST NOT be deleted.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>callback  [CopyFileCallback](../../com.uploadcare.android.library.callbacks/-copy-file-callback/index.md) with either an UploadcareCopyFile response or a failure exception.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>Application context. @link android.content.Context<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>source| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>File Resource UUID or A CDN URL.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>store| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileLocalStorageAsync/#android.content.Context#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>The parameter only applies to the Uploadcare storage and MUST be either true or false.<br><br>
  
  




---
title: copyFileAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[copyFileAsync](copy-file-async.md)



# copyFileAsync  
[androidJvm]  
Content  
~~fun~~ [~~copyFileAsync~~](copy-file-async.md)~~(~~~~context~~~~:~~ [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)~~,~~ ~~source~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~storage~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? ~~= null~~~~,~~ ~~callback~~~~:~~ [CopyFileCallback](../../com.uploadcare.android.library.callbacks/-copy-file-callback/index.md)? ~~= null~~~~)~~  
More info  


## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>callback  [CopyFileCallback](../../com.uploadcare.android.library.callbacks/-copy-file-callback/index.md) with either an UploadcareCopyFile response or a failure exception.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>Application context. @link android.content.Context<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>source| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>File Resource UUID or A CDN URL.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a>storage| <a name="com.uploadcare.android.library.api/UploadcareClient/copyFileAsync/#android.content.Context#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.CopyFileCallback?/PointingToDeclaration/"></a><br><br>Target storage name. If {@code null} local file copy will be executed, else remote file copy will be executed.<br><br>
  
  




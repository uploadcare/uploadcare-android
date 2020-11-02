---
title: saveFileAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[saveFileAsync](save-file-async.md)



# saveFileAsync  
[androidJvm]  
Content  
fun [saveFileAsync](save-file-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), fileId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))  
More info  


Marks a file as saved Asynchronously.



This has to be done for all files you want to keep. Unsaved files are eventually purged.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String/PointingToDeclaration/"></a><br><br>Application context. [android.content.Context](https://developer.android.com/reference/kotlin/android/content/Context.html)<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String/PointingToDeclaration/"></a>fileId| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String/PointingToDeclaration/"></a><br><br>Resource UUID<br><br>
  
  


[androidJvm]  
Content  
fun [saveFileAsync](save-file-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), fileId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), callback: [RequestCallback](../../com.uploadcare.android.library.callbacks/-request-callback/index.md)? = null)  
More info  


Marks a file as saved Asynchronously.



This has to be done for all files you want to keep. Unsaved files are eventually purged.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String#com.uploadcare.android.library.callbacks.RequestCallback?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String#com.uploadcare.android.library.callbacks.RequestCallback?/PointingToDeclaration/"></a><br><br>callback  [RequestCallback](../../com.uploadcare.android.library.callbacks/-request-callback/index.md) with either an HTTP response or a failure exception.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String#com.uploadcare.android.library.callbacks.RequestCallback?/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String#com.uploadcare.android.library.callbacks.RequestCallback?/PointingToDeclaration/"></a><br><br>Application context. @link android.content.Context<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String#com.uploadcare.android.library.callbacks.RequestCallback?/PointingToDeclaration/"></a>fileId| <a name="com.uploadcare.android.library.api/UploadcareClient/saveFileAsync/#android.content.Context#kotlin.String#com.uploadcare.android.library.callbacks.RequestCallback?/PointingToDeclaration/"></a><br><br>Resource UUID<br><br>
  
  




---
title: asListAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[FilesQueryBuilder](index.md)/[asListAsync](as-list-async.md)



# asListAsync  
[androidJvm]  
Content  
fun [asListAsync](as-list-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), limit: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), next: [URI](https://developer.android.com/reference/kotlin/java/net/URI.html)?, callback: [UploadcareFilesCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-files-callback/index.md)?)  
More info  


Returns a limited amount of resources with specified offset Asynchronously.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a><br><br>[UploadcareFilesCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-files-callback/index.md).<br><br>
| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a><br><br>application context. @link android.content.Context<br><br>
| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a>limit| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a><br><br>amount of resources returned in callback.<br><br>
| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a>next| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#android.content.Context#kotlin.Int#java.net.URI?#com.uploadcare.android.library.callbacks.UploadcareFilesCallback?/PointingToDeclaration/"></a><br><br>amount of resources to skip. if null then no offset will be applied.<br><br>
  
  


[androidJvm]  
Content  
fun [asListAsync](as-list-async.md)(callback: [UploadcareAllFilesCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-all-files-callback/index.md)?)  
More info  


Iterates through all resources and returns a complete list Asynchronously.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#com.uploadcare.android.library.callbacks.UploadcareAllFilesCallback?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/FilesQueryBuilder/asListAsync/#com.uploadcare.android.library.callbacks.UploadcareAllFilesCallback?/PointingToDeclaration/"></a><br><br>[UploadcareAllFilesCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-all-files-callback/index.md).<br><br>
  
  




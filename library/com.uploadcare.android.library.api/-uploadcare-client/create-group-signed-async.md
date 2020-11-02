---
title: createGroupSignedAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[createGroupSignedAsync](create-group-signed-async.md)



# createGroupSignedAsync  
[androidJvm]  
Content  
fun [createGroupSignedAsync](create-group-signed-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), fileIds: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), expire: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), jsonpCallback: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null, callback: [UploadcareGroupCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-group-callback/index.md)? = null)  
More info  


Create files group from a set of files by using their UUIDs.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a><br><br>callback  [UploadcareGroupCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-group-callback/index.md) with either an UploadcareGroup response or a failure exception.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a>expire| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a><br><br>sets the time until your signature is valid. It is a Unix time.(ex 1454902434)<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a>fileIds| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a><br><br>That parameter defines a set of files you want to join in a group.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a>jsonpCallback| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a><br><br>Sets the name of your JSONP callback function.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a>signature| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSignedAsync/#android.content.Context#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?#com.uploadcare.android.library.callbacks.UploadcareGroupCallback?/PointingToDeclaration/"></a><br><br>is a string sent along with your upload request. It requires your Uploadcare     project secret key and hence should be crafted on your back end.<br><br>
  
  




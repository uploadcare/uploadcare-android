---
title: createGroupSigned -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[createGroupSigned](create-group-signed.md)



# createGroupSigned  
[androidJvm]  
Content  
fun [createGroupSigned](create-group-signed.md)(fileIds: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), expire: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), jsonpCallback: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null): [UploadcareGroup](../-uploadcare-group/index.md)  
More info  


Create files group from a set of files by using their UUIDs. Using Signed Uploads.



#### Return  


New created Group resource instance.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a>expire| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a><br><br>sets the time until your signature is valid. It is a Unix time.(ex 1454902434)<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a>fileIds| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a><br><br>That parameter defines a set of files you want to join in a group.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a>jsonpCallback| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a><br><br>Sets the name of your JSONP callback function.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a>signature| <a name="com.uploadcare.android.library.api/UploadcareClient/createGroupSigned/#kotlin.collections.List[kotlin.String]#kotlin.String#kotlin.String#kotlin.String?/PointingToDeclaration/"></a><br><br>is a string sent along with your upload request. It requires your Uploadcare     project secret key and hence should be crafted on your back end.<br><br>
  
  




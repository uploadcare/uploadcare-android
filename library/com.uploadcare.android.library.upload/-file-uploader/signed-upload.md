---
title: signedUpload -
---
//[library](../../index.md)/[com.uploadcare.android.library.upload](../index.md)/[FileUploader](index.md)/[signedUpload](signed-upload.md)



# signedUpload  
[androidJvm]  
Content  
fun [signedUpload](signed-upload.md)(signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), expire: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [FileUploader](index.md)  
More info  


Signed Upload - let you control who and when can upload files to a specified Uploadcare project.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/FileUploader/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a>expire| <a name="com.uploadcare.android.library.upload/FileUploader/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>sets the time until your signature is valid. It is a Unix time.(ex 1454902434)<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a>signature| <a name="com.uploadcare.android.library.upload/FileUploader/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>is a string sent along with your upload request. It requires your Uploadcare project secret key and hence should be crafted on your back end.<br><br>
  
  




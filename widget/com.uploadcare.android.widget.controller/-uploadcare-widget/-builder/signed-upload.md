---
title: signedUpload -
---
//[widget](../../../index.md)/[com.uploadcare.android.widget.controller](../../index.md)/[UploadcareWidget](../index.md)/[Builder](index.md)/[signedUpload](signed-upload.md)



# signedUpload  
[androidJvm]  
Content  
fun [signedUpload](signed-upload.md)(signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), expire: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [UploadcareWidget.Builder](index.md)  
More info  


Signed Upload.



Signed Upload will be only used if SocialNetwork.SOCIAL_NETWORK_CAMERA, SocialNetwork.SOCIAL_NETWORK_VIDEOCAM or SocialNetwork.SOCIAL_NETWORK_FILE is selected. Signed upload won't be used if Selected file is from external network like Instagram/Facebook etc. is selected.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Builder/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a>expire| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Builder/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>sets the time until your signature is valid. It is a Unix time.(ex 1454902434)<br><br>
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Builder/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a>signature| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Builder/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>is a string sent along with your upload request. It requires your Uploadcare project secret key and hence should be crafted on your back end.<br><br>
  
  




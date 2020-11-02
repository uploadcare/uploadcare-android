---
title: createWebhook -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[createWebhook](create-webhook.md)



# createWebhook  
[androidJvm]  
Content  
fun [createWebhook](create-webhook.md)(targetUrl: [URI](https://developer.android.com/reference/kotlin/java/net/URI.html), event: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), isActive: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true): [UploadcareWebhook](../-uploadcare-webhook/index.md)  
More info  


Create and subscribe to webhook.



#### Return  


New created webhook resource instance.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/createWebhook/#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>event| <a name="com.uploadcare.android.library.api/UploadcareClient/createWebhook/#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a><br><br>An event you subscribe to. Only "file.uploaded" event supported.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createWebhook/#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>isActive| <a name="com.uploadcare.android.library.api/UploadcareClient/createWebhook/#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a><br><br>Marks a subscription as either active or not.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/createWebhook/#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>targetUrl| <a name="com.uploadcare.android.library.api/UploadcareClient/createWebhook/#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a><br><br>A URL that is triggered by an event.<br><br>
  
  




---
title: updateWebhook -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[updateWebhook](update-webhook.md)



# updateWebhook  
[androidJvm]  
Content  
fun [updateWebhook](update-webhook.md)(webhookId: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), targetUrl: [URI](https://developer.android.com/reference/kotlin/java/net/URI.html), event: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), isActive: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true): [UploadcareWebhook](../-uploadcare-webhook/index.md)  
More info  


Update webhook attributes.



#### Return  


New webhook resource instance.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>event| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a><br><br>An event you subscribe to. Only "file.uploaded" event supported. If {@code null} then this field     won't be updated.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>isActive| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a><br><br>Marks a subscription as either active or not. Default value is {@code true}.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>targetUrl| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a><br><br>A URL that is triggered by an event. If {@code null} then this field won't be updated.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>webhookId| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhook/#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a><br><br>Webhook id. If {@code null} then this field won't be updated.<br><br>
  
  




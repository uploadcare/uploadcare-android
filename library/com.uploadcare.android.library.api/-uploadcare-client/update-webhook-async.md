---
title: updateWebhookAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[UploadcareClient](index.md)/[updateWebhookAsync](update-webhook-async.md)



# updateWebhookAsync  
[androidJvm]  
Content  
fun [updateWebhookAsync](update-webhook-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), webhookId: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), targetUrl: [URI](https://developer.android.com/reference/kotlin/java/net/URI.html), event: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), isActive: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, callback: [UploadcareWebhookCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-webhook-callback/index.md))  
More info  


Update webhook attributes Asynchronously.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a><br><br>callback  [UploadcareWebhookCallback](../../com.uploadcare.android.library.callbacks/-uploadcare-webhook-callback/index.md) with either a response with UploadcareWebhook or a failure exception.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a><br><br>Application context. [android.content.Context](https://developer.android.com/reference/kotlin/android/content/Context.html)<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a>event| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a><br><br>An event you subscribe to. Only "file.uploaded" event supported. If {@code null} then this field     won't be updated.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a>isActive| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a><br><br>Marks a subscription as either active or not. Default value is {@code true}.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a>targetUrl| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a><br><br>A URL that is triggered by an event. If {@code null} then this field won't be updated.<br><br>
| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a>webhookId| <a name="com.uploadcare.android.library.api/UploadcareClient/updateWebhookAsync/#android.content.Context#kotlin.Int#java.net.URI#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.UploadcareWebhookCallback/PointingToDeclaration/"></a><br><br>Webhook id. If {@code null} then this field won't be updated.<br><br>
  
  




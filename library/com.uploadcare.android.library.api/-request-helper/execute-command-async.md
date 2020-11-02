---
title: executeCommandAsync -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[RequestHelper](index.md)/[executeCommandAsync](execute-command-async.md)



# executeCommandAsync  
[androidJvm]  
Content  
fun [executeCommandAsync](execute-command-async.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), requestType: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), url: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), apiHeaders: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), callback: [RequestCallback](../../com.uploadcare.android.library.callbacks/-request-callback/index.md)? = null, requestBody: RequestBody? = null, requestBodyMD5: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null)  
More info  


Executes the request et the Uploadcare API and return the HTTP Response object.



The existence of this method(and it's return type) enables the end user to extend the functionality of the Uploadcare API client by creating a subclass of [com.uploadcare.android.library.api.UploadcareClient](../-uploadcare-client/index.md).



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>apiHeaders| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>TRUE if the default API headers should be set<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>callback| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>callback  [RequestCallback](../../com.uploadcare.android.library.callbacks/-request-callback/index.md)<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>application context. @link android.content.Context<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>requestBody| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>body of POST request, used only with request type REQUEST_POST.<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>requestType| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>request type (ex. "GET", "POST", "DELETE");<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>url| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommandAsync/#android.content.Context#kotlin.String#kotlin.String#kotlin.Boolean#com.uploadcare.android.library.callbacks.RequestCallback?#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>request url<br><br>
  
  




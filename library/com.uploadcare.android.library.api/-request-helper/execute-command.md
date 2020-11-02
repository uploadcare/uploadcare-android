---
title: executeCommand -
---
//[library](../../index.md)/[com.uploadcare.android.library.api](../index.md)/[RequestHelper](index.md)/[executeCommand](execute-command.md)



# executeCommand  
[androidJvm]  
Content  
fun [executeCommand](execute-command.md)(requestType: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), url: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), apiHeaders: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), requestBody: RequestBody? = null, requestBodyMD5: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null): Response  
More info  


Executes the request et the Uploadcare API and return the HTTP Response object.



The existence of this method(and it's return type) enables the end user to extend the functionality of the Uploadcare API client by creating a subclass of [com.uploadcare.android.library.api.UploadcareClient](../-uploadcare-client/index.md).



#### Return  


HTTP Response object



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>apiHeaders| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>TRUE if the default API headers should be set<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>requestBody| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>body of POST request, used only with request type REQUEST_POST.<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>requestType| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>request type (ex. "GET", "POST", "DELETE");<br><br>
| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a>url| <a name="com.uploadcare.android.library.api/RequestHelper/executeCommand/#kotlin.String#kotlin.String#kotlin.Boolean#okhttp3.RequestBody?#kotlin.String?/PointingToDeclaration/"></a><br><br>request url<br><br>
  
  




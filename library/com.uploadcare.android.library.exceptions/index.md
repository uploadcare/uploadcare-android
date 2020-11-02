---
title: com.uploadcare.android.library.exceptions -
---
//[library](../index.md)/[com.uploadcare.android.library.exceptions](index.md)



# Package com.uploadcare.android.library.exceptions  


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.exceptions/UploadcareApiException///PointingToDeclaration/"></a>[UploadcareApiException](-uploadcare-api-exception/index.md)| <a name="com.uploadcare.android.library.exceptions/UploadcareApiException///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open class [UploadcareApiException](-uploadcare-api-exception/index.md) : [RuntimeException](https://developer.android.com/reference/kotlin/java/lang/RuntimeException.html)  <br>More info  <br>A generic error of the uploadcare API.  <br><br><br>
| <a name="com.uploadcare.android.library.exceptions/UploadcareAuthenticationException///PointingToDeclaration/"></a>[UploadcareAuthenticationException](-uploadcare-authentication-exception/index.md)| <a name="com.uploadcare.android.library.exceptions/UploadcareAuthenticationException///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>class [UploadcareAuthenticationException](-uploadcare-authentication-exception/index.md)(**message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?) : [UploadcareApiException](-uploadcare-api-exception/index.md)  <br>More info  <br>An authentication error returned by the uploadcare API  <br><br><br>
| <a name="com.uploadcare.android.library.exceptions/UploadcareException///PointingToDeclaration/"></a>[UploadcareException](-uploadcare-exception/index.md)| <a name="com.uploadcare.android.library.exceptions/UploadcareException///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>data class [UploadcareException](-uploadcare-exception/index.md)(**message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)  <br><br><br>
| <a name="com.uploadcare.android.library.exceptions/UploadcareInvalidRequestException///PointingToDeclaration/"></a>[UploadcareInvalidRequestException](-uploadcare-invalid-request-exception/index.md)| <a name="com.uploadcare.android.library.exceptions/UploadcareInvalidRequestException///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>class [UploadcareInvalidRequestException](-uploadcare-invalid-request-exception/index.md)(**message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?) : [UploadcareApiException](-uploadcare-api-exception/index.md)  <br>More info  <br>Error produced in case the http request sent to the Uploadcare API was invalid.  <br><br><br>
| <a name="com.uploadcare.android.library.exceptions/UploadFailureException///PointingToDeclaration/"></a>[UploadFailureException](-upload-failure-exception/index.md)| <a name="com.uploadcare.android.library.exceptions/UploadFailureException///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>class [UploadFailureException](-upload-failure-exception/index.md) : [UploadcareApiException](-uploadcare-api-exception/index.md)  <br><br><br>


---
title: BaseListCallback -
---
//[library](../../index.md)/[com.uploadcare.android.library.callbacks](../index.md)/[BaseListCallback](index.md)



# BaseListCallback  
 [androidJvm] interface [BaseListCallback](index.md)<[T](index.md)> : [BaseCallback](../-base-callback/index.md)<[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[T](index.md)>>    


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open operator fun [equals](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [hashCode](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.uploadcare.android.library.callbacks/BaseCallback/onFailure/#com.uploadcare.android.library.exceptions.UploadcareApiException/PointingToDeclaration/"></a>[onFailure](../-base-callback/on-failure.md)| <a name="com.uploadcare.android.library.callbacks/BaseCallback/onFailure/#com.uploadcare.android.library.exceptions.UploadcareApiException/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>abstract fun [onFailure](../-base-callback/on-failure.md)(@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)()e: [UploadcareApiException](../../com.uploadcare.android.library.exceptions/-uploadcare-api-exception/index.md))  <br><br><br>
| <a name="com.uploadcare.android.library.callbacks/BaseCallback/onSuccess/#kotlin.collections.List[TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a>[onSuccess](index.md#%5Bcom.uploadcare.android.library.callbacks%2FBaseCallback%2FonSuccess%2F%23kotlin.collections.List%5BTypeParam%28bounds%3D%5Bkotlin.Any%3F%5D%29%5D%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)| <a name="com.uploadcare.android.library.callbacks/BaseCallback/onSuccess/#kotlin.collections.List[TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>abstract fun [onSuccess](index.md#%5Bcom.uploadcare.android.library.callbacks%2FBaseCallback%2FonSuccess%2F%23kotlin.collections.List%5BTypeParam%28bounds%3D%5Bkotlin.Any%3F%5D%29%5D%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)(@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)()result: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[T](index.md)>)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [toString](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.uploadcare.android.library.callbacks/UploadcareAllFilesCallback///PointingToDeclaration/"></a>[UploadcareAllFilesCallback](../-uploadcare-all-files-callback/index.md)
| <a name="com.uploadcare.android.library.callbacks/UploadcareAllGroupsCallback///PointingToDeclaration/"></a>[UploadcareAllGroupsCallback](../-uploadcare-all-groups-callback/index.md)
| <a name="com.uploadcare.android.library.callbacks/UploadFilesCallback///PointingToDeclaration/"></a>[UploadFilesCallback](../-upload-files-callback/index.md)
| <a name="com.uploadcare.android.library.callbacks/UploadcareWebhooksCallback///PointingToDeclaration/"></a>[UploadcareWebhooksCallback](../-uploadcare-webhooks-callback/index.md)


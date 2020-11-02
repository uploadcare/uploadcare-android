---
title: UploadcareWidget -
---
//[widget](../../index.md)/[com.uploadcare.android.widget.controller](../index.md)/[UploadcareWidget](index.md)



# UploadcareWidget  
 [androidJvm] class [UploadcareWidget](index.md)

UploadcareWidget class has multiple options for selecting files from Social networks, uses UploadcareClient internally and provides UploadcareWidgetResult with uploaded file info or error.



Replace variables in res/strings.xml file with you public/private keys from Uploadcare console.

   


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Builder///PointingToDeclaration/"></a>[Builder](-builder/index.md)| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Builder///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>class [Builder](-builder/index.md)  <br><br><br>
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Companion///PointingToDeclaration/"></a>[Companion](-companion/index.md)| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Companion///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>object [Companion](-companion/index.md) : [SingletonHolder](../../com.uploadcare.android.widget.utils/-singleton-holder/index.md)<[UploadcareWidget](index.md), [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)>   <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/backgroundUploadResult/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a>[backgroundUploadResult](background-upload-result.md)| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/backgroundUploadResult/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>fun [backgroundUploadResult](background-upload-result.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), uuid: [UUID](https://developer.android.com/reference/kotlin/java/util/UUID.html)): [LiveData](https://developer.android.com/reference/kotlin/androidx/lifecycle/LiveData.html)<[UploadcareWidgetResult](../-uploadcare-widget-result/index.md)>  <br>More info  <br>Gets a {@link LiveData} of the {@link UploadcareWidgetResult} for a given background upload.  <br><br><br>
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/cancelBackgroundUpload/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a>[cancelBackgroundUpload](cancel-background-upload.md)| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/cancelBackgroundUpload/#android.content.Context#java.util.UUID/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>fun [cancelBackgroundUpload](cancel-background-upload.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), uuid: [UUID](https://developer.android.com/reference/kotlin/java/util/UUID.html))  <br>More info  <br>Cancel background upload that is happening.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open operator fun [equals](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [hashCode](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/selectFile/#android.app.Activity/PointingToDeclaration/"></a>[selectFile](select-file.md)| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/selectFile/#android.app.Activity/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>fun [selectFile](select-file.md)(activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html)): [UploadcareWidget.Builder](-builder/index.md)  <br>fun [selectFile](select-file.md)(fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)): [UploadcareWidget.Builder](-builder/index.md)  <br>More info  <br>Select and upload file to Uploadcare.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [toString](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/uploadcareClient/#/PointingToDeclaration/"></a>[uploadcareClient](uploadcare-client.md)| <a name="com.uploadcare.android.widget.controller/UploadcareWidget/uploadcareClient/#/PointingToDeclaration/"></a> [androidJvm] val [uploadcareClient](uploadcare-client.md): UploadcareClient   <br>


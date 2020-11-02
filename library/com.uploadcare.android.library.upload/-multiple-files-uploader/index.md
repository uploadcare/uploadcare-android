---
title: MultipleFilesUploader -
---
//[library](../../index.md)/[com.uploadcare.android.library.upload](../index.md)/[MultipleFilesUploader](index.md)



# MultipleFilesUploader  
 [androidJvm] class [MultipleFilesUploader](index.md) : [MultipleUploader](../-multiple-uploader/index.md)

Uploadcare uploader for multiple files.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/MultipleFilesUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.collections.List[java.io.File]/PointingToDeclaration/"></a>[MultipleFilesUploader](-multiple-files-uploader.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/MultipleFilesUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.collections.List[java.io.File]/PointingToDeclaration/"></a> [androidJvm] fun [MultipleFilesUploader](-multiple-files-uploader.md)(client: [UploadcareClient](../../com.uploadcare.android.library.api/-uploadcare-client/index.md), files: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[File](https://developer.android.com/reference/kotlin/java/io/File.html)>)Creates a new uploader from a list of files on disk (not to be confused with a file resource from Uploadcare API).   <br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/MultipleFilesUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.collections.List[android.net.Uri]#android.content.Context/PointingToDeclaration/"></a>[MultipleFilesUploader](-multiple-files-uploader.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/MultipleFilesUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.collections.List[android.net.Uri]#android.content.Context/PointingToDeclaration/"></a> [androidJvm] fun [MultipleFilesUploader](-multiple-files-uploader.md)(client: [UploadcareClient](../../com.uploadcare.android.library.api/-uploadcare-client/index.md), uris: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html)>, context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html))Creates a new uploader from a list of [android.net.Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html) objects references.   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader.Companion///PointingToDeclaration/"></a>[Companion](-companion/index.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader.Companion///PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/cancel/#/PointingToDeclaration/"></a>[cancel](cancel.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/cancel/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open override fun [cancel](cancel.md)()  <br>More info  <br>Cancel upload of the files.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open operator fun [equals](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [hashCode](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/pause/#/PointingToDeclaration/"></a>[pause](pause.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/pause/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>fun [pause](pause.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Pause upload of the file.  <br><br><br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/resume/#/PointingToDeclaration/"></a>[resume](resume.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/resume/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>fun [resume](resume.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Resume upload of the file that was previously paused.  <br><br><br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a>[signedUpload](signed-upload.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/signedUpload/#kotlin.String#kotlin.String/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>fun [signedUpload](signed-upload.md)(signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), expire: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [MultipleFilesUploader](index.md)  <br>More info  <br>Signed Upload - let you control who and when can upload files to a specified Uploadcare project.  <br><br><br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/store/#kotlin.Boolean/PointingToDeclaration/"></a>[store](store.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/store/#kotlin.Boolean/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open override fun [store](store.md)(store: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [MultipleFilesUploader](index.md)  <br>More info  <br>Store the files upon uploading.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [toString](../../com.uploadcare.android.library.utils/-moshi-adapter/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F2103969333)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/upload/#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a>[upload](upload.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/upload/#com.uploadcare.android.library.callbacks.ProgressCallback?/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open override fun [upload](upload.md)(progressCallback: [ProgressCallback](../../com.uploadcare.android.library.callbacks/-progress-callback/index.md)?): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[UploadcareFile](../../com.uploadcare.android.library.api/-uploadcare-file/index.md)>  <br>More info  <br>Synchronously uploads the files to Uploadcare.  <br><br><br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/uploadAsync/#com.uploadcare.android.library.callbacks.UploadFilesCallback/PointingToDeclaration/"></a>[uploadAsync](upload-async.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/uploadAsync/#com.uploadcare.android.library.callbacks.UploadFilesCallback/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open override fun [uploadAsync](upload-async.md)(callback: [UploadFilesCallback](../../com.uploadcare.android.library.callbacks/-upload-files-callback/index.md))  <br>More info  <br>Asynchronously uploads the files to Uploadcare.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/isCanceled/#/PointingToDeclaration/"></a>[isCanceled](is-canceled.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/isCanceled/#/PointingToDeclaration/"></a> [androidJvm] var [isCanceled](is-canceled.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false   <br>
| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/isPaused/#/PointingToDeclaration/"></a>[isPaused](is-paused.md)| <a name="com.uploadcare.android.library.upload/MultipleFilesUploader/isPaused/#/PointingToDeclaration/"></a> [androidJvm] var [isPaused](is-paused.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false   <br>


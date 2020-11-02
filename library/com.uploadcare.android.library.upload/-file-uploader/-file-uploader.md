---
title: FileUploader -
---
//[library](../../index.md)/[com.uploadcare.android.library.upload](../index.md)/[FileUploader](index.md)/[FileUploader](-file-uploader.md)



# FileUploader  
[androidJvm]  
Content  
fun [FileUploader](-file-uploader.md)(client: [UploadcareClient](../../com.uploadcare.android.library.api/-uploadcare-client/index.md), file: [File](https://developer.android.com/reference/kotlin/java/io/File.html))  
More info  


Creates a new uploader from a file on disk (not to be confused with a file resource from Uploadcare API).



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.File/PointingToDeclaration/"></a>client| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.File/PointingToDeclaration/"></a><br><br>Uploadcare client<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.File/PointingToDeclaration/"></a>file| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.File/PointingToDeclaration/"></a><br><br>File on disk<br><br>
  
  


[androidJvm]  
Content  
fun [FileUploader](-file-uploader.md)(client: [UploadcareClient](../../com.uploadcare.android.library.api/-uploadcare-client/index.md), uri: [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html), context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html))  
More info  


Creates a new uploader from a [android.net.Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html) object reference. (not to be confused with a file resource from Uploadcare API).



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#android.net.Uri#android.content.Context/PointingToDeclaration/"></a>client| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#android.net.Uri#android.content.Context/PointingToDeclaration/"></a><br><br>Uploadcare client<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#android.net.Uri#android.content.Context/PointingToDeclaration/"></a>context| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#android.net.Uri#android.content.Context/PointingToDeclaration/"></a><br><br>Application context [android.content.Context](https://developer.android.com/reference/kotlin/android/content/Context.html).<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#android.net.Uri#android.content.Context/PointingToDeclaration/"></a>uri| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#android.net.Uri#android.content.Context/PointingToDeclaration/"></a><br><br>Object reference [android.net.Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html).<br><br>
  
  


[androidJvm]  
Content  
fun [FileUploader](-file-uploader.md)(client: [UploadcareClient](../../com.uploadcare.android.library.api/-uploadcare-client/index.md), stream: [InputStream](https://developer.android.com/reference/kotlin/java/io/InputStream.html), filename: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = DEFAULT_FILE_NAME)  
More info  


Creates a new uploader from InputStream.



InputStream data upload only supported for data less than 10485760 bytes, multipart upload is not supported for InputStream, because we don't know size in advance, consider using different method or Uploader class for such cases.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.InputStream#kotlin.String/PointingToDeclaration/"></a>client| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.InputStream#kotlin.String/PointingToDeclaration/"></a><br><br>Uploadcare client<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.InputStream#kotlin.String/PointingToDeclaration/"></a>filename| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.InputStream#kotlin.String/PointingToDeclaration/"></a><br><br>Original filename<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.InputStream#kotlin.String/PointingToDeclaration/"></a>stream| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#java.io.InputStream#kotlin.String/PointingToDeclaration/"></a><br><br>InputStream<br><br>
  
  


[androidJvm]  
Content  
fun [FileUploader](-file-uploader.md)(client: [UploadcareClient](../../com.uploadcare.android.library.api/-uploadcare-client/index.md), bytes: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html), filename: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = DEFAULT_FILE_NAME)  
More info  


Creates a new uploader from binary data.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.ByteArray#kotlin.String/PointingToDeclaration/"></a>bytes| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.ByteArray#kotlin.String/PointingToDeclaration/"></a><br><br>File contents as binary data<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.ByteArray#kotlin.String/PointingToDeclaration/"></a>client| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.ByteArray#kotlin.String/PointingToDeclaration/"></a><br><br>Uploadcare client<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.ByteArray#kotlin.String/PointingToDeclaration/"></a>filename| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.ByteArray#kotlin.String/PointingToDeclaration/"></a><br><br>Original filename<br><br>
  
  


[androidJvm]  
Content  
fun [FileUploader](-file-uploader.md)(client: [UploadcareClient](../../com.uploadcare.android.library.api/-uploadcare-client/index.md), content: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), filename: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = DEFAULT_FILE_NAME)  
More info  


Creates a new uploader from binary data.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.String#kotlin.String/PointingToDeclaration/"></a>client| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>Uploadcare client<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.String#kotlin.String/PointingToDeclaration/"></a>content| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>Contents data as String object.<br><br>
| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.String#kotlin.String/PointingToDeclaration/"></a>filename| <a name="com.uploadcare.android.library.upload/FileUploader/FileUploader/#com.uploadcare.android.library.api.UploadcareClient#kotlin.String#kotlin.String/PointingToDeclaration/"></a><br><br>Original filename<br><br>
  
  




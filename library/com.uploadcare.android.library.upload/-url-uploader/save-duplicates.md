---
title: saveDuplicates -
---
//[library](../../index.md)/[com.uploadcare.android.library.upload](../index.md)/[UrlUploader](index.md)/[saveDuplicates](save-duplicates.md)



# saveDuplicates  
[androidJvm]  
Content  
fun [saveDuplicates](save-duplicates.md)(saveDuplicates: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [UrlUploader](index.md)  
More info  


Save duplicates upon file uploading.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/UrlUploader/saveDuplicates/#kotlin.Boolean/PointingToDeclaration/"></a>saveDuplicates| <a name="com.uploadcare.android.library.upload/UrlUploader/saveDuplicates/#kotlin.Boolean/PointingToDeclaration/"></a><br><br>Provides the save/update URL behavior. The parameter can be used if you believe a     source_url will be used more than once. If you don’t explicitly define "saveDuplicates",     it is by default set to the value of "checkDuplicates".<br><br>
  
  


[androidJvm]  
Content  
fun [saveDuplicates](save-duplicates.md)(filename: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [UrlUploader](index.md)  
More info  


Sets the name for a file uploaded from URL. If not defined, the filename is obtained from either response headers or a source URL.



## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.library.upload/UrlUploader/saveDuplicates/#kotlin.String/PointingToDeclaration/"></a>filename| <a name="com.uploadcare.android.library.upload/UrlUploader/saveDuplicates/#kotlin.String/PointingToDeclaration/"></a><br><br>name for a file uploaded from URL.<br><br>
  
  




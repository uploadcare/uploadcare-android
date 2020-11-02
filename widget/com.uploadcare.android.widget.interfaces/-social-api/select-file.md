---
title: selectFile -
---
//[widget](../../index.md)/[com.uploadcare.android.widget.interfaces](../index.md)/[SocialApi](index.md)/[selectFile](select-file.md)



# selectFile  
[androidJvm]  
Content  
@FormUrlEncoded()  
@POST(value = "{done}")  
  
abstract fun [selectFile](select-file.md)(@Header(value = "Cookie")authCookie: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = "done")done: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Field(value = "file")fileUrl: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): Call<[SelectedFile](../../com.uploadcare.android.widget.data/-selected-file/index.md)>  




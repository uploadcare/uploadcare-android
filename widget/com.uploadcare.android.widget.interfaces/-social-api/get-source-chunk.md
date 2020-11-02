---
title: getSourceChunk -
---
//[widget](../../index.md)/[com.uploadcare.android.widget.interfaces](../index.md)/[SocialApi](index.md)/[getSourceChunk](get-source-chunk.md)



# getSourceChunk  
[androidJvm]  
Content  
@GET(value = "{base}/{chunk}/{offset}")  
  
abstract fun [getSourceChunk](get-source-chunk.md)(@Header(value = "Cookie")authCookie: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = "base")sourceBase: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = "chunk")chunk: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), @Path(value = "offset")loadMore: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): Call<[ChunkResponse](../../com.uploadcare.android.widget.data/-chunk-response/index.md)>  




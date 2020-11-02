---
title: SingletonHolder -
---
//[widget](../../index.md)/[com.uploadcare.android.widget.utils](../index.md)/[SingletonHolder](index.md)



# SingletonHolder  
 [androidJvm] open class [SingletonHolder](index.md)<out [T](index.md), in [A](index.md)>(**creator**: ([A](index.md)) -> [T](index.md))

Reusable SingletonHolder implementation. Double-checked locking algorithm.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.widget.utils/SingletonHolder/SingletonHolder/#kotlin.Function1[TypeParam(bounds=[kotlin.Any?]),TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a>[SingletonHolder](-singleton-holder.md)| <a name="com.uploadcare.android.widget.utils/SingletonHolder/SingletonHolder/#kotlin.Function1[TypeParam(bounds=[kotlin.Any?]),TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a> [androidJvm] fun <out [T](index.md), in [A](index.md)> [SingletonHolder](-singleton-holder.md)(creator: ([A](index.md)) -> [T](index.md))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open operator fun [equals](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.uploadcare.android.widget.utils/SingletonHolder/getInstance/#/PointingToDeclaration/"></a>[getInstance](get-instance.md)| <a name="com.uploadcare.android.widget.utils/SingletonHolder/getInstance/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [getInstance](get-instance.md)(): [T](index.md)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [hashCode](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[androidJvm]  <br>Content  <br>open fun [toString](../../com.uploadcare.android.widget.worker/-uploadcare-work-manager-initializer/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F814613827)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.uploadcare.android.widget.utils/SingletonHolder/creator/#/PointingToDeclaration/"></a>[creator](creator.md)| <a name="com.uploadcare.android.widget.utils/SingletonHolder/creator/#/PointingToDeclaration/"></a> [androidJvm] var [creator](creator.md): ([A](index.md)) -> [T](index.md)?   <br>
| <a name="com.uploadcare.android.widget.utils/SingletonHolder/instance/#/PointingToDeclaration/"></a>[instance](instance.md)| <a name="com.uploadcare.android.widget.utils/SingletonHolder/instance/#/PointingToDeclaration/"></a> [androidJvm] @[Volatile](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-volatile/index.html)()  <br>  <br>var [instance](instance.md): [T](index.md)? = null   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.uploadcare.android.widget.controller/UploadcareWidget.Companion///PointingToDeclaration/"></a>[UploadcareWidget](../../com.uploadcare.android.widget.controller/-uploadcare-widget/-companion/index.md)


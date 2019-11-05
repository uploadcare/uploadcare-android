uploadcare-android-widget
===============

This is an Android widget for Uploadcare.

Supported features:

- Upload image/video/other files from Social networks/camera/local files to Uploadcare.
- Handle social networks authorization and file selection for you.
- Custom appearance of the widget with ability to customize styles.
- Includes Uploadcare Android library for direct access to Uploadcare API's.

[Documentation](http://uploadcare.github.io/uploadcare-android/widget/index.html)

## jCenter

Latest stable version is available from jCenter.

To include it in your Android project, add this to the gradle.build file:

```
implementation 'com.uploadcare.android.widget:uploadcare-android-widget:2.1.0'

```

## Examples

### Basic API Usage

Place your Uploadcare public/private keys into ../res/strings.xml file (example below):

```xml
<resources>
    <!--Replace with your public/private keys to use UploadcareWidget-->
    <string name="uploadcare_public_key" translatable="false">place_uploadcare_public_key_here</string>
    <string name="uploadcare_private_key" translatable="false">place_uploadcare_private_key_here</string>
</resources>
```

Select and upload file to Uploadcare from any available social network/camera/local file from Activity/Fragment.

Java
```java
// Launch UploadcareWidget
Context context = ...// Context
Fragment fragment = this; //or Activity activity = this;
boolean storeUponUpload = true;
UploadcareWidget.getInstance(context).selectFile(fragment, storeUponUpload)

// Handle result
@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    UploadcareWidgetResult result = UploadcareWidgetResult.fromIntent(data);
    if(result!=null){
        //handle result.
    }
}
```
Kotlin
```kotlin
// Launch UploadcareWidget
val context = ...// Context
val fragment = this //or val activity = this;
val storeUponUpload = true
UploadcareWidget.getInstance(context).selectFile(fragment, storeUponUpload)

// Handle result
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    val result = UploadcareWidgetResult.fromIntent(data)
    result?.let {
        //handle result.
    }
}
```

Select and upload Video file to Uploadcare from Facebook network.

Java
```java
// Launch UploadcareWidget
Context context = ...// Context
Fragment fragment = this; //or Activity activity = this;
boolean storeUponUpload = true;
UploadcareWidget.getInstance(context).selectFileFrom(
                fragment,
                SocialNetwork.SOCIAL_NETWORK_FACEBOOK,
                FileType.video,
                storeUponUpload);

// Handle result
@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    UploadcareWidgetResult result = UploadcareWidgetResult.fromIntent(data);
    if(result!=null){
        //handle result.
    }
}
```
Kotlin
```kotlin
// Launch UploadcareWidget
val context = ...// Context
val fragment = this //or val activity = this;
val storeUponUpload = true
UploadcareWidget.getInstance(context).selectFileFrom(
                fragment,
                SocialNetwork.SOCIAL_NETWORK_FACEBOOK,
                FileType.video,
                storeUponUpload)

// Handle result
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    val result = UploadcareWidgetResult.fromIntent(data)
    result?.let {
        //handle result.
    }
}
```

See documentation for details:

* [UploadcareWidget](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/widget/com.uploadcare.android.widget.controller/-uploadcare-widget/index.html)
* [UploadcareWidgetResult](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/widget/com.uploadcare.android.widget.controller/-uploadcare-widget-result/index.html)

Custom appearance of the widget with custom style.

Paste in your /res/values/styles.xml
```xml
<style name="CustomUploadCareStyle" parent="UploadcareStyle">
    <item name="uploadcareColorAccent">#FF1744</item>
    <item name="uploadcareColorPrimary">#4CAF50</item>
    <item name="uploadcareColorPrimaryDark">#388E3C</item>
</style>
```

Set UploadcareWidget to use your custom style.

Java
```java
UploadcareWidget.getInstance(context).setStyle(R.style.CustomUploadCareStyle);
```

Kotlin
```kotlin
UploadcareWidget.getInstance(context).style = R.style.CustomUploadCareStyle
```

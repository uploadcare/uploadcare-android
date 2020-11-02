uploadcare-android-widget
===============

This is an Android widget for Uploadcare.

Supported features:

- Upload image/video/other files from Social networks/camera/local files to Uploadcare.
- Handle social networks authorization and file selection for you.
- Custom appearance of the widget with ability to customize styles.
- Includes Uploadcare Android library for direct access to Uploadcare API's.
- Upload selected file/files in Background (using WorkManager internally), report result as LiveData.

[Documentation](http://uploadcare.github.io/uploadcare-android/widget/index.html)

## jCenter

Latest stable version is available from jCenter.

To include it in your Android project, add this to the gradle.build file:

```
implementation 'com.uploadcare.android.widget:uploadcare-android-widget:3.0.1'

```

## Examples

### Basic API Usage

Place your Uploadcare public/private keys into ../res/strings.xml file (example below):

```xml
<resources>
    <!--Replace with your public/private keys to use UploadcareWidget. Private key is optional, required only if you use Rest API features.-->
    <string name="uploadcare_public_key" translatable="false">place_uploadcare_public_key_here</string>
    <string name="uploadcare_private_key" translatable="false">place_uploadcare_private_key_here</string>
</resources>
```

Select and upload file to Uploadcare from any available social network/camera/local file from Activity/Fragment.

Java
```java
// Launch UploadcareWidget
Fragment fragment = this; //or Activity activity = this;
UploadcareWidget.getInstance()
                .selectFile(fragment)
                //set other parameters for upload
                .launch();

// Handle result
@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    UploadcareWidgetResult result = UploadcareWidgetResult.fromIntent(data);
    if(result != null){
        //handle result.
    }
}
```
Kotlin
```kotlin
// Launch UploadcareWidget
val fragment = this //or val activity = this;
UploadcareWidget.getInstance()
                .selectFile(fragment)
                //set other parameters for upload
                .launch()

// Handle result
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    val result = UploadcareWidgetResult.fromIntent(data)
    result?.let {
        //handle result.
    }
}
```

Select and upload Video file to Uploadcare from Facebook network. Cancelable and showing progress UI.

Java
```java
// Launch UploadcareWidget
Fragment fragment = this; //or Activity activity = this;
UploadcareWidget.getInstance()
                .selectFile(fragment)
                .from(SocialNetwork.SOCIAL_NETWORK_FACEBOOK)
                .fileType(FileType.video)
                .cancelable(true) // Allows user to cancel upload.
                .showProgress(true) // Shows progress UI so user can see upload progress.
                //set other parameters for upload
                .launch();

// Handle result
@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    UploadcareWidgetResult result = UploadcareWidgetResult.fromIntent(data);
    if(result != null){
        //handle result.
    }
}
```
Kotlin
```kotlin
// Launch UploadcareWidget
val fragment = this //or val activity = this;
UploadcareWidget.getInstance()
                .selectFile(fragment)
                .from(SocialNetwork.SOCIAL_NETWORK_FACEBOOK)
                .fileType(FileType.video)
                .cancelable(true) // Allows user to cancel upload.
                .showProgress(true) // Shows progress UI so user can see upload progress.
                //set other parameters for upload
                .launch()

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

* [UploadcareWidget](http://uploadcare.github.io/uploadcare-android/widget/com.uploadcare.android.widget.controller/-uploadcare-widget/index.html)
* [UploadcareWidgetResult](http://uploadcare.github.io/uploadcare-android/widget/com.uploadcare.android.widget.controller/-uploadcare-widget-result/index.html)

Custom appearance of the widget with custom style. We only provide ability to set colors for Regular (Day) mode, Dark Mode will use default dark style.

Paste in your /res/values/styles.xml
```xml
<style name="CustomUploadCareStyle" parent="UploadcareStyle">
    <item name="uploadcareColorSecondary">#FF1744</item>
    <item name="uploadcareColorPrimary">#4CAF50</item>
    <item name="uploadcareColorPrimaryVariant">#388E3C</item>
</style>
```

Set UploadcareWidget to use your custom style.

Java
```java
UploadcareWidget.getInstance()
                .selectFile(fragment) //or Activity
                .style(R.style.CustomUploadCareStyle);
                //...
                .launch()
```

Kotlin
```kotlin
UploadcareWidget.getInstance()
                .selectFile(fragment) //or Activity
                .style(R.style.CustomUploadCareStyle)
                //...
                .launch()
```

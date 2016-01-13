uploadcare-android-widget
===============

This is an Android widget for Uploadcare.

Supported features:

- Upload image/video/other files from Social networks/camera/local files to Uploadcare.
- Handle social networks authorization and file selection for you.
- Custom appearance of the widget with ability to customize styles.
- Includes Uploadcare Android library for direct access to Uploadcare API's.

[Documentation](http://uploadcare.github.io/uploadcare-android/index.html)

## jCenter

Latest stable version is available from jCenter.

To include it in your Android project, add this to the gradle.build file:

```
compile 'com.uploadcare.android.widget:uploadcare-android-widget:1.0.5'

```

## Examples

### Basic API Usage

Select and upload file to Uploadcare from any available social network/camera/local file.
```java
UploadcareWidget.getInstance().init("demopublickey", "demoprivatekey");
boolean storeUponUpload = true;
UploadcareWidget.getInstance().selectFile(context, storeUponUpload, new UploadcareFileCallback() {
                    @Override
                    public void onFailure(UploadcareApiException e) {
                        //handle errors.
                    }

                    @Override
                    public void onSuccess(UploadcareFile file) {
                        //successfully uploaded file to Uploadcare.
                    }
                });
```

Select and upload Video file to Uploadcare from Facebook network.
```java
UploadcareWidget.getInstance().init("demopublickey", "demoprivatekey");
boolean storeUponUpload = true;
UploadcareWidget.getInstance().selectFileFrom(context, UploadcareWidget.SOCIAL_NETWORK_FACEBOOK, UploadcareWidget.FILE_TYPE_VIDEO, storeUponUpload, new UploadcareFileCallback() {
                    @Override
                    public void onFailure(UploadcareApiException e) {
                        //handle errors.
                    }

                    @Override
                    public void onSuccess(UploadcareFile file) {
                        //successfully uploaded file to Uploadcare.
                    }
                });
```

See documentation for details:

* [UploadcareWidget](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/widget/controller/UploadcareWidget.html)

Custom appearance of the widget with custom style.

Paste in your /res/values/styles.xml
```xml
<style name="CustomUploadCareStyle" parent="UploadcareStyle">
    <item name="uploadcareColorAccent">#FF1744</item>
    <item name="uploadcareColorPrimary">#4CAF50</item>
    <item name="uploadcareColorPrimaryDark">#388E3C</item>
</style>
```

When initialize UploadcareWidget use your custom style.
```java
UploadcareWidget.getInstance().init("demopublickey", "demoprivatekey", R.style.CustomUploadCareStyle);
```
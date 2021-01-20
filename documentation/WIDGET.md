# Widget - Documentation

### Basic Usage

##### Place your Uploadcare public/private keys into ../res/strings.xml file (example below):

```xml
<resources>
    <!--Replace with your public/private keys to use UploadcareWidget. Private key is optional, required only if you use Rest API features.-->
    <string name="uploadcare_public_key" translatable="false">place_uploadcare_public_key_here</string>
    <string name="uploadcare_private_key" translatable="false">place_uploadcare_private_key_here</string>
</resources>
```

##### Select and upload file to Uploadcare from any available social network/camera/local file from Activity/Fragment.

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

##### Select and upload Video file to Uploadcare from Facebook network. Cancelable and showing progress UI.

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

##### Custom widget appearance and style. We provide two color schemes, Regular (Day) mode, and Dark Mode.

Paste in your /res/values/styles.xml
```xml
<style name="CustomUploadCareStyle" parent="UploadcareStyle">
    <item name="uploadcareColorSecondary">#FF1744</item>
    <item name="uploadcareColorPrimary">#4CAF50</item>
    <item name="uploadcareColorPrimaryVariant">#388E3C</item>
</style>
```

##### Set UploadcareWidget to use your custom style.

Kotlin
```kotlin
UploadcareWidget.getInstance()
                .selectFile(fragment) //or Activity
                .style(R.style.CustomUploadCareStyle)
                //...
                .launch()
```
Java
```java
UploadcareWidget.getInstance()
                .selectFile(fragment) //or Activity
                .style(R.style.CustomUploadCareStyle);
                //...
                .launch()
```
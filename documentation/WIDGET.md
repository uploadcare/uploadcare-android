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

#### If your app targets Android 14 (API level 34) or higher you must specify a foreground service type for all long-running workers.

Declare your worker's foreground service type with `dataSync` foreground service type in your app's manifest to support our worker:

```xml
<service
    android:name="androidx.work.impl.foreground.SystemForegroundService"
    android:foregroundServiceType="dataSync|your_type"
    tools:node="replace" />
```

##### Select and upload file to Uploadcare from any available social network/camera/local file from Activity/Fragment.

Kotlin
```kotlin
// Register Activity Result Launcher
val uploadcareLauncher = registerForActivityResult(UploadcareActivityResultContract) { result ->
    result?.let {
        //handle result.
    }
}

// Launch UploadcareWidget
val params = UploadcareWidgetParams() // set parameters for upload
uploadcareLauncher.launch(params)
```
Java
```java
// Register Activity Result Launcher
ActivityResultLauncher<UploadcareWidgetParams> uploadcareLauncher = registerForActivityResult(UploadcareActivityResultContract.INSTANCE, result -> {
    if (result != null) {
        // handle result.
    }
});

// Launch UploadcareWidget
UploadcareWidgetParams params = new UploadcareWidgetParams(); // set parameters for upload
uploadcareLauncher.launch(params);
```

##### Select and upload Video from Facebook with a progress bar and an option to cancel upload.

Kotlin
```kotlin
// Register Activity Result Launcher
val uploadcareLauncher = registerForActivityResult(UploadcareActivityResultContract) { result ->
    result?.let {
        //handle result.
    }
}

// Launch UploadcareWidget
val params = UploadcareWidgetParams(
    network = SocialNetwork.SOCIAL_NETWORK_FACEBOOK,
    fileType = FileType.video,
    cancelable = true,
    showProgress = true,
) // set parameters for upload
uploadcareLauncher.launch(params)
```
Java
```java
// Register Activity Result Launcher
ActivityResultLauncher<UploadcareWidgetParams> uploadcareLauncher = registerForActivityResult(UploadcareActivityResultContract.INSTANCE, result -> {
    if (result != null) {
        // handle result.
    }
});

// Launch UploadcareWidget
UploadcareWidgetParams params = new UploadcareWidgetParams();
params.setNetwork(SocialNetwork.SOCIAL_NETWORK_FACEBOOK); // set parameters for upload
params.setFileType(FileType.video);
params.setCancelable(true);
params.setShowProgress(true);
uploadcareLauncher.launch(params);
```

##### Custom widget appearance and style. Two color scheme presets: Regular (Day), and Dark modes.

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
val params = UploadcareWidgetParams(
    style = R.style.CustomUploadCareStyle,
    // ...
)
uploadcareLauncher.launch(params)
```
Java
```java
UploadcareWidgetParams params = new UploadcareWidgetParams();
params.setStyle(R.style.CustomUploadCareStyle);
// ...
uploadcareLauncher.launch(params);
```

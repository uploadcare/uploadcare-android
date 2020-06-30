uploadcare-android-library
===============

[![Build Status](https://travis-ci.org/uploadcare/uploadcare-android.png?branch=master)](https://travis-ci.org/uploadcare/uploadcare-android)

This is an Android library for Uploadcare.

Supported features:

- Complete file, group and project API v0.6
- Paginated resources fetching.
- CDN path builder.
- File uploads from file, byte array, Uri, and URL.
- All operations available in synchronous and asynchronous modes.

[Documentation](http://uploadcare.github.io/uploadcare-android/library/index.html)

## jCenter

Latest stable version is available from jCenter.

To include it in your Android project, add this to the gradle.build file:

```
implementation 'com.uploadcare.android.library:uploadcare-android:2.2.0'

```

## Examples

### Basic API Usage

Asynchronous fetch all files.

Java
```java
UploadcareClient client = new UploadcareClient("publickey", "secretkey");
client.getFiles().asListAsync(new UploadcareAllFilesCallback() {
    @Override
    public void onFailure(@NotNull UploadcareApiException e) {
        //handle errors.
    }

    @Override
    public void onSuccess(@NonNull List<UploadcareFile> result) {
        //successfully fetched list of all UploadcareFile files.
    }
});
```
Kotlin
```kotlin
val client = UploadcareClient("publickey", "secretkey")
client.getFiles().asListAsync(object : UploadcareAllFilesCallback {
    override fun onFailure(e: UploadcareApiException) {
        //handle errors.
    }

    override fun onSuccess(result: List<UploadcareFile>) {
        //successfully fetched list of all UploadcareFile files.
    }
})
```

Synchronous fetch all files.

Java
```java
UploadcareClient client = new UploadcareClient("publickey", "secretkey");
Iterable<UploadcareFile> files = client.getFiles().asIterable();
for (UploadcareFile file : files) {
    System.out.println(file.toString());
}
```
Kotlin
```kotlin
val client = UploadcareClient("publickey", "secretkey")
val files = client.getFiles().asList()
for(file in files){
    System.out.println(file)
}
```

See documentation for details:

* [UploadcareClient](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.api/-uploadcare-client/index.html)
* [UploadcareFile](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.api/-uploadcare-file/index.html)
* [Project](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.api/-project/index.html)

Asynchronous fetch all groups.

Java
```java
UploadcareClient client = new UploadcareClient("publickey", "secretkey");
client.getGroups().asListAsync(new UploadcareAllGroupsCallback() {
    @Override
    public void onFailure(@NotNull UploadcareApiException e) {
        //handle errors.
    }

    @Override
    public void onSuccess(@NonNull List<UploadcareGroup> result) {
        //successfully fetched list of all UploadcareGroup groups.
    }
});
```
Kotlin
```kotlin
val client = UploadcareClient("publickey", "secretkey")
client.getGroups().asListAsync(object : UploadcareAllGroupsCallback {
    override fun onFailure(e: UploadcareApiException) {
        //handle errors.
    }

    override fun onSuccess(result: List<UploadcareGroup>) {
        //successfully fetched list of all UploadcareGroup groups.
    }
})
```

Synchronous fetch all groups.

Java
```java
UploadcareClient client = new UploadcareClient("publickey", "secretkey");
Iterable<UploadcareGroup> groups = client.getGroups().asIterable();
for (UploadcareGroup group : groups) {
    System.out.println(group.toString());
}
```
Kotlin
```kotlin
val client = UploadcareClient("publickey", "secretkey")
val groups = client.getGroups().asIterable()
for (group in groups) {
    println(group)
}
```

See documentation for details:

* [UploadcareClient](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.api/-uploadcare-client/index.html)
* [UploadcareGroup](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.api/-uploadcare-group/index.html)

### Building CDN URLs

Java
```java
UploadcareFile file = client.getFile("85b5644f-e692-4855-9db0-8c5a83096e25");
CdnPathBuilder builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale();
URI url = Urls.cdn(builder);
```
Kotlin
```kotlin
val file = client.getFile("85b5644f-e692-4855-9db0-8c5a83096e25")
val builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale()
val url = Urls.cdn(builder)
```

See documentation for details:

* [CdnPathBuilder](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.urls/-cdn-path-builder/index.html)
* [Urls](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.urls/-urls/index.html)

### File uploads

Asynchronous upload file from Uri.

Java
```java
UploadcareClient client = UploadcareClient.demoClient();
Context context = getApplicationContext();
Uri fileUri = ...//resource representing file.
Uploader uploader = new FileUploader(client, fileUri, context)
                .store(true);
        uploader.uploadAsync(new UploadcareFileCallback() {
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
Kotlin
```kotlin
val client = UploadcareClient.demoClient()
val context = ...// Context
val fileUri = ...//resource representing file.
val uploader = FileUploader(client, fileUri, context).store(true)
uploader.uploadAsync(object : UploadcareFileCallback {
    override fun onFailure(e: UploadcareApiException) {
        //handle errors.
    }

    override fun onSuccess(result: UploadcareFile) {
        //successfully uploaded file to Uploadcare.
    }
})
```

Synchronous upload file from Uri.

Java
```java
UploadcareClient client = UploadcareClient.demoClient();
Context context = getApplicationContext();
Uri fileUri = ...//resource representing file.
Uploader uploader = new FileUploader(client, fileUri, context)
                .store(true);
try {
    UploadcareFile file = uploader.upload();
    //successfully uploaded file to Uploadcare.
} catch (UploadFailureException e) {
    //handle errors.
}
```
Kotlin
```kotlin
val client = UploadcareClient.demoClient()
val context = ...// Context
val fileUri = ...//resource representing file.
val uploader = FileUploader(client, fileUri, context).store(true)
try {
    val file = uploader.upload()
    //successfully uploaded file to Uploadcare.
} catch (e: UploadFailureException) {
    //handle errors.
}
```

See documentation for details:

* [FileUploader](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.upload/-file-uploader/index.html)
* [MultipleFilesUploader](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.upload/-multiple-files-uploader/index.html)
* [UrlUploader](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.upload/-url-uploader/index.html)
* [MultipleUrlsUploader](http://uploadcare.github.io/uploadcare-android/library/com.uploadcare.android.library.upload/-multiple-urls-uploader/index.html)
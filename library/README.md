uploadcare-android-library
===============

This is an Android library for Uploadcare.

Supported features:

- Complete file, group and project API v0.4
- Paginated resources fetching.
- CDN path builder.
- File uploads from file, byte array, Uri, and URL.
- All operations available in synchronous and asynchronous modes.

[Documentation](http://uploadcare.github.io/uploadcare-android/index.html)

## jCenter

Latest stable version is available from jCenter.

To include it in your Android project, add this to the gradle.build file:

```
compile 'com.uploadcare.android.library:uploadcare-android:1.0.4'

```

## Examples

### Basic API Usage

Asynchronous fetch all files.
```java
UploadcareClient client = new UploadcareClient("publickey", "privatekey");
Project project = client.getProject();
Project.Collaborator owner = project.getOwner();

client.getFiles().asListAsync(new UploadcareAllFilesCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                //handle errors.
            }

            @Override
            public void onSuccess(List<UploadcareFile> files) {
                //successfully fetched list of all UploadcareFile files.
            }
        });
```
Synchronous fetch all files.
```java
UploadcareClient client = new UploadcareClient("publickey", "privatekey");
Project project = client.getProject();
Project.Collaborator owner = project.getOwner();

Iterable<UploadcareFile> files = client.getFiles().asIterable();
for (UploadcareFile file : files) {
    System.out.println(file.toString());
}
```

See documentation for details:

* [UploadcareClient](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/api/UploadcareClient.html)
* [UploadcareFile](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/api/UploadcareFile.html)
* [Project](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/api/Project.html)

Asynchronous fetch all groups.
```java
UploadcareClient client = new UploadcareClient("publickey", "privatekey");

client.getGroups().asListAsync(new UploadcareGroupsCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                //handle errors.
            }

            @Override
            public void onSuccess(List<UploadcareGroup> groups, URI next) {
                //successfully fetched list of all UploadcareGroup groups.
            }
        });
```
Synchronous fetch all groups.
```java
UploadcareClient client = new UploadcareClient("publickey", "privatekey");

Iterable<UploadcareGroup> groups = client..getGroups().asIterable();
for (UploadcareGroup group : groups) {
    System.out.println(group.toString());
}
```

See documentation for details:

* [UploadcareClient](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/api/UploadcareClient.html)
* [UploadcareGroup](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/api/UploadcareGroup.html)

### Building CDN URLs

```java
UploadcareFile file = client.getFile("85b5644f-e692-4855-9db0-8c5a83096e25");
CdnPathBuilder builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale();
URI url = Urls.cdn(builder);
```

See documentation for details:

* [CdnPathBuilder](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/urls/CdnPathBuilder.html)
* [Urls](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/urls/Urls.html)

### File uploads

Asynchronous upload file from Uri.
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

Synchronous upload file from Uri.
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

See documentation for details:

* [FileUploader](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/upload/FileUploader.html)
* [MultipleFilesUploader](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/upload/MultipleFilesUploader.html)
* [UrlUploader](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/upload/UrlUploader.html)
* [MultipleUrlsUploader](http://uploadcare.github.io/uploadcare-android/com/uploadcare/android/library/upload/MultipleUrlsUploader.html)
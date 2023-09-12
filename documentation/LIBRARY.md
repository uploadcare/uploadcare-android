# Library - REST API Documentation

* [Initialization](#initialization)
* [List of files](#list-of-files-api-reference)
* [File info](#file-info-api-reference)
* [Store file](#store-file-api-reference)
* [Batch Store files](#batch-store-files-api-reference)
* [Delete file](#delete-file-api-reference)
* [Batch Delete files](#batch-delete-files-api-reference)
* [Copy file to local storage](#copy-file-to-local-storage-api-reference)
* [Copy file to remote storage](#copy-file-to-remote-storage-api-reference)
* [List of groups](#list-of-groups-api-reference)
* [Group info](#group-info-api-reference)
* [Store group](#store-group-api-reference)
* [Project info](#project-info-api-reference)
* [List of webhooks](#list-of-webhooks-api-reference)
* [Create webhook](#create-webhook-api-reference)
* [Update webhook](#update-webhook-api-reference)
* [Delete webhook](#delete-webhook-api-reference)
* [Convert documents](#convert-documents-api-reference)
* [Convert videos](#convert-videos-api-reference)
* [Content delivery](#content-delivery-reference)
* [Secure Content delivery](#secure-content-delivery-reference)

# Library - UPLOAD API Documentation

* [Initialization Upload](#initialization-upload)
* [Upload File](#upload-file-api-reference)
* [Upload File from URL](#upload-file-url-api-reference)
* [Create file group](#create-files-group-api-reference)

## Initialization

##### REST API requires both public and secret keys. If you use Upload API only, you can specify just "YOUR_PUBLIC_KEY".

Kotlin
```kotlin
val uploadcare = UploadcareClient("YOUR_PUBLIC_KEY", "YOUR_SECRET_KEY")
```
Java
```java
UploadcareClient uploadcare = new UploadcareClient("YOUR_PUBLIC_KEY", "YOUR_SECRET_KEY");
```

## List of files ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/filesList)) ##

##### Asynchronous file fetch.

Kotlin
```kotlin
// Get a query object
val filesQueryBuilder = uploadcare.getFiles()
        .stored(true)
        .ordering(Order.SIZE_DESC)
        // other query parameters...

// Get a complete file list Asynchronously.
filesQueryBuilder.asListAsync(object : UploadcareAllFilesCallback {
    override fun onFailure(e: UploadcareApiException) {
        // Handle errors.
    }

    override fun onSuccess(result: List<UploadcareFile>) {
        // Successfully fetched list of all files.
    }
})
```
Java
```java
// Get a query object
FilesQueryBuilder filesQueryBuilder = uploadcare.getFiles()
        .stored(true)
        .ordering(Order.SIZE_DESC);
        // other query parameters...

// Get a file list asynchronously.
filesQueryBuilder.asListAsync(new UploadcareAllFilesCallback() {
    @Override
    public void onFailure(@NotNull UploadcareApiException e) {
        // Handle errors.
    }

    @Override
    public void onSuccess(@NonNull List<UploadcareFile> result) {
        // Fetched list of all files successfully.
    }
});
```

##### Synchronous file fetch.

Kotlin
```kotlin
// Get a query object.
val filesQueryBuilder = uploadcare.getFiles()
        .stored(true)
        .ordering(Order.SIZE_DESC)
        // other query parameters...

// Get a complete file list Synchronously.
val files = filesQueryBuilder.asList()
```
Java
```java
// Get a query object.
FilesQueryBuilder filesQueryBuilder = uploadcare.getFiles()
        .stored(true)
        .ordering(Order.SIZE_DESC);
        // other query parameters...

// Get a complete file list Synchronously.
List<UploadcareFile> files = filesQueryBuilder.asList();
```

## File info ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/fileInfo)) ##

##### Asynchronous file info fetch.

Kotlin
```kotlin
uploadcare.getFileAsync(
        context, // Context
        "YOUR_FILE_UUID", // File UUID
        object : UploadcareFileCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: UploadcareFile) {
                // Successfully fetched file.
            }
        })
```
Java
```java
uploadcare.getFileAsync(
        context, // Context
        "YOUR_FILE_UUID", // File UUID
        new UploadcareFileCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull UploadcareFile result) {
                // Successfully fetched file.
            }
        });
```

##### Synchronous file info fetch.

Kotlin
```kotlin
val file = uploadcare.getFile("YOUR_FILE_UUID")
```
Java
```java
UploadcareFile file = uploadcare.getFile("YOUR_FILE_UUID");
```

## Store a file ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/storeFile)) ##

##### Asynchronous file store.

Kotlin
```kotlin
uploadcare.saveFileAsync(context, "YOUR_FILE_UUID")
```
Java
```java
uploadcare.saveFileAsync(context, "YOUR_FILE_UUID");
```

##### Synchronous file store.

Kotlin
```kotlin
uploadcare.saveFile("YOUR_FILE_UUID")
```
Java
```java
uploadcare.saveFile("YOUR_FILE_UUID");
```

## Batch file store ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/filesStoring)) ##

##### Asynchronous multiple files store.

Kotlin
```kotlin
val fileIds = listOf("YOUR_FILE_UUID_1", "YOUR_FILE_UUID_2", "YOUR_FILE_UUID_3")
uploadcare.saveFilesAsync(context, fileIds)
```
Java
```java
List<String> fileIds = ... // list of file UUID's
uploadcare.saveFilesAsync(context, fileIds);
```

##### Synchronous multiple files store.

Kotlin
```kotlin
val fileIds = listOf("YOUR_FILE_UUID_1", "YOUR_FILE_UUID_2", "YOUR_FILE_UUID_3")
uploadcare.saveFiles(fileIds)
```
Java
```java
List<String> fileIds = ... // list of file UUID's
uploadcare.saveFiles(fileIds);
```

## Delete file ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/deleteFile)) ##

##### Asynchronous file delete.

Kotlin
```kotlin
uploadcare.deleteFileAsync(context, "YOUR_FILE_UUID")
```
Java
```java
uploadcare.deleteFileAsync(context, "YOUR_FILE_UUID");
```

##### Synchronous file delete.

Kotlin
```kotlin
uploadcare.deleteFile("YOUR_FILE_UUID")
```
Java
```java
uploadcare.deleteFile("YOUR_FILE_UUID");
```

## Batch file delete ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/filesDelete)) ##

##### Asynchronous multiple files delete.

Kotlin
```kotlin
val fileIds = listOf("YOUR_FILE_UUID_1", "YOUR_FILE_UUID_2", "YOUR_FILE_UUID_3")
uploadcare.deleteFilesAsync(context, fileIds)
```
Java
```java
List<String> fileIds = ... // list of file UUID's
uploadcare.deleteFilesAsync(context, fileIds);
```

##### Synchronous multiple files delete.

Kotlin
```kotlin
val fileIds = listOf("YOUR_FILE_UUID_1", "YOUR_FILE_UUID_2", "YOUR_FILE_UUID_3")
uploadcare.deleteFiles(fileIds)
```
Java
```java
List<String> fileIds = ... // list of file UUID's
uploadcare.deleteFiles(fileIds);
```

## Copy file to local storage ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/copyFileLocal)) ##

##### Asynchronous file copy to local storage.

Kotlin
```kotlin
val source : String = "YOUR_FILE_UUID" // File Resource UUID or A CDN URL.
uploadcare.copyFileLocalStorageAsync(
        context, // Context
        source,
        callback = object : CopyFileCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: UploadcareCopyFile) {
                // Successfully copied file.
            }
        })
```
Java
```java
String source = "YOUR_FILE_UUID"; // File Resource UUID or A CDN URL.
Boolean store = true;
uploadcare.copyFileLocalStorageAsync(
        context, // Context
        source,
        store,
        new CopyFileCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull UploadcareCopyFile result) {
                // Successfully copied file.
            }
        });
```

##### Synchronous copy file to local storage.

Kotlin
```kotlin
val source : String = "YOUR_FILE_UUID" // File Resource UUID or A CDN URL.
val copyResult : UploadcareCopyFile = copyuploadcare.copyFileLocalStorage(source)
// process result that will have either UploadcareFile or URI depending on source.
```
Java
```java
String source = "YOUR_FILE_UUID"; // File Resource UUID or A CDN URL.
Boolean store = true;
UploadcareCopyFile copyResult = uploadcare.copyFileLocalStorage(source, true);
// process result that will have either UploadcareFile or URI depending on source.
```

## Copy file to remote storage ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/copyFile)) ##

##### Asynchronous copy file to remote storage.

Kotlin
```kotlin
val source : String = "YOUR_FILE_UUID" // File Resource UUID or A CDN URL.
val target : String = ... // Custom storage name related to your project.
uploadcare.copyFileRemoteStorageAsync(
        context, // Context
        source,
        target,
        callback = object : CopyFileCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: UploadcareCopyFile) {
                // Successfully copied file.
            }
        })
```
Java
```java
String source = "YOUR_FILE_UUID"; // File Resource UUID or A CDN URL.
String target = ... // Custom storage name related to your project.
Boolean makePublic = true;
uploadcare.copyFileRemoteStorageAsync(
        context, // Context
        source,
        target,
        makePublic,
        null,
        new CopyFileCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull UploadcareCopyFile result) {
                // Successfully copied file.
            }
        });
```

##### Synchronous file copy to remote storage.

Kotlin
```kotlin
val source : String = "YOUR_FILE_UUID" // File Resource UUID or A CDN URL.
val target : String = ... // Custom storage name related to your project.
val copyResult : UploadcareCopyFile = copyuploadcare.copyFileRemoteStorage(source, target)
// process result that will have either UploadcareFile or URI depending on source.
```
Java
```java
String source = "YOUR_FILE_UUID"; // File Resource UUID or A CDN URL.
String target = ... // Custom storage name related to your project.
Boolean makePublic = true;
UploadcareCopyFile copyResult = uploadcare.copyFileRemoteStorage(source, target, true, null);
// process result that will have either UploadcareFile or URI depending on source.
```

## List of groups ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/groupsList)) ##

##### Asynchronous group list fetch.

Kotlin
```kotlin
// Get a query object
val groupsQueryBuilder = uploadcare.getGroups()
        .from(...) // Datetime from objects will be returned.
        .ordering(Order.SIZE_DESC)

// Get a complete group list Asynchronously.
groupsQueryBuilder.asListAsync(object : UploadcareAllGroupsCallback {
    override fun onFailure(e: UploadcareApiException) {
        // Handle errors.
    }

    override fun onSuccess(result: List<UploadcareGroup>) {
        // Successfully fetched list of all groups.
    }
})
```
Java
```java
// Get a query object
GroupsQueryBuilder groupsQueryBuilder = uploadcare.getGroups()
        .from(...) // Datetime from objects will be returned.
        .ordering(Order.SIZE_DESC);

// Get a complete groups list Asynchronously.
groupsQueryBuilder.asListAsync(new UploadcareAllGroupsCallback() {
    @Override
    public void onFailure(@NotNull UploadcareApiException e) {
        // Handle errors.
    }

    @Override
    public void onSuccess(@NonNull List<UploadcareGroup> result) {
        // Successfully fetched list of all groups.
    }
});
```

##### Synchronous group list fetch.

Kotlin
```kotlin
// Get a query object
val groupsQueryBuilder = uploadcare.getGroups()
        .from(...) // Datetime from objects will be returned.
        .ordering(Order.SIZE_DESC)

// Get a complete groups list Synchronously.
val groups = groupsQueryBuilder.asList()
```
Java
```java
// Get a query object
GroupsQueryBuilder groupsQueryBuilder = uploadcare.getGroups()
        .from(...) // Datetime from objects will be returned.
        .ordering(Order.SIZE_DESC);

// Get a complete groups list Synchronously.
List<UploadcareGroup> groups = groupsQueryBuilder.asList();
```

## Group info ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/groupInfo)) ##

##### Asynchronous group info fetch.

Kotlin
```kotlin
uploadcare.getGroupAsync(
        context, // Context
        "YOUR_GROUP_UUID", // group UUID
        object : UploadcareGroupCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: UploadcareGroup) {
                // Successfully fetched group.
            }
        })
```
Java
```java
uploadcare.getGroupAsync(
        context, // Context
        "YOUR_GROUP_UUID", // group UUID
        new UploadcareGroupCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull UploadcareGroup result) {
                // Successfully fetched group.
            }
        });
```

##### Synchronous group info fetch.

Kotlin
```kotlin
val group = uploadcare.getGroup("YOUR_GROUP_UUID")
```
Java
```java
UploadcareGroup group = uploadcare.getGroup("YOUR_GROUP_UUID");
```

## Store group ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#tag/Group/paths/~1groups~1%3Cuuid%3E~1storage~1/put)) ##

##### Asynchronous group store.

Kotlin
```kotlin
uploadcare.storeGroupAsync(context, "YOUR_GROUP_UUID")
```
Java
```java
uploadcare.storeGroupAsync(context, "YOUR_GROUP_UUID", null); // callback is optional
```

##### Synchronous group store.

Kotlin
```kotlin
uploadcare.storeGroup("YOUR_GROUP_UUID")
```
Java
```java
uploadcare.storeGroup("YOUR_GROUP_UUID");
```

## Project info ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/projectInfo)) ##

##### Asynchronous project info fetch.

Kotlin
```kotlin
uploadcare.getProjectAsync(
        context, // Context
        object : ProjectCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: Project) {
                // Successfully fetched project.
            }
        })
```
Java
```java
uploadcare.getProjectAsync(
        context, // Context
        new ProjectCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull Project result) {
                // Successfully fetched project.
            }
        });
```

##### Synchronous project info fetch.

Kotlin
```kotlin
val project = uploadcare.getProject()
```
Java
```java
Project project = uploadcare.getProject();
```

## List of webhooks ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/webhooksList)) ##

##### Asynchronous webhook list fetch.

Kotlin
```kotlin
uploadcare.getWebhooksAsync(
        context, // Context
        object : UploadcareWebhooksCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: List<UploadcareWebhook>) {
                // Successfully fetched list of all webhooks.
            }
        })
```
Java
```java
uploadcare.getWebhooksAsync(
        context, // Context
        new UploadcareWebhooksCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull List<UploadcareWebhook> result) {
                // Successfully fetched list of all webhooks.
            }
        });
```

##### Synchronous webhook list fetch.

Kotlin
```kotlin
val webhooks = uploadcare.getWebhooks()
```
Java
```java
List<UploadcareWebhook> webhooks = uploadcare.getWebhooks();
```

## Create webhook ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/webhookCreate)) ##

##### Asynchronous webhook create.

Kotlin
```kotlin
uploadcare.createWebhookAsync(
        context, // Context
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        signingSecret = "YOUR_WEBHOOK_SIGNING_SECRET",
        callback = object : UploadcareWebhookCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: UploadcareWebhook) {
                // Successfully created webhook.
            }
        })
```
Java
```java
uploadcare.createWebhookAsync(
        context, // Context
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        true, // is webhook active or not.
        "YOUR_WEBHOOK_SIGNING_SECRET",
        new UploadcareWebhookCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull UploadcareWebhook result) {
                // Successfully created webhook.
            }
        });
```

##### Synchronous webhook create.

Kotlin
```kotlin
val webhook = uploadcare.createWebhook(
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        true, // is webhook active or not.
        "YOUR_WEBHOOK_SIGNING_SECRET"
        )
```
Java
```java
UploadcareWebhook webhook = uploadcare.createWebhook(
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        true, // is webhook active or not.
        "YOUR_WEBHOOK_SIGNING_SECRET"
        );
```

## Update webhook ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/updateWebhook)) ##

##### Asynchronous webhook update.

Kotlin
```kotlin
uploadcare.updateWebhookAsync(
        context, // Context
        "YOUR_WEBHOOK_UUID",
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        signingSecret = "YOUR_WEBHOOK_SIGNING_SECRET",
        callback = object : UploadcareWebhookCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: UploadcareWebhook) {
                // Successfully updated webhook.
            }
        })
```
Java
```java
uploadcare.updateWebhookAsync(
        context, // Context
        "YOUR_WEBHOOK_UUID",
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        true, // is webhook active or not.
        "YOUR_WEBHOOK_SIGNING_SECRET",
        new UploadcareWebhookCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull UploadcareWebhook result) {
                // Successfully updated webhook.
            }
        });
```

##### Synchronous webhook update.

Kotlin
```kotlin
val webhook = uploadcare.updateWebhook(
        "YOUR_WEBHOOK_UUID",
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        true, // is webhook active or not.
        "YOUR_WEBHOOK_SIGNING_SECRET"
        )
```
Java
```java
UploadcareWebhook webhook = uploadcare.updateWebhook(
        "YOUR_WEBHOOK_UUID",
        URI.create("YOUR_WEBHOOK_URL"),
        "YOUR_WEBHOOK_EVENT",
        true, // is webhook active or not.
        "YOUR_WEBHOOK_SIGNING_SECRET"
        );
```

## Delete webhook ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/webhookUnsubscribe)) ##

##### Asynchronous webhook delete.

Kotlin
```kotlin
uploadcare.deleteWebhookAsync(context, URI.create("YOUR_WEBHOOK_URL"))
```
Java
```java
uploadcare.deleteWebhookAsync(context, URI.create("YOUR_WEBHOOK_URL"), null); //callback is optional
```

##### Synchronous webhook delete.

Kotlin
```kotlin
uploadcare.deleteWebhook(URI.create("YOUR_WEBHOOK_URL"))
```
Java
```java
uploadcare.deleteWebhook(URI.create("YOUR_WEBHOOK_URL"));
```

## Convert documents ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/documentConvert)) ##

##### Asynchronous documents convert.

Kotlin
```kotlin
// Create conversion job
val conversionJob = DocumentConversionJob("YOUR_FILE_UUID").apply {
    setFormat(DocumentFormat.JPG)
    // other conversion parameters...
}

// Create document converter, multiple DocumentConversionJob are supported.
val converter = DocumentConverter(uploadcare, listOf(conversionJob))

// Convert
converter.convertAsync(object : ConversionFilesCallback {
    override fun onFailure(e: UploadcareApiException) {
        // Handle errors.
    }

    override fun onSuccess(result: List<UploadcareFile>) {
        // Successfully converted documents.
    }
})
```
Java
```java
// Create conversion job
DocumentConversionJob conversionJob = new DocumentConversionJob("YOUR_FILE_UUID")
    .setFormat(DocumentFormat.JPG);
    // other conversion parameters...

// Multiple DocumentConversionJob are supported.
List<DocumentConversionJob> conversionJobs = new ArrayList();
conversionJobs.add(conversionJob);

// Create document converter
DocumentConverter converter = new DocumentConverter(uploadcare, conversionJobs);

// Convert
converter.convertAsync(
        new ConversionFilesCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull List<UploadcareFile> result) {
                // Successfully converted documents.
            }
        });
```

##### Synchronous documents convert.

Kotlin
```kotlin
// Create conversion job
val conversionJob = DocumentConversionJob("YOUR_FILE_UUID").apply {
    setFormat(DocumentFormat.JPG)
    // other conversion parameters...
}

// Create document converter, multiple DocumentConversionJob are supported.
val converter = DocumentConverter(uploadcare, listOf(conversionJob))

// Convert
val result : List<UploadcareFile> = converter.convert()
```
Java
```java
// Create conversion job
DocumentConversionJob conversionJob = new DocumentConversionJob("YOUR_FILE_UUID")
    .setFormat(DocumentFormat.JPG);
    // other conversion parameters...

// Multiple DocumentConversionJob are supported.
List<DocumentConversionJob> conversionJobs = new ArrayList();
conversionJobs.add(conversionJob);

// Create document converter
DocumentConverter converter = new DocumentConverter(uploadcare, conversionJobs);

// Convert
List<UploadcareFile> result = converter.convert();
```

## Convert videos ([API Reference](https://uploadcare.com/api-refs/rest-api/v0.6.0/#operation/videoConvert)) ##

##### Asynchronous videos convert.

Kotlin
```kotlin
// Create conversion job
val conversionJob = VideoConversionJob("YOUR_FILE_UUID").apply {
    setFormat(VideoFormat.MP4)
    quality(VideoQuality.NORMAL)
    thumbnails(2)
    // other conversion parameters...
}

// Create video converter, multiple VideoConversionJob are supported.
val converter = VideoConverter(uploadcare, listOf(conversionJob))

// Convert
converter.convertAsync(object : ConversionFilesCallback {
    override fun onFailure(e: UploadcareApiException) {
        // Handle errors.
    }

    override fun onSuccess(result: List<UploadcareFile>) {
        // Successfully converted videos.
    }
})
```
Java
```java
// Create conversion job
VideoConversionJob conversionJob = new VideoConversionJob("YOUR_FILE_UUID")
    .setFormat(DocumentFormat.JPG);
    // other conversion parameters...

// Multiple VideoConversionJob are supported.
List<VideoConversionJob> conversionJobs = new ArrayList();
conversionJobs.add(conversionJob);

// Create document converter
VideoConverter converter = new VideoConverter(uploadcare, conversionJobs);

// Convert
converter.convertAsync(
        new ConversionFilesCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull List<UploadcareFile> result) {
                // Successfully converted videos.
            }
        });
```

##### Synchronous videos convert.

Kotlin
```kotlin
// Create conversion job
val conversionJob = VideoConversionJob("YOUR_FILE_UUID").apply {
    setFormat(VideoFormat.MP4)
    quality(VideoQuality.NORMAL)
    thumbnails(2)
    // other conversion parameters...
}

// Create video converter, multiple VideoConversionJob are supported.
val converter = VideoConverter(uploadcare, listOf(conversionJob))

// Convert
val result : List<UploadcareFile> = converter.convert()
```
Java
```java
// Create conversion job
VideoConversionJob conversionJob = new VideoConversionJob("YOUR_FILE_UUID")
    .setFormat(DocumentFormat.JPG);
    // other conversion parameters...

// Multiple VideoConversionJob are supported.
List<VideoConversionJob> conversionJobs = new ArrayList();
conversionJobs.add(conversionJob);

// Create document converter
VideoConverter converter = new VideoConverter(uploadcare, conversionJobs);

// Convert
List<UploadcareFile> result = converter.convert();
```

## Content delivery ([Reference](https://uploadcare.com/docs/delivery/cdn/)) ##

##### CDN URLs build.

Kotlin
```kotlin
val file = uploadcare.getFile("YOUR_FILE_UUID")
val builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale()
        // other delivery parameters...

val url = Urls.cdn(builder)
```
Java
```java
UploadcareFile file = uploadcare.getFile("YOUR_FILE_UUID");
CdnPathBuilder builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale();
        // other delivery parameters...

URI url = Urls.cdn(builder);
```

## Secure Content delivery ([Reference](https://uploadcare.com/docs/security/secure_delivery/)) ##

##### Secure CDN URLs build.

Kotlin
```kotlin
val file = uploadcare.getFile("YOUR_FILE_UUID")
val domain = "YOUR_CDN_DOMAIN"
val token = "YOUR_CDN_TOKEN"
val expire = "YOUR_CDN_EXPIRE_DATE"
val builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale()
        // other delivery parameters...

val urlAkamai = Urls.cdnAkamai(domain, builder, token, expire)
// or
val urlKeyCDN = Urls.cdnKeyCDN(domain, builder, token, expire)
```
Java
```java
UploadcareFile file = uploadcare.getFile("YOUR_FILE_UUID");
String domain = "YOUR_CDN_DOMAIN";
String token = "YOUR_CDN_TOKEN";
String expire = "YOUR_CDN_EXPIRE_DATE";
CdnPathBuilder builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale();
        // other delivery parameters...

URI urlAkamai = Urls.cdn(domain, builder, token, expire);
// or
URI urlKeyCDN = Urls.cdn(domain, builder, token, expire);
```

## Upload initialization

##### Upload API requires just public key. For REST API, specify "YOUR_SECRET_KEY" as well.

Kotlin
```kotlin
val uploadcare = UploadcareClient("YOUR_PUBLIC_KEY")
```
Java
```java
UploadcareClient uploadcare = new UploadcareClient("YOUR_PUBLIC_KEY");
```

# Library - UPLOAD API Documentation


## Upload File ([API Reference](https://uploadcare.com/api-refs/upload-api/)) ##

##### Asynchronous file upload.

Kotlin
```kotlin
val context = ...// Context
val fileUri = ...//resource representing file (File/Uri/InputStream/ByteArray/String types are supported).
val uploader = FileUploader(uploadcare, fileUri, context) // Use "MultipleFilesUploader" for multiple files.
    .store(true)
    // Other upload parameters.

uploader.uploadAsync(object : UploadFileCallback {
    override fun onFailure(e: UploadcareApiException) {
        // Handle errors.
    }

    override fun onProgressUpdate(
                        bytesWritten: Long,
                        contentLength: Long,
                        progress: Double) {
        // Upload progress info.
    }

    override fun onSuccess(result: UploadcareFile) {
        // Successfully uploaded file to Uploadcare.
    }
})

// Cancel upload in progress.
uploader.cancel()
```
Java
```java
Context context = ...// Context
Uri fileUri = ...//resource representing file (File/Uri/InputStream/ByteArray/String types are supported).
Uploader uploader = new FileUploader(uploadcare, fileUri, context) // Use "MultipleFilesUploader" for multiple files.
    .store(true);
    // Other upload parameters.

uploader.uploadAsync(new UploadFileCallback() {
    @Override
    public void onFailure(UploadcareApiException e) {
        // Handle errors.
    }

    @Override
    public void onProgressUpdate(
                        Long bytesWritten,
                        Long contentLength,
                        Double progress) {
        // Upload progress info.
    }

    @Override
    public void onSuccess(UploadcareFile file) {
        // Successfully uploaded file to Uploadcare.
    }
});

// Cancel upload in progress.
uploader.cancel();
```

##### Synchronous file upload.

Kotlin
```kotlin
val context = ...// Context
val fileUri = ...//resource representing file (File/Uri/InputStream/ByteArray/String types are supported).
val uploader = FileUploader(uploadcare, fileUri, context) // Use "MultipleFilesUploader" for multiple files.
    .store(true)
    // Other upload parameters.

try {
    val file = uploader.upload()
    // Successfully uploaded file to Uploadcare.
} catch (e: UploadFailureException) {
    // Handle errors.
}
```
Java
```java
Context context = ...// Context
Uri fileUri = ...//resource representing file (File/Uri/InputStream/ByteArray/String types are supported).
Uploader uploader = new FileUploader(uploadcare, fileUri, context) // Use "MultipleFilesUploader" for multiple files.
    .store(true);
    // Other upload parameters.

try {
    UploadcareFile file = uploader.upload();
    // Successfully uploaded file to Uploadcare.
} catch (UploadFailureException e) {
    // Handle errors.
}
```

## Upload File from Url ([API Reference](https://uploadcare.com/api-refs/upload-api/#operation/fromURLUpload)) ##

##### Asynchronous file upload from URI.

Kotlin
```kotlin
val sourceUrl = "YOU_FILE_URL"
val uploader = UrlUploader(uploadcare, sourceUrl) // Use "MultipleUrlsUploader" for multiple files.
    .store(true)
    // Other upload parameters.

uploader.uploadAsync(object : UploadFileCallback {
    override fun onFailure(e: UploadcareApiException) {
        // Handle errors.
    }

    override fun onProgressUpdate(
                        bytesWritten: Long,
                        contentLength: Long,
                        progress: Double) {
        // Upload progress info.
    }

    override fun onSuccess(result: UploadcareFile) {
        // Successfully uploaded file to Uploadcare.
    }
})

// Cancel upload in progress.
uploader.cancel()
```
Java
```java
String sourceUrl = "YOU_FILE_URL";
Uploader uploader = new UrlUploader(uploadcare, sourceUrl) // Use "MultipleUrlsUploader" for multiple files.
    .store(true);
    // Other upload parameters.

uploader.uploadAsync(new UploadFileCallback() {
    @Override
    public void onFailure(UploadcareApiException e) {
        // Handle errors.
    }

    @Override
    public void onProgressUpdate(
                        Long bytesWritten,
                        Long contentLength,
                        Double progress) {
        // Upload progress info.
    }

    @Override
    public void onSuccess(UploadcareFile file) {
        // Successfully uploaded file to Uploadcare.
    }
});

// Cancel upload in progress.
uploader.cancel();
```

##### Synchronous file upload from URI.

Kotlin
```kotlin
val sourceUrl = "YOU_FILE_URL"
val uploader = UrlUploader(uploadcare, sourceUrl) // Use "MultipleUrlsUploader" for multiple files.
    .store(true)
    // Other upload parameters.

try {
    val file = uploader.upload()
    // Successfully uploaded file to Uploadcare.
} catch (e: UploadFailureException) {
    // Handle errors.
}
```
Java
```java
String sourceUrl = "YOU_FILE_URL";
Uploader uploader = new UrlUploader(uploadcare, sourceUrl) // Use "MultipleUrlsUploader" for multiple files.
    .store(true);
    // Other upload parameters.

try {
    UploadcareFile file = uploader.upload();
    // Successfully uploaded file to Uploadcare.
} catch (UploadFailureException e) {
    // Handle errors.
}
```

## Create file group ([API Reference](https://uploadcare.com/api-refs/upload-api/#operation/createFilesGroup)) ##

##### Asynchronous file group create.

Kotlin
```kotlin
val fileIds = listOf("YOUR_FILE_UUID_1", "YOUR_FILE_UUID_2", "YOUR_FILE_UUID_3")

uploadcare.createGroupAsync(
        fileIds,
        callback = object : UploadcareGroupCallback {
            override fun onFailure(e: UploadcareApiException) {
                // Handle errors.
            }

            override fun onSuccess(result: UploadcareGroup) {
                // Successfully created file group.
            }
        })
```
Java
```java
List<String> fileIds = ... // list of file UUID's

uploadcare.createGroupAsync(
        fileIds,
        null,
        new UploadcareGroupCallback() {
            @Override
            public void onFailure(@NotNull UploadcareApiException e) {
                // Handle errors.
            }

            @Override
            public void onSuccess(@NonNull UploadcareGroup result) {
                // Successfully created file group.
            }
        });
```

##### Synchronous file group create.

Kotlin
```kotlin
val fileIds = listOf("YOUR_FILE_UUID_1", "YOUR_FILE_UUID_2", "YOUR_FILE_UUID_3")

val uploadcareGroup = uploadcare.createGroup(fileIds)
```
Java
```java
List<String> fileIds = ... // list of file UUID's

UploadcareGroup uploadcareGroup = uploadcare.createGroup(fileIds, null);
```

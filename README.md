uploadcare-android
===============

This is an Android library for Uploadcare.

Supported features:

- Complete file and project API v0.4
- Paginated resources fetched.
- CDN path builder.
- File uploads from disk, byte array, Uri, and URL.

## Maven

Latest stable version is available from Maven Central.

To include it in your Android project, add this to the gradle.build file:

```
compile 'com.uploadcare.android.library:1.0.0'

```

## Examples

### Basic API Usage

```java
Client client = new Client("publickey", "privatekey");
Project project = client.getProject();
Project.Collaborator owner = project.getOwner();

List<URI> published = new ArrayList<URI>();
Iterable<File> files = client.getFiles().asIterable();
for (File file : files) {
    if (file.isMadePublic()) {
        published.add(file.getOriginalFileUrl());
    }
}
```

### Building CDN URLs

```java
File file = client.getFile("85b5644f-e692-4855-9db0-8c5a83096e25");
CdnPathBuilder builder = file.cdnPath()
        .resizeWidth(200)
        .cropCenter(200, 200)
        .grayscale();
URI url = Urls.cdn(builder);
```

### File uploads

```java
Client client = Client.demoClient();
java.io.File file = new java.io.File("olympia.jpg");
Uploader uploader = new FileUploader(client, sourceFile);
try {
    File file = uploader.upload().save();
    System.out.println(file.getOriginalFileUrl());
} catch (UploadFailureException e) {
    System.out.println("Upload failed :(");
}
```
# Changelog

# History
## 3.0.0
- Example:
    - Add Background upload example.
    - Add Cancel upload example.
    - Add Upload Progress example.
    - Add Pause/Resume Upload example.
- Widget:
    - "UploadcareWidget.getInstance()" doesn't require Context no more.
    - Background upload (using WorkManager internally).
    - Full support for Android 11 (compile/target SDK 30).
    - Update to full usage of MaterialComponents.
    - Dark theme support.
    - Refactor widget methods to be much easier to use, builder like pattern.
    - Add ability to specify upload parameters like cancellation, show progress etc.
    - Signed uploads for local Files.
    - Update dependencies.
- Library:
    - Add Secure Delivery support (Authenticated Urls) from custom CDN (Akamai, KeyCDN).
    - Add ability to Pause/Resume upload when using FileUploader/MultipleFilesUploader.
    - Add ability to cancel upload in progress.
    - Add ability to listen for upload progress for File/Url and Multiple Files/Urls.
    - Add "UploadFileCallback", use it instead of "UploadcareFileCallback" for uploading file.
    - Rename "privateKey" to "secretKey".
    - Add ability to get File/Files with Object Recognition information.
    - Signed uploads.
    - Add support for API v0.6
    - Add ability to Create file Group.
    - Add ability to copy files in local/remote storage. Update old copy file method, mark as Deprecated.
    - Add ability to get/create/update/delete Webhooks.
    - Update dependencies.

## 2.2.0
- Example:
    - Update Widget/Library versions
- Widget:
    - Add ability to use UploadcareWidget without "private" key, for selection and uploading files.
    - Add ability to provide custom "Request code" for file selection/upload request so you can filter result for specific request.
    - Fix possible issue when showing error that doesn't have message.
- Library:
    - Add ability to use UploadcareClient without "private" key, for uploading files.
    - Update UploadcareFile data model, make "url" and "datetimeUploaded" fields optional, those fields can be empty when you use UploadcareClient without "private" key, for uploading files.
    - Update dependencies
    
## 2.1.0
- Example:
    - Update Widget/Library versions
- Widget:
    - Update Library version
- Library:
    - Fix batch store/delete calls support more than 100 Files

## 2.0.1
- Example:
    - Update dependencies.
- Widget:
    - Update dependencies
    - Fix: use existing UploadcareClient instance instead of creating new one
    - Fix: UploadCareWidget.selectFileFrom() didn't work with Camera/Video/File social networks
    - Fix: possible issue when Social Source dialog items were not visible in dialog fragment on some devices
- Library:
    - Update dependencies
    - Add support for batch store/delete calls for Files
    - Set default UploadcareClient auth method to HMAC-based
    - Fix: "Project" data model, now **UploadcareClient.getProject()** and **UploadcareClient.getProjectAsync()** work properly
    - Internal network layer optimizations and improvements

## 2.0.0
- Example: Update dependencies, rewrite example to Kotlin.
- Widget: Update dependencies, rewrite widget to Kotlin, Fixes, Add "Google Photos" support.
- Library: Update dependencies, rewrite library to Kotlin, Fixes, Add support for API v0.5

## 1.0.4
- Library: CdnPathBuilder dimensions guard value update to higher values. Updated minSdkVersion 14.
- Widget: Added Russian localization. Updated minSdkVersion 14.

## 1.0.3
- Library: Added group API.
- Widget: Initial release.

## 1.0.1
- Updated documentation.

## 1.0.0
- Initial release
- Support Uploadcare REST API v0.4

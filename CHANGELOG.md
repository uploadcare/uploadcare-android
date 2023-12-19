# Changelog

## 4.1.0
**Library changes:**
- Added file metadata [methods](https://uploadcare.com/api-refs/rest-api/v0.7.0/#tag/File-metadata).
- Added delete group [method](https://uploadcare.com/api-refs/rest-api/v0.7.0/#tag/Group/operation/deleteGroup).
- Added check from URL status [method](https://uploadcare.com/api-refs/upload-api/#tag/Upload/operation/fromURLUploadStatus).
- Added check document conversion status [method](https://uploadcare.com/api-refs/rest-api/v0.7.0/#tag/Conversion/operation/documentConvert).
- Added check video conversion status [method](https://uploadcare.com/api-refs/rest-api/v0.7.0/#tag/Conversion/operation/videoConvertStatus).

## 4.0.0

**Library changes:**
- Migrated REST API support from v0.6 to [v0.7](http://uploadcare.com/api-refs/rest-api/v0.7.0/).
- Added support for:
  - [Object recognition](https://uploadcare.com/docs/intelligence/object-recognition/),
  - [Unsafe content detection](https://uploadcare.com/docs/unsafe-content/),
  - [Malware protection](https://uploadcare.com/docs/security/malware-protection/),
  - [Background removal](https://uploadcare.com/docs/remove-bg/).
- Added support for [new event types](https://uploadcare.com/docs/webhooks/#event-types) for webhooks.
- Added support for converting [multi-page documents](https://uploadcare.com/docs/transformations/document-conversion/#multipage-conversion) into a group of files.
- Added `default_effects` field in `UploadcareFile`.
- Added reaction to request throttling (429 code response).
- Added Proguard rule to keep DTO classes to avoid issues with network JSON deserialization.
- Fixed authorization signature for requests with URL parameters.
- Removed sorting methods by file size, as the corresponding parameter has been removed from the API query parameters.
- Removed the `UploadcareClient.storeGroup()` and `UploadcareGroup.store()` methods, as their endpoints have been removed.

**Widget changes:**
- SocialApi doesn't use `GET /sources` method anymore.
- Added Proguard rule to keep DTO classes to avoid issues with network JSON deserialization.

**Build changes:**
- Migrated Gradle builds from Groovy to Kotlin.

**Example app changes:**
- Removed sorting options by file size from `UploadFragment`.
- Enabled R8 shrinking code for release build to enable Proguard's rules.

## 3.3.0
- Library:
  - Added support for [signing your webhooks](https://uploadcare.com/docs/webhooks/#signed-webhooks) using the `signingSecret` parameter.
  - Fixed an issue with `datetime` filtering.
  - Improved the handling of large file uploads.
- Dependencies:
  - Update the target SDK version to 34.
  - Update AGP (Android Gradle Plugin) version to 8.1.1.
  - Update dependencies.

## 3.2.0
- Widget:
    - The methods `UploadcareWidget.selectFile()` and `Fragment.startActivityForResult` have been deprecated.
      We provide a new way of launching selection of the file to upload via `Fragment.registerForActivityResult`
      and `Activity.registerForActivityResult` with `UploadcareActivityResultContract`
    - Documentation about using widget via `registerForActivityResult` with `UploadcareActivityResultContract` is updated accordingly
- Dependencies:
    - Replace deprecated dependencies and methods usage under the hood
    - Update the target SDK version to 33
    - Update AGP version to 8.1.0 and Gradle version to 8.1
    - Update dependencies

## 3.1.0
- Widget:
    - Update documentation
- Library:
    - Add ability to convert documents/videos.
    - Update documentation

## 3.0.1
- Widget:
    - Fix proper reference to Uploadcare Library dependency.

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

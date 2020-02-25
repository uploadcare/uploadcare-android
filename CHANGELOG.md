# History
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

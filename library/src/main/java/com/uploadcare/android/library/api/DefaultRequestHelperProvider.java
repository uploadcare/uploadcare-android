package com.uploadcare.android.library.api;

class DefaultRequestHelperProvider implements RequestHelperProvider {

    public RequestHelper get(UploadcareClient client) {
        return new RequestHelper(client);
    }

}
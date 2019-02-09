package com.uploadcare.android.library.upload;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;

public class UploadUtils {

    public static final MediaType MEDIA_TYPE_TEXT_PLAIN = MediaType.parse("text/plain");

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static MediaType getMimeType(File file) {
        if (file == null) {
            return MEDIA_TYPE_TEXT_PLAIN;
        } else {
            return getMimeType(file.getName());
        }
    }

    public static MediaType getMimeType(String fileName) {
        if (fileName == null) {
            return MEDIA_TYPE_TEXT_PLAIN;
        }

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        int index = fileName.lastIndexOf('.') + 1;
        String ext = fileName.substring(index).toLowerCase();
        String type = mime.getMimeTypeFromExtension(ext);
        if (type == null) {
            return MEDIA_TYPE_TEXT_PLAIN;
        }
        return MediaType.parse(type);
    }

    public static MediaType getMimeType(ContentResolver contentResolver, Uri uri) {
        if (uri == null) {
            return MEDIA_TYPE_TEXT_PLAIN;
        }

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            return MediaType.parse(contentResolver.getType(uri));
        } else {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            String type = mime.getMimeTypeFromExtension(extension);
            if (type == null) {
                return MEDIA_TYPE_TEXT_PLAIN;
            }
            return MediaType.parse(type);
        }
    }
}

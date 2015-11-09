package com.uploadcare.android.library.api;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.uploadcare.android.library.BuildConfig;
import com.uploadcare.android.library.callbacks.BaseCallback;
import com.uploadcare.android.library.callbacks.BasePaginationCallback;
import com.uploadcare.android.library.callbacks.RequestCallback;
import com.uploadcare.android.library.data.DataWrapper;
import com.uploadcare.android.library.data.FileData;
import com.uploadcare.android.library.data.FilePageData;
import com.uploadcare.android.library.data.PageData;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.exceptions.UploadcareAuthenticationException;
import com.uploadcare.android.library.exceptions.UploadcareInvalidRequestException;
import com.uploadcare.android.library.urls.UrlParameter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.uploadcare.android.library.urls.UrlUtils.trustedBuild;

/**
 * A helper class for doing API calls to the Uploadcare API. Supports API version 0.4.
 *
 * TODO Support of throttled requests needs to be added
 */
public class RequestHelper {

    private final UploadcareClient client;

    public static final String REQUEST_GET = "GET";

    public static final String REQUEST_POST = "POST";

    public static final String REQUEST_DELETE = "DELETE";

    public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private static final String EMPTY_MD5 = "d41d8cd98f00b204e9800998ecf8427e";

    private static final String JSON_CONTENT_TYPE = "application/json";

    RequestHelper(UploadcareClient client) {
        this.client = client;
    }

    public static String rfc2822(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RequestHelper.DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(UTC);
        return dateFormat.format(date);
    }

    public static String iso8601(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RequestHelper.DATE_FORMAT_ISO_8601,
                Locale.US);
        dateFormat.setTimeZone(UTC);
        return dateFormat.format(date);
    }

    public String makeSignature(String url, String date, String requestType)
            throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = new StringBuilder();
        sb.append(requestType)
                .append("\n").append(EMPTY_MD5)
                .append("\n").append(JSON_CONTENT_TYPE)
                .append("\n").append(date)
                .append("\n").append(url);

        byte[] privateKeyBytes = client.getPrivateKey().getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(privateKeyBytes, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] hmacBytes = mac.doFinal(sb.toString().getBytes());
        return new String(encodeHex(hmacBytes));
    }

    public void setApiHeaders(Request.Builder requestBuilder, String url, String requestType,
            BaseCallback callback) {
        Calendar calendar = new GregorianCalendar(UTC);
        String formattedDate = rfc2822(calendar.getTime());

        requestBuilder.addHeader("Accept", "application/vnd.uploadcare-v0.4+json");
        requestBuilder.addHeader("Date", formattedDate);
        requestBuilder.addHeader("User-Agent",
                String.format("javauploadcare-android/%s/%s", BuildConfig.VERSION_NAME,
                        client.getPublicKey()));
        String authorization = null;
        if (client.isSimpleAuth()) {
            authorization = "Uploadcare.Simple " + client.getPublicKey() + ":" + client
                    .getPrivateKey();
        } else {
            try {
                String signature = makeSignature(url, formattedDate, requestType);
                authorization = "Uploadcare " + client.getPublicKey() + ":" + signature;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                if (callback == null) {
                    throw new UploadcareApiException("Error when signing the request", e);
                } else {
                    callback.onFailure(
                            new UploadcareApiException("Error when signing the request", e));
                }
            }
        }
        requestBuilder.addHeader("Authorization", authorization);
    }

    public <T> T executeQuery(String requestType, String url, boolean apiHeaders,
            Class<T> dataClass, RequestBody requestBody) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        switch (requestType) {
            case REQUEST_GET:
                requestBuilder.get();
                break;
            case REQUEST_POST:
                requestBuilder.post(requestBody);
                break;
            case REQUEST_DELETE:
                requestBuilder.delete();
                break;
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, null);
        }
        try {
            Response response = client.getHttpClient().newCall(requestBuilder.build()).execute();

            checkResponseStatus(response);

            return client.getObjectMapper().readValue(response.body().string(), dataClass);
        } catch (IOException e) {
            throw new UploadcareApiException(e);
        }
    }

    public <T, U> void executeQueryAsync(final Context context, String requestType, String url,
            boolean apiHeaders, final Class<T> dataClass,
            final DataWrapper<U, T> dataWrapper,
            final BaseCallback callback, RequestBody requestBody) {
        final Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        switch (requestType) {
            case REQUEST_GET:
                requestBuilder.get();
                break;
            case REQUEST_POST:
                requestBuilder.post(requestBody);
                break;
            case REQUEST_DELETE:
                requestBuilder.delete();
                break;
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, callback);
        }
        client.getHttpClient().newCall(requestBuilder.build()).enqueue(new Callback() {
            Handler mainHandler = new Handler(context.getMainLooper());

            @Override
            public void onFailure(Request request, final IOException e) {
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(new UploadcareApiException(e));
                    }
                });

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(
                                    new UploadcareApiException("Unexpected code " + response));
                        }
                    });
                }

                try {
                    checkResponseStatus(response);
                    final T result = client.getObjectMapper().readValue(response.body().string(),
                            dataClass);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dataWrapper != null) {
                                callback.onSuccess(dataWrapper.wrap(result));
                            } else {
                                callback.onSuccess(result);
                            }
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(new UploadcareApiException(e));
                        }
                    });
                }
            }
        });
    }

    public static void setQueryParameters(Uri.Builder builder, List<UrlParameter> parameters) {
        for (UrlParameter parameter : parameters) {
            builder.appendQueryParameter(parameter.getParam(), parameter.getValue());
        }
    }

    public <T, U> Iterable<T> executePaginatedQuery(
            final URI url,
            final List<UrlParameter> urlParameters,
            final boolean apiHeaders,
            final Class<? extends PageData<U>> dataClass,
            final DataWrapper<T, U> dataWrapper) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private URI next = null;

                    private boolean more;

                    private Iterator<U> pageIterator;

                    {
                        getNext();
                    }

                    private void getNext() {
                        URI pageUrl;
                        if (next == null) {
                            Uri.Builder builder = Uri.parse(url.toString())
                                    .buildUpon();

                            setQueryParameters(builder, urlParameters);
                            pageUrl = trustedBuild(builder);
                        } else {
                            pageUrl = next;
                        }
                        PageData<U> pageData = executeQuery(REQUEST_GET, pageUrl.toString(),
                                apiHeaders,
                                dataClass, null);
                        more = pageData.hasMore();
                        next = pageData.getNext();
                        pageIterator = pageData.getResults().iterator();
                    }

                    public boolean hasNext() {
                        if (pageIterator.hasNext()) {
                            return true;
                        } else if (more) {
                            getNext();
                            return true;
                        } else {
                            return false;
                        }
                    }

                    public T next() {
                        return dataWrapper.wrap(pageIterator.next());
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public void executePaginatedQueryWithOffsetLimitAsync(final Context context,
            final URI url,
            final List<UrlParameter> urlParameters,
            final boolean apiHeaders,
            final FileDataWrapper dataWrapper,
            final BasePaginationCallback callback) {

        Uri.Builder builder = Uri.parse(url.toString())
                .buildUpon();

        setQueryParameters(builder, urlParameters);
        URI pageUrl = trustedBuild(builder);

        final Request.Builder requestBuilder = new Request.Builder()
                .url(pageUrl.toString());

        requestBuilder.get();
        if (apiHeaders) {
            try {
                setApiHeaders(requestBuilder, pageUrl.toString(), REQUEST_GET, null);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure(new UploadcareApiException(e));
                return;
            }
        }
        client.getHttpClient().newCall(requestBuilder.build()).enqueue(new Callback() {
            Handler mainHandler = new Handler(context.getMainLooper());

            @Override
            public void onFailure(Request request, final IOException e) {
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(new UploadcareApiException(e));
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(
                                    new UploadcareApiException("Unexpected code " + response));
                        }
                    });
                }
                try {
                    checkResponseStatus(response);
                    final PageData<FileData> pageData = client.getObjectMapper()
                            .readValue(response.body().string(), FilePageData.class);
                    final boolean hasMore = pageData.hasMore();
                    final List<UploadcareFile> files = new ArrayList<>();
                    for (FileData fileData : pageData.getResults()) {
                        files.add(dataWrapper.wrap(fileData));
                    }
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(files, pageData.getNext());
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(new UploadcareApiException(e));
                        }
                    });
                }
            }
        });

    }

    /**
     * Executes the request et the Uploadcare API and return the HTTP Response object.
     *
     * The existence of this method(and it's return type) enables the end user to extend the
     * functionality of the
     * Uploadcare API client by creating a subclass of {@link com.uploadcare.android.library.api.UploadcareClient}.
     *
     * @param requestType request type (ex. "GET", "POST", "DELETE");
     * @param url         request url
     * @param apiHeaders  TRUE if the default API headers should be set
     * @param requestBody body of POST request, used only with request type REQUEST_POST.
     * @return HTTP Response object
     */
    public Response executeCommand(String requestType, String url, boolean apiHeaders,
            RequestBody requestBody) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        switch (requestType) {
            case REQUEST_GET:
                requestBuilder.get();
                break;
            case REQUEST_POST:
                requestBuilder.post(requestBody);
                break;
            case REQUEST_DELETE:
                requestBuilder.delete();
                break;
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, null);
        }

        try {
            Response response = client.getHttpClient().newCall(requestBuilder.build()).execute();
            checkResponseStatus(response);
            return response;
        } catch (IOException e) {
            throw new UploadcareApiException(e);
        }
    }

    /**
     * Executes the request et the Uploadcare API and return the HTTP Response object.
     *
     * The existence of this method(and it's return type) enables the end user to extend the
     * functionality of the
     * Uploadcare API client by creating a subclass of {@link com.uploadcare.android.library.api.UploadcareClient}.
     *
     * @param context     application context. @link android.content.Context
     * @param requestType request type (ex. "GET", "POST", "DELETE");
     * @param url         request url
     * @param apiHeaders  TRUE if the default API headers should be set
     * @param callback    callback  {@link RequestCallback}
     * @param requestBody body of POST request, used only with request type REQUEST_POST.
     */
    public void executeCommandAsync(final Context context, String requestType, String url,
            boolean apiHeaders, final RequestCallback callback, RequestBody requestBody) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        switch (requestType) {
            case REQUEST_GET:
                requestBuilder.get();
                break;
            case REQUEST_POST:
                requestBuilder.post(requestBody);
                break;
            case REQUEST_DELETE:
                requestBuilder.delete();
                break;
        }
        if (apiHeaders) {
            setApiHeaders(requestBuilder, url, requestType, callback);
        }

        client.getHttpClient().newCall(requestBuilder.build()).enqueue(new Callback() {
            Handler mainHandler = new Handler(context.getMainLooper());

            @Override
            public void onFailure(Request request, final IOException e) {
                if (callback != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(new UploadcareApiException(e));
                        }
                    });
                }
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (callback != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(
                                        new UploadcareApiException(
                                                "Unexpected code " + response.body().toString()));
                            }
                        });
                    }
                }
                checkResponseStatus(response, callback);
                if (callback != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(response);
                        }
                    });
                }
            }
        });
    }

    /**
     * Verifies that the response status codes are within acceptable boundaries and throws
     * corresponding exceptions
     * otherwise.
     *
     * @param response The response object to be checked
     */
    private void checkResponseStatus(Response response) throws IOException {

        int statusCode = response.code();

        if (statusCode >= 200 && statusCode < 300) {
            return;
        } else if (statusCode == 401 || statusCode == 403) {
            throw new UploadcareAuthenticationException(response.body().string());
        } else if (statusCode == 400 || statusCode == 404) {
            throw new UploadcareInvalidRequestException(response.body().string());
        } else {
            throw new UploadcareApiException(
                    "Unknown exception during an API call, response:" + response.body().string());
        }
    }

    /**
     * Verifies that the response status codes are within acceptable boundaries and calls
     * corresponding callback method exceptions otherwise.
     * otherwise.
     *
     * @param response The response object to be checked
     * @param callback callback  {@link BaseCallback}
     */
    private void checkResponseStatus(Response response, BaseCallback callback) {

        int statusCode = response.code();
        String requestBody = null;
        try {
            requestBody = response.body().string();
        } catch (IOException e) {
            callback.onFailure(new UploadcareApiException(e));
        }
        if (statusCode >= 200 && statusCode < 300) {
            return;
        } else if (statusCode == 401 || statusCode == 403) {
            callback.onFailure(new UploadcareAuthenticationException(requestBody));
        } else if (statusCode == 400 || statusCode == 404) {
            callback.onFailure(new UploadcareInvalidRequestException(requestBody));
        } else {
            callback.onFailure(new UploadcareApiException(
                    "Unknown exception during an API call, response:" + requestBody));
        }
    }

    /**
     * * Converts an array of bytes into an array of characters representing the hexadecimal values
     * of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters
     * to represent any
     * given byte.
     *
     * @param data a byte[] to convert to Hex characters
     * @return array of characters the hexadecimal values of each byte in order
     */
    protected static char[] encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_UPPER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_UPPER[0x0F & data[i]];
        }
        return out;
    }

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
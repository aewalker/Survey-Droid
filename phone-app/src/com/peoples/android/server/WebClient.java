/*
 * Communicates with the SOC Server and notifies incoming survey whenever needed. 
 * Adapted from Android Wikitionary example provided with the Android SDK
 */

package com.peoples.android.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Helper methods to simplify talking with and parsing responses from a
 * SOC Online API. 
 */
public class WebClient {
    private static final String TAG = "WebClient";
    private static final int HTTP_STATUS_OK = 200;
    /**
     * Thrown when there were problems contacting the remote API server, either
     * because of a network error, or the server returned a bad status code.
     */
    @SuppressWarnings("serial")
    public static class ApiException extends Exception {
        public ApiException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ApiException(String detailMessage) {
            super(detailMessage);
        }
    }

    /**
     * Pull the raw text content of the given URL. This call blocks until the
     * operation has completed, and is synchronized because it uses a shared
     * buffer {@link #sBuffer}.
     *
     * @param url The exact URL to request.
     * @return The raw content returned by the server.
     * @throws ApiException If any connection or server error occurs.
     */
    protected static synchronized String getUrlContent(String url) throws ApiException {
        
        byte[] sBuffer = new byte[512];
        
        // Create client and set our specific user-agent string
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
//        request.setHeader("User-Agent", sUserAgent);

        try {
            HttpResponse response = client.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }

            // Pull content stream from response
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();

            ByteArrayOutputStream content = new ByteArrayOutputStream();

            // Read response into a buffered stream
            int readBytes = 0;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }

            // Return result from buffered stream
            return new String(content.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Problem communicating with API", e);
        }
    }
    
    protected static synchronized boolean postToUrl(String url, String name, String value) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List nameValuePairs = new ArrayList(1);
            nameValuePairs.add(new BasicNameValuePair(name, value));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpPost);
            
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HTTP_STATUS_OK)
                return true;
        } catch (Exception e) { }
        return false;
    }
}

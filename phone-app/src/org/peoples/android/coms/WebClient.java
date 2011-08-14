/*---------------------------------------------------------------------------*
 * Communicates with the SOC Server and notifies incoming survey whenever    *
 * needed.  Adapted from Android Wikitionary example provided with the       *
 * Android SDK.                                                              *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.peoples.android.Config;
import org.peoples.android.Util;

import android.content.Context;

/**
 * Helper methods to simplify talking with and parsing responses from a
 * SOC Online API.
 *
 * @author Tony Xaio
 * @author Austin Walker
 */
public abstract class WebClient
{
	//logging tag
	private static final String TAG = "WebClient";
	
	//config key used to indicate that we need to use the custom keystore
	//used as an optimization to avoid catching a lot of exceptions
	private static final String USE_PRIVATE_KEYSTORE =
		"use_private_keystore";

	//status codes
    private static final int HTTP_STATUS_OK = 200;

    /**
     * Thrown when there were problems contacting the remote API server, either
     * because of a network error, or the server returned a bad status code.
     */
    @SuppressWarnings("serial")
    public static class ApiException extends Exception
    {
        public ApiException(String detailMessage, Throwable throwable)
        {
            super(detailMessage, throwable);
        }

        public ApiException(String detailMessage)
        {
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
    protected static synchronized String getUrlContent(Context ctxt, String url)
    throws ApiException
    {
    	return getUrlContent(ctxt, url, true);
    }
    
    //same as above with recursion counter
    private static synchronized String getUrlContent(Context ctxt, String url,
    		boolean firstCall) throws ApiException
    {
        // Create client and set our specific user-agent string
        HttpClient client = getClient(ctxt);
        HttpGet request = new HttpGet(url);

        try
        {
        	// Execute HTTP Post Request
            HttpResponse response;
			try
			{
				response = client.execute(request);
			}
			catch (SSLException e)
			{
				Util.w(ctxt, TAG, Util.fmt(e));
				//make sure this isn't the recursive call
				if (!firstCall)
					throw new ApiException("Untrusted certificate!");
				//switch to using the other client
				swapKeyStore(ctxt);
				//the just do a recursive call
				return getUrlContent(ctxt, url, false);
			}
			catch (SocketException e)
			{
				throw new ApiException("Network problem", e);
			}

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK)
            {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
            }
            return getInputStreamAsString(ctxt,
            		response.getEntity().getContent());
        }
        catch (IOException e)
        {
            throw new ApiException("Problem communicating with API", e);
        }
    }
    
    /**
     * Posts given JSON content to a url
     * 
     * @param ctxt - the current {@link Context} (usually a service)
     * @param url - full url to send content to
     * @param value - json content
     * 
     * @return true on success
     */
    protected static synchronized boolean
		postJsonToUrl(Context ctxt, String url, String value)
    {
    	return postJsonToUrl(ctxt, url, value, true);
    }

    //same as above with recursion counter
    private static synchronized boolean
    	postJsonToUrl(Context ctxt, String url, String value, boolean firstCall)
    {
        HttpClient httpclient = getClient(ctxt);
        HttpPost httpPost = new HttpPost(url);
        
    	try
    	{
            StringEntity se = new StringEntity(value);
            se.setContentEncoding(
            		new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);

            // Execute HTTP Post Request
            HttpResponse response;
			try
			{
				response = httpclient.execute(httpPost);
			}
			catch (SSLException e)
			{
				//make sure this isn't the recursive call
				if (!firstCall)
					throw new ApiException("Untrusted certificate");
				//switch to using the other client
				swapKeyStore(ctxt);
				//the just do a recursive call
				return postJsonToUrl(ctxt, url, value, false);
			}

            Util.d(ctxt, TAG, "Content: " + getInputStreamAsString(ctxt,
            		response.getEntity().getContent()));

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HTTP_STATUS_OK)
                return true;
    	}
    	catch (Exception e)
    	{
    		Util.e(ctxt, TAG, Util.fmt(e));
    	}
        return false;
    }
    
    private static void swapKeyStore(Context ctxt)
    {
    	Config.putSetting(ctxt, USE_PRIVATE_KEYSTORE,
				!Config.getSetting(ctxt, USE_PRIVATE_KEYSTORE, false));
    }
    
    private static HttpClient getClient(Context ctxt)
    {
    	if (Config.getSetting(ctxt, USE_PRIVATE_KEYSTORE, false))
    	{
    		return new SocHttpClient(ctxt);
    	}
    	else
    	{
    		return new DefaultHttpClient();
    	}
    }

    private static String getInputStreamAsString(Context ctxt, InputStream is)
    {
        byte[] sBuffer = new byte[512];
        ByteArrayOutputStream content = new ByteArrayOutputStream();

        // Read response into a buffered stream
        int readBytes = 0;
        try
        {
            while ((readBytes = is.read(sBuffer)) != -1)
            {
                content.write(sBuffer, 0, readBytes);
            }
        }
        catch (IOException e)
        {
            Util.e(ctxt, TAG, Util.fmt(e));
        }
        return new String(content.toByteArray());
    }
}

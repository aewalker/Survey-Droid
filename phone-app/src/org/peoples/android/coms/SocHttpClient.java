/*---------------------------------------------------------------------------*
 * SocHttpClient.java                                                        *
 *                                                                           *
 * Custom http client for PEOPLES; can be set to use the default keystore,   *
 * or fall back to the custom keystore.                                      *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.peoples.android.R;

import android.content.Context;
import android.util.Log;

/**
 * Custom version of the Apache http client.
 * 
 * @author Tony Xiao, Austin Walker
 */
public class SocHttpClient extends DefaultHttpClient
{
	//the keystore password
	private static final String PASSWORD = "peoples";
	
	//logging tag
	private static final String TAG = "SocHttpClient";
	
	//current context
	private final Context ctxt;
	
	/**
	 * Constructor
	 * 
	 * @param c - the current context
	 */
	public SocHttpClient(Context c)
	{
		ctxt = c;
	}
	
	@Override
	protected ClientConnectionManager createClientConnectionManager()
	{
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		registry.register(new Scheme("https",
				newSslSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
	}

	private SSLSocketFactory newSslSocketFactory()
	{
		try
		{
			KeyStore trusted = KeyStore.getInstance("BKS");
			InputStream in = ctxt.getResources().openRawResource(
					R.raw.peoples_keystore);
			try
			{
				trusted.load(in, PASSWORD.toCharArray());
			}
			catch (CertificateException e)
			{
				Log.e(TAG, "Cert Exception!");
				throw new AssertionError(e);
			}
			finally
			{
				in.close();
			}
			SSLSocketFactory sf = new SSLSocketFactory(trusted);
			//TODO look into this
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return sf;
		}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
			throw new AssertionError(e);
		}
	}
}

/*---------------------------------------------------------------------------*
 * SDHttpClient.java                                                         *
 *                                                                           *
 * Custom http client for Survey Droid; can be set to use the default        *
 * keystore, or fall back to the custom keystore.                            *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
 *                                                                           *
 * This file is part of Survey Droid.                                        *
 *                                                                           *
 * Survey Droid is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU General Public License as published by      *
 * the Free Software Foundation, either version 3 of the License, or         *
 * (at your option) any later version.                                       *
 *                                                                           *
 * Survey Droid is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 * GNU General Public License for more details.                              *
 *                                                                           *
 * You should have received a copy of the GNU General Public License         *
 * along with Survey Droid.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package org.surveydroid.android.coms;

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
import org.surveydroid.android.R;
import org.surveydroid.android.Util;

import android.content.Context;

/**
 * Custom version of the <a href="http://hc.apache.org/httpclient-3.x/">
 * Apache http client</a>.
 * 
 * @author Tony Xiao
 * @author Austin Walker
 */
public class SDHttpClient extends DefaultHttpClient
{
	//the keystore password
	private static final String PASSWORD = "peoples";
	
	//logging tag
	private static final String TAG = "SDHttpClient";
	
	//current context
	private final Context ctxt;
	
	/**
	 * Constructor
	 * 
	 * @param c - the current context
	 */
	public SDHttpClient(Context c)
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
					R.raw.sd_keystore);
			try
			{
				trusted.load(in, PASSWORD.toCharArray());
			}
			catch (CertificateException e)
			{
				Util.e(ctxt, TAG, "Cert Exception: " + Util.fmt(e));
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
			Util.e(ctxt, TAG, Util.fmt(e));
			throw new AssertionError(e);
		}
	}
}

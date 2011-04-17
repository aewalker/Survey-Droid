package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class WebClient {
	public static InputStream getFromUrl(String url) {
		InputStream content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
                } catch (Exception e) {
			//handle the exception !
		}
		return content;
	}

	public static InputStream postToUrl(String url) {
		InputStream content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
                } catch (Exception e) {
			//handle the exception !
		}
		return content;
	}

	public static void main(String[] args) throws IOException {

		System.out.println("testing");
		BufferedReader rd = new BufferedReader(new InputStreamReader(getFromUrl("http://www.eigendiego.com/cake/app/webroot/answers/pull/")), 4096);
		String line;
		StringBuilder sb =  new StringBuilder();
		while ((line = rd.readLine()) != null) {
				sb.append(line);
		}
		rd.close();
		String contentOfMyInputStream = sb.toString();
		System.out.println(contentOfMyInputStream);

	}
}

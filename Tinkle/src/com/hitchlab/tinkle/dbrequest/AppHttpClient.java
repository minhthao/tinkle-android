package com.hitchlab.tinkle.dbrequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class AppHttpClient {
	//public static final String url = "http://192.168.1.8:8080/Frevent/MainServlet";
	public static final String url = "http://FreventServer-6kvbkxqtmm.elasticbeanstalk.com/MainServlet";
	
	/** The time it takes for our client to timeout */
	public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds

	/** Single instance of our HttpClient */
	private static HttpClient mHttpClient;

	/**
	 * Get our single instance of our HttpClient object.
	 *
	 * @return an HttpClient object with connection parameters set
	 */
	private static HttpClient getHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return mHttpClient;
	}

	/**
	 * Performs an HTTP Post request to the specified url with the
	 * specified parameters.
	 * @param url The web address to post the request to
	 * @param postParameters The parameters to send via the request
	 * @return The result of the request
	 * @throws Exception
	 */
	public static void executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws Exception {
		HttpClient client = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
		HttpResponse httpResponse = client.execute(httpPost);
		if (httpResponse.getEntity() != null) httpResponse.getEntity().consumeContent();
	}

	/**
	 * Performs an HTTP Post request to the specified url with the
	 * specified parameters.
	 * @param url The web address to post the request to
	 * @param postParameters The parameters to send via the request
	 * @return The result of the request
	 * @throws Exception
	 */
	public static String executeHttpPostWithReturnValue(String url, ArrayList<NameValuePair> postParameters) throws Exception {
		HttpClient client = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
		HttpResponse httpResponse = client.execute(httpPost);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
		StringBuilder buffer = new StringBuilder("");
		String line;
		String NL = System.getProperty("line.separator");
		while ((line = bufferedReader.readLine()) != null) {
			buffer.append(line).append(NL);
		}
		String page = buffer.toString();
		bufferedReader.close();
		if (httpResponse.getEntity() != null) httpResponse.getEntity().consumeContent();
		return page;
	}
}

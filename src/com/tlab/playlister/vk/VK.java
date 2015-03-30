package com.tlab.playlister.vk;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

//TODO: move into own package
public class VK {
	private static String accessToken = null;
	private static String userId = null;

	public static void requestAccessToken() {
		String url = "https://oauth.vk.com/authorize?client_id=4781664&scope=audio,offline&redirect_uri=https://oauth.vk.com/blank.html&display=page&v=5.29&response_type=token";
		//TODO: navigate
	}

	public static String getUserId() throws VKNotReadyException {
		getReady();
		return userId;
	}

	public static JSONObject api(String method, String params) throws URISyntaxException, IOException, ParseException, VKNotReadyException {
		getReady();

		String str = "https://api.vk.com/method/"+method+"?access_token="+accessToken+"&v=5.28&"+params;
		URI uri = new URI(str);

		JSONParser parser = new JSONParser();
		return (JSONObject)parser.parse(http_get(uri));
	}

	//TODO: move this one
	private static String http_get(URI uri) throws IOException {
		String responseAsString = "";
		HttpGet httpget = new HttpGet(uri);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = null;
			try {
				instream = entity.getContent();
				responseAsString = IOUtils.toString(instream);
			} finally {
				if(instream != null) instream.close();
			}

		}

		return responseAsString;
	}

	private static boolean ready() { return (accessToken!=null && userId!=null); }

	private static void getReady() throws VKNotReadyException {
		if(ready()) return;
		//TODO: replace with loading from file
		//TODO: use requestAccessToken to get token if you don't have one
		accessToken = "<place your token here>";
		userId = "<id>";
		if(!ready()) throw new VKNotReadyException();
	}
}

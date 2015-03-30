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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class VK {
	public static final String ACCESS_TOKEN_URL = "https://oauth.vk.com/authorize?client_id=4781664&scope=audio,offline&redirect_uri=https://oauth.vk.com/blank.html&display=page&v=5.29&response_type=token";

	private static String accessToken = null;
	private static String userId = null;

	public static void authorized(String url) {
		String token = url.substring(url.indexOf("access_token=")+13);
		accessToken = token.substring(0, token.indexOf("&"));
		userId = url.substring(url.indexOf("user_id=")+8);
	}

	public static void requestAccessToken() throws VKNotReadyException {
		accessToken = null;
		userId = null;
		VKAuthBrowser.main(null);
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
		if(entity != null) {
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

		final String filename = "creds.txt";

		File file = new File(filename);
		if(file.exists() && !file.isDirectory()) {
			try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
				accessToken = reader.readLine();
				userId = reader.readLine();
			} catch(Exception e) { throw new VKNotReadyException(); }
		} else {
			if(file.isDirectory()) throw new VKNotReadyException();

			System.out.println("You don't have \""+filename+"\". This file contains your user id and access token.");
			System.out.println("To get token, you have to authorize in VK. Now we'll open a browser for you to do it.");
			System.out.println("");
			System.out.println("In case you don't trust embed browser or have some troubles with it, open this link in your browser:");
			System.out.println(ACCESS_TOKEN_URL);
			System.out.println("");
			System.out.println("After you authorize, you'll be redirected to https://oauth.vk.com/blank.html page.");
			System.out.println("Copy access token and user id from there into \""+filename+"\" file on separate lines and restart this application.");

			requestAccessToken();

			if(accessToken==null || userId==null) throw new VKNotReadyException();

			try {
				PrintWriter writer = new PrintWriter(filename, "UTF-8");
				writer.println(accessToken);
				writer.println(userId);
				writer.close();
			} catch(Exception e) {
				throw new VKNotReadyException();
			}
		}

		if(accessToken.length()!=85) System.err.println("Unusual length of access token.");
		if(!accessToken.matches("[a-f0-9]{85}")) System.err.println("Access token is probably damaged.");
		if(!userId.matches("[0-9]+")) {
			System.err.println("User id is invalid.");
			userId = null;
		}

		if(!ready()) throw new VKNotReadyException();
	}
}

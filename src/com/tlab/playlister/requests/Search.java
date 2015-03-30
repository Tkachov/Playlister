package com.tlab.playlister.requests;

import com.tlab.playlister.vk.VK;
import com.tlab.playlister.tracks.IgnoreType;
import com.tlab.playlister.tracks.TrackInfo;
import com.tlab.playlister.tracks.TracksPackage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Search implements RequestManager {
	private Queue<RequestInfo> requests = new ArrayDeque<>();

	@Override public void note(RequestInfo info) { requests.add(info); }

	@Override
	public TracksPackage execute() {
		RequestInfo request = requests.poll();
		if(request == null) return null;

		//parsing
		String query = request.info.substring(request.info.indexOf(' ') + 1);
		int count = Integer.valueOf(request.info.substring(0, request.info.indexOf(' ')));

		//doing query
		return audioSearch(query, count, request.ignoreType, request.priority);
	}

	@Override public boolean empty() { return requests.isEmpty(); }

	private TracksPackage audioSearch(String query, int count, IgnoreType ignoreType, int priority) {
		List<TrackInfo> list = new ArrayList<>();

		try {
			System.out.println("SEARCH (" + query + "):");
			query = query.replace(" ", "%20");
			JSONObject jsonResponse = VK.api("audio.search", "q=" + query + "&search_own=1&count=" + count);
			JSONObject resp = (JSONObject)jsonResponse.get("response");
			JSONArray posts = (JSONArray)resp.get("items");

			int workingLinks = 0;
			for(Object post: posts) {
				JSONObject attach = (JSONObject)post;
				TrackInfo track = new TrackInfo(attach);
				list.add(track);
				if(!track.isRemoved()) ++workingLinks;
			}

			System.out.println("SEARCH - OK ("+workingLinks+"/"+posts.size()+"/"+count+")");
			System.out.println("");
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("SEARCH - FAIL");
			System.out.println("");
		}

		return new TracksPackage(list, ignoreType, priority);
	}
}

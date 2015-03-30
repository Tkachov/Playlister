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

public class Audios implements RequestManager {
	private Queue<RequestInfo> requests = new ArrayDeque<>();

	@Override public void note(RequestInfo info) { requests.add(info); }

	@Override
	public TracksPackage execute() {
		RequestInfo request = requests.poll();
		if(request == null) return null;

		//parsing
		String query;
		int count;

		if(request.info.contains(" ")) {
			query = request.info.substring(0, request.info.indexOf(' '));
			count = Integer.valueOf(request.info.substring(request.info.indexOf(' ') + 1));
		} else {
			query = request.info;
			count = 0;
		}

		//doing query
		return audioGet(query, count, request.ignoreType, request.priority);
	}

	@Override public boolean empty() { return requests.isEmpty(); }

	private TracksPackage audioGet(String ownerId, int count, IgnoreType ignoreType, int priority) {
		List<TrackInfo> list = new ArrayList<>();
		boolean needGet = true;

		if(count == 0) {
			try {
				JSONObject jsonResponse = VK.api("audio.getCount", "owner_id="+ownerId);
				count = Integer.valueOf((jsonResponse.get("response")).toString());
				if(count>6000) count = 6000;
				else if(count<0) count = 0;
			} catch (Exception e) {
				System.out.println("AUDIOS (" + ownerId + "): FAIL -- getCount");
				System.out.println("");
				needGet = false;
			}
		}

		if(needGet) {
			if(count==0) {
				System.out.println("AUDIOS (" + ownerId + "): OK, (0/0/0)");
				System.out.println("");
				needGet = false;
			}
		}

		if(needGet) {
			try {
				JSONObject jsonResponse = VK.api("audio.get", "oid="+ownerId+"&need_user=0&count="+count);
				JSONObject response = (JSONObject)jsonResponse.get("response");
				JSONArray mp3list = (JSONArray)response.get("items");
				int workingLinks = 0;
				for(int i=1; i<mp3list.size(); ++i) {
					JSONObject attach = (JSONObject)mp3list.get(i);
					TrackInfo track = new TrackInfo(attach);
					list.add(track);
					if(!track.isRemoved()) ++workingLinks;
				}
				System.out.println("AUDIOS ("+ownerId+"): OK ("+workingLinks+"/"+mp3list.size()+"/"+count+")");
				System.out.println("");
			} catch (Exception e) {
				System.out.println("AUDIOS ("+ownerId+"): FAIL -- get");
				System.out.println("");
			}
		}

		return new TracksPackage(list, ignoreType, priority);
	}
}

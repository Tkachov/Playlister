package com.tlab.playlister.requests;

import com.tlab.playlister.vk.VK;
import com.tlab.playlister.tracks.TrackInfo;
import com.tlab.playlister.tracks.TracksPackage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Walls implements RequestManager {
	private Queue<RequestInfo> requests = new ArrayDeque<>();
	private Queue<TracksPackage> packages = new ArrayDeque<>();

	@Override public void note(RequestInfo info) { requests.add(info); }

	@Override
	public TracksPackage execute() {
		if(!packages.isEmpty()) return packages.poll();

		final int MAX_WALLS_IN_ONE_REQUEST = 20;
		List<RequestInfo> pack = new ArrayList<>();
		while(!requests.isEmpty() && pack.size()<MAX_WALLS_IN_ONE_REQUEST) pack.add(requests.poll());
		wallGet(pack);

		return packages.poll();
	}

	@Override public boolean empty() { return requests.isEmpty() && packages.isEmpty(); }

	private List<JSONArray> extractAttachments(JSONObject post) throws Exception {
		if(post!=null) {
			if(post.containsKey("attachments")) {
				List<JSONArray> arr = new ArrayList<>();
				arr.add((JSONArray)post.get("attachments"));
				return arr;
			} else if(post.containsKey("copy_history")) {
				List<JSONArray> arr = new ArrayList<>();
				JSONArray copy_history = (JSONArray)post.get("copy_history");
				for(Object copy_history_object: copy_history) {
					JSONObject copy_history_post = (JSONObject)copy_history_object;
					arr.add((JSONArray)copy_history_post.get("attachments"));
				}
				return arr;
			}
		}

		throw new Exception(); //no attachments found
	}

	private void wallGet(List<RequestInfo> pack) {
		String wallsList = null;
		for(RequestInfo request: pack) {
			if(request == null) continue;
			if(wallsList==null) wallsList = request.info;
			else wallsList += ","+request.info;
		}

		if(wallsList==null || pack.size()==0) return;

		try {
			System.out.println("WALLS ("+wallsList+"):");
			JSONObject jsonResponse = VK.api("wall.getById", "posts="+wallsList+"&extended=0&copy_history_depth=2");
			JSONArray posts = (JSONArray)jsonResponse.get("response");
			for(int i = 0; i<posts.size(); i++) {
				JSONObject post = (JSONObject)posts.get(i);
				try {
					int workingLinks = 0;
					int totalItems = 0;
					List<TrackInfo> list = new ArrayList<>();

					List<JSONArray> array = extractAttachments(post);
					for(JSONArray attachments: array) {
						for(Object attachment: attachments) {
							JSONObject attach = (JSONObject)attachment;
							if(!attach.get("type").equals("audio")) continue;
							TrackInfo track = new TrackInfo((JSONObject)attach.get("audio"));
							list.add(track);
							if(!track.isRemoved()) ++workingLinks;
							++totalItems;
						}
					}

					RequestInfo wall = pack.get(i);
					//there is no proof that walls are given in the same order, so here it goes:
					String wallId = post.get("owner_id")+"_"+post.get("id");
					for(RequestInfo request: pack) {
						if(request == null) continue; //this breaks indexes as well
						if(request.info.equals(wallId)) {
							wall = request;
							break;
						}
					}

					packages.add(new TracksPackage(list, wall.ignoreType, wall.priority));
					System.out.println("\tpost #"+(i+1)+" ok ("+workingLinks+"/"+totalItems+")");
				} catch (Exception e) {
					System.out.println("\tpost #"+(i+1)+" failed");
				}
			}

			System.out.println("WALLS - OK");
			System.out.println("");
		} catch(Exception e) {
			System.out.println("WALLS - FAIL");
			System.out.println("");
		}
	}
}

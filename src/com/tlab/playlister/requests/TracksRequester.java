package com.tlab.playlister.requests;

import com.tlab.playlister.io.Ignorer;
import com.tlab.playlister.io.PlaylistWriter;
import com.tlab.playlister.vk.VK;
import com.tlab.playlister.tracks.IgnoreType;
import com.tlab.playlister.tracks.TracksPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TracksRequester {
	private static Map<String, RequestManager> managers = null;

	private static void makeManagers() {
		if(managers != null) return;
		managers = new HashMap<>();
		managers.put("audios", new Audios());
		managers.put("search", new Search());
		managers.put("walls", new Walls());
	}

	//TODO: rename
	private static boolean isSetting(String line) {
		if(line.startsWith("ignore ")) {
			Ignorer.addIgnores(line.substring(7));
			return true;
		}

		if(line.startsWith("newIgnoreFile ")) {
			Ignorer.setNewIgnoresFilename(line.substring(14));
			return true;
		}

		if(line.startsWith("noTitles")) {
			PlaylistWriter.setTracksTitles(false);
			return true;
		}

		return false;
	}

	public static List<TracksPackage> getTracks(List<String> lines) {
		makeManagers();

		int currentPriority = 0;
		for(String line: lines) {
			if(isSetting(line)) continue;
			++currentPriority;

			IgnoreType ignoreType = IgnoreType.NO_IGNORE; //TODO: variable default ignore type
			if(line.startsWith("no_ignore ")) {
				ignoreType = IgnoreType.NO_IGNORE;
				line = line.substring(10);
			} else if(line.startsWith("ignore_ids ")) {
				ignoreType = IgnoreType.IGNORE_TRACK_ID;
				line = line.substring(11);
			} else if(line.startsWith("ignore_tracks ")) {
				ignoreType = IgnoreType.IGNORE_TRACK_NAME;
				line = line.substring(13);
			} else if(line.startsWith("ignore_artists ")) {
				ignoreType = IgnoreType.IGNORE_ARTIST;
				line = line.substring(14);
			}

			if(line.contains("/audios")) {
				managers.get("audios").note(new RequestInfo(line.substring(line.indexOf("/audios")+7), ignoreType, currentPriority));
			} else if(line.contains("/wall")) {
				managers.get("walls").note(new RequestInfo(line.substring(line.indexOf("/wall")+5), ignoreType, currentPriority));
			} else if(line.startsWith("me")) {
				String userId = "";
				try { userId = VK.getUserId(); } catch (Exception e) { /**/ }
				managers.get("audios").note(new RequestInfo(line.replace("me", userId), ignoreType, currentPriority));
			} else if(line.startsWith("search")) {
				managers.get("search").note(new RequestInfo(line.substring(7), ignoreType, currentPriority));
			} else if(line.equals("") || line.startsWith("#") || line.startsWith("//")) {
				//ignore -- that's a comment or an empty line
			} else {
				System.err.println("Unknown request: {\"" + line + "\"");
			}
		}

		List<TracksPackage> packages = new ArrayList<>();
		for(RequestManager manager: managers.values()) {
			while(!manager.empty()) {
				TracksPackage pack = manager.execute();
				if(pack!=null) packages.add(pack);
			}
		}

		return packages;
	}
}

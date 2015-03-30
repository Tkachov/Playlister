package com.tlab.playlister.tracks;

import org.json.simple.JSONObject;

public class TrackInfo {
	private String artist, title, url, trackId;
	private long duration;

	public TrackInfo(JSONObject mp3) {
		artist = (String)mp3.get("artist");
		title = (String)mp3.get("title");
		url = (String)mp3.get("url");
		trackId = mp3.get("owner_id") + "_" + mp3.get("id");
		duration = (Long)mp3.get("duration");
	}

	public String getArtist() { return artist; }
	public String getTitle() { return title; }
	public String getUrl() { return url; }
	public String getTrackId() { return trackId; }
	public long getDuration() { return duration; }

	public String getFullTitle() { return artist + " - " + title; }
	public boolean isRemoved() { return url.equals(""); }
}

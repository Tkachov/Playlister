package com.tlab.playlister.tracks;

import java.util.ArrayList;
import java.util.List;

public class TracksPackage {
	private List<TrackInfo> tracks;
	private IgnoreType ignoreType;
	private int priority;

	public TracksPackage() {
		tracks = new ArrayList<>();
		ignoreType = IgnoreType.NO_IGNORE;
		priority = 0;
	}

	public TracksPackage(List<TrackInfo> list, IgnoreType ignore, int p) {
		tracks = list;
		ignoreType = ignore;
		priority = p;
	}

	public int size() { return tracks.size(); }
	public IgnoreType getIgnoreType() { return ignoreType; }
	public int getPriority() { return priority; }

	public void add(TrackInfo track) { tracks.add(track); }
	public void setIgnoreType(IgnoreType type) { ignoreType = type; }
	public void setPriority(int p) { priority = p; }

	public List<TrackInfo> getTracks() {
		return tracks;
	}
}

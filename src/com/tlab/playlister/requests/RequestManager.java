package com.tlab.playlister.requests;

import com.tlab.playlister.tracks.TracksPackage;

public interface RequestManager {
	public void note(RequestInfo info);
	public TracksPackage execute();
	public boolean empty();
}

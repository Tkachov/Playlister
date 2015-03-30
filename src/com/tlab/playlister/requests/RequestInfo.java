package com.tlab.playlister.requests;

import com.tlab.playlister.tracks.IgnoreType;

//TODO: rewrite
public class RequestInfo {
	public String info;
	public IgnoreType ignoreType;
	public int priority;

	public RequestInfo(String i, IgnoreType it, int p) {
		info = i;
		ignoreType = it;
		priority = p;
	}
}

package com.tlab.playlister.io;

import com.tlab.playlister.tracks.TrackInfo;
import com.tlab.playlister.tracks.TracksPackage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class PlaylistWriter {
	private static boolean writeTrackTitles = true;

	public static void setTracksTitles(boolean value) { writeTrackTitles = value; }

	public static void writeList(String filename, List<TracksPackage> packages) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filename, "UTF-8");
		writer.println("#EXTM3U");
		for(TracksPackage pack: packages) {
			for(TrackInfo track: pack.getTracks()) {
				if(track.isRemoved()) continue;
				writer.println("#EXTINF:" + track.getDuration() + "," + track.getFullTitle());
				writer.println(morphUrl(track));
			}
		}
		writer.close();
	}

	private static String morphUrl(TrackInfo track) {
		String url = track.getUrl();
		if(writeTrackTitles) url += "&title=" + track.getFullTitle().replace(' ', '_');
		return url;
	}
}

package com.tlab.playlister.io;

import com.tlab.playlister.tracks.IgnoreType;
import com.tlab.playlister.tracks.TrackInfo;
import com.tlab.playlister.tracks.TracksPackage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Ignorer {
	private static List<IgnoreEntry> ignored = new ArrayList<>();
	private static String newIgnoresFilename = null;

	public static void addIgnores(String filename) {
		try(BufferedReader reader = Files.newBufferedReader(Paths.get(filename), Charset.forName("UTF-8"))) {
			String l;
			while((l = reader.readLine()) != null) ignored.add(new IgnoreEntry(l));
		} catch(Exception e) { /**/ }
	}

	public static void setNewIgnoresFilename(String filename) { newIgnoresFilename = filename; }

	public static List<TracksPackage> removeIgnored(List<TracksPackage> packages) {
		if(ignored.size() == 0) return packages;

		List<TracksPackage> filtered = new ArrayList<>();
		for(TracksPackage pack: packages) {
			TracksPackage morphed = morphTracksPackage(pack);
			if(!morphed.getTracks().isEmpty()) filtered.add(morphed);
		}

		return filtered;
	}

	private static TracksPackage morphTracksPackage(TracksPackage pack) {
		List<TrackInfo> tracks = new ArrayList<>();
		for(TrackInfo track: pack.getTracks()) {
			boolean skip = false;
			for(IgnoreEntry entry: ignored) {
				if(entry.meets(track)) {
					skip = true;
					break;
				}
			}
			if(!skip) tracks.add(track);
		}
		return new TracksPackage(tracks, pack.getIgnoreType(), pack.getPriority());
	}

	public static void writeIgnores(List<TracksPackage> packages) throws FileNotFoundException, UnsupportedEncodingException {
		if(newIgnoresFilename==null || newIgnoresFilename.equals("")) return;
		PrintWriter writer = new PrintWriter(newIgnoresFilename, "UTF-8");
		for(TracksPackage pack: packages) {
			if(pack.getIgnoreType() == IgnoreType.NO_IGNORE) continue;
			for(TrackInfo track: pack.getTracks()) {
				if(track.isRemoved()) continue;
				switch(pack.getIgnoreType()) {
					case IGNORE_TRACK_ID:
						writer.println("id "+track.getTrackId());
					break;

					case IGNORE_TRACK_NAME:
						writer.println("track "+track.getArtist()+" -- "+track.getTitle());
					break;

					case IGNORE_ARTIST:
						writer.println("artist "+track.getArtist());
					break;
				}
			}
		}
		writer.close();
	}
}

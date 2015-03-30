package com.tlab.playlister.io;

import com.tlab.playlister.tracks.TrackInfo;

public class IgnoreEntry {
		private String type; //artist ("artist"), artist+title ("track"), vkID ("track_id", in artist field), unknown ("")
		private String artist, title;

		public IgnoreEntry(String line) {
			//parse line and make filter
			if(line.startsWith("artist ")) {
				type = "artist";
				artist = normalize(line.substring(7));
			} else if(line.startsWith("track ")) {
				type = "track";
				String q = line.substring(6);
				int ind = q.indexOf(" -- ");
				if(ind == -1) {
					type = "artist";
					artist = q;
				} else {
					artist = normalize(q.substring(0, ind));
					title = normalize(q.substring(ind + 4));
				}
			} else if(line.startsWith("id ")) {
				type = "track_id";
				artist = normalize(line.substring(3));
			}
		}

		private String normalize(String name) {
			return name.toLowerCase().replaceAll("\\s+", " ").trim();
		}

		public boolean meets(TrackInfo item) {
			if(type == null) return false;

			switch(type) {
				case "artist": return artist.equals(normalize(item.getArtist()));
				case "track":
					return (artist.equals(normalize(item.getArtist())) && title.equals(normalize(item.getTitle())));
				case "track_id":
					return (artist.equals(item.getTrackId()));
			}
			return false;
		}
}

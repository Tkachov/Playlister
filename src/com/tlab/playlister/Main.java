package com.tlab.playlister;

import com.tlab.playlister.io.Ignorer;
import com.tlab.playlister.io.InputProvider;
import com.tlab.playlister.io.PlaylistWriter;
import com.tlab.playlister.requests.TracksRequester;
import com.tlab.playlister.tracks.TracksPackage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;

public class Main {
	public static void main(String[] args) throws URISyntaxException, IOException {
		try {
			if(args.length == 0) showUsage();

			List<String> input = InputProvider.getInput(args);
			List<TracksPackage> packages = TracksRequester.getTracks(input);

			packages.sort(new Comparator<TracksPackage>() {
				@Override
				public int compare(TracksPackage o1, TracksPackage o2) {
					return ((Integer)o1.getPriority()).compareTo(o2.getPriority());
				}
			});

			packages = Ignorer.removeIgnored(packages);
			PlaylistWriter.writeList("playlist_java.m3u", packages);
			Ignorer.writeIgnores(packages);
		} catch (Exception e) {
			System.out.println(e.getClass().getName()+": "+e.getMessage());
			e.printStackTrace();
		}
	}

	private static void showUsage() {
		System.out.println("Usage:");
		System.out.println("\t<program> input_filename.txt");
		System.out.println("or");
		System.out.println("\t<program> url1 [url2 ...]");
		System.out.println("");
		System.out.println("In case you run the program without console arguments (like now), you're able to pass the commands and then finish your input entering '.'.");
		System.out.println("");
		System.out.println("Commands:");
		System.out.println("* /wallXXXXXX_YYYYYY -- get all audio attachments from post XXXXXX_YYYYYY;");
		System.out.println("* /audiosXXXXXX [N] -- get audios from playlist of user XXXXXX (all of them or first N);");
		System.out.println("me [N] -- get audios from current user playlist (all of them or first N);");
		System.out.println("search <N> <query> -- search <query> and get first <N> audios.");
		System.out.println("");
		System.out.println("* means you can write links or anything before this command.");
		System.out.println("");
		System.out.println("You can also use these commands to configure program outputs:");
		System.out.println("noTitles -- do not add \"&track=...\" information in playlist;");
		System.out.println("ignore <filename> -- read filters from <filename>. All tracks that meet one of these filters won't be added into playlist;");
		System.out.println("newIgnoreFile <filename> -- write ignore filters of all found tracks into file <filename>. These filters could be used to ignore found tracks in the future.");
		System.out.println("");
		System.out.println("Filters format: (one at a line)");
		System.out.println("artist <artist> -- ignore tracks with this <artist> tag;");
		System.out.println("track <artist> -- <title> -- ignore track with these <artist> and <title> tags;");
		System.out.println("id <ownerId_trackId> -- ignore this exact track.");
		System.out.println("");
		System.out.println("These commands modifiers (should be written in the beginning of the line) could be used:");
		System.out.println("no_ignore -- tracks from this command won't be written to newIgnoreFile;");
		System.out.println("ignore_ids -- ignore tracks from this command with id filter (default);");
		System.out.println("ignore_tracks -- ignore tracks from this command with track filter;");
		System.out.println("ignore_artists -- ignore tracks from this command with artist filter.");
	}
}

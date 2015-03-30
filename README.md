# Playlister

This is a console application, which uses [VK API](https://vk.com/dev/) to create M3U playlists.
Playlists could be used in different players, so you can shuffle your tracks or use hotkeys.
Music is streamed from VK servers.

## Repository contents

This repository is a copy of my IntelliJ IDEA project folder. It contains source code and necessary libraries.

## How to use

There are three ways of providing input for the application:
* run the application and write commands in standard input stream, and then finish with '.';
* run the application with '*.txt' console argument. It would read this file and execute commands from it;
* pass other console arguments. These arguments would be treated as separate commands.

You can write such commands as:
* **search** **<N>** **<query>** — search **query** and add first **N** found tracks into playlist;
* **me** **[<N>]** — add tracks from your page (all of them or first **N** if given);
* ***/audios<XXXXXX>** **[<N>]** — add tracks from page of user with id **XXXXXX** (all of them or first **N** if given);
* ***/wall<XXXXXX>_<YYYYYY>** — add tracks attached to post **XXXXXX_YYYYYY**.

In two last commands * means you can write anything there. For example, you can copy a link to a post or audios page and use it as a command.

This application can also filter some tracks. For example, if you don't want to listen to a group or don't like a track, you can ask the application to ignore it.
In order to do that, you should write a special file and specify it using **ignore** **<filename>** command.
In that file you should use following syntax (each directive on a separate line):
* **id <ownerId>_<trackId>** — ignore this exact track;
* **track <artist> -- <title>** — ignore tracks with <artist> and <title> tags;
* **artist** **<artist>** — ignore tracks of this artist.

Of course it's not really easy to do on your own, especially if you want to ignore tracks you've added earlier.
In this case you can use **newIgnoreFile <filename>** command.
Application will automatically write ignore directives for every found track into that file.
To control which tracks should be ignored and which ignore directive should be used, you can use special command modifiers:
* **no_ignore** — don't ignore tracks from this command;
* **ignore_ids** — ignore tracks from this command with **id** directive;
* **ignore_tracks** — ignore tracks from this command with **track** directive;
* **ignore_artists** — ignore tracks from this command with **artist** directive.

Command modifiers should be before commands. For example:
	# set up
	ignore ignored.list
	newIgnoreFile new_ignored.list

	# find one NewTone track and don't ignore it
	no_ignore search 1 NewTone

	# get 10 tracks from current user's audios page and never add them in the future
	ignore_ids me 10

	# get 5 Skrillex tracks, but never add tracks with such artist-title pair in the future
	ignore_tracks search 5 Skrillex

	# get tracks from this post, but never add tracks from any of artists who wrote those in the future
	ignore_artists https://vk.com/wall-36495885_147233

When you launch the application for the first time, you'll have to authorize in VK and give permissions to this application.
It shows embed browser and automatically saves access token and user id into "creds.txt" file.

If you don't like it, you can copy a link from standard console output and open it in your desktop browser.
After you authorize, you'll be  be redirected to https://oauth.vk.com/blank.html page.
Copy access token and user id from there into "creds.txt" file on separate lines and restart the application to get it working.
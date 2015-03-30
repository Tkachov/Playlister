package com.tlab.playlister.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class InputProvider {
	public static List<String> getInput(String[] args) throws IOException {
		if(args.length == 0) return userInput();
		if(args[0].endsWith(".txt")) return textFile(args[0]);
		return commandLineArguments(args);
	}

	private static List<String> commandLineArguments(String[] args) {
		List<String> lines = new ArrayList<>();
		Collections.addAll(lines, args);
		return lines;
	}

	private static List<String> textFile(String filename) throws IOException {
		List<String> lines = new ArrayList<>();
		try(BufferedReader reader = Files.newBufferedReader(Paths.get(filename), Charset.forName("UTF-8"))) {
			String line;
			while((line = reader.readLine()) != null) lines.add(line);
		}
		return lines;
	}

	private static List<String> userInput() {
		List<String> lines = new ArrayList<>();
		Scanner c = new Scanner(System.in);
		String line;
		while((line = c.nextLine()) != null) {
			if(line.equals(".")) break;
			lines.add(line);
		}
		return lines;
	}
}

package com.aurorionsmp.client;

import net.minecraft.client.gui.Font;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

	public static List<String> wrapText(String text, Font font, int maxWidth) {
		List<String> lines = new ArrayList<>();
		if (text == null || text.isEmpty()) {
			return lines;
		}

		String[] words = text.split(" ");
		StringBuilder currentLine = new StringBuilder();

		for (String word : words) {
			String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

			if (font.width(testLine) <= maxWidth) {
				currentLine = new StringBuilder(testLine);
			} else {
				if (currentLine.length() > 0) {
					lines.add(currentLine.toString());
					currentLine = new StringBuilder(word);
				} else {
					// Palavra muito grande, quebrar por caracteres
					lines.addAll(breakLongWord(word, font, maxWidth));
					currentLine = new StringBuilder();
				}
			}
		}

		if (currentLine.length() > 0) {
			lines.add(currentLine.toString());
		}

		return lines;
	}

	private static List<String> breakLongWord(String word, Font font, int maxWidth) {
		List<String> parts = new ArrayList<>();
		StringBuilder current = new StringBuilder();

		for (char c : word.toCharArray()) {
			String test = current.toString() + c;
			if (font.width(test) <= maxWidth) {
				current.append(c);
			} else {
				if (current.length() > 0) {
					parts.add(current.toString());
					current = new StringBuilder(String.valueOf(c));
				} else {
					parts.add(String.valueOf(c));
				}
			}
		}

		if (current.length() > 0) {
			parts.add(current.toString());
		}

		return parts;
	}
}
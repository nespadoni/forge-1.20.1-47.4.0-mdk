package com.aurorionsmp;

import java.util.HashMap;
import java.util.Map;

public class Config {
	// Configurações do chat
	public static double chatRange = 20.0;

	// Configurações dos balões
	public static double balloonsHeightOffset = 0.9;
	public static int distanceBetweenBalloons = 3;
	public static int maxBalloons = 7;
	public static int minBalloonWidth = 13;
	public static int maxBalloonWidth = 180;
	public static int balloonAge = 15;
	public static boolean showBalloons = true;
	public static boolean showOwnBalloon = true;
	public static String balloonStyle = "rounded";

	// Cores (formato ARGB)
	public static int balloonColor = 0x80FFFFFF;           // Branco semi-transparente
	public static int distantBalloonColor = 0x80FFCCCC;    // Rosa semi-transparente
	public static int textColor = 0xFF000000;              // Preto
	public static int distantTextColor = 0xFF666666;       // Cinza
	public static int borderColor = 0xFF000000;            // Preto

	// Valores legados para compatibilidade
	public static int normalTextColor = textColor;
	public static int balloonDuration = balloonAge * 20;
	public static int balloonPadding = 6;
	public static int borderWidth = 1;

	// Cores predefinidas
	private static final Map<String, Integer> PREDEFINED_COLORS = new HashMap<>();

	static {
		// Cores básicas
		PREDEFINED_COLORS.put("branco", 0x80FFFFFF);
		PREDEFINED_COLORS.put("white", 0x80FFFFFF);

		PREDEFINED_COLORS.put("preto", 0x80000000);
		PREDEFINED_COLORS.put("black", 0x80000000);

		PREDEFINED_COLORS.put("vermelho", 0x80FF0000);
		PREDEFINED_COLORS.put("red", 0x80FF0000);

		PREDEFINED_COLORS.put("verde", 0x8000FF00);
		PREDEFINED_COLORS.put("green", 0x8000FF00);

		PREDEFINED_COLORS.put("azul", 0x800000FF);
		PREDEFINED_COLORS.put("blue", 0x800000FF);

		PREDEFINED_COLORS.put("amarelo", 0x80FFFF00);
		PREDEFINED_COLORS.put("yellow", 0x80FFFF00);

		PREDEFINED_COLORS.put("dourado", 0x80FFD700);
		PREDEFINED_COLORS.put("gold", 0x80FFD700);

		PREDEFINED_COLORS.put("roxo", 0x80800080);
		PREDEFINED_COLORS.put("purple", 0x80800080);

		PREDEFINED_COLORS.put("rosa", 0x80FFC0CB);
		PREDEFINED_COLORS.put("pink", 0x80FFC0CB);

		PREDEFINED_COLORS.put("laranja", 0x80FFA500);
		PREDEFINED_COLORS.put("orange", 0x80FFA500);

		PREDEFINED_COLORS.put("marrom", 0x80964B00);
		PREDEFINED_COLORS.put("brown", 0x80964B00);

		PREDEFINED_COLORS.put("cinza", 0x80808080);
		PREDEFINED_COLORS.put("gray", 0x80808080);
		PREDEFINED_COLORS.put("grey", 0x80808080);
	}

	// Cores de texto predefinidas (sem transparência)
	private static final Map<String, Integer> PREDEFINED_TEXT_COLORS = new HashMap<>();

	static {
		PREDEFINED_TEXT_COLORS.put("branco", 0xFFFFFFFF);
		PREDEFINED_TEXT_COLORS.put("white", 0xFFFFFFFF);

		PREDEFINED_TEXT_COLORS.put("preto", 0xFF000000);
		PREDEFINED_TEXT_COLORS.put("black", 0xFF000000);

		PREDEFINED_TEXT_COLORS.put("vermelho", 0xFFFF0000);
		PREDEFINED_TEXT_COLORS.put("red", 0xFFFF0000);

		PREDEFINED_TEXT_COLORS.put("verde", 0xFF00FF00);
		PREDEFINED_TEXT_COLORS.put("green", 0xFF00FF00);

		PREDEFINED_TEXT_COLORS.put("azul", 0xFF0000FF);
		PREDEFINED_TEXT_COLORS.put("blue", 0xFF0000FF);

		PREDEFINED_TEXT_COLORS.put("amarelo", 0xFFFFFF00);
		PREDEFINED_TEXT_COLORS.put("yellow", 0xFFFFFF00);

		PREDEFINED_TEXT_COLORS.put("dourado", 0xFFFFD700);
		PREDEFINED_TEXT_COLORS.put("gold", 0xFFFFD700);

		PREDEFINED_TEXT_COLORS.put("roxo", 0xFF800080);
		PREDEFINED_TEXT_COLORS.put("purple", 0xFF800080);

		PREDEFINED_TEXT_COLORS.put("rosa", 0xFFFFC0CB);
		PREDEFINED_TEXT_COLORS.put("pink", 0xFFFFC0CB);

		PREDEFINED_TEXT_COLORS.put("laranja", 0xFFFFA500);
		PREDEFINED_TEXT_COLORS.put("orange", 0xFFFFA500);

		PREDEFINED_TEXT_COLORS.put("marrom", 0xFF964B00);
		PREDEFINED_TEXT_COLORS.put("brown", 0xFF964B00);

		PREDEFINED_TEXT_COLORS.put("cinza", 0xFF808080);
		PREDEFINED_TEXT_COLORS.put("gray", 0xFF808080);
		PREDEFINED_TEXT_COLORS.put("grey", 0xFF808080);
	}

	// Métodos para atualizar cores dinamicamente
	public static void setBalloonColor(String color) {
		Integer predefined = PREDEFINED_COLORS.get(color.toLowerCase());
		if (predefined != null) {
			balloonColor = predefined;
		} else {
			balloonColor = parseHexColor(color, balloonColor);
		}
	}

	public static void setDistantBalloonColor(String color) {
		Integer predefined = PREDEFINED_COLORS.get(color.toLowerCase());
		if (predefined != null) {
			distantBalloonColor = predefined;
		} else {
			distantBalloonColor = parseHexColor(color, distantBalloonColor);
		}
	}

	public static void setTextColor(String color) {
		Integer predefined = PREDEFINED_TEXT_COLORS.get(color.toLowerCase());
		if (predefined != null) {
			textColor = predefined;
		} else {
			textColor = parseHexColor(color, textColor);
		}
		normalTextColor = textColor;
	}

	public static void setBorderColor(String color) {
		Integer predefined = PREDEFINED_TEXT_COLORS.get(color.toLowerCase());
		if (predefined != null) {
			borderColor = predefined;
		} else {
			borderColor = parseHexColor(color, borderColor);
		}
	}

	private static int parseHexColor(String hex, int defaultColor) {
		try {
			if (hex.startsWith("#")) {
				hex = hex.substring(1);
			}

			// Se for só RGB, adicionar alpha
			if (hex.length() == 6) {
				return (int) Long.parseLong(hex, 16) | 0x80000000;
			}
			// Se for ARGB completo
			else if (hex.length() == 8) {
				return (int) Long.parseLong(hex, 16);
			}

			return defaultColor;
		} catch (NumberFormatException e) {
			return defaultColor;
		}
	}

	public static boolean isValidPredefinedColor(String color) {
		return PREDEFINED_COLORS.containsKey(color.toLowerCase()) || PREDEFINED_TEXT_COLORS.containsKey(color.toLowerCase());
	}
}
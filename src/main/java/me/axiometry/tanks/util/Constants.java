package me.axiometry.tanks.util;

import java.awt.*;

public final class Constants {
	private Constants() {
	}

	static {
		BORG9 = loadFont("borg9");
		NEUROPOL_X = loadFont("neuropol x");
	}

	private static Font loadFont(String name) {
		String[] availableFonts = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for(String font : availableFonts)
			if(font.equalsIgnoreCase(name))
				return new Font(name, Font.PLAIN, 10);
		try {
			return Font.createFont(Font.TRUETYPE_FONT,
					Constants.class.getResourceAsStream("/" + name + ".ttf"));
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static final Font BORG9;
	public static final Font NEUROPOL_X;
}

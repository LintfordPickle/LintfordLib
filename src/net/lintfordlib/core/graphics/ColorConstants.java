package net.lintfordlib.core.graphics;

// @formatter:off

public class ColorConstants {

	private ColorConstants() {
		// non instantiatable
	}
	
	// --------------------------------------
	// Constant Methods
	// --------------------------------------

	/**
	 * The TempColor object is used to redovide a temporary data storage instance for passing colors to LintfordCore objects.
	 */
	private static final Color TempColor = new Color();

	public static Color getWhiteWithAlpha(float alphalpha) {
		TempColor.setRGBA(1.f, 1.f, 1.f, alphalpha);
		return TempColor;
	}

	public static Color getBlackWithAlpha(float alphalpha) {
		TempColor.setRGBA(0.f, 0.f, 0.f, alphalpha);
		return TempColor;
	}

	public static Color getColorWithAlpha(Color pColor, float alphalpha) {
		TempColor.setRGBA(pColor.r, pColor.g, pColor.b, alphalpha);
		return TempColor;
	}

	public static Color getColor(Color color) {
		TempColor.setFromColor(color);
		return TempColor;
	}

	public static Color getColor(int color) {
		TempColor.setRGBA(((color >> 16) & 0x0000FF) / 255.f, ((color >> 8) & 0x0000FF) / 255.f, ((color >> 0) & 0x0000FF) / 255.f, 1.f);
		return TempColor;
	}

	public static Color getColor(float red, float green, float blue) {
		TempColor.setRGBA(red, green, blue, 1.f);
		return TempColor;
	}

	public static Color getColor(float red, float green, float blue, float alpha) {
		TempColor.setRGBA(red, green, blue, alpha);
		return TempColor;
	}

	public static Color getColorWithRGBMod(float red, float green, float blue, float alpha, float colorMod) {
		TempColor.setRGBA(red * colorMod, green * colorMod, blue * colorMod, alpha);
		return TempColor;
	}

	public static Color getColorWithRGBAMod(float red, float green, float blue, float alpha, float colorMod) {
		TempColor.setRGBA(red * colorMod, green * colorMod, blue * colorMod, alpha * colorMod);
		return TempColor;
	}

	public static Color getColorWithRGBMod(Color color, float colorMod) {
		TempColor.setRGBA(color.r * colorMod, color.g * colorMod, color.b * colorMod, color.a);
		return TempColor;
	}

	public static Color getColorWithRGBAMod(Color color, float colorMod) {
		TempColor.setRGBA(color.r * colorMod, color.g * colorMod, color.b * colorMod, color.a * colorMod);
		return TempColor;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final Color PrimaryColor = new Color(112.f / 255.f, 67.5f / 255.f, 60.f / 255.f, 1.f);
	public static final Color SecondaryColor = new Color(71.f / 255.f, 87.f / 255.f, 130.f / 255.f, 1.f);
	public static final Color TertiaryColor = new Color(73.f / 255.f, 112.f / 255.f, 61.f / 255.f, 1.f);

	public static final Color MenuPanelPrimaryColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, 1.f);
	public static final Color MenuPanelSecondaryColor = new Color(SecondaryColor.r, SecondaryColor.g, SecondaryColor.b, 1.f);
	public static final Color MenuPanelTertiaryColor = new Color(TertiaryColor.r, TertiaryColor.g, TertiaryColor.b, 1.f);

	public static final Color MenuEntryHighlightColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, .32f);
	public static final Color MenuEntrySelectedColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, .64f);

	public static final Color TextHeadingColor = new Color(.96f, .96f, .96f, 1.f);
	public static final Color TextSubHeadingColor = new Color(.94f, .94f, .94f, 1.f);
	public static final Color TextEntryColor = new Color(.96f, .96f, .96f, 1.f);

	public static final Color Debug_Transparent_Magenta = new Color(1.f, .2f, .2f, .3f);

	// Window Clear Color
	public static final Color BUFFER_CLEAR_DEBUG = new Color(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f);
	public static final Color BUFFER_CLEAR_RELEASE = new Color(0.0f / 255.0f, 0.0f / 255.0f, 0.0f / 255.0f);

	// Common Colors
	public static final Color WHITE = new Color(1f, 1f, 1f);
	public static final Color GREY_LIGHT = new Color(200f / 255f, 200f / 255f, 200f / 255f);
	public static final Color GREY_DARK = new Color(100f / 255f, 100f / 255f, 100f / 255f);
	public static final Color BLACK = new Color(0.f, 0.f, 0.f);

	public static final Color RED = new Color(255f / 255f, 0f / 255f, 0f / 255f);
	public static final Color GREEN = new Color(0f / 255f, 255f / 255f, 0f / 255f);
	public static final Color BLUE = new Color(0f / 255f, 0f / 255f, 255f / 255f);
	public static final Color YELLOW = new Color(255f / 255f, 255f / 255f, 0f / 255f);

	// light sources
	public static final Color CANDLE = new Color(255f / 255f, 147f / 255f, 41f / 255f);
	public static final Color FLAME = new Color(255f / 255f, 147f / 255f, 41f / 255f);
	public static final Color TUNGSTEN40 = new Color(255f / 255f, 197f / 255f, 143f / 255f);
	public static final Color TUNGSTEN100 = new Color(255f / 255f, 214f / 255f, 224f / 255f);
	public static final Color HALOGEN = new Color(255f / 255f, 241f / 255f, 224f / 255f);

	// Day
	public static final Color SUN = new Color(255f / 255f, 255f / 255f, 251f / 255f);
	public static final Color BLUE_SKY = new Color(64f / 255f, 156f / 255f, 255f / 255f);
	public static final Color OVERCAST = new Color(201f / 255f, 226f / 255f, 255f / 255f);

	// Skin Tones
	public static final Color SKINTONE00 = Color.GetNewColorFromRGBA(237.f, 207.f, 199.f);
	public static final Color SKINTONE01 = Color.GetNewColorFromRGBA(226.f, 168.f, 152.f);
	public static final Color SKINTONE02 = Color.GetNewColorFromRGBA(204.f, 154.f, 139.f);
	public static final Color SKINTONE03 = Color.GetNewColorFromRGBA(209.f, 148.f, 119.f);
	public static final Color SKINTONE04 = Color.GetNewColorFromRGBA(196.f, 146.f, 107.f);
	public static final Color SKINTONE05 = Color.GetNewColorFromRGBA(89.f, 48.f, 16.f);

	private static final Color[] SKINTONES = new Color[] { SKINTONE00, SKINTONE01, SKINTONE02, SKINTONE03, SKINTONE04, SKINTONE05 };

	public static Color[] skinTones() {
		return SKINTONES;
	}

	private static final Color[] HAIRCOLORS = new Color[] { 
			new Color(19.f / 255.f, 18.f / 255.f, 16.f / 255.f), 
			new Color(19.f / 255.f, 18.f / 255.f, 16.f / 255.f), // Black
			new Color(44.f / 255.f, 34.f / 255.f, 43.f / 255.f), // Off-Black
			new Color(59.f / 255.f, 48.f / 255.f, 38.f / 255.f), // Dark Brown
			new Color(78.f / 255.f, 87.f / 255.f, 63.f / 255.f), // Med. Dark Brown
			new Color(98.f / 255.f, 68.f / 255.f, 69.f / 255.f), // Chestnut Brown
			new Color(112.f / 255.f, 47.f / 255.f, 43.f / 255.f), // Rust
			new Color(106.f / 255.f, 78.f / 255.f, 66.f / 255.f), // Lt. Chestnut Brown
			new Color(85.f / 255.f, 72.f / 255.f, 56.f / 255.f), // Dark Golden Brown
			new Color(167.f / 255.f, 133.f / 255.f, 106.f / 255.f), // Light Golden Brown
			new Color(194.f / 255.f, 151.f / 255.f, 128.f / 255.f), // Dark Honey Blonde
			new Color(220.f / 255.f, 208.f / 255.f, 186.f / 255.f), // Bleached Blonde
			new Color(229.f / 255.f, 200.f / 255.f, 168.f / 255.f), // Pale Golden Blonde
			new Color(165.f / 255.f, 137.f / 255.f, 70.f / 255.f), // Strawberry Blonde
			new Color(145.f / 255.f, 85.f / 255.f, 61.f / 255.f), // Light Auburn
			new Color(225.f / 255.f, 245.f / 255.f, 225.f / 255.f), // White Blonde
			new Color(202.f / 255.f, 191.f / 255.f, 177.f / 255.f), // Plaburn Blonde
			new Color(141.f / 255.f, 74.f / 255.f, 67f / 255.f), // Russet Red
			new Color(191.f / 255.f, 74.f / 255.f, 67f / 255.f), //
			new Color(221.f / 255.f, 54.f / 255.f, 67f / 255.f), //
			new Color(118.f / 255.f, 221.f / 255.f, 17f / 255.f), //
			new Color(78.f / 255.f, 131.f / 255.f, 32f / 255.f), //
			new Color(78.f / 255.f, 131.f / 255.f, 222f / 255.f), //
			new Color(118.f / 255.f, 128.f / 255.f, 189f / 255.f) //
	};

	public static Color[] hairColors() {
		return HAIRCOLORS;
	}
	
}

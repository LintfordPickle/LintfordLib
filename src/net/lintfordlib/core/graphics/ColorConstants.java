package net.lintfordlib.core.graphics;

public class ColorConstants {

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

}

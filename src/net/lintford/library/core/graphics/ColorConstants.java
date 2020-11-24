package net.lintford.library.core.graphics;

public class ColorConstants {

	// --------------------------------------
	// Constant Methods
	// --------------------------------------

	/**
	 * The TempColor object is used to provide a temporary data storage instance for passing colors to LintfordCore objects.
	 */
	private static final Color TempColor = new Color();

	public static Color getWhiteWithAlpha(float pAlpha) {
		TempColor.setRGBA(1.f, 1.f, 1.f, pAlpha);
		return TempColor;
	}

	public static Color getBlackWithAlpha(float pAlpha) {
		TempColor.setRGBA(0.f, 0.f, 0.f, pAlpha);
		return TempColor;
	}

	public static Color getColorWithAlpha(Color pColor, float pAlpha) {
		TempColor.setRGBA(pColor.r, pColor.g, pColor.b, pAlpha);
		return TempColor;
	}

	public static Color getColor(float pR, float pG, float pB) {
		TempColor.setRGBA(pR, pG, pB, 1.f);
		return TempColor;
	}

	public static Color getColor(float pR, float pG, float pB, float pA) {
		TempColor.setRGBA(pR, pG, pB, pA);
		return TempColor;
	}

	public static Color getColorWithRGBMod(float pR, float pG, float pB, float pA, float pColorMod) {
		TempColor.setRGBA(pR * pColorMod, pG * pColorMod, pB * pColorMod, pA);
		return TempColor;
	}

	public static Color getColorWithRGBAMod(float pR, float pG, float pB, float pA, float pColorMod) {
		TempColor.setRGBA(pR * pColorMod, pG * pColorMod, pB * pColorMod, pA * pColorMod);
		return TempColor;
	}

	public static Color getColorWithRGBMod(Color pColor, float pColorMod) {
		TempColor.setRGBA(pColor.r * pColorMod, pColor.g * pColorMod, pColor.b * pColorMod, pColor.a);
		return TempColor;
	}

	public static Color getColorWithRGBAMod(Color pColor, float pColorMod) {
		TempColor.setRGBA(pColor.r * pColorMod, pColor.g * pColorMod, pColor.b * pColorMod, pColor.a * pColorMod);
		return TempColor;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static Color PrimaryColor = new Color(112.f / 255.f, 67.5f / 255.f, 60.f / 255.f, 1.f);
	public static Color SecondaryColor = new Color(71.f / 255.f, 87.f / 255.f, 130.f / 255.f, 1.f);
	public static Color TertiaryColor = new Color(73.f / 255.f, 112.f / 255.f, 61.f / 255.f, 1.f);

	public static Color MenuPanelPrimaryColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, .62f);
	public static Color MenuPanelSecondaryColor = new Color(SecondaryColor.r, SecondaryColor.g, SecondaryColor.b, .62f);
	public static Color MenuPanelTertiaryColor = new Color(TertiaryColor.r, TertiaryColor.g, TertiaryColor.b, .62f);

	public static Color MenuEntryHighlightColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, .32f);

	public static Color TextHeadingColor = new Color(.96f, .96f, .96f);
	public static Color TextSubHeadingColor = new Color(.94f, .94f, .94f);
	public static Color TextEntryColor = new Color(.96f, .96f, .96f);

	public static Color Debug_Transparent_Magenta = new Color(1.f, .2f, .2f, .3f);

	// Window Clear Color
	public final static Color BUFFER_CLEAR_DEBUG = new Color(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f);
	public final static Color BUFFER_CLEAR_RELEASE = new Color(0.0f / 255.0f, 0.0f / 255.0f, 0.0f / 255.0f);

	// Common Colors
	public final static Color WHITE = new Color(1f, 1f, 1f);
	public final static Color GREY_LIGHT = new Color(200f / 255f, 200f / 255f, 200f / 255f);
	public final static Color GREY_DARK = new Color(100f / 255f, 100f / 255f, 100f / 255f);
	public final static Color BLACK = new Color(0.f, 0.f, 0.f);

	public final static Color RED = new Color(255f / 255f, 51f / 255f, 51f / 255f);
	public final static Color GREEN = new Color(51f / 255f, 255f / 255f, 51f / 255f);
	public final static Color YELLOW = new Color(255f / 255f, 255f / 255f, 255f / 255f);
	public final static Color BLUE = new Color(51f / 255f, 51f / 255f, 255f / 255f);

	// light sources
	public final static Color CANDLE = new Color(255f / 255f, 147f / 255f, 41f / 255f);
	public final static Color FLAME = new Color(255f / 255f, 147f / 255f, 41f / 255f);
	public final static Color TUNGSTEN40 = new Color(255f / 255f, 197f / 255f, 143f / 255f);
	public final static Color TUNGSTEN100 = new Color(255f / 255f, 214f / 255f, 224f / 255f);
	public final static Color HALOGEN = new Color(255f / 255f, 241f / 255f, 224f / 255f);

	// Day
	public final static Color SUN = new Color(255f / 255f, 255f / 255f, 251f / 255f);
	public final static Color BLUE_SKY = new Color(64f / 255f, 156f / 255f, 255f / 255f);
	public final static Color OVERCAST = new Color(201f / 255f, 226f / 255f, 255f / 255f);

}

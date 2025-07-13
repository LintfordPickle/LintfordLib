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
	private static final Color tempColor = new Color();

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getWhiteWithAlpha(float alpha) {
		tempColor.setRGBA(1.f, 1.f, 1.f, alpha);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getBlackWithAlpha(float alpha) {
		tempColor.setRGBA(0.f, 0.f, 0.f, alpha);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColorWithAlpha(Color pColor, float alpha) {
		tempColor.setRGBA(pColor.r, pColor.g, pColor.b, alpha);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getNewColor(Color color) {
		return getNewColor(color.r, color.g, color.b, color.a);
	}
	
	public static Color getNewColor(float r, float g, float b, float a) {
		return new Color(r, g, b, a);
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color getTempColorCopy(Color color) {
		tempColor.setFromColor(color);
		return tempColor;
	}
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColor(int color) {
		tempColor.setRGBA(((color >> 16) & 0x0000FF) / 255.f, ((color >> 8) & 0x0000FF) / 255.f, ((color >> 0) & 0x0000FF) / 255.f, 1.f);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColor(float red, float green, float blue) {
		tempColor.setRGBA(red, green, blue, 1.f);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColor(float red, float green, float blue, float alpha) {
		tempColor.setRGBA(red, green, blue, alpha);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColorWithRGBMod(float red, float green, float blue, float alpha, float colorMod) {
		tempColor.setRGBA(red * colorMod, green * colorMod, blue * colorMod, alpha);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColorWithRGBAMod(float red, float green, float blue, float alpha, float colorMod) {
		tempColor.setRGBA(red * colorMod, green * colorMod, blue * colorMod, alpha * colorMod);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColorWithRGBMod(Color color, float colorMod) {
		tempColor.setRGBA(color.r * colorMod, color.g * colorMod, color.b * colorMod, color.a);
		return tempColor;
	}

	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static Color getColorWithRGBAMod(Color color, float colorMod) {
		tempColor.setRGBA(color.r * colorMod, color.g * colorMod, color.b * colorMod, color.a * colorMod);
		return tempColor;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	// Window Clear Color
	public static final Color BUFFER_CLEAR_DEBUG = new Color(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f);
	public static final Color BUFFER_CLEAR_RELEASE = new Color(0.0f / 255.0f, 0.0f / 255.0f, 0.0f / 255.0f);

	// Common Colors
	private static final Color WHITE = new Color(1f, 1f, 1f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color WHITE() { return getTempColorCopy(WHITE); }
	
	private static final Color GREY_LIGHT = new Color(200f / 255f, 200f / 255f, 200f / 255f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color GREY_LIGHT() { return getTempColorCopy(GREY_LIGHT); }
	
	private static final Color GREY_DARK = new Color(100f / 255f, 100f / 255f, 100f / 255f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color GREY_DARK() { return getTempColorCopy(GREY_DARK); }
	
	private static final Color BLACK = new Color(0.f, 0.f, 0.f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color BLACK() { return getTempColorCopy(BLACK); }

	private static final Color RED = new Color(255f / 255f, 0f / 255f, 0f / 255f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color RED() { return getTempColorCopy(RED); }
	
	private static final Color GREEN = new Color(0f / 255f, 255f / 255f, 0f / 255f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color GREEN() { return getTempColorCopy(GREEN); }
	
	private static final Color BLUE = new Color(0f / 255f, 0f / 255f, 255f / 255f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color BLUE() { return getTempColorCopy(BLUE); }
	
	private static final Color YELLOW = new Color(255f / 255f, 255f / 255f, 0f / 255f);
	
	/**
	 * Returns a {@link Color} instance using the <i>shared</i> temp color object (ColorConstants.tempColor).
	 * Note: you cannot rely on the temp color object not being changed between frames!
	 */
	public static final Color YELLOW() { return getTempColorCopy(YELLOW); }

	// TODO: Move these into the 
	
	public static final Color PrimaryColor = new Color(112.f / 255.f, 67.5f / 255.f, 60.f / 255.f, 1.f);
	public static final Color SecondaryColor = new Color(71.f / 255.f, 87.f / 255.f, 130.f / 255.f, 1.f);
	public static final Color TertiaryColor = new Color(73.f / 255.f, 112.f / 255.f, 61.f / 255.f, 1.f);

	public static final Color MenuPanelPrimaryColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, 1.f);
	public static final Color MenuPanelSecondaryColor = new Color(SecondaryColor.r, SecondaryColor.g, SecondaryColor.b, 1.f);
	public static final Color MenuPanelTertiaryColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, .64f);

	public static final Color MenuEntryHighlightColor = new Color(PrimaryColor.r, PrimaryColor.g, PrimaryColor.b, .32f);
	public static final Color MenuEntrySelectedColor = new Color(.75f, .72f, .24f, .4f);

	public static final Color TextHeadingColor = new Color(.96f, .96f, .96f, 1.f);
	public static final Color TextSubHeadingColor = new Color(.94f, .94f, .94f, 1.f);
	public static final Color TextEntryColor = new Color(.96f, .96f, .96f, 1.f);

	public static final Color Debug_Transparent_Magenta = new Color(1.f, .2f, .2f, .3f);

}

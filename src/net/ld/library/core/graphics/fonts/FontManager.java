package net.ld.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import net.ld.library.core.graphics.ResourceManager;

public class FontManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int SYSTEM_FONT_POINTSIZE_SMALL = 12;
	public static final int SYSTEM_FONT_POINTSIZE_NORMAL = 24;
	public static final int SYSTEM_FONT_POINTSIZE_LARGE = 35;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FontUnit mSystemFont;
	private Map<String, FontUnit> mFontMap;

	private ResourceManager mResourceManager;
	private boolean misLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public FontUnit systemFont() {
		return mSystemFont;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontManager() {
		mFontMap = new HashMap<>();

		misLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		System.out.println("FontManager loading GL content");

		for (FontUnit lFont : mFontMap.values()) {
			lFont.loadGLContent(pResourceManager);
		}

		mResourceManager = pResourceManager;

		// Load a default system font
		// We will explicitly add a system font, which will be the fall backfont for all text rendering
		String lSystemFontName = "SystemFont";
		final int lPointSize = 35;
		BitmapFont lBitmapFont = new BitmapFont(lSystemFontName, lPointSize);
		lBitmapFont.loadFontFromResource("/res/fonts/pixel.ttf");
		mSystemFont = new FontUnit(lSystemFontName, lPointSize, lBitmapFont);
		mFontMap.put(lSystemFontName, mSystemFont);
		
		mSystemFont.loadGLContent(pResourceManager);
		
		misLoaded = true;

	}

	public void unloadGLContent() {
		System.out.println("FontManager unloading GL content");

		for (FontUnit lFont : mFontMap.values()) {
			lFont.unloadGLContent();
		}

		mFontMap.clear();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public FontUnit loadFontFromFile(String pName, String pFontPath, int pPointSize) {
		// First check if this font already exists:
		if (mFontMap.containsKey(pName)) {
			// and if so, return the loaded font of the same name.
			return mFontMap.get(pName);
		}

		// Create the bitmap font
		BitmapFont lBitmapFont = new BitmapFont(pName, pPointSize);
		if (!lBitmapFont.loadFontFromFile(pFontPath)) {
			return null;
		}

		FontUnit lNewFont = new FontUnit(pName, pPointSize, lBitmapFont);
		if (misLoaded) {
			lNewFont.loadGLContent(mResourceManager);
		}
		mFontMap.put(pName, lNewFont);

		return lNewFont;

	}

	public FontUnit loadFontFromResource(String pName, String pResourceLocation, int pPointSize) {
		// First check if this font already exists:
		if (mFontMap.containsKey(pName)) {
			// and if so, return the loaded font of the same name.
			return mFontMap.get(pName);
		}

		// Create the bitmap font
		BitmapFont lBitmapFont = new BitmapFont(pName, pPointSize);
		if (!lBitmapFont.loadFontFromResource(pResourceLocation)) {
			return mSystemFont;
		}

		FontUnit lNewFont = new FontUnit(pName, pPointSize, lBitmapFont);
		if (misLoaded) {
			lNewFont.loadGLContent(mResourceManager);
		}
		mFontMap.put(pName, lNewFont);

		return lNewFont;

	}

	public FontUnit getFont(String pFontName) {
		if (mFontMap.containsKey(pFontName)) {
			return mFontMap.get(pFontName);
		}

		System.out.println(pFontName + "doesn't exist (was it loaded correctly?). Returning default system font instead.");

		return mSystemFont;

	}

}

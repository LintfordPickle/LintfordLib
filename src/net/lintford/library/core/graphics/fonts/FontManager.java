package net.lintford.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;

public class FontManager {

	public class FontUnit {

		private static final int MIN_POINTSIZE = 6;
		private static final int MAX_POINTSIZE = 100;

		// --------------------------------------
		// Variables
		// --------------------------------------

		private String mFontName;
		private String mFontPath;
		private boolean mAntiAlias;
		private int mFontPointSize;
		private BitmapFont mBitmapFont;
		private AWTBitmapFontSpriteBatch mFontSpriteBatch;
		private boolean mIsLoaded;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public AWTBitmapFontSpriteBatch spriteBatch() {
			return mFontSpriteBatch;
		}

		public boolean isLoaded() {
			return mIsLoaded;
		}

		public String fontName() {
			return mFontName;
		}

		public String fontPath() {
			return mFontPath;
		}

		public int fontPointSize() {
			return mFontPointSize;
		}

		public BitmapFont bitmap() {
			return mBitmapFont;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public FontUnit(String pFontName, String pFontPath, int pPointSize) {
			this(pFontName, pFontPath, pPointSize, true);

		}

		public FontUnit(String pFontName, String pFontPath, int pPointSize, boolean pAntiAlias) {
			mFontName = pFontName;
			mFontPath = pFontPath;

			mFontPointSize = pPointSize;
			if (mFontPointSize < MIN_POINTSIZE)
				mFontPointSize = MIN_POINTSIZE;

			if (mFontPointSize > MAX_POINTSIZE)
				mFontPointSize = MAX_POINTSIZE;

			mAntiAlias = pAntiAlias;

		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		public void loadGLContent(ResourceManager pResourceManager) {

			mBitmapFont = new BitmapFont(mFontName, mFontPath, mFontPointSize, mAntiAlias);
			mBitmapFont.loadGLContent(pResourceManager);

			mFontSpriteBatch = new AWTBitmapFontSpriteBatch(mBitmapFont);
			mFontSpriteBatch.loadGLContent(pResourceManager);

			mIsLoaded = true;

		}

		public void unloadGLContent() {
			mIsLoaded = false;

		}

		public void begin(ICamera pCamera) {
			mFontSpriteBatch.begin(pCamera);
		}

		public void draw(String pText, float pX, float pY, float pScale) {
			mFontSpriteBatch.draw(pText, pX, pY, pScale);
		}

		public void draw(String pText, float pX, float pY, float pZ, float pScale) {
			mFontSpriteBatch.draw(pText, pX, pY, pZ, pScale, AWTBitmapFontSpriteBatch.NO_WORD_WRAP);
		}

		public void draw(String pText, float pX, float pY, float pZ, float pScale, float pWordWrapWidth) {
			mFontSpriteBatch.draw(pText, pX, pY, pZ, pScale, pWordWrapWidth);
		}

		public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale) {
			mFontSpriteBatch.draw(pText, pX, pY, pZ, pR, pG, pB, pA, pScale, -1);

		}

		public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth) {
			mFontSpriteBatch.draw(pText, pX, pY, pZ, pR, pG, pB, pA, pScale, pWordWrapWidth);
		}

		public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth, int pCapWidth) {
			mFontSpriteBatch.draw(pText, pX, pY, pZ, pR, pG, pB, pA, pScale, pWordWrapWidth, pCapWidth);
		}

		public void end() {
			mFontSpriteBatch.end();

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int SYSTEM_FONT_POINTSIZE_SMALL = 12;
	public static final int SYSTEM_FONT_POINTSIZE_NORMAL = 24;
	public static final int SYSTEM_FONT_POINTSIZE_LARGE = 35;

	// TODO (John): The system font should be dynamically loaded (dependency on external .ttf file).
	public static final String SYSTEM_FONT_NAME = "SystemFont18";
	public static final String SYSTEM_FONT_PATH = " ";
	public static final int SYSTEM_FONT_SIZE = 18;

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

		mSystemFont = new FontUnit(SYSTEM_FONT_NAME, SYSTEM_FONT_PATH, SYSTEM_FONT_SIZE);
		mFontMap.put(SYSTEM_FONT_NAME, mSystemFont);

		misLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		for (FontUnit lFont : mFontMap.values()) {
			lFont.loadGLContent(pResourceManager);
			
		}

		mResourceManager = pResourceManager;
		misLoaded = true;

	}

	public void unloadGLContent() {
		for (FontUnit lFont : mFontMap.values()) {
			lFont.unloadGLContent();
			
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public FontUnit loadNewFont(String pName, String pFontPath, int pPointSize) {
		return this.loadNewFont(pName, pFontPath, pPointSize, true);

	}

	public FontUnit loadNewFont(String pName, String pFontPath, int pPointSize, boolean pAntiAlias) {
		// First check if this font already exists:
		if (mFontMap.containsKey(pName)) {
			// and if so, return the loaded font of the same name.
			return mFontMap.get(pName);
		}

		// First check to see if the fontpath is valid and the font exists
		FontUnit lNewFont = new FontUnit(pName, pFontPath, pPointSize, pAntiAlias);
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

		return mSystemFont;

	}

}

package net.lintford.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;

public class FontManager {

	// FIXME: This seems to be redundant - looks just like AWTBitmapFontSpriteBatch with more fluff!
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
		private boolean mDrawShadow;
		private boolean mTrimText;
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

		public boolean drawShadow() {
			return mDrawShadow;
		}

		public void drawShadow(boolean pNewValue) {
			mDrawShadow = pNewValue;
		}

		public boolean trimText() {
			return mTrimText;
		}

		public void trimText(boolean pNewValue) {
			mTrimText = pNewValue;
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
			mDrawShadow = true;
			mTrimText = true;

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
			draw(pText, pX, pY, -0.1f, pScale);

		}

		public void draw(String pText, float pX, float pY, float pZ, float pScale) {
			draw(pText, pX, pY, pZ, pScale, -1);

		}

		public void draw(String pText, float pX, float pY, float pZ, float pScale, float pWordWrapWidth) {
			draw(pText, pX, pY, pZ, 1f, 1f, 1f, 1f, pScale, pWordWrapWidth);

		}

		public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale) {
			draw(pText, pX, pY, pZ, pR, pG, pB, pA, pScale, -1);

		}

		public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth) {
			draw(pText, pX, pY, pZ, pR, pG, pB, pA, pScale, pWordWrapWidth, AWTBitmapFontSpriteBatch.NO_WIDTH_CAP);

		}

		public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth, int pCapWidth) {
			mFontSpriteBatch.shadowEnabled(mDrawShadow);
			mFontSpriteBatch.trimText(mTrimText);
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

	public static final String SYSTEM_FONT_NAME = "SystemFont18";
	public static final String SYSTEM_FONT_PATH = "res/fonts/OxygenMono.ttf";
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
		return this.loadNewFont(pName, pFontPath, pPointSize, true, false);

	}

	public FontUnit loadNewFont(String pName, String pFontPath, int pPointSize, boolean pReload) {
		return this.loadNewFont(pName, pFontPath, pPointSize, true, pReload);

	}

	public FontUnit loadNewFont(String pName, String pFontPath, int pPointSize, boolean pAntiAlias, boolean pReload) {
		// First check if this font already exists:
		if (mFontMap.containsKey(pName)) {
			if (!pReload)
				return mFontMap.get(pName);

			mFontMap.remove(pName);

		}

		// First check to see if the fontpath is valid and the font exists
		FontUnit lNewFont = new FontUnit(pName, pFontPath, pPointSize, pAntiAlias);
		if (misLoaded) {
			lNewFont.loadGLContent(mResourceManager);

		}

		if (pName.equals(SYSTEM_FONT_NAME)) {
			mSystemFont = lNewFont;
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

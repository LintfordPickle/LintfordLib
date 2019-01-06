package net.lintford.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;

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
		private int mEntityGroupID;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public int entityGroupID() {
			return mEntityGroupID;
		}

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

		public FontUnit(String pFontName, String pFontPath, int pPointSize, int pEntityGroupID) {
			this(pFontName, pFontPath, pPointSize, true, pEntityGroupID);

		}

		public FontUnit(String pFontName, String pFontPath, int pPointSize, boolean pAntiAlias, int pEntityGroupID) {
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
			mEntityGroupID = pEntityGroupID;

		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		public void loadGLContent(ResourceManager pResourceManager) {
			mBitmapFont = new BitmapFont(mFontName, mFontPath, mFontPointSize, mAntiAlias, mEntityGroupID);
			mBitmapFont.loadGLContent(pResourceManager);

			mFontSpriteBatch = new AWTBitmapFontSpriteBatch(mBitmapFont);
			mFontSpriteBatch.loadGLContent(pResourceManager);

			mIsLoaded = true;

		}

		public void unloadGLContent() {
			mBitmapFont.unloadGLContent();
			mFontSpriteBatch.unloadGLContent();
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
	public static final String SYSTEM_FONT_PATH = "/res/fonts/OxygenMono-Regular.ttf";
	public static final int SYSTEM_FONT_SIZE = 18;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FontUnit mSystemFont;
	private Map<Integer, Map<String, FontUnit>> mFontMap;

	private ResourceManager mResourceManager;

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

		mFontMap.put(LintfordCore.CORE_ENTITY_GROUP_ID, new HashMap<>());

		mSystemFont = new FontUnit(SYSTEM_FONT_NAME, SYSTEM_FONT_PATH, SYSTEM_FONT_SIZE, LintfordCore.CORE_ENTITY_GROUP_ID);
		mFontMap.get(LintfordCore.CORE_ENTITY_GROUP_ID).put(SYSTEM_FONT_NAME, mSystemFont);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		Map<String, FontUnit> coreFonts = mFontMap.get(LintfordCore.CORE_ENTITY_GROUP_ID);

		// Load the GL Contents of the CORE fonts only
		if (coreFonts != null) {
			for (FontUnit lFont : coreFonts.values()) {
				lFont.loadGLContent(pResourceManager);

			}

		}

		mResourceManager = pResourceManager;

	}

	public void unloadGLContent() {
		Map<String, FontUnit> coreFonts = mFontMap.get(LintfordCore.CORE_ENTITY_GROUP_ID);

		if (coreFonts != null) {
			for (FontUnit lFont : coreFonts.values()) {
				lFont.unloadGLContent();

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public FontUnit loadNewFont(String pName, String pFontPath, int pPointSize, int pEntityGroupID) {
		return this.loadNewFont(pName, pFontPath, pPointSize, true, false, pEntityGroupID);

	}

	public FontUnit loadNewFont(String pName, String pFontPath, int pPointSize, boolean pReload, int pEntityGroupID) {
		return this.loadNewFont(pName, pFontPath, pPointSize, true, pReload, pEntityGroupID);

	}

	public FontUnit loadNewFont(String pName, String pFontPath, int pPointSize, boolean pAntiAlias, boolean pReload, int pEntityGroupID) {
		Map<String, FontUnit> lFontGroup = mFontMap.get(pEntityGroupID);
		if (lFontGroup == null) {
			lFontGroup = new HashMap<>();
			mFontMap.put(pEntityGroupID, lFontGroup);

		}

		// First check if this font already exists:
		if (lFontGroup.containsKey(pName)) {
			if (!pReload)
				return lFontGroup.get(pName);

			lFontGroup.remove(pName);

		}

		// First check to see if the fontpath is valid and the font exists
		FontUnit lNewFont = new FontUnit(pName, pFontPath, pPointSize, pAntiAlias, pEntityGroupID);
		lNewFont.loadGLContent(mResourceManager);

		if (pEntityGroupID == LintfordCore.CORE_ENTITY_GROUP_ID && pName.equals(SYSTEM_FONT_NAME)) {
			mSystemFont = lNewFont;

		}

		lFontGroup.put(pName, lNewFont);

		return lNewFont;

	}

	public FontUnit getFont(String pFontName, int pEntityGroupID) {
		Map<String, FontUnit> lFontGroup = mFontMap.get(pEntityGroupID);
		if (lFontGroup.containsKey(pFontName)) {
			return lFontGroup.get(pFontName);

		}

		return mSystemFont;

	}

	public void unloadFontGroup(int pEntityGroupID) {
		Map<String, FontUnit> lFontGroup = mFontMap.get(pEntityGroupID);
		if (lFontGroup != null) {
			for (FontUnit lFont : lFontGroup.values()) {
				lFont.unloadGLContent();

			}

		}

		lFontGroup = null;
		mFontMap.remove(pEntityGroupID);

	}

}

package net.lintford.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.storage.FileUtils;

public class FontManager {

	public class FontUnitMetaDataDefinition {
		public int pointsize;
		public String filepath;
		public String fontname;
	}

	public class FontUnitMetaData {
		public FontUnitMetaDataDefinition[] FontUnitMetaDefinitions;

	}

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
		public AWTBitmapFontSpriteBatch mFontSpriteBatch;
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
			mDrawShadow = false;
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

		void unloadGLContent() {
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
			draw(pText, pX, pY, pZ, ColorConstants.WHITE, pScale, pWordWrapWidth);

		}

		public void draw(String pText, float pX, float pY, float pZ, Color pTint, float pScale) {
			draw(pText, pX, pY, pZ, pTint, pScale, -1);

		}

		public void draw(String pText, float pX, float pY, float pZ, Color pTint, float pScale, float pWordWrapWidth) {
			draw(pText, pX, pY, pZ, pTint, pScale, pWordWrapWidth, AWTBitmapFontSpriteBatch.NO_WIDTH_CAP);

		}

		public void draw(String pText, float pX, float pY, float pZ, Color pTint, float pScale, float pWordWrapWidth, int pCapWidth) {
			mFontSpriteBatch.shadowEnabled(mDrawShadow);
			mFontSpriteBatch.trimText(mTrimText);
			mFontSpriteBatch.draw(pText, pX, pY, pZ, pTint, pScale, pWordWrapWidth, pCapWidth);

		}

		public void end() {
			mFontSpriteBatch.end();

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String META_FILE_LOCATION = "/res/fonts/meta.json";

	public static final String FONT_FONTNAME_TEXT = "FONT_CORE_TEXT";
	public static final String FONT_FONTNAME_TITLE = "FONT_CORE_TITLE";
	public static final String FONT_FONTNAME_HEADER = "FONT_CORE_HEADER";

	public static final String FONT_FONTNAME_TOOLTIP = "FONT_TOOLTIP";
	public static final String FONT_FONTNAME_TOAST = "FONT_TOAST";

	// TODO: This should be loaded from an embedded bitmap (not from system font files).
	public static final String SYSTEM_FONT_NAME = "FONT_SYSTEM";
	public static final String SYSTEM_FONT_PATH = "/res/fonts/Rajdhani-Bold.ttf";
	public static final int SYSTEM_FONT_SIZE = 18;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FontUnit mSystemFont;
	Map<String, FontUnit> mFontMap;

	private ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public FontUnit systemFont() {
		return mSystemFont;
	}

	public int fontCount() {
		return mFontMap.size();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontManager() {
		mFontMap = new HashMap<String, FontManager.FontUnit>();

		// Automatically add the system font
		// TODO: Make the system font (which is a fallback) independent of font files
		mSystemFont = new FontUnit(SYSTEM_FONT_NAME, SYSTEM_FONT_PATH, SYSTEM_FONT_SIZE);
		mFontMap.put(SYSTEM_FONT_NAME, mSystemFont);

		loadFontsFromMetafile(META_FILE_LOCATION);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		for (FontUnit lFont : mFontMap.values()) {
			lFont.loadGLContent(pResourceManager);

		}

		mResourceManager = pResourceManager;

	}

	public void unloadGLContent() {
		final var lFontMap = mFontMap;
		for (final var lFontUnit : lFontMap.entrySet()) {
			lFontUnit.getValue().unloadGLContent();

		}

		lFontMap.clear();

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
		if (mResourceManager != null) {
			lNewFont.loadGLContent(mResourceManager);

		}

		if (pName.equals(SYSTEM_FONT_NAME)) {
			mSystemFont = lNewFont;

		}

		mFontMap.put(pName, lNewFont);

		return lNewFont;

	}

	public void loadFontsFromMetafile(String pMetaFileLocation) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading Fonts from meta-file %s", pMetaFileLocation));

		final Gson GSON = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		FontUnitMetaData lFontMetaObject = null;

		lMetaFileContentsString = FileUtils.loadString(pMetaFileLocation);
		lFontMetaObject = GSON.fromJson(lMetaFileContentsString, FontUnitMetaData.class);

		if (lFontMetaObject == null || lFontMetaObject.FontUnitMetaDefinitions == null || lFontMetaObject.FontUnitMetaDefinitions.length == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load fonts from font meta file");
			return;

		}

		final int lNumberOfFontUnitDefinitions = lFontMetaObject.FontUnitMetaDefinitions.length;
		for (int i = 0; i < lNumberOfFontUnitDefinitions; i++) {
			final var lFontUnitDefinition = lFontMetaObject.FontUnitMetaDefinitions[i];
			final var lFontName = lFontUnitDefinition.fontname;
			final var lNewFont = loadNewFont(lFontName, lFontUnitDefinition.filepath, lFontUnitDefinition.pointsize, true, false);

			mFontMap.put(lFontName, lNewFont);

		}

	}

	public FontUnit getFont(String pFontName) {
		if (mFontMap.containsKey(pFontName)) {
			return mFontMap.get(pFontName);

		}

		return mSystemFont;

	}

}

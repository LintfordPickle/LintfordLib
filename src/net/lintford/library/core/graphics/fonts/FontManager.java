package net.lintford.library.core.graphics.fonts;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.lintford.library.core.EntityGroupManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class FontManager extends EntityGroupManager {

	public class FontUnitMetaDataDefinition {
		public int pointsize;
		public String filepath;
		public String fontname;
	}

	public class FontUnitMetaData {
		public FontUnitMetaDataDefinition[] FontUnitMetaDefinitions;

	}

	public class FontGroup {

		// --------------------------------------
		// Variables
		// --------------------------------------

		Map<String, FontUnit> mFontMap;

		boolean automaticUnload = true; // False for CORE resources
		int entityGroupID;
		String name = "";
		int referenceCount = 0;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public FontUnit getFont(String pFontName, int pEntityGroupID) {
			if (mFontMap.containsKey(pFontName)) {
				return mFontMap.get(pFontName);

			}

			return mSystemFont;

		}

		public Map<String, FontUnit> fontMap() {
			return mFontMap;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public FontGroup(int pEntityGroupID) {
			mFontMap = new HashMap<>();

			entityGroupID = pEntityGroupID;

		}

	}

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
		public AWTBitmapFontSpriteBatch mFontSpriteBatch;
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

	public static final String META_FILE_LOCATION = "/res/fonts/meta.json";

	public static final String FONT_FONTNAME_TEXT = "FONT_CORE_TEXT";
	public static final String FONT_FONTNAME_TITLE = "FONT_CORE_TITLE";
	public static final String FONT_FONTNAME_HEADER = "FONT_CORE_HEADER";

	// TODO: This should be loaded from an embedded bitmap (not from system font files).
	public static final String SYSTEM_FONT_NAME = "FONT_SYSTEM";
	public static final String SYSTEM_FONT_PATH = "/res/fonts/Rajdhani-Bold.ttf";
	public static final int SYSTEM_FONT_SIZE = 18;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FontUnit mSystemFont;
	private Map<Integer, FontGroup> mFontGroupMap;

	private ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public FontUnit systemFont() {
		return mSystemFont;
	}

	public int fontGroupCount() {
		return mFontGroupMap.size();
	}

	public FontGroup getFontGroup(int pEntityGroupID) {
		if (!mFontGroupMap.containsKey(pEntityGroupID)) {
			final var lNewFontGroup = new FontGroup(pEntityGroupID);
			mFontGroupMap.put(pEntityGroupID, lNewFontGroup);

			return lNewFontGroup;
		}

		return mFontGroupMap.get(pEntityGroupID);

	}

	public Map<Integer, FontGroup> fontGroups() {
		return mFontGroupMap;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontManager() {
		mFontGroupMap = new HashMap<>();
		FontGroup lCoreFontGroup = new FontGroup(LintfordCore.CORE_ENTITY_GROUP_ID);

		lCoreFontGroup.automaticUnload = false;
		lCoreFontGroup.referenceCount = 1;

		mFontGroupMap.put(LintfordCore.CORE_ENTITY_GROUP_ID, lCoreFontGroup);

		mSystemFont = new FontUnit(SYSTEM_FONT_NAME, SYSTEM_FONT_PATH, SYSTEM_FONT_SIZE, LintfordCore.CORE_ENTITY_GROUP_ID);
		mFontGroupMap.get(LintfordCore.CORE_ENTITY_GROUP_ID).mFontMap.put(SYSTEM_FONT_NAME, mSystemFont);

		loadFontsFromMetafile(META_FILE_LOCATION, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		FontGroup coreFonts = mFontGroupMap.get(LintfordCore.CORE_ENTITY_GROUP_ID);

		// Load the GL Contents of the CORE fonts only
		if (coreFonts != null) {
			for (FontUnit lFont : coreFonts.mFontMap.values()) {
				lFont.loadGLContent(pResourceManager);

			}

		}

		mResourceManager = pResourceManager;

	}

	public void unloadGLContent() {
		Map<Integer, FontGroup> map = mFontGroupMap;
		for (Map.Entry<Integer, FontGroup> entry : map.entrySet()) {

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("TextureGroup %s (%d)..", entry.getValue().name, entry.getValue().entityGroupID));

			FontGroup lFontGroup = entry.getValue();

			Map<String, FontUnit> lGroupMap = lFontGroup.fontMap();
			for (Map.Entry<String, FontUnit> lFontUnit : lGroupMap.entrySet()) {
				lFontUnit.getValue().unloadGLContent();

			}

			lGroupMap.clear();

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
		FontGroup lFontGroup = mFontGroupMap.get(pEntityGroupID);

		if (lFontGroup == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Cannot load font %s for EntityGroupID %d: EntityGroupID does not exist!", pName, pEntityGroupID));
			return null;

		}

		// First check if this font already exists:
		if (lFontGroup.mFontMap.containsKey(pName)) {
			if (!pReload)
				return lFontGroup.mFontMap.get(pName);

			lFontGroup.mFontMap.remove(pName);

		}

		// First check to see if the fontpath is valid and the font exists
		FontUnit lNewFont = new FontUnit(pName, pFontPath, pPointSize, pAntiAlias, pEntityGroupID);
		if (mResourceManager != null) {
			lNewFont.loadGLContent(mResourceManager);

		}

		if (pEntityGroupID == LintfordCore.CORE_ENTITY_GROUP_ID && pName.equals(SYSTEM_FONT_NAME)) {
			mSystemFont = lNewFont;

		}

		lFontGroup.mFontMap.put(pName, lNewFont);

		return lNewFont;

	}

	public void loadFontsFromMetafile(String pMetaFileLocation, int pEntityGroupID) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading Fonts from meta-file %s with EntityGroupID %d", pMetaFileLocation, pEntityGroupID));

		final Gson GSON = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		FontUnitMetaData lFontMetaObject = null;

		lMetaFileContentsString = FileUtils.loadString(pMetaFileLocation);// new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
		lFontMetaObject = GSON.fromJson(lMetaFileContentsString, FontUnitMetaData.class);

		if (lFontMetaObject == null || lFontMetaObject.FontUnitMetaDefinitions == null || lFontMetaObject.FontUnitMetaDefinitions.length == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load fonts from font meta file");
			return;

		}

		final var lFontGroup = getFontGroup(pEntityGroupID);
		if (lFontGroup == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Cannot load font %s for EntityGroupID %d: EntityGroupID does not exist!", (pMetaFileLocation + " (META)"), pEntityGroupID));
			return;

		}

		final int lNumberOfFontUnitDefinitions = lFontMetaObject.FontUnitMetaDefinitions.length;
		for (int i = 0; i < lNumberOfFontUnitDefinitions; i++) {
			final var lFontUnitDefinition = lFontMetaObject.FontUnitMetaDefinitions[i];
			final var lFontName = lFontUnitDefinition.fontname;

			if (!fontExists(lFontName, pEntityGroupID)) {
				final var lNewFont = loadNewFont(lFontName, lFontUnitDefinition.filepath, lFontUnitDefinition.pointsize, true, false, pEntityGroupID);
				lFontGroup.mFontMap.put(lFontName, lNewFont);

			}

		}

	}

	private boolean fontExists(String pFontName, int pEntityGroupID) {
		final var lFontGroup = mFontGroupMap.get(pEntityGroupID);

		if (lFontGroup == null)
			return false;

		return lFontGroup.mFontMap.containsKey(pFontName);
	}

	public FontUnit getFont(String pFontName, int pEntityGroupID) {
		FontGroup lFontGroup = mFontGroupMap.get(pEntityGroupID);

		if (lFontGroup == null)
			return null;

		if (lFontGroup.mFontMap.containsKey(pFontName)) {
			return lFontGroup.mFontMap.get(pFontName);

		}

		return mSystemFont;

	}

	public void unloadFontGroup(int pEntityGroupID) {
		FontGroup lFontGroup = mFontGroupMap.get(pEntityGroupID);
		if (lFontGroup != null) {
			for (FontUnit lFont : lFontGroup.mFontMap.values()) {
				lFont.unloadGLContent();

			}

		}

		lFontGroup = null;
		mFontGroupMap.remove(pEntityGroupID);

	}

	@Override
	public int increaseReferenceCounts(int pEntityGroupID) {
		FontGroup lFontGroup = mFontGroupMap.get(pEntityGroupID);

		if (lFontGroup == null) {
			lFontGroup = new FontGroup(pEntityGroupID);
			lFontGroup.referenceCount = 1;

			mFontGroupMap.put(pEntityGroupID, lFontGroup);

		} else {
			lFontGroup.referenceCount++;

		}

		return lFontGroup.referenceCount;

	}

	@Override
	public int decreaseReferenceCounts(int pEntityGroupID) {
		FontGroup lTextureGroup = mFontGroupMap.get(pEntityGroupID);

		// Create a new TextureGroup for this EntityGroupID if one doesn't exist
		if (lTextureGroup == null) {
			return 0;

		} else {
			lTextureGroup.referenceCount--;

		}

		if (lTextureGroup.referenceCount <= 0) {
			// Unload textures for this entityGroupID
			// unloadEntityGroup(pEntityGroupID);

			mFontGroupMap.remove(pEntityGroupID);
			lTextureGroup = null;

			return 0;

		}

		return lTextureGroup.referenceCount;

	}

}

package net.lintford.library.core.graphics.fonts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class BitmapFontManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class BitmapFontDataDefinition {
		public String fontName;
		public String filepath;
	}

	public class BitmapFontMetaData {
		public BitmapFontDataDefinition[] bitmapFontLocations;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public static final String SYSTEM_FONT_CORE_NAME = "FONT_SYSTEM_CORE";
	public static final String SYSTEM_FONT_CONSOLE_NAME = "FONT_SYSTEM_CONSOLE";

	private Map<Integer, Map<String, FontUnit>> mFontUnitGroups;
	private ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns the {@link FontUnit} which encapsulates a {@link BitmapFontDefinition} (A texture atlas of glyphs) and a {@link BitmapFontRenderer}. All FontUnits are shared, and are unique based on Fontfile and Point
	 * size.
	 */
	public FontUnit getFontUnit(String pBitmapFontName, int pEntityGroupID) {
		Map<String, FontUnit> lBitmapFontGroup = mFontUnitGroups.get(pEntityGroupID);
		if (lBitmapFontGroup != null) {
			return lBitmapFontGroup.get(pBitmapFontName);
		}

		return null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/** Creates a new instance of {@link BitmapFontManager}. */
	public BitmapFontManager() {
		mFontUnitGroups = new HashMap<>();

		// Create default FontUnit group for the engine
		mFontUnitGroups.put(LintfordCore.CORE_ENTITY_GROUP_ID, new HashMap<String, FontUnit>());
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;

		loadBitmapFontDefinitionFromResource(SYSTEM_FONT_CORE_NAME, "/res/fonts/fontCore.json", LintfordCore.CORE_ENTITY_GROUP_ID);
		loadBitmapFontDefinitionFromResource(SYSTEM_FONT_CONSOLE_NAME, "/res/fonts/fontConsole.json", LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	public void unloadGLContent() {

	}

	// --------------------------------------

	public void loadBitmapFontDefinitionFromMeta(final String pMetaFileLocation, int pEntityGroupID) {
		if (pMetaFileLocation == null || pMetaFileLocation.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file cannot be null or empty when loading SpriteSheets.");
			return;

		}

		final Gson GSON = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		BitmapFontMetaData lSpriteMetaObject = null;
		try {
			lMetaFileContentsString = new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
			lSpriteMetaObject = GSON.fromJson(lMetaFileContentsString, BitmapFontMetaData.class);

			if (lSpriteMetaObject == null || lSpriteMetaObject.bitmapFontLocations == null || lSpriteMetaObject.bitmapFontLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load bitmap font definitions from meta file");

				return;

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		final int lSpriteCount = lSpriteMetaObject.bitmapFontLocations.length;
		for (int i = 0; i < lSpriteCount; i++) {
			BitmapFontDataDefinition lFontDataDefinition = lSpriteMetaObject.bitmapFontLocations[i];

			if (lFontDataDefinition == null)
				continue;

			loadBitmapFont(lFontDataDefinition.fontName, lFontDataDefinition.filepath, pEntityGroupID);
		}
	}

	public FontUnit loadBitmapFont(String pFontName, String pFilepath, int pEntityGroupID) {
		FontUnit lExistingFontUnit = getFontUnit(pFontName, pEntityGroupID);
		if (lExistingFontUnit != null) {
			return lExistingFontUnit;
		}

		if (pFilepath == null || pFilepath.length() == 0) {
			Debug.debugManager().logger().v(getClass().getSimpleName(), "Error loading bitmap font definition. Pathname is null! ");
			return null;
		}

		if (pFilepath.charAt(0) == '/') {
			return loadBitmapFontDefinitionFromResource(pFontName, pFilepath, pEntityGroupID);

		} else {
			return loadBitmapFontDefinitionFromFile(pFontName, pFilepath, pEntityGroupID);
		}
	}

	private FontUnit loadBitmapFontDefinitionFromFile(String pFontname, String pFilepath, int pEntityGroupID) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + pFilepath + " doesn't exist!");
			return null;

		}

		final Gson GSON = new GsonBuilder().create();

		try {
			final String lFileContents = new String(Files.readAllBytes(lFile.toPath()));
			final BitmapFontDefinition lBitmapFontDefinition = GSON.fromJson(lFileContents, BitmapFontDefinition.class);

			return createFontUnit(pFontname, lBitmapFontDefinition, pEntityGroupID);

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (Syntax): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (IO): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		}
	}

	public FontUnit loadBitmapFontDefinitionFromResource(String pFontname, String pFilepath, int pEntityGroupID) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		final Gson GSON = new GsonBuilder().create();

		try {

			InputStream lInputStream = FileUtils.class.getResourceAsStream(pFilepath);

			JsonReader reader = new JsonReader(new InputStreamReader(lInputStream, "UTF-8"));

			BitmapFontDefinition lBitmapFontDefinition = null;
			try {
				lBitmapFontDefinition = GSON.fromJson(reader, BitmapFontDefinition.class);
			} catch (JsonSyntaxException ex) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Error deserializing BitmapFont (JsonSyntaxException) " + ex.getMessage());
				return null;
			}

			if (lBitmapFontDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading spritesheet " + pFilepath);
				return null;

			}

			return createFontUnit(pFontname, lBitmapFontDefinition, pEntityGroupID);

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + pFilepath);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + pFilepath);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		}
	}

	private FontUnit createFontUnit(String pFontname, BitmapFontDefinition pBitmapFontDefintion, int pEntityGroupID) {
		FontUnit lNewFontUnit = new FontUnit(0, pBitmapFontDefintion, pEntityGroupID);
		lNewFontUnit.onLoadGlContent(mResourceManager);
		pBitmapFontDefintion.reloadable(false);

		Map<String, FontUnit> lFontUnitGroup = mFontUnitGroups.get(pEntityGroupID);
		if (lFontUnitGroup == null) {
			lFontUnitGroup = new HashMap<>();
			mFontUnitGroups.put(pEntityGroupID, lFontUnitGroup);
		}

		lFontUnitGroup.put(pFontname, lNewFontUnit);

		return lNewFontUnit;
	}
}
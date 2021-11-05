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

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.fonts.FontMetaData.BitmapFontDataDefinition;
import net.lintford.library.core.storage.FileUtils;

public class BitmapFontManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public static final FontMetaData CoreFonts = new FontMetaData();

	public class BitmapFontMetaData {
		public BitmapFontDataDefinition[] bitmapFontLocations;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public static final String SYSTEM_FONT_CORE_TEXT_NAME = "FONT_SYSTEM_CORE_TEXT";
	public static final String SYSTEM_FONT_CORE_TITLE_NAME = "FONT_SYSTEM_CORE_TITLE";
	public static final String SYSTEM_FONT_CONSOLE_NAME = "FONT_SYSTEM_CONSOLE";

	private Map<String, FontUnit> mFontUnits;
	private ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public FontUnit getFontUnit(String pBitmapFontName) {
		if (getFontUnitExists(pBitmapFontName) == false) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to retrieve FontUnit: " + pBitmapFontName);
			return getCoreFont();
		}
		return mFontUnits.get(pBitmapFontName);
	}

	public boolean getFontUnitExists(String pBitmapFontName) {
		return mFontUnits.containsKey(pBitmapFontName);
	}

	public FontUnit getCoreFont() {
		return mFontUnits.get(SYSTEM_FONT_CORE_TEXT_NAME);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/** Creates a new instance of {@link BitmapFontManager}. */
	public BitmapFontManager() {
		mFontUnits = new HashMap<>();

		CoreFonts.AddIfNotExists(SYSTEM_FONT_CORE_TEXT_NAME, "/res/fonts/fontCoreText.json");
		CoreFonts.AddIfNotExists(SYSTEM_FONT_CORE_TITLE_NAME, "/res/fonts/fontCoreTitle.json");
		CoreFonts.AddIfNotExists(SYSTEM_FONT_CONSOLE_NAME, "/res/fonts/fontConsole.json");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;
	}

	// --------------------------------------

	public void loadBitmapFontDefinitionsFromMetaData(FontMetaData pFontMetaData) {
		final int lSpriteCount = pFontMetaData.items.size();
		for (int i = 0; i < lSpriteCount; i++) {
			var lFontDataDefinition = pFontMetaData.items.get(i);

			if (lFontDataDefinition == null)
				continue;

			loadBitmapFont(lFontDataDefinition.fontName, lFontDataDefinition.filepath);
		}
	}

	public void loadBitmapFontDefinitionFromMetaFile(final String pMetaFileLocation) {
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

			loadBitmapFont(lFontDataDefinition.fontName, lFontDataDefinition.filepath);
		}
	}

	public FontUnit loadBitmapFont(String pFontName, String pFilepath) {
		if (getFontUnitExists(pFontName)) {
			return getFontUnit(pFontName);
		}

		if (pFilepath == null || pFilepath.length() == 0) {
			Debug.debugManager().logger().v(getClass().getSimpleName(), "Error loading bitmap font definition. Pathname is null! ");
			return null;
		}

		if (pFilepath.charAt(0) == '/') {
			return loadBitmapFontDefinitionFromResource(pFontName, pFilepath);

		} else {
			return loadBitmapFontDefinitionFromFile(pFontName, pFilepath);
		}
	}

	private FontUnit loadBitmapFontDefinitionFromFile(String pFontname, String pFilepath) {
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

			return createFontUnit(pFontname, lBitmapFontDefinition);

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

	public FontUnit loadBitmapFontDefinitionFromResource(String pFontname, String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		final Gson GSON = new GsonBuilder().create();

		try {

			InputStream lInputStream = FileUtils.class.getResourceAsStream(pFilepath);

			if (lInputStream == null) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to load BitmapFontDefinition '" + pFontname + "'. The resource '" + pFilepath + "' doesn't exist");
				return null;
			}

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

			return createFontUnit(pFontname, lBitmapFontDefinition);

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

	private FontUnit createFontUnit(String pFontname, BitmapFontDefinition pBitmapFontDefintion) {
		FontUnit lNewFontUnit = new FontUnit(0, pBitmapFontDefintion);
		lNewFontUnit.loadResources(mResourceManager);
		pBitmapFontDefintion.reloadable(false);
		mFontUnits.put(pFontname, lNewFontUnit);

		return lNewFontUnit;
	}
}
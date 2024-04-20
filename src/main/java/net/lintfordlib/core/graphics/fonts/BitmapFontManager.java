package net.lintfordlib.core.graphics.fonts;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.fonts.FontMetaData.BitmapFontDataDefinition;
import net.lintfordlib.core.storage.FileUtils;

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

	public FontUnit getFontUnit(String bitmapFontName) {
		if (getFontUnitExists(bitmapFontName) == false) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to retrieve FontUnit: " + bitmapFontName);
			return getCoreFont();
		}
		return mFontUnits.get(bitmapFontName);
	}

	public boolean getFontUnitExists(String bitmapFontName) {
		return mFontUnits.containsKey(bitmapFontName);
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

	public void initialize(ResourceManager resourceManager) {
		mResourceManager = resourceManager;
	}

	// --------------------------------------

	public void loadBitmapFontDefinitionsFromMetaData(FontMetaData fontMetaData) {
		final int lSpriteCount = fontMetaData.items.size();
		for (int i = 0; i < lSpriteCount; i++) {
			var lFontDataDefinition = fontMetaData.items.get(i);

			if (lFontDataDefinition == null)
				continue;

			loadBitmapFont(lFontDataDefinition.fontName, lFontDataDefinition.filepath);
		}
	}

	public void loadBitmapFontDefinitionFromMetaFile(final String metaFileLocation) {
		if (metaFileLocation == null || metaFileLocation.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file cannot be null or empty when loading SpriteSheets.");
			return;
		}

		final var lGson = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		BitmapFontMetaData lSpriteMetaObject = null;
		try {
			lMetaFileContentsString = new String(Files.readAllBytes(Paths.get(metaFileLocation)));
			lSpriteMetaObject = lGson.fromJson(lMetaFileContentsString, BitmapFontMetaData.class);

			if (lSpriteMetaObject == null || lSpriteMetaObject.bitmapFontLocations == null || lSpriteMetaObject.bitmapFontLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load bitmap font definitions from meta file");
				return;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		final int lSpriteCount = lSpriteMetaObject.bitmapFontLocations.length;
		for (int i = 0; i < lSpriteCount; i++) {
			final var lFontDataDefinition = lSpriteMetaObject.bitmapFontLocations[i];

			if (lFontDataDefinition == null)
				continue;

			loadBitmapFont(lFontDataDefinition.fontName, lFontDataDefinition.filepath);
		}
	}

	public FontUnit loadBitmapFont(String fontName, String filepath) {
		if (getFontUnitExists(fontName)) {
			return getFontUnit(fontName);
		}

		if (filepath == null || filepath.length() == 0) {
			Debug.debugManager().logger().v(getClass().getSimpleName(), "Error loading bitmap font definition. Pathname is null! ");
			return null;
		}

		if (filepath.charAt(0) == '/') {
			return loadBitmapFontDefinitionFromResource(fontName, filepath);
		} else {
			return loadBitmapFontDefinitionFromFile(fontName, filepath);
		}
	}

	private FontUnit loadBitmapFontDefinitionFromFile(String fontname, String filepath) {
		if (filepath == null || filepath.length() == 0)
			return null;

		final var lFile = new File(filepath);
		if (!lFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + filepath + " doesn't exist!");
			return null;
		}

		final var lGson = new GsonBuilder().create();

		try {
			final String lFileContents = new String(Files.readAllBytes(lFile.toPath()));
			final BitmapFontDefinition lBitmapFontDefinition = lGson.fromJson(lFileContents, BitmapFontDefinition.class);

			return createFontUnit(fontname, lBitmapFontDefinition);
		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (Syntax): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (IO): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}

		return null;
	}

	public FontUnit loadBitmapFontDefinitionFromResource(String fontname, String filepath) {
		if (filepath == null || filepath.length() == 0)
			return null;

		final var lGson = new GsonBuilder().create();

		try {
			final var lInputStream = FileUtils.class.getResourceAsStream(filepath);

			if (lInputStream == null) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to load BitmapFontDefinition '" + fontname + "'. The resource '" + filepath + "' doesn't exist");
				return null;
			}

			final var lJSonReader = new JsonReader(new InputStreamReader(lInputStream, "UTF-8"));

			BitmapFontDefinition lBitmapFontDefinition = null;
			try {
				lBitmapFontDefinition = lGson.fromJson(lJSonReader, BitmapFontDefinition.class);
			} catch (JsonSyntaxException ex) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Error deserializing BitmapFont (JsonSyntaxException) " + ex.getMessage());
				return null;
			}

			if (lBitmapFontDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading spritesheet " + filepath);
				return null;
			}

			return createFontUnit(fontname, lBitmapFontDefinition);

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + filepath);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + filepath);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		}
	}

	private FontUnit createFontUnit(String fontname, BitmapFontDefinition bitmapFontDefintion) {
		final var lNewFontUnit = new FontUnit(bitmapFontDefintion);
		lNewFontUnit.loadResources(mResourceManager);
		bitmapFontDefintion.reloadable(false);
		mFontUnits.put(fontname, lNewFontUnit);

		return lNewFontUnit;
	}
}
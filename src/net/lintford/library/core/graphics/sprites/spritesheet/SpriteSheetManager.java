package net.lintford.library.core.graphics.sprites.spritesheet;

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

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.storage.FileUtils;

public class SpriteSheetManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class SpriteSheetMetaData {
		public String[] spriteSheetLocations;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Contains a collection of SpriteSheets which has been loaded by this {@link SpriteSheetManager}. */
	private Map<String, SpriteSheetDef> spriteSheetMap;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the {@link SpriteSheetDef} to which the specified key string is mapped, or null if no such {@link SpriteSheetDef} exists. */
	public SpriteSheetDef getSpriteSheet(String string) {
		return this.spriteSheetMap.get(string);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/** Creates a new instance of {@link SpriteSheetManager}. */
	public SpriteSheetManager() {
		this.spriteSheetMap = new HashMap<String, SpriteSheetDef>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {
		// for (Map.Entry<String, SpriteSheetDef> entry : this.spriteSheetMap.entrySet()) {
		// SpriteSheetDef lSpriteSheet = entry.getValue();
		//
		// lSpriteSheet.initialise(this);
		//
		// }

	}

	public SpriteSheetDef loadSpriteSheet(String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0) {
			DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "Error loading spritesheet. Pathname is null! ");
			return null;
		}

		if (pFilepath.charAt(0) == '/') {
			return loadSpriteSheetFromResource(pFilepath);

		} else {
			return loadSpriteSheetFromFile(pFilepath);

		}

	}

	private SpriteSheetDef loadSpriteSheetFromFile(String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + pFilepath + " doesn't exist!");
			return null;

		}

		final Gson GSON = new GsonBuilder().create();

		try {

			final String lFileContents = new String(Files.readAllBytes(lFile.toPath()));
			final SpriteSheetDef lSpriteSheet = GSON.fromJson(lFileContents, SpriteSheetDef.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteSheet == null || lSpriteSheet.getSpriteCount() == 0) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Error loading spritesheet " + lFile.getPath());
				return null;

			}

			if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
				DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "SpriteSheet " + lFile.getPath() + " loaded (" + lSpriteSheet.spriteSheetName + ")");

			}

			lSpriteSheet.fileSizeOnLoad(lFile.length());
			lSpriteSheet.spriteSheetFilename = lFile.getPath();
			lSpriteSheet.reloadable(true);
			lSpriteSheet.loadGLContent();

			// Add the spritesheet to the collection, using the FILENAME as the key
			this.spriteSheetMap.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			return lSpriteSheet;

		} catch (JsonSyntaxException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + lFile.getPath());
			DebugManager.DEBUG_MANAGER.logger().printException(getClass().getSimpleName(), e);
			return null;

		} catch (IOException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + lFile.getPath());
			DebugManager.DEBUG_MANAGER.logger().printException(getClass().getSimpleName(), e);
			return null;

		}
	}

	private SpriteSheetDef loadSpriteSheetFromResource(String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + pFilepath + " doesn't exist!");
			return null;

		}

		final Gson GSON = new GsonBuilder().create();

		try {

			InputStream lInputStream = FileUtils.class.getResourceAsStream(pFilepath);

			JsonReader reader = new JsonReader(new InputStreamReader(lInputStream, "UTF-8"));

			final SpriteSheetDef lSpriteSheet = GSON.fromJson(reader, SpriteSheetDef.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteSheet == null || lSpriteSheet.getSpriteCount() == 0) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Error loading spritesheet " + lFile.getPath());
				return null;

			}

			if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
				DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "SpriteSheet " + lFile.getPath() + " loaded (" + lSpriteSheet.spriteSheetName + ")");

			}

			lSpriteSheet.reloadable(false);
			lSpriteSheet.loadGLContent();

			// Add the spritesheet to the collection, using the FILENAME as the key
			this.spriteSheetMap.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			return lSpriteSheet;

		} catch (JsonSyntaxException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + lFile.getPath());
			DebugManager.DEBUG_MANAGER.logger().printException(getClass().getSimpleName(), e);
			return null;

		} catch (IOException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + lFile.getPath());
			DebugManager.DEBUG_MANAGER.logger().printException(getClass().getSimpleName(), e);
			return null;

		}
	}

	/** Loads a set of spritesheets from a meta file at the given location. */
	public void loadSpriteSheetFromMeta(final String pMetaFileLocation) {
		if (pMetaFileLocation == null || pMetaFileLocation.length() == 0) {
			DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file cannot be null or empty when loading SpriteSheets.");
			return;

		}

		final Gson GSON = new GsonBuilder().create();

		// Load the Sprite meta data
		String lMetaFileContentsString = null;
		SpriteSheetMetaData lSpriteMetaObject = null;
		try {
			lMetaFileContentsString = new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
			lSpriteMetaObject = GSON.fromJson(lMetaFileContentsString, SpriteSheetMetaData.class);

			if (lSpriteMetaObject == null || lSpriteMetaObject.spriteSheetLocations == null || lSpriteMetaObject.spriteSheetLocations.length == 0) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Couldn't load sprites from sprite meta file");

				return;

			}
		} catch (IOException e1) {
			e1.printStackTrace();

		}

		// Iterate through the sprite files, and load the individual sprites
		final int SPRITE_COUNT = lSpriteMetaObject.spriteSheetLocations.length;
		for (int i = 0; i < SPRITE_COUNT; i++) {
			final File lSpriteSheetFile = new File(lSpriteMetaObject.spriteSheetLocations[i]);

			if (!lSpriteSheetFile.exists()) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Error loading sprite sheet from " + lSpriteSheetFile.getPath() + " doesn't exist!");

				continue;

			}

			try {

				final String lSpriteSheetFileContents = new String(Files.readAllBytes(lSpriteSheetFile.toPath()));
				final SpriteSheetDef lSpriteSheet = GSON.fromJson(lSpriteSheetFileContents, SpriteSheetDef.class);

				// Check the integrity of the loaded spritsheet
				if (lSpriteSheet == null) {
					DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Error loading spritesheet " + lSpriteSheetFile.getPath());
					continue;

				}

				if (lSpriteSheet.spriteMap == null || lSpriteSheet.spriteMap.size() == 0) {
					DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Loaded SpriteSheetDefinition which has no Sprites defined within: " + lSpriteSheetFile.getPath());

				}

				lSpriteSheet.fileSizeOnLoad(lSpriteSheetFile.length());
				lSpriteSheet.spriteSheetFilename = lSpriteSheetFile.getPath();
				lSpriteSheet.loadGLContent();

				if (lSpriteSheet.spriteMap == null || lSpriteSheet.spriteMap.size() == 0) {
					DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Loaded SpriteSheetDefinition which has neither sprites nor frames defined within: " + lSpriteSheetFile.getPath());

				}

				// Add the spritesheet to the collection, using the FILENAME as the key
				this.spriteSheetMap.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			} catch (JsonSyntaxException e) {
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (Syntax): " + lSpriteSheetFile.getPath());
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), e.getMessage());
				continue;

			} catch (IOException e) {
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (IO): " + lSpriteSheetFile.getPath());
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), e.getMessage());
				continue;

			}

		}

	}

	public void reload() {

		final Gson GSON = new GsonBuilder().create();

		for (Map.Entry<String, SpriteSheetDef> entry : this.spriteSheetMap.entrySet()) {
			SpriteSheetDef lSpriteSheet = entry.getValue();
			if (!lSpriteSheet.reloadable)
				continue;

			File lSpriteSheetFile = new File(lSpriteSheet.spriteSheetFilename);
			if (lSpriteSheetFile.length() != lSpriteSheet.fileSizeOnLoad()) {
				//
				DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Reloading SpriteSheet (size on disk change detected).");

				try {

					final String lSpriteSheetFileContents = new String(Files.readAllBytes(lSpriteSheetFile.toPath()));
					final SpriteSheetDef lNewSpriteSheet = GSON.fromJson(lSpriteSheetFileContents, SpriteSheetDef.class);

					lNewSpriteSheet.loadGLContent();

					lSpriteSheet.unloadGLContent();

					entry.setValue(lNewSpriteSheet);

				} catch (JsonSyntaxException e) {
					DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (Syntax): " + lSpriteSheetFile.getPath());
					DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), e.getMessage());

					continue;

				} catch (IOException e) {
					DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (IO): " + lSpriteSheetFile.getPath());
					DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), e.getMessage());

					continue;

				}

			}

		}

	}

}

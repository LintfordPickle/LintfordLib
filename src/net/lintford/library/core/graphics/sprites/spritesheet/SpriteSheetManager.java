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

	/** A class which holds meta information about {@link SpriteSheet}s to be loaded. */
	public class SpriteMeta {
		public String Sprite_Directory;

		public String[] Sprite_Files;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Contains a collection of SpriteSheets which has been loaded by this {@link SpriteSheetManager}. */
	private Map<String, SpriteSheet> spriteSheetMap;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/** Creates a new instance of {@link SpriteSheetManager}. */
	public SpriteSheetManager() {
		this.spriteSheetMap = new HashMap<String, SpriteSheet>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public SpriteSheet loadSpriteSheet(String pFilepath) {
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

	private SpriteSheet loadSpriteSheetFromFile(String pFilepath) {
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
			final SpriteSheet lSpriteSheet = GSON.fromJson(lFileContents, SpriteSheet.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteSheet == null || lSpriteSheet.getSpriteCount() == 0) {
				System.err.println("Error loading spritesheet " + lFile.getPath());
				return null;

			}

			if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
				DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "SpriteSheet " + lFile.getPath() + " loaded (" + lSpriteSheet.spriteSheetName + ")");

			}

			lSpriteSheet.loadGLContent();

			// Add the spritesheet to the collection, using the FILENAME as the key
			this.spriteSheetMap.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			return lSpriteSheet;

		} catch (JsonSyntaxException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + lFile.getPath());
			return null;

		} catch (IOException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + lFile.getPath());
			return null;

		}
	}

	private SpriteSheet loadSpriteSheetFromResource(String pFilepath) {
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

			final SpriteSheet lSpriteSheet = GSON.fromJson(reader, SpriteSheet.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteSheet == null || lSpriteSheet.getSpriteCount() == 0) {
				DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "Error loading spritesheet " + lFile.getPath());
				return null;

			}

			if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
				DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "SpriteSheet " + lFile.getPath() + " loaded (" + lSpriteSheet.spriteSheetName + ")");

			}

			lSpriteSheet.loadGLContent();

			// Add the spritesheet to the collection, using the FILENAME as the key
			this.spriteSheetMap.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			return lSpriteSheet;

		} catch (JsonSyntaxException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + lFile.getPath());
			return null;

		} catch (IOException e) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + lFile.getPath());
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
		String META_CONTENTS = null;
		SpriteMeta SPRITE_META = null;
		try {
			META_CONTENTS = new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
			SPRITE_META = GSON.fromJson(META_CONTENTS, SpriteMeta.class);

			if (SPRITE_META == null || SPRITE_META.Sprite_Files == null || SPRITE_META.Sprite_Files.length == 0) {
				System.out.println();
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Couldn't load sprites from sprite meta file");

				return;

			}
		} catch (IOException e1) {
			e1.printStackTrace();

		}

		if (SPRITE_META == null) {
			DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Unable to load SpriteSheet Meta file from location: " + pMetaFileLocation);

			return;
		}

		// Iterate through the sprite files, and load the individual sprites
		final int SPRITE_COUNT = SPRITE_META.Sprite_Files.length;
		for (int i = 0; i < SPRITE_COUNT; i++) {
			final File SPRITE_FILE = new File(SPRITE_META.Sprite_Directory + SPRITE_META.Sprite_Files[i]);

			if (!SPRITE_FILE.exists()) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Error loading sprite sheet from " + SPRITE_FILE.getPath() + " doesn't exist!");

				continue;

			}

			try {

				final String SPRITE_FILE_CONTENTS = new String(Files.readAllBytes(SPRITE_FILE.toPath()));
				final SpriteSheet SPRITE_SHEET = GSON.fromJson(SPRITE_FILE_CONTENTS, SpriteSheet.class);

				// Check the integrity of the loaded spritsheet
				if (SPRITE_SHEET == null || SPRITE_SHEET.getSpriteCount() == 0) {
					System.err.println("Error loading spritesheet " + SPRITE_FILE.getPath());
					continue;

				}

				SPRITE_SHEET.loadGLContent();

				// Add the spritesheet to the collection, using the FILENAME as the key
				this.spriteSheetMap.put(SPRITE_SHEET.spriteSheetName, SPRITE_SHEET);

			} catch (JsonSyntaxException e) {
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + SPRITE_FILE.getPath());

				continue;

			} catch (IOException e) {
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + SPRITE_FILE.getPath());

				continue;

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the {@link SpriteSheet} to which the specified key string is mapped, or null if no such {@link SpriteSheet} exists. */
	public SpriteSheet getSpriteSheet(String string) {
		return this.spriteSheetMap.get(string);

	}

}

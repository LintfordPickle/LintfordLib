package net.ld.library.core.graphics.sprites;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class SpriteSheetManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	/**
	 * A class which holds meta information about {@link SpriteSheet}s to be
	 * loaded.
	 */
	public class SpriteMeta {
		public String Sprite_Directory;

		public String[] Sprite_Files;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	/**
	 * Contains a collection of SpriteSheets which has been loaded by this
	 * {@link SpriteSheetManager}.
	 */
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

	/** Loads a set of spritesheets from a meta file at the given location. */
	public void loadSpriteSheetFromMeta(final String pMetaFileLocation) {
		if (pMetaFileLocation == null || pMetaFileLocation.length() == 0) {
			System.err.println("SpriteSheetManager meta file cannot be null or empty when loading SpriteSheets.");
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
				System.out.println("Couldn't load sprites from sprite meta file");
				return;

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (SPRITE_META == null) {
			System.err.println("Unable to load SpriteSheet Meta file from location: " + pMetaFileLocation);
			return;
		}

		// Iterate through the sprite files, and load the individual sprites
		final int SPRITE_COUNT = SPRITE_META.Sprite_Files.length;
		for (int i = 0; i < SPRITE_COUNT; i++) {
			final File SPRITE_FILE = new File(SPRITE_META.Sprite_Directory + SPRITE_META.Sprite_Files[i]);

			if (!SPRITE_FILE.exists()) {
				System.out.println("Error loading sprite sheet from " + SPRITE_FILE.getPath() + " doesn't exist!");
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

				System.out.println(
						"SpriteSheet " + SPRITE_FILE.getPath() + " loaded (" + SPRITE_SHEET.spriteSheetName + ")");
				SPRITE_SHEET.loadGLContent();

				// Add the spritesheet to the collection, using the FILENAME as
				// the key
				this.spriteSheetMap.put(SPRITE_SHEET.spriteSheetName, SPRITE_SHEET);

			} catch (JsonSyntaxException e) {
				System.err.println("Failed to parse JSON SpriteSheet (Syntax): " + SPRITE_FILE.getPath());
				System.err.println(e.getMessage());
				e.printStackTrace();

				continue;

			} catch (IOException e) {
				System.err.println("Failed to parse JSON SpriteSheet (IO): " + SPRITE_FILE.getPath());
				System.err.println(e.getMessage());
				continue;

			}

		}

	}

	/** Unloads the {@link SpriteSheet}'s GL content. */
	public void unloadGLContent() {
		System.out.println("SpriteSheetManager unloading GL content");

		// TODO: implement SpriteSheetManager GL content unloading on context switch

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Returns the {@link SpriteSheet} to which the specified key string is
	 * mapped, or null if no such {@link SpriteSheet} exists.
	 */
	public SpriteSheet getSpriteSheet(String string) {
		return this.spriteSheetMap.get(string);

	}

}

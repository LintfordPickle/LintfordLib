package net.ld.library.core.graphics.sprites;

import java.util.HashMap;
import java.util.Map;

import net.ld.library.core.graphics.spritesheet.xml.SpriteSheetLoader;
import net.ld.library.core.graphics.spritesheet.xml.SpriteSheetMetaLoader;

public class SpriteSheetManager {

	// =============================================
	// Constants
	// =============================================

	public static final String GAME_SPRITEPACK_FILENAME = "bin/res/spritesheets/spritesheets_meta.xml";

	// =============================================
	// Variables
	// =============================================

	private Map<String, SpriteSheet> mSpriteSheets;
	
	// =============================================
	// Constructor
	// =============================================

	public SpriteSheetManager() {
		mSpriteSheets = new HashMap<String, SpriteSheet>();
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise() {

	}

	public void loadContent() {
		
		// Load the string array of sprite sheet locations from pLocation
		SpriteSheetMetaLoader lMetaLoader = new SpriteSheetMetaLoader();
		Map<String, String> lSheetLocations = lMetaLoader.loadSpriteSheetMeta(GAME_SPRITEPACK_FILENAME);

		SpriteSheetLoader lSSLoader = new SpriteSheetLoader();
		
		for (String key : lSheetLocations.keySet()) {
			SpriteSheet lSpriteSheet = lSSLoader.loadSpriteSheet(lSheetLocations.get(key), key);
			lSpriteSheet.loadContent();
			mSpriteSheets.put(key, lSpriteSheet);
		}
	}

	public SpriteSheet getSpriteSheet(String string) {
		return mSpriteSheets.get(string);
	}

	// =============================================
	// Methods
	// =============================================
}

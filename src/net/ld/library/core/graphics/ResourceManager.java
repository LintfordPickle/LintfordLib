package net.ld.library.core.graphics;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.sprites.SpriteSheetManager;
import net.ld.library.core.graphics.textures.TextureManager;

public class ResourceManager {

	// =============================================
	// Variables
	// =============================================

	protected SpriteSheetManager mSpriteSheetManager;
	protected DisplayConfig mDisplayConfig;

	// SoundManager
	// MusicManager

	// =============================================
	// Properties
	// =============================================

	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	public SpriteSheetManager spriteSheetManager() {
		return mSpriteSheetManager;
	}

	// =============================================
	// Constructor
	// =============================================

	public ResourceManager(DisplayConfig pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;
		mSpriteSheetManager = new SpriteSheetManager();
	}

	// =============================================
	// Core-Method
	// =============================================

	public void loadContent() {
		mSpriteSheetManager.loadContent();

		// Force creation here if not already
		TextureManager.textureManager();
		
		TextureManager.textureManager().loadTexture("Font", "res/textures/font.png");
	}

	// =============================================
	// Methods
	// =============================================

}

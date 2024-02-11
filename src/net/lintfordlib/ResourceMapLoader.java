package net.lintfordlib;

import java.io.File;

import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.ResourceMapIo;

public class ResourceMapLoader extends ResourceLoader {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected float mRunningTime;
	protected String mResourceMapFilepath;

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public ResourceMapLoader(ResourceManager resourceManager, DisplayManager displayManager, String resourceMapFilepath, int entityGroupUid) {
		super(resourceManager, displayManager, true, entityGroupUid);

		mResourceMapFilepath = resourceMapFilepath;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void resourcesToLoadInBackground(int entityGroupUid) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading game assets from res_map into group: " + entityGroupUid);

		final var lResMapFile = new File(mResourceMapFilepath);

		if (lResMapFile == null || lResMapFile.exists() == false)
			return;

		final var lResMap = ResourceMapIo.tryLoadResourceMapFromFile(lResMapFile);
		if (lResMap == null) {
			// Failed to load resource map
			return;
		}

		currentStatusMessage("Loading from res_map");
		mResourceManager.addProtectedEntityGroupUid(entityGroupUid);

		final var lNumTextureMetaFiles = lResMap.textureMetaFiles.size();
		for (int i = 0; i < lNumTextureMetaFiles; i++) {
			final var lTextureMetaFileToLoad = lResMap.textureMetaFiles.get(i);
			mResourceManager.textureManager().loadTexturesFromMetafile(lTextureMetaFileToLoad, entityGroupUid);
		}

		final var lNumSpritesheetMetaFiles = lResMap.spritesheetMetaFiles.size();
		for (int i = 0; i < lNumSpritesheetMetaFiles; i++) {
			final var lSpritesheetMetaFileToLoad = lResMap.spritesheetMetaFiles.get(i);
			mResourceManager.spriteSheetManager().loadSpriteSheet(lSpritesheetMetaFileToLoad, entityGroupUid);
		}

		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_12", "res/fonts/fontNulshock12.json");
		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_16", "res/fonts/fontNulshock16.json");
		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_22", "res/fonts/fontNulshock22.json");
	}

}

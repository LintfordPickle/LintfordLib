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
		Debug.debugManager().logger().i(getClass().getSimpleName(), "  loading " + lNumTextureMetaFiles + " texture meta files into " + entityGroupUid);
		for (int i = 0; i < lNumTextureMetaFiles; i++) {
			final var lTextureMetaFileToLoad = lResMap.textureMetaFiles.get(i);
			mResourceManager.textureManager().loadTexturesFromMetafile(lTextureMetaFileToLoad, entityGroupUid);
		}

		final var lNumSpritesheetMetaFiles = lResMap.spritesheetMetaFiles.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), "  loading " + lNumSpritesheetMetaFiles + " spritesheet meta files into " + entityGroupUid);
		for (int i = 0; i < lNumSpritesheetMetaFiles; i++) {
			final var lSpritesheetMetaFileToLoad = lResMap.spritesheetMetaFiles.get(i);
			mResourceManager.spriteSheetManager().loadSpriteSheetFromMeta(lSpritesheetMetaFileToLoad, entityGroupUid);
		}

		Debug.debugManager().logger().i(getClass().getSimpleName(), "  loading fonts files into " + entityGroupUid);
		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_12", "res/fonts/fontNulshock12.json");
		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_16", "res/fonts/fontNulshock16.json");
		mResourceManager.fontManager().loadBitmapFont("FONT_NULSHOCK_22", "res/fonts/fontNulshock22.json");
	}

}
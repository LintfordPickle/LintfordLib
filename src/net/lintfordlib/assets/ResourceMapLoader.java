package net.lintfordlib.assets;

import java.io.File;
import java.nio.file.Paths;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.options.DisplayManager;

public class ResourceMapLoader extends ResourceLoader {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected float mRunningTime;
	protected String mResourceMapFilepath;

	protected boolean mLoadedFromResMapSuccessfully;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean loadedFromResMapSuccessfully() {
		return mLoadedFromResMapSuccessfully;
	}

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

		final var workspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
		final var resMapFilePath = Paths.get(workspacePath, mResourceMapFilepath);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading resources from : " + resMapFilePath.toString());

		final var resMapFile = new File(resMapFilePath.toString());

		if (!resMapFile.exists()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "The ResMap.json file could not be found:" + resMapFile.toString());
			return;
		}

		final var resMap = ResourceMapIo.tryLoadResourceMapFromFile(resMapFile);
		if (resMap == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "The ResMap.json file could not be loaded.");
			return;
		}

		mLoadedFromResMapSuccessfully = true;

		currentStatusMessage("Loading from res_map");
		mResourceManager.addProtectedEntityGroupUid(entityGroupUid);

		final var numTextureMetaFiles = resMap.textureMetaFiles.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), "  loading " + numTextureMetaFiles + " texture meta files into " + entityGroupUid);
		for (int i = 0; i < numTextureMetaFiles; i++) {
			final var textureMetaFileToLoad = resMap.textureMetaFiles.get(i);
			final var resourceFilePath = Paths.get(workspacePath, textureMetaFileToLoad);

			mResourceManager.textureManager().loadTexturesFromMetafile(resourceFilePath.toString(), entityGroupUid);
		}

		final var numSpritesheetMetaFiles = resMap.spritesheetMetaFiles.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), "  loading " + numSpritesheetMetaFiles + " spritesheet meta files into " + entityGroupUid);
		for (int i = 0; i < numSpritesheetMetaFiles; i++) {
			final var spritesheetMetaFileToLoad = resMap.spritesheetMetaFiles.get(i);
			final var resourceFilePath = Paths.get(workspacePath, spritesheetMetaFileToLoad);

			mResourceManager.spriteSheetManager().loadSpriteSheetFromMeta(resourceFilePath.toString(), entityGroupUid);
		}
	}
}

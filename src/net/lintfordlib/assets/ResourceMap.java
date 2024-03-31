package net.lintfordlib.assets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.debug.Debug;

/**
 * Loaded by each individual project. Defines the locations to load the assets needed.
 **/
public class ResourceMap {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String ResourceMap_Filename = "res_map.json";

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "Textures")
	public final List<String> textureMetaFiles = new ArrayList<>();

	@SerializedName(value = "Spritesheets")
	public final List<String> spritesheetMetaFiles = new ArrayList<>();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ResourceMap() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadResourcesIntoManager(ResourceManager resourceManager, String baseDirectory, int entityGroupUid) {
		loadTexturesFromResMap(resourceManager, baseDirectory, entityGroupUid);
		loadSpritesheetsFromResMap(resourceManager, baseDirectory, entityGroupUid);
	}

	private void loadTexturesFromResMap(ResourceManager resourceManager, String baseDirectory, int entityGroupUid) {
		final var lTextureMetaFilesToLoad = textureMetaFiles;
		final var lNumTextureMetaFiles = lTextureMetaFilesToLoad.size();
		for (int i = 0; i < lNumTextureMetaFiles; i++) {
			final var lMetaFileLocation = lTextureMetaFilesToLoad.get(i);
			if (lMetaFileLocation == null || lMetaFileLocation.length() == 0)
				continue;

			final var lMetaFilepath = new File(baseDirectory, lMetaFileLocation);
			if (lMetaFilepath == null || lMetaFilepath.exists() == false) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load textures from metafile (res_map): " + lMetaFilepath);
				continue;
			}

			if (lMetaFilepath.isFile() == false) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load textures from metafile (res_map): " + lMetaFilepath);
				continue;
			}

			resourceManager.textureManager().loadTexturesFromMetafile(lMetaFilepath.getAbsolutePath(), entityGroupUid);
		}
	}

	private void loadSpritesheetsFromResMap(ResourceManager resourceManager, String baseDirectory, int entityGroupUid) {
		final var lSpritesheetMetaFilesToLoad = spritesheetMetaFiles;
		final var lNumSpritesheetMetaFiles = lSpritesheetMetaFilesToLoad.size();
		for (int i = 0; i < lNumSpritesheetMetaFiles; i++) {
			final var lMetaFileLocation = lSpritesheetMetaFilesToLoad.get(i);
			if (lMetaFileLocation == null || lMetaFileLocation.length() == 0)
				continue;

			final var lMetaFilepath = new File(baseDirectory, lMetaFileLocation);
			if (lMetaFilepath == null || lMetaFilepath.exists() == false) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load spritesheets from metafile (res_map): " + lMetaFilepath);
				continue;
			}

			if (lMetaFilepath.isFile() == false) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load spritesheets from metafile (res_map): " + lMetaFilepath);
				continue;
			}

			resourceManager.spriteSheetManager().loadSpriteSheetFromMeta(lMetaFilepath.getAbsolutePath(), entityGroupUid);
		}
	}

}

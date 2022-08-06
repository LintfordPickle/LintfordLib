package net.lintford.library.core.geometry.spritegraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.EntityGroupManager;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.EntityLocationProvider;
import net.lintford.library.core.entity.definitions.DefinitionManager;
import net.lintford.library.core.geometry.spritegraph.definitions.SpriteGraphDefinition;

public class SpriteGraphRepository extends EntityGroupManager {

	public class SpriteGraphGroup extends DefinitionManager<SpriteGraphDefinition> {

		// --------------------------------------
		// Variables
		// --------------------------------------

		boolean automaticUnload = true;
		int entityGroupID;
		String name = "";
		int referenceCount = 0;

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public SpriteGraphGroup(int pEntityGroupID) {
			entityGroupID = pEntityGroupID;
			referenceCount = 0;

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider pEntityLocationProvider) {
		}

		@Override
		public void loadDefinitionsFromMetaFile(String pMetaFileLocation) {
			if (pMetaFileLocation == null || pMetaFileLocation.length() == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteGraphManager meta file cannot be null or empty when loading SpriteSheets.");
				return;
			}

			final var lGson = new GsonBuilder().create();
			SpriteGraphMetaData lSpriteGraphMetaData = null;
			try {
				String lMetaFileContents = new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
				lSpriteGraphMetaData = lGson.fromJson(lMetaFileContents, SpriteGraphMetaData.class);

				if (lSpriteGraphMetaData == null || lSpriteGraphMetaData.spriteGraphLocations == null || lSpriteGraphMetaData.spriteGraphLocations.length == 0) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load sprites from SpriteGraphDef meta file: " + pMetaFileLocation);

					return;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			final int lSpriteGraphDefinitionCount = lSpriteGraphMetaData.spriteGraphLocations.length;
			for (int i = 0; i < lSpriteGraphDefinitionCount; i++) {
				loadDefinitionFromFile(lSpriteGraphMetaData.spriteGraphLocations[i]);
			}
		}

		@Override
		public void loadDefinitionFromFile(String pFilepath) {
			final var lSpriteGraphFile = new File(pFilepath);

			if (!lSpriteGraphFile.exists()) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading SpriteGraphDef from " + lSpriteGraphFile.getPath() + ". File doesn't exist!");
				return;

			}

			final SpriteGraphDefinition lSpriteGraphDef = SpriteGraphDefinition.load(lSpriteGraphFile);
			if (lSpriteGraphDef == null) {
				return;
			}

			lSpriteGraphDef.filename = lSpriteGraphFile.getPath();

			addDefintion(lSpriteGraphDef);
		}

		public void unloadDefinitions() {
			for (var definition : mDefinitions.values()) {
				definition.unload();
			}
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public class SpriteGraphMetaData {
		public String[] spriteGraphLocations;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourceManager mResourceManager;
	private Map<Integer, SpriteGraphGroup> mSpriteGraphGroupMap;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ResourceManager resourceManager() {
		return mResourceManager;
	}

	public Map<Integer, SpriteGraphGroup> spriteGraphGroups() {
		return mSpriteGraphGroupMap;
	}

	public SpriteGraphGroup spriteGraphGroup(int pEntityGroupID) {
		if (!mSpriteGraphGroupMap.containsKey(pEntityGroupID)) {
			SpriteGraphGroup lNewSpriteGraphGroup = new SpriteGraphGroup(pEntityGroupID);
			lNewSpriteGraphGroup.referenceCount = 1;

			mSpriteGraphGroupMap.put(pEntityGroupID, lNewSpriteGraphGroup);
			return lNewSpriteGraphGroup;
		}

		return mSpriteGraphGroupMap.get(pEntityGroupID);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphRepository() {
		mSpriteGraphGroupMap = new HashMap<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;
	}

	public void unloadResources() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public int increaseReferenceCounts(int pEntityGroupID) {
		var lSpriteGraphGroup = mSpriteGraphGroupMap.get(pEntityGroupID);

		if (!mSpriteGraphGroupMap.containsKey(pEntityGroupID)) {
			mSpriteGraphGroupMap.put(pEntityGroupID, lSpriteGraphGroup);

		} else {
			lSpriteGraphGroup.referenceCount++;

		}

		return lSpriteGraphGroup.referenceCount;

	}

	@Override
	public int decreaseReferenceCounts(int pEntityGroupID) {
		var lSpriteGraphGroup = mSpriteGraphGroupMap.get(pEntityGroupID);

		if (lSpriteGraphGroup == null) {
			return 0;

		} else {
			lSpriteGraphGroup.referenceCount--;

		}

		if (lSpriteGraphGroup.referenceCount <= 0) {
			// Unload SpriteGraphs for this entityGroupID
			unloadEntityGroup(pEntityGroupID);

			if (mSpriteGraphGroupMap.containsKey(pEntityGroupID)) {
				mSpriteGraphGroupMap.remove(pEntityGroupID);

			}
			lSpriteGraphGroup = null;

			return 0;

		}

		return lSpriteGraphGroup.referenceCount;

	}

	public void unloadEntityGroup(int pEntityGroupID) {
		var lSpriteGraphGroup = mSpriteGraphGroupMap.get(pEntityGroupID);

		if (lSpriteGraphGroup == null)
			return;

		final var lSpriteGraphCount = lSpriteGraphGroup.definitionCount();
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Unloading SpriteGraphGroup %d (freeing total %d SpriteGraphs)", pEntityGroupID, lSpriteGraphCount));

		lSpriteGraphGroup.unloadDefinitions();

	}

	public void loadSpriteGraphsFromMeta(String pSpritegraphMetafileLocation, int pEntityGroupID) {
		var lSpriteGraphGroup = spriteGraphGroup(pEntityGroupID); // mSpriteGraphGroupMap.get(pEntityGroupID);

		lSpriteGraphGroup.loadDefinitionsFromMetaFile(pSpritegraphMetafileLocation);

	}

	public SpriteGraphDefinition getSpriteGraphDefinition(String pSpriteGraphDefinitionName, int mEntityGroupID) {
		final var lSpriteGraphGroup = spriteGraphGroup(mEntityGroupID);
		return lSpriteGraphGroup.getByName(pSpriteGraphDefinitionName);
	}
}

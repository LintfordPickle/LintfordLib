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

		private int mReferenceCount = 0;

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public SpriteGraphGroup(int pEntityGroupID) {
			mReferenceCount = 0;
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider entityLocationProvider) {

		}

		@Override
		public void loadDefinitionsFromMetaFile(String metaFileLocation) {
			if (metaFileLocation == null || metaFileLocation.length() == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteGraphManager meta file cannot be null or empty when loading SpriteSheets.");
				return;
			}

			final var lGson = new GsonBuilder().create();
			SpriteGraphMetaData lSpriteGraphMetaData = null;
			try {
				String lMetaFileContents = new String(Files.readAllBytes(Paths.get(metaFileLocation)));
				lSpriteGraphMetaData = lGson.fromJson(lMetaFileContents, SpriteGraphMetaData.class);

				if (lSpriteGraphMetaData == null || lSpriteGraphMetaData.spriteGraphLocations == null || lSpriteGraphMetaData.spriteGraphLocations.length == 0) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load sprites from SpriteGraphDef meta file: " + metaFileLocation);

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
		public void loadDefinitionFromFile(String filepath) {
			final var lSpriteGraphFile = new File(filepath);

			if (!lSpriteGraphFile.exists()) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading SpriteGraphDef from " + lSpriteGraphFile.getPath() + ". File doesn't exist!");
				return;
			}

			final var lSpriteGraphDefinition = SpriteGraphDefinition.load(lSpriteGraphFile);
			if (lSpriteGraphDefinition == null) {
				return;
			}

			lSpriteGraphDefinition.filename(lSpriteGraphFile.getPath());

			addDefintion(lSpriteGraphDefinition);
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

	public SpriteGraphGroup spriteGraphGroup(int entityGroupUid) {
		if (!mSpriteGraphGroupMap.containsKey(entityGroupUid)) {
			final var lNewSpriteGraphGroup = new SpriteGraphGroup(entityGroupUid);
			lNewSpriteGraphGroup.mReferenceCount = 1;

			mSpriteGraphGroupMap.put(entityGroupUid, lNewSpriteGraphGroup);
			return lNewSpriteGraphGroup;
		}

		return mSpriteGraphGroupMap.get(entityGroupUid);
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

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;
	}

	public void unloadResources() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public int increaseReferenceCounts(int entityGroupUid) {
		var lSpriteGraphGroup = mSpriteGraphGroupMap.get(entityGroupUid);

		if (!mSpriteGraphGroupMap.containsKey(entityGroupUid))
			mSpriteGraphGroupMap.put(entityGroupUid, lSpriteGraphGroup);
		else
			lSpriteGraphGroup.mReferenceCount++;

		return lSpriteGraphGroup.mReferenceCount;
	}

	@Override
	public int decreaseReferenceCounts(int entityGroupUid) {
		var lSpriteGraphGroup = mSpriteGraphGroupMap.get(entityGroupUid);

		if (lSpriteGraphGroup == null)
			return 0;
		else
			lSpriteGraphGroup.mReferenceCount--;

		if (lSpriteGraphGroup.mReferenceCount <= 0) {
			unloadEntityGroup(entityGroupUid);

			if (mSpriteGraphGroupMap.containsKey(entityGroupUid))
				mSpriteGraphGroupMap.remove(entityGroupUid);

			lSpriteGraphGroup = null;

			return 0;
		}

		return lSpriteGraphGroup.mReferenceCount;
	}

	public void unloadEntityGroup(int entityGroupUid) {
		var lSpriteGraphGroup = mSpriteGraphGroupMap.get(entityGroupUid);
		if (lSpriteGraphGroup == null)
			return;

		final var lSpriteGraphCount = lSpriteGraphGroup.definitionCount();
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Unloading SpriteGraphGroup %d (freeing total %d SpriteGraphs)", entityGroupUid, lSpriteGraphCount));

		lSpriteGraphGroup.unloadDefinitions();
	}

	public void loadSpriteGraphsFromMeta(String spritegraphMetafileLocation, int entityGroupUid) {
		var lSpriteGraphGroup = spriteGraphGroup(entityGroupUid);
		lSpriteGraphGroup.loadDefinitionsFromMetaFile(spritegraphMetafileLocation);
	}

	public SpriteGraphDefinition getSpriteGraphDefinition(String spriteGraphDefinitionName, int entityGroupUid) {
		final var lSpriteGraphGroup = spriteGraphGroup(entityGroupUid);
		return lSpriteGraphGroup.getByName(spriteGraphDefinitionName);
	}
}

package net.lintfordlib.core.geometry.spritegraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;

import net.lintfordlib.assets.EntityGroupManager;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;
import net.lintfordlib.core.geometry.spritegraph.definitions.SpriteGraphDefinition;

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
		public void loadDefinitionsFromMetaFile(File metaFile) {
			if (metaFile == null || metaFile.exists() == false) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteGraphManager meta file cannot be null or empty when loading SpriteSheets.");
				return;
			}

			final var lGson = new GsonBuilder().create();
			SpriteGraphMetaData lSpriteGraphMetaData = null;
			try {
				String lMetaFileContents = new String(Files.readAllBytes(Paths.get(metaFile.getAbsolutePath())));
				lSpriteGraphMetaData = lGson.fromJson(lMetaFileContents, SpriteGraphMetaData.class);

				if (lSpriteGraphMetaData == null || lSpriteGraphMetaData.spriteGraphLocations == null || lSpriteGraphMetaData.spriteGraphLocations.length == 0) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load sprites from SpriteGraphDef meta file: " + metaFile.getAbsolutePath());

					return;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			final int lSpriteGraphDefinitionCount = lSpriteGraphMetaData.spriteGraphLocations.length;
			for (int i = 0; i < lSpriteGraphDefinitionCount; i++) {
				final var lSPriteDefinitionFile = new File(lSpriteGraphMetaData.spriteGraphLocations[i]);

				loadDefinitionFromFile(lSPriteDefinitionFile);
			}
		}

		@Override
		public SpriteGraphDefinition loadDefinitionFromFile(File filepath) {
			if (filepath == null || filepath.exists() == false) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading SpriteGraphDef. File doesn't exist!");
				return null;
			}

			final var lSpriteGraphDefinition = SpriteGraphDefinition.load(filepath);
			if (lSpriteGraphDefinition == null) {
				return null;
			}

			lSpriteGraphDefinition.filename(filepath.getPath());

			addDefintion(lSpriteGraphDefinition);

			return lSpriteGraphDefinition;
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
		lSpriteGraphGroup.loadDefinitionsFromMetaFile(new File(spritegraphMetafileLocation));
	}

	public SpriteGraphDefinition getSpriteGraphDefinition(String spriteGraphDefinitionName, int entityGroupUid) {
		final var lSpriteGraphGroup = spriteGraphGroup(entityGroupUid);
		return lSpriteGraphGroup.getByName(spriteGraphDefinitionName);
	}
}

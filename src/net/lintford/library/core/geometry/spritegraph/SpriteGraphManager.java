package net.lintford.library.core.geometry.spritegraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.spritegraph.definition.GraphObjectDefinition;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInst;

public class SpriteGraphManager {

	public class SpriteGraphGroup {

		// --------------------------------------
		// Variables
		// --------------------------------------

		Map<String, GraphObjectDefinition> mSpriteGraphs;

		boolean automaticUnload = true;
		int entityGroupID;
		String name = "";
		int referenceCount = 0;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public Map<String, GraphObjectDefinition> spriteGraphs() {
			return mSpriteGraphs;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public SpriteGraphGroup(int pEntityGroupID) {
			mSpriteGraphs = new HashMap<>();

			entityGroupID = pEntityGroupID;
			referenceCount = 0;

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public GraphObjectDefinition getSpriteGraphByName(String pTextureName) {
			if (mSpriteGraphs.containsKey(pTextureName)) {
				return mSpriteGraphs.get(pTextureName);

			}

			return null;
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

	private List<SpriteGraphInst> mGraphInstPool;
	private List<SpriteGraphNodeInst> mGraphNodeInstPool;

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
			mSpriteGraphGroupMap.put(pEntityGroupID, lNewSpriteGraphGroup);

			return lNewSpriteGraphGroup;
		}

		return mSpriteGraphGroupMap.get(pEntityGroupID);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphManager() {
		mSpriteGraphGroupMap = new HashMap<>();

		mGraphInstPool = new ArrayList<>();
		mGraphNodeInstPool = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;

	}

	public void unloadGLContent() {

	}

	public void loadSpriteGraphsFromMeta(final String pMetaFileLocation, int pEntityGroupID) {
		if (pMetaFileLocation == null || pMetaFileLocation.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteGraphManager meta file cannot be null or empty when loading SpriteSheets.");
			return;

		}

		final Gson GSON = new GsonBuilder().create();

		// Load the Sprite meta data
		SpriteGraphMetaData lSpriteGraphMetaData = null;

		try {
			String lMetaFileContents = new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
			lSpriteGraphMetaData = GSON.fromJson(lMetaFileContents, SpriteGraphMetaData.class);

			if (lSpriteGraphMetaData == null || lSpriteGraphMetaData.spriteGraphLocations == null || lSpriteGraphMetaData.spriteGraphLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load sprites from SpriteGraphDef meta file: " + pMetaFileLocation);

				return;

			}
		} catch (IOException e1) {
			e1.printStackTrace();

		}

		// Iterate through the sprite files, and load the individual sprites
		final int SPRITE_COUNT = lSpriteGraphMetaData.spriteGraphLocations.length;
		for (int i = 0; i < SPRITE_COUNT; i++) {
			final File lSpriteGraphFile = new File(lSpriteGraphMetaData.spriteGraphLocations[i]);

			if (!lSpriteGraphFile.exists()) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading SpriteGraphDef from " + lSpriteGraphFile.getPath() + ". File doesn't exist!");
				continue;

			}

			final GraphObjectDefinition lSpriteGraphDef = GraphObjectDefinition.load(lSpriteGraphFile);

			if (lSpriteGraphDef == null) {
				return;
			}

			lSpriteGraphDef.filename = lSpriteGraphFile.getPath();

			SpriteGraphGroup lSpriteGraphGroup = mSpriteGraphGroupMap.get(pEntityGroupID);
			if (lSpriteGraphGroup == null) {
				lSpriteGraphGroup = new SpriteGraphGroup(pEntityGroupID);

				mSpriteGraphGroupMap.put(pEntityGroupID, lSpriteGraphGroup);

			}

			// Add the SpriteGraphDef to the collection, using the name as the key.
			lSpriteGraphGroup.mSpriteGraphs.put(lSpriteGraphDef.name, lSpriteGraphDef);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteGraphInst getInstanceOfGraph(String pGraphDefName, int pEntityGroupID) {
		// TODO: Check the pool of SpriteGraphs and return a recycled, unused one

		SpriteGraphGroup lSpriteGraphGroup = mSpriteGraphGroupMap.get(pEntityGroupID);

		if (lSpriteGraphGroup == null)
			return null;

		GraphObjectDefinition lGraphDefinition = lSpriteGraphGroup.getSpriteGraphByName(pGraphDefName);

		if (lGraphDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Couldn't return SpriteGraphDef with ID " + pGraphDefName);
			return null;

		}

		return new SpriteGraphInst(lGraphDefinition);

	}

	public void killGraph(SpriteGraphInst pSpriteGraphInst) {
		// TODO: ---> Return all nodes to the pool for reuse

	}

}

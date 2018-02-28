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

import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.graphics.ResourceManager;

public class SpriteGraphManager {

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
	private Map<String, SpriteGraphDef> mSpriteGraphs;

	private List<SpriteGraphInst> mGraphInstPool;
	private List<SpriteGraphNodeInst> mGraphNodeInstPool;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphManager() {
		mSpriteGraphs = new HashMap<>();

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

	public void loadSpriteGraphsFromMeta(final String pMetaFileLocation) {
		if (pMetaFileLocation == null || pMetaFileLocation.length() == 0) {
			DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "SpriteGraphManager meta file cannot be null or empty when loading SpriteSheets.");
			return;

		}

		final Gson GSON = new GsonBuilder().create();

		// Load the Sprite meta data
		SpriteGraphMetaData lSpriteGraphMetaData = null;

		try {
			String lMetaFileContents = new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
			lSpriteGraphMetaData = GSON.fromJson(lMetaFileContents, SpriteGraphMetaData.class);

			if (lSpriteGraphMetaData == null || lSpriteGraphMetaData.spriteGraphLocations == null || lSpriteGraphMetaData.spriteGraphLocations.length == 0) {
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Couldn't load sprites from SpriteGraphDef meta file: " + pMetaFileLocation);

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
				DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "Error loading SpriteGraphDef from " + lSpriteGraphFile.getPath() + ". File doesn't exist!");
				continue;

			}

			final SpriteGraphDef lSpriteGraphDef = SpriteGraphDef.load(lSpriteGraphFile);

			if (lSpriteGraphDef == null) {
				return;
			}

			lSpriteGraphDef.filename = lSpriteGraphFile.getPath();

			// Add the SpriteGraphDef to the collection, using the name as the key.
			this.mSpriteGraphs.put(lSpriteGraphDef.name, lSpriteGraphDef);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteGraphInst getInstanceOfGraph(String pGraphDefName) {
		// TODO: Check the pool of SpriteGraphs and return a recycled, unused one
		
		SpriteGraphDef lGraphDefinition = mSpriteGraphs.get(pGraphDefName);
		if (lGraphDefinition == null) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Couldn't return SpriteGraphDef with ID " + pGraphDefName);
			return null;

		}

		return new SpriteGraphInst(lGraphDefinition);

	}

	public void killGraph(SpriteGraphInst pSpriteGraphInst) {
		// TODO: ---> Return all nodes to the pool for reuse

	}

}

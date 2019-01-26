package net.lintford.library.core.graphics.sprites.spritesheet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public class SpriteSheetManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class SpriteSheetMetaData {
		public String[] spriteSheetLocations;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Contains a collection of SpriteSheets which has been loaded by this {@link SpriteSheetManager}. */
	private Map<Integer, Map<String, SpriteSheetDef>> mSpriteSheetGroups;

	private ResourceManager mResourceManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the {@link SpriteSheetDef} to which the specified key string is mapped, or null if no such {@link SpriteSheetDef} exists. */
	public SpriteSheetDef getSpriteSheet(String pName, int pEntityGroupID) {
		Map<String, SpriteSheetDef> lSpriteSheetGroup = mSpriteSheetGroups.get(pEntityGroupID);
		if (lSpriteSheetGroup != null) {
			return lSpriteSheetGroup.get(pName);

		}

		return null;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/** Creates a new instance of {@link SpriteSheetManager}. */
	public SpriteSheetManager() {
		mSpriteSheetGroups = new HashMap<>();

		mSpriteSheetGroups.put(LintfordCore.CORE_ENTITY_GROUP_ID, new HashMap<String, SpriteSheetDef>());

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {
		// for (Map.Entry<String, SpriteSheetDef> entry : this.spriteSheetMap.entrySet()) {
		// SpriteSheetDef lSpriteSheet = entry.getValue();
		//
		// lSpriteSheet.initialise(this);
		//
		// }

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;

	}

	public void unloadGLContent() {

	}

	public SpriteSheetDef loadSpriteSheet(String pFilepath, int pEntityGroupID) {
		if (pFilepath == null || pFilepath.length() == 0) {
			Debug.debugManager().logger().v(getClass().getSimpleName(), "Error loading spritesheet. Pathname is null! ");
			return null;
		}

		if (pFilepath.charAt(0) == '/') {
			return loadSpriteSheetFromResource(pFilepath, pEntityGroupID);

		} else {
			return loadSpriteSheetFromFile(pFilepath, pEntityGroupID);

		}

	}

	private SpriteSheetDef loadSpriteSheetFromFile(String pFilepath, int pEntityGroupID) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + pFilepath + " doesn't exist!");
			return null;

		}

		final Gson GSON = new GsonBuilder().create();

		try {

			final String lFileContents = new String(Files.readAllBytes(lFile.toPath()));
			final SpriteSheetDef lSpriteSheet = GSON.fromJson(lFileContents, SpriteSheetDef.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteSheet == null || lSpriteSheet.getSpriteCount() == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), lSpriteSheet.spriteSheetName + " has no SpriteMap Sprites defined (SpriteMap is empty!) " + lFile.getPath());

			}

			Debug.debugManager().logger().v(getClass().getSimpleName(), "SpriteSheet " + lFile.getPath() + " loaded (" + lSpriteSheet.spriteSheetName + ")");

			lSpriteSheet.fileSizeOnLoad(lFile.length());
			lSpriteSheet.spriteSheetFilename = lFile.getPath();
			lSpriteSheet.reloadable(true);
			lSpriteSheet.loadGLContent(mResourceManager, pEntityGroupID);

			Map<String, SpriteSheetDef> lSpriteSheetGroup = mSpriteSheetGroups.get(pEntityGroupID);
			if (lSpriteSheetGroup == null) {
				lSpriteSheetGroup = new HashMap<>();
				mSpriteSheetGroups.put(pEntityGroupID, lSpriteSheetGroup);

			}

			lSpriteSheetGroup.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			return lSpriteSheet;

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (Syntax): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (IO): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		}
	}

	private SpriteSheetDef loadSpriteSheetFromResource(String pFilepath, int pEntityGroupID) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + pFilepath + " doesn't exist!");
			return null;

		}

		final Gson GSON = new GsonBuilder().create();

		try {

			InputStream lInputStream = FileUtils.class.getResourceAsStream(pFilepath);

			JsonReader reader = new JsonReader(new InputStreamReader(lInputStream, "UTF-8"));

			final SpriteSheetDef lSpriteSheet = GSON.fromJson(reader, SpriteSheetDef.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteSheet == null || lSpriteSheet.getSpriteCount() == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading spritesheet " + lFile.getPath());
				return null;

			}

			Map<String, SpriteSheetDef> lSpriteSheetGroup = mSpriteSheetGroups.get(pEntityGroupID);
			if (lSpriteSheetGroup == null) {
				lSpriteSheetGroup = new HashMap<>();
				mSpriteSheetGroups.put(pEntityGroupID, lSpriteSheetGroup);

			}

			lSpriteSheet.reloadable(false);
			lSpriteSheet.loadGLContent(mResourceManager);

			lSpriteSheetGroup.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			Debug.debugManager().logger().v(getClass().getSimpleName(), String.format("Loaded SpriteSheet '%s' loaded from %s", lSpriteSheet.spriteSheetName, lFile.getPath()));

			return lSpriteSheet;

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + lFile.getPath());
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + lFile.getPath());
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

			return null;

		}
	}

	/** Loads a set of spritesheets from a meta file at the given location. */
	public void loadSpriteSheetFromMeta(final String pMetaFileLocation, int pEntityGroupID) {
		if (pMetaFileLocation == null || pMetaFileLocation.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file cannot be null or empty when loading SpriteSheets.");
			return;

		}

		final Gson GSON = new GsonBuilder().create();

		// Load the Sprite meta data
		String lMetaFileContentsString = null;
		SpriteSheetMetaData lSpriteMetaObject = null;
		try {
			lMetaFileContentsString = new String(Files.readAllBytes(Paths.get(pMetaFileLocation)));
			lSpriteMetaObject = GSON.fromJson(lMetaFileContentsString, SpriteSheetMetaData.class);

			if (lSpriteMetaObject == null || lSpriteMetaObject.spriteSheetLocations == null || lSpriteMetaObject.spriteSheetLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load sprites from sprite meta file");

				return;

			}
		} catch (IOException e1) {
			e1.printStackTrace();

		}

		// Iterate through the sprite files, and load the individual sprites
		final int SPRITE_COUNT = lSpriteMetaObject.spriteSheetLocations.length;
		for (int i = 0; i < SPRITE_COUNT; i++) {
			final File lSpriteSheetFile = new File(lSpriteMetaObject.spriteSheetLocations[i]);

			if (!lSpriteSheetFile.exists()) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading sprite sheet from " + lSpriteSheetFile.getPath() + " doesn't exist!");

				continue;

			}

			try {

				final String lSpriteSheetFileContents = new String(Files.readAllBytes(lSpriteSheetFile.toPath()));
				final SpriteSheetDef lSpriteSheet = GSON.fromJson(lSpriteSheetFileContents, SpriteSheetDef.class);

				// Check the integrity of the loaded spritsheet
				if (lSpriteSheet == null) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Error loading spritesheet " + lSpriteSheetFile.getPath());
					continue;

				}

				if (lSpriteSheet.spriteMap == null || lSpriteSheet.spriteMap.size() == 0) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Loaded SpriteSheetDefinition which has no Sprites defined within: " + lSpriteSheetFile.getPath());

				}

				lSpriteSheet.fileSizeOnLoad(lSpriteSheetFile.length());
				lSpriteSheet.spriteSheetFilename = lSpriteSheetFile.getPath();
				lSpriteSheet.loadGLContent(mResourceManager, pEntityGroupID);

				if (lSpriteSheet.spriteMap == null || lSpriteSheet.spriteMap.size() == 0) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Loaded SpriteSheetDefinition which has neither sprites nor frames defined within: " + lSpriteSheetFile.getPath());

				}

				Map<String, SpriteSheetDef> lSpriteSheetGroup = mSpriteSheetGroups.get(pEntityGroupID);
				if (lSpriteSheetGroup == null) {
					lSpriteSheetGroup = new HashMap<>();
					mSpriteSheetGroups.put(pEntityGroupID, lSpriteSheetGroup);

				}

				// Add the spritesheet to the collection, using the FILENAME as the key
				lSpriteSheetGroup.put(lSpriteSheet.spriteSheetName, lSpriteSheet);

			} catch (JsonSyntaxException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (Syntax): " + lSpriteSheetFile.getPath());
				Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
				continue;

			} catch (IOException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (IO): " + lSpriteSheetFile.getPath());
				Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
				continue;

			}

		}

	}

	public void reload() {

		final Gson GSON = new GsonBuilder().create();

		for (Map<String, SpriteSheetDef> lSpriteSheetGroup : mSpriteSheetGroups.values()) {
			for (Map.Entry<String, SpriteSheetDef> entry : lSpriteSheetGroup.entrySet()) {
				SpriteSheetDef lSpriteSheet = entry.getValue();
				if (!lSpriteSheet.reloadable)
					continue;

				File lSpriteSheetFile = new File(lSpriteSheet.spriteSheetFilename);
				if (lSpriteSheetFile.length() != lSpriteSheet.fileSizeOnLoad()) {
					//
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Reloading SpriteSheet (size on disk change detected).");

					try {

						final String lSpriteSheetFileContents = new String(Files.readAllBytes(lSpriteSheetFile.toPath()));
						final SpriteSheetDef lNewSpriteSheet = GSON.fromJson(lSpriteSheetFileContents, SpriteSheetDef.class);

						lNewSpriteSheet.loadGLContent(mResourceManager);

						lSpriteSheet.unloadGLContent();

						entry.setValue(lNewSpriteSheet);

					} catch (JsonSyntaxException e) {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (Syntax): " + lSpriteSheetFile.getPath());
						Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

						continue;

					} catch (IOException e) {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (IO): " + lSpriteSheetFile.getPath());
						Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

						continue;

					}

				}

			}
		}

	}

}

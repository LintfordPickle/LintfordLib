package net.lintfordlib.core.graphics.sprites.spritesheet;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public class SpriteSheetManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class SpriteSheetMetaData {
		public String[] spriteSheetLocations;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CORE_SPRITESHEET_NAME = "SPRITESHEET_CORE";

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Contains a collection of SpriteSheets which has been loaded by this {@link SpriteSheetManager}. */
	private Map<Integer, Map<String, SpriteSheetDefinition>> mSpriteSheetGroups;

	private ResourceManager mResourceManager;

	private final List<ISpritesheetManagerChangeListener> mChangeListeners = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Map<String, SpriteSheetDefinition> spritesheetGroup(int entityGroupUid) {
		return mSpriteSheetGroups.get(entityGroupUid);
	}

	public SpriteSheetDefinition coreSpritesheet() {
		return mSpriteSheetGroups.get(LintfordCore.CORE_ENTITY_GROUP_ID).get(CORE_SPRITESHEET_NAME);
	}

	/** Returns the {@link SpriteSheetDefinition} to which the specified key string is mapped, or null if no such {@link SpriteSheetDefinition} exists. */
	public SpriteSheetDefinition getSpriteSheet(String spriteSheetDefinitionName, int entityGroupUid) {
		Map<String, SpriteSheetDefinition> lSpriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
		if (lSpriteSheetGroup != null) {
			return lSpriteSheetGroup.get(spriteSheetDefinitionName);
		}

		return null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/**
	 * Creates a new instance of {@link SpriteSheetManager}.
	 */
	public SpriteSheetManager() {
		mSpriteSheetGroups = new HashMap<>();
		mSpriteSheetGroups.put(LintfordCore.CORE_ENTITY_GROUP_ID, new HashMap<String, SpriteSheetDefinition>());
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	private void notifyListenersOfChange() {
		final var lNumListeners = mChangeListeners.size();
		for (int i = 0; i < lNumListeners; i++) {
			mChangeListeners.get(i).onSpritesheetsChanged();
		}
	}

	public void initialize(ResourceManager resourceManager) {
		mResourceManager = resourceManager;
		loadSpriteSheetFromResource("/res/spritesheets/core/spritesheetCore.json", LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	public void addChangeListener(ISpritesheetManagerChangeListener listener) {
		if (!mChangeListeners.contains(listener))
			mChangeListeners.add(listener);
	}

	public void removeChangeListener(ISpritesheetManagerChangeListener listener) {
		if (mChangeListeners.contains(listener))
			mChangeListeners.remove(listener);
	}

	/**
	 * Attempts to retrieve the {@link SpriteSheetDefinition} with the given name. If no spritesheet definition can be found, then it is automatically loaded using the given {@link filepath} parameter.
	 * 
	 * @param spritesheetName The name of the Spritesheet to attempt to load.
	 * @param filepath        The filepath of the Spritesheet to load, should it not yet have been.
	 * @param entityGroupUid  The entityGroupUid to use when loading the resource.
	 * 
	 * @implNote If no SpritesheetDefinition with the given name can be found, then it will be loaded from the filepath provided. The new SpritesheetDefinition will be assigned the name in the definition file, and *not* the parameter.
	 */

	public SpriteSheetDefinition loadSpriteSheet(String spritesheetName, String filepath, int entityGroupUid) {
		final var lExists = getSpriteSheet(spritesheetName, entityGroupUid);
		if (lExists != null)
			return lExists;

		return loadSpriteSheet(filepath, entityGroupUid);
	}

	public SpriteSheetDefinition loadSpriteSheet(String filepath, int entityGroupUid) {
		if (filepath == null || filepath.length() == 0) {
			Debug.debugManager().logger().v(getClass().getSimpleName(), "Error loading spritesheet. Pathname is null! ");
			return null;
		}

		if (filepath.charAt(0) == '/') {
			return loadSpriteSheetFromResource(filepath, entityGroupUid);
		} else {
			return loadSpriteSheetFromFile(filepath, entityGroupUid);
		}
	}

	private SpriteSheetDefinition loadSpriteSheetFromFile(String filepath, int entityGroupUid) {
		if (filepath == null || filepath.length() == 0)
			return null;

		final var lCleanFilename = FileUtils.cleanFilename(filepath);
		final var lFile = new File(System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME), lCleanFilename);

		if (!lFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + filepath + " doesn't exist!");
			return null;
		}

		final Gson gson = new GsonBuilder().create();

		try {
			final var lFileContents = new String(Files.readAllBytes(lFile.toPath()));
			final var lSpriteSheetDefinition = gson.fromJson(lFileContents, SpriteSheetDefinition.class);

			if (lSpriteSheetDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), " Error deserializing Spritesheetdefinition '" + lFile.getPath() + "'.");
				return null;
			}

			if (lSpriteSheetDefinition.getSpriteCount() == 0)
				Debug.debugManager().logger().w(getClass().getSimpleName(), lSpriteSheetDefinition.mSpriteSheetName + " has no SpriteMap Sprites defined (SpriteMap is empty!) " + lFile.getPath());

			Debug.debugManager().logger().v(getClass().getSimpleName(), "SpriteSheet " + lFile.getPath() + " loaded (" + lSpriteSheetDefinition.mSpriteSheetName + ")");

			Map<String, SpriteSheetDefinition> lSpriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
			if (lSpriteSheetGroup == null) {
				lSpriteSheetGroup = new HashMap<>();
				mSpriteSheetGroups.put(entityGroupUid, lSpriteSheetGroup);
			}

			if (lSpriteSheetGroup.containsKey(lSpriteSheetDefinition.mSpriteSheetName)) {
				final var lOldSpritesheet = lSpriteSheetGroup.get(lSpriteSheetDefinition.mSpriteSheetName);
				lOldSpritesheet.unloadResources();
				lOldSpritesheet.copyFrom(lSpriteSheetDefinition);
				lOldSpritesheet.loadResources(mResourceManager);

				return lOldSpritesheet;
			} else {
				lSpriteSheetDefinition.fileSizeOnLoad(lFile.length());
				lSpriteSheetDefinition.mSpriteSheetFilename = lFile.getPath();
				lSpriteSheetDefinition.reloadable(true);
				lSpriteSheetDefinition.loadResources(mResourceManager, entityGroupUid);

				lSpriteSheetGroup.put(lSpriteSheetDefinition.mSpriteSheetName, lSpriteSheetDefinition);

				return lSpriteSheetDefinition;
			}
		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (Syntax): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (IO): %s", lFile.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}

		notifyListenersOfChange();

		return null;
	}

	private SpriteSheetDefinition loadSpriteSheetFromResource(String filepath, int entityGroupUid) {
		if (filepath == null || filepath.length() == 0)
			return null;

		final Gson GSON = new GsonBuilder().create();

		try {

			final var lInputStream = FileUtils.class.getResourceAsStream(filepath);
			final var lGsonreader = new JsonReader(new InputStreamReader(lInputStream, "UTF-8"));

			final SpriteSheetDefinition lSpriteSheet = GSON.fromJson(lGsonreader, SpriteSheetDefinition.class);

			if (lSpriteSheet == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading spritesheet " + filepath);
				return null;
			}

			var lSpriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
			if (lSpriteSheetGroup == null) {
				lSpriteSheetGroup = new HashMap<>();
				mSpriteSheetGroups.put(entityGroupUid, lSpriteSheetGroup);
			}

			lSpriteSheet.reloadable(false);
			lSpriteSheet.loadResources(mResourceManager, entityGroupUid);

			lSpriteSheetGroup.put(lSpriteSheet.mSpriteSheetName, lSpriteSheet);

			Debug.debugManager().logger().v(getClass().getSimpleName(), String.format("Loaded SpriteSheet '%s' loaded from %s", lSpriteSheet.mSpriteSheetName, filepath));

			notifyListenersOfChange();

			return lSpriteSheet;

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + filepath);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + filepath);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}

		return null;
	}

	/** Loads a set of spritesheets from a meta file at the given location. */
	public void loadSpriteSheetFromMeta(final String metaFileLocation, int entityGroupUid) {
		if (metaFileLocation == null || metaFileLocation.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file cannot be null or empty when loading SpriteSheets.");
			return;
		}

		final var lFile = new File(metaFileLocation);
		if (lFile.exists() == false) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file doesn't exist - skipping");
			return;
		}

		final Gson lGson = new GsonBuilder().create();

		// Load the Sprite meta data
		String lMetaFileContentsString = null;
		SpriteSheetMetaData lSpriteMetaObject = null;
		try {
			lMetaFileContentsString = new String(Files.readAllBytes(Paths.get(metaFileLocation)));
			lSpriteMetaObject = lGson.fromJson(lMetaFileContentsString, SpriteSheetMetaData.class);

			if (lSpriteMetaObject == null || lSpriteMetaObject.spriteSheetLocations == null || lSpriteMetaObject.spriteSheetLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load sprites from sprite meta file");
				return;
			}
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		}

		// Iterate through the sprite files, and load the individual sprites
		final int lSpriteCount = lSpriteMetaObject.spriteSheetLocations.length;
		for (int i = 0; i < lSpriteCount; i++) {
			final var lCleanFilename = FileUtils.cleanFilename(lSpriteMetaObject.spriteSheetLocations[i]);
			final var lSpriteSheetFile = new File(System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME), lCleanFilename);

			if (!lSpriteSheetFile.exists()) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading sprite sheet from " + lSpriteSheetFile.getPath() + " doesn't exist!");
				continue;
			}

			try {
				final String lSpriteSheetFileContents = new String(Files.readAllBytes(lSpriteSheetFile.toPath()));
				final SpriteSheetDefinition lSpriteSheet = lGson.fromJson(lSpriteSheetFileContents, SpriteSheetDefinition.class);

				// Check the integrity of the loaded spritsheet
				if (lSpriteSheet == null) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Error loading spritesheet " + lSpriteSheetFile.getPath());
					continue;
				}

				lSpriteSheet.fileSizeOnLoad(lSpriteSheetFile.length());
				lSpriteSheet.mSpriteSheetFilename = lSpriteSheetFile.getPath();
				lSpriteSheet.loadResources(mResourceManager, entityGroupUid);

				if (lSpriteSheet.mAnimationFramesMap == null || lSpriteSheet.mAnimationFramesMap.size() == 0) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Loaded SpriteSheetDefinition which has neither sprites nor frames defined within: " + lSpriteSheetFile.getPath());
				}

				Map<String, SpriteSheetDefinition> lSpriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
				if (lSpriteSheetGroup == null) {
					lSpriteSheetGroup = new HashMap<>();
					mSpriteSheetGroups.put(entityGroupUid, lSpriteSheetGroup);
				}

				Debug.debugManager().logger().i(getClass().getSimpleName(), "Loaded spritesheet " + lSpriteSheet.mSpriteSheetName);

				// Add the spritesheet to the collection, using the FILENAME as the key
				lSpriteSheetGroup.put(lSpriteSheet.mSpriteSheetName, lSpriteSheet);

			} catch (JsonSyntaxException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (Syntax): " + lSpriteSheetFile.getPath());
				Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
			} catch (IOException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (IO): " + lSpriteSheetFile.getPath());
				Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
			}
		}

		notifyListenersOfChange();
	}

	public void reload() {
		final var lGson = new GsonBuilder().create();

		for (final var lSpriteSheetGroup : mSpriteSheetGroups.values()) {
			for (final var entry : lSpriteSheetGroup.entrySet()) {
				var lSpriteSheet = entry.getValue();
				if (!lSpriteSheet.mIsReloadable)
					continue;

				final var lSpriteSheetFile = new File(lSpriteSheet.mSpriteSheetFilename);
				if (lSpriteSheetFile.length() != lSpriteSheet.fileSizeOnLoad()) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Reloading SpriteSheet (size on disk change detected).");

					try {
						final var lSpriteSheetFileContents = new String(Files.readAllBytes(lSpriteSheetFile.toPath()));
						final var lNewSpriteSheet = lGson.fromJson(lSpriteSheetFileContents, SpriteSheetDefinition.class);

						lNewSpriteSheet.loadResources(mResourceManager);

						lSpriteSheet.unloadResources();

						entry.setValue(lNewSpriteSheet);

					} catch (JsonSyntaxException e) {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (Syntax): " + lSpriteSheetFile.getPath());
						Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
					} catch (IOException e) {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (IO): " + lSpriteSheetFile.getPath());
						Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
					}
				}
			}
		}
	}
}
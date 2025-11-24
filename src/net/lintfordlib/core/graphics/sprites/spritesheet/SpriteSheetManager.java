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
		final var spriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
		if (spriteSheetGroup != null) {
			return spriteSheetGroup.get(spriteSheetDefinitionName);
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
		final var numListeners = mChangeListeners.size();
		for (int i = 0; i < numListeners; i++) {
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
		final var spriteSheetExists = getSpriteSheet(spritesheetName, entityGroupUid);
		if (spriteSheetExists != null)
			return spriteSheetExists;

		return loadSpriteSheet(filepath, entityGroupUid);
	}

	public SpriteSheetDefinition loadSpriteSheet(String filepath, int entityGroupUid) {
		if (filepath == null || filepath.length() == 0) {
			Debug.debugManager().logger().v(getClass().getSimpleName(), "Error loading spritesheet. Pathname is null! ");
			return null;
		}

		if (FileUtils.getIsFilePathAResource(filepath)) {
			return loadSpriteSheetFromResource(FileUtils.getResourceUrl(filepath), entityGroupUid);
		} else {
			return loadSpriteSheetFromFile(filepath, entityGroupUid);
		}
	}

	private SpriteSheetDefinition loadSpriteSheetFromFile(String filepath, int entityGroupUid) {
		if (filepath == null || filepath.length() == 0)
			return null;

		final var cleanFilename = FileUtils.cleanFilename(filepath);
		final var file = new File(System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME), cleanFilename);

		if (!file.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + filepath + " doesn't exist!");
			return null;
		}

		final Gson gson = new GsonBuilder().create();

		try {
			final var fileContents = new String(Files.readAllBytes(file.toPath()));
			final var spriteSheetDefinition = gson.fromJson(fileContents, SpriteSheetDefinition.class);

			if (spriteSheetDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), " Error deserializing Spritesheetdefinition '" + file.getPath() + "'.");
				return null;
			}

			if (spriteSheetDefinition.getSpriteCount() == 0)
				Debug.debugManager().logger().w(getClass().getSimpleName(), spriteSheetDefinition.mSpriteSheetName + " has no SpriteMap Sprites defined (SpriteMap is empty!) " + file.getPath());

			Debug.debugManager().logger().v(getClass().getSimpleName(), "SpriteSheet " + file.getPath() + " loaded (" + spriteSheetDefinition.mSpriteSheetName + ")");

			var spriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
			if (spriteSheetGroup == null) {
				spriteSheetGroup = new HashMap<>();
				mSpriteSheetGroups.put(entityGroupUid, spriteSheetGroup);
			}

			if (spriteSheetGroup.containsKey(spriteSheetDefinition.mSpriteSheetName)) {
				final var oldSpritesheet = spriteSheetGroup.get(spriteSheetDefinition.mSpriteSheetName);
				oldSpritesheet.unloadResources();
				oldSpritesheet.copyFrom(spriteSheetDefinition);
				oldSpritesheet.loadResources(mResourceManager);

				return oldSpritesheet;
			} else {
				spriteSheetDefinition.fileSizeOnLoad(file.length());
				spriteSheetDefinition.mSpriteSheetFilename = file.getPath();
				spriteSheetDefinition.reloadable(true);
				spriteSheetDefinition.loadResources(mResourceManager, entityGroupUid);

				spriteSheetGroup.put(spriteSheetDefinition.mSpriteSheetName, spriteSheetDefinition);

				return spriteSheetDefinition;
			}
		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (Syntax): %s", file.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse JSON SpriteSheet (IO): %s", file.getPath()));
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}

		notifyListenersOfChange();

		return null;
	}

	private SpriteSheetDefinition loadSpriteSheetFromResource(String filepath, int entityGroupUid) {
		if (filepath == null || filepath.length() == 0)
			return null;

		// TODO: add the 'remov' RES// code to within this method - its being called from a few locations, and different in each place.

		final Gson gson = new GsonBuilder().create();

		try {

			final var inputStream = FileUtils.class.getResourceAsStream(filepath);
			final var gsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

			final SpriteSheetDefinition spriteSheet = gson.fromJson(gsonReader, SpriteSheetDefinition.class);

			if (spriteSheet == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading spritesheet " + filepath);
				return null;
			}

			var spriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
			if (spriteSheetGroup == null) {
				spriteSheetGroup = new HashMap<>();
				mSpriteSheetGroups.put(entityGroupUid, spriteSheetGroup);
			}

			spriteSheet.reloadable(false);
			spriteSheet.loadResources(mResourceManager, entityGroupUid);

			spriteSheetGroup.put(spriteSheet.mSpriteSheetName, spriteSheet);

			Debug.debugManager().logger().v(getClass().getSimpleName(), String.format("Loaded SpriteSheet '%s' loaded from %s", spriteSheet.mSpriteSheetName, filepath));

			notifyListenersOfChange();

			return spriteSheet;

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
	public void loadSpriteSheetFromMeta(String metaFileLocation, int entityGroupUid) {
		if (metaFileLocation == null || metaFileLocation.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file cannot be null or empty when loading SpriteSheets.");
			return;
		}

		final var isFromResource = FileUtils.getIsFilePathAResource(metaFileLocation);

		final var workspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
		if (!isFromResource && !metaFileLocation.startsWith(workspacePath)) {
			metaFileLocation = Paths.get(workspacePath, metaFileLocation).toString();
		}

		final var metaFile = new File(metaFileLocation);
		if (metaFile.exists() == false) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file doesn't exist - skipping");
			return;
		}

		final Gson gson = new GsonBuilder().create();

		// Load the Sprite meta data
		String metaFileContentsString = null;
		SpriteSheetMetaData spriteMetaObject = null;
		try {
			metaFileContentsString = new String(Files.readAllBytes(Paths.get(metaFileLocation)));
			spriteMetaObject = gson.fromJson(metaFileContentsString, SpriteSheetMetaData.class);

			if (spriteMetaObject == null || spriteMetaObject.spriteSheetLocations == null || spriteMetaObject.spriteSheetLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load sprites from sprite meta file");
				return;
			}
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		}

		// Iterate through the sprite files, and load the individual sprites
		final var spriteSheetCount = spriteMetaObject.spriteSheetLocations.length;
		for (int i = 0; i < spriteSheetCount; i++) {
			var filePath = FileUtils.cleanFilename(spriteMetaObject.spriteSheetLocations[i]);

			// TODO: This needs to be a general prepass - all resources get RES// and all files get made absolute before loading.
			if (isFromResource) {

				if (!filePath.startsWith(FileUtils.RESOURCE_LOCATION_PREFIX))
					filePath = FileUtils.RESOURCE_LOCATION_PREFIX + filePath;

			} else {

				if (!filePath.startsWith(metaFileContentsString))
					filePath = Paths.get(workspacePath, filePath).toString();

			}

			// TODO: Need to check this, preiviously was appending the WORKSPACE_LOCATION to it
			final var spriteSheetFile = new File(filePath);

			Debug.debugManager().logger().i(getClass().getSimpleName(), "  Loading spritesheet from: " + spriteSheetFile.toString());

			if (!spriteSheetFile.exists()) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Sprtiesheet file doesn't exist: " + spriteSheetFile.getPath());
				continue;
			}

			try {
				final var spriteSheetFileContents = new String(Files.readAllBytes(spriteSheetFile.toPath()));
				final var spriteSheet = gson.fromJson(spriteSheetFileContents, SpriteSheetDefinition.class);

				if (spriteSheet == null) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Error loading spritesheet " + spriteSheetFile.getPath());
					continue;
				}

				spriteSheet.fileSizeOnLoad(spriteSheetFile.length());
				spriteSheet.mSpriteSheetFilename = spriteSheetFile.getPath();
				spriteSheet.loadResources(mResourceManager, entityGroupUid);

				if (spriteSheet.mAnimationFramesMap == null || spriteSheet.mAnimationFramesMap.size() == 0) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Loaded SpriteSheetDefinition which has neither sprites nor frames defined within: " + spriteSheetFile.getPath());
				}

				var spriteSheetGroup = mSpriteSheetGroups.get(entityGroupUid);
				if (spriteSheetGroup == null) {
					spriteSheetGroup = new HashMap<>();
					mSpriteSheetGroups.put(entityGroupUid, spriteSheetGroup);
				}

				Debug.debugManager().logger().i(getClass().getSimpleName(), "Loaded spritesheet " + spriteSheet.mSpriteSheetName);

				spriteSheetGroup.put(spriteSheet.mSpriteSheetName, spriteSheet);

			} catch (JsonSyntaxException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (Syntax): " + spriteSheetFile.getPath());
				Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
			} catch (IOException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse SpriteSheet (IO): " + spriteSheetFile.getPath());
				Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
			}
		}

		notifyListenersOfChange();
	}

	public void reload() {
		final var gson = new GsonBuilder().create();

		for (final var spriteSheetGroup : mSpriteSheetGroups.values()) {
			for (final var entry : spriteSheetGroup.entrySet()) {
				var spriteSheet = entry.getValue();
				if (!spriteSheet.mIsReloadable)
					continue;

				final var spriteSheetFile = new File(spriteSheet.mSpriteSheetFilename);
				if (spriteSheetFile.length() != spriteSheet.fileSizeOnLoad()) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Reloading SpriteSheet (size on disk change detected).");

					try {
						final var lSpriteSheetFileContents = new String(Files.readAllBytes(spriteSheetFile.toPath()));
						final var newSpriteSheet = gson.fromJson(lSpriteSheetFileContents, SpriteSheetDefinition.class);

						newSpriteSheet.loadResources(mResourceManager);

						spriteSheet.unloadResources();

						entry.setValue(newSpriteSheet);

					} catch (JsonSyntaxException e) {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (Syntax): " + spriteSheetFile.getPath());
						Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
					} catch (IOException e) {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to reload SpriteSheet (IO): " + spriteSheetFile.getPath());
						Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
					}
				}
			}
		}
	}
}
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
	 * @param spriteSheetName     The name of the Spritesheet to attempt to load.
	 * @param spriteSheetLocation The filepath of the Spritesheet to load, should it not yet have been.
	 * @param entityGroupUid      The entityGroupUid to use when loading the resource.
	 * 
	 * @implNote If no SpritesheetDefinition with the given name can be found, then it will be loaded from the filepath provided. The new SpritesheetDefinition will be assigned the name in the definition file, and *not* the parameter.
	 */

	public SpriteSheetDefinition loadSpriteSheet(String spriteSheetName, String spriteSheetLocation, int entityGroupUid) {
		final var spriteSheetExists = getSpriteSheet(spriteSheetName, entityGroupUid);
		if (spriteSheetExists != null)
			return spriteSheetExists;

		// TODO: This method isn't quite correct - if no spritesheet is found, then after it is loaded (and assuming it loads successfully), then we should put this in the entityGroup with the given name.

		return loadSpriteSheet(spriteSheetLocation, entityGroupUid);
	}

	public SpriteSheetDefinition loadSpriteSheet(String spriteSheetLocation, int entityGroupUid) {
		if (spriteSheetLocation == null || spriteSheetLocation.length() == 0) {
			Debug.debugManager().logger().v(getClass().getSimpleName(), "Error loading spritesheet. Pathname is null! ");
			return null;
		}

		if (FileUtils.getIsFilePathAResource(spriteSheetLocation)) {
			final var embeddedResourceLocation = FileUtils.getResourceUrl(spriteSheetLocation);
			return loadSpriteSheetFromResource(embeddedResourceLocation, entityGroupUid);

		} else {

			final var workspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
			final var cleanedResourceFileName = FileUtils.cleanFilename(spriteSheetLocation);

			if (!spriteSheetLocation.startsWith(workspacePath)) {
				spriteSheetLocation = Paths.get(workspacePath, cleanedResourceFileName).toString();
			}

			return loadSpriteSheetFromFile(spriteSheetLocation, entityGroupUid);
		}
	}

	/** Loads SpriteSheetDefinition from an absolute filePath. */
	private SpriteSheetDefinition loadSpriteSheetFromFile(String filePath, int entityGroupUid) {
		if (filePath == null || filePath.length() == 0)
			return null;

		final var file = new File(filePath);

		if (!file.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Error: Spritesheet file " + filePath + " doesn't exist!");
			return null;
		}

		final var gson = new GsonBuilder().create();

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

	private SpriteSheetDefinition loadSpriteSheetFromResource(String filePath, int entityGroupUid) {
		if (filePath == null || filePath.length() == 0)
			return null;

		final var gson = new GsonBuilder().create();

		try {

			final var inputStream = FileUtils.class.getResourceAsStream(filePath);
			final var gsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

			final var spriteSheet = (SpriteSheetDefinition) gson.fromJson(gsonReader, SpriteSheetDefinition.class);

			if (spriteSheet == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading spritesheet " + filePath);
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

			Debug.debugManager().logger().v(getClass().getSimpleName(), String.format("Loaded SpriteSheet '%s' loaded from %s", spriteSheet.mSpriteSheetName, filePath));

			notifyListenersOfChange();

			return spriteSheet;

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (Syntax): " + filePath);
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON SpriteSheet (IO): " + filePath);
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

		final var metaFile = new File(metaFileLocation);
		if (metaFile.exists() == false) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "SpriteSheetManager meta file doesn't exist - skipping");
			return;
		}

		final var gson = new GsonBuilder().create();

		var metaFileContentsString = (String) null;
		var spriteSheetMetaObject = (SpriteSheetMetaData) null;

		metaFileContentsString = FileUtils.loadString(metaFileLocation);
		spriteSheetMetaObject = gson.fromJson(metaFileContentsString, SpriteSheetMetaData.class);

		if (spriteSheetMetaObject == null || spriteSheetMetaObject.spriteSheetLocations == null || spriteSheetMetaObject.spriteSheetLocations.length == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the sprite sheet meta file");
			return;
		}

		// Iterate through the sprite files, and load the individual sprites
		final var spriteSheetCount = spriteSheetMetaObject.spriteSheetLocations.length;
		for (int i = 0; i < spriteSheetCount; i++) {

			final var spriteSheetLocation = spriteSheetMetaObject.spriteSheetLocations[i];

			loadSpriteSheet(spriteSheetLocation, entityGroupUid);

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
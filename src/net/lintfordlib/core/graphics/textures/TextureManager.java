package net.lintfordlib.core.graphics.textures;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import com.google.gson.GsonBuilder;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.EntityGroupManager;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.storage.FileUtils;

public class TextureManager extends EntityGroupManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	// used for loading lots of textures at once (defined within a meta files).
	public class TextureDataDefinition {
		private String textureName;
		private String filepath;
		private int filterIndex;
	}

	// used for loading lots of textures at once (defined within a meta files).
	public class TextureMetaData {
		private TextureDataDefinition[] textureDefinitions;
	}

	// used as a colection for textures, identified by a common key.
	public class TextureGroup {

		// --------------------------------------
		// Variables
		// --------------------------------------

		protected Map<String, Texture> mTextureMap;
		protected boolean mAutomaticUnload = true;
		protected int mEntityGroupID;
		protected String mTextureGroupName = "";
		protected int mReferenceCount = 0;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public Map<String, Texture> textureMap() {
			return mTextureMap;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public TextureGroup(int entityGroupUid) {
			mTextureMap = new HashMap<>();

			mEntityGroupID = entityGroupUid;
			mReferenceCount = 0;
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		Texture getTextureByName(String textureName) {
			return mTextureMap.get(textureName);
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String CORE_META_FILE = FileUtils.RESOURCE_LOCATION_PREFIX + "/res/textures/_meta.json";

	/** When enabled, missing textures will be filled with a magenta color. */
	public static final boolean USE_DEBUG_MISSING_TEXTURES = true;

	/*
	 * These three texture names are loaded when the TextureManager is instantiated. They are potentially shared between many resource groups, and are therefore protected so they are not inadvertently unloaded when, for example, changing scenes.
	 */

	public static final String TEXTURE_WHITE_NAME = "TEXTURE_WHITE";
	public static final String TEXTURE_BLACK_NAME = "TEXTURE_BLACK";
	public static final String TEXTURE_NOT_FOUND_NAME = "TEXTURE_NOT_FOUND";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Map<Integer, TextureGroup> mTextureGroupMap;

	private ResourceManager mResourceManager;

	private Texture mTextureNotFound;
	private Texture mTextureWhite;
	private Texture mTextureBlack;
	private Texture mTextureChecker;
	private Texture mTextureCheckerIndexed;

	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ResourceManager resourceManager() {
		return mResourceManager;
	}

	public Map<Integer, TextureGroup> textureGroups() {
		return mTextureGroupMap;
	}

	public TextureGroup textureGroup(int entityGroupUid) {
		return mTextureGroupMap.computeIfAbsent(entityGroupUid, k -> new TextureGroup(entityGroupUid));
	}

	public int textureGroupCount() {
		return mTextureGroupMap.size();
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public Texture textureNotFound() {
		return mTextureNotFound;
	}

	public Texture textureWhite() {
		return mTextureWhite;
	}

	public Texture textureBlack() {
		return mTextureBlack;
	}

	public Texture checkerTexture() {
		return mTextureChecker;
	}

	public Texture checkerIndexedTexture() {
		return mTextureCheckerIndexed;
	}

	public boolean isTextureLoaded(Texture texture) {
		return (texture != null && !texture.name().equals(TextureManager.TEXTURE_NOT_FOUND_NAME));
	}

	public Texture getTextureOrLoad(String textureName, String textureFilepath, int textureFilter, int entityGroupUid) {
		final var returnTexture = isTextureLoaded(textureName, entityGroupUid);

		if (isTextureLoaded(returnTexture))
			return returnTexture;

		if (textureFilepath == null || textureFilepath.length() == 0)
			return null;

		final var resolvedTextureName = textureName != null ? textureName : textureFilepath;

		return loadTexture(resolvedTextureName, textureFilepath, textureFilter, entityGroupUid);
	}

	// returns the texture if one is found, otherwise null
	public Texture isTextureLoaded(String textureName, int entityGroupUid) {
		final var textureGroup = mTextureGroupMap.get(entityGroupUid);
		if (textureGroup == null)
			return null;

		return textureGroup.getTextureByName(textureName);
	}

	/** Returns the {@link Texture} with the given name. If no {@link Texture} by the given name is found, a default MAGENTA texture will be returned. */
	public Texture getTexture(String textureName, int entityGroupUid) {
		TextureGroup textureGroup = mTextureGroupMap.get(entityGroupUid);

		if (textureGroup == null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't getTexture %s: TextureGroup %d doesn't exit", textureName, entityGroupUid));
			return mTextureNotFound;
		}

		if (textureGroup.mTextureMap.containsKey(textureName))
			return textureGroup.mTextureMap.get(textureName);

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't getTexture %s: TextureGroup %d doesn't exit", textureName, entityGroupUid));
		return mTextureNotFound;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TextureManager() {
		mTextureGroupMap = new HashMap<>();

		final var coreTextureGroup = new TextureGroup(LintfordCore.CORE_ENTITY_GROUP_ID);
		coreTextureGroup.mAutomaticUnload = false;
		coreTextureGroup.mTextureGroupName = "CORE";
		coreTextureGroup.mReferenceCount = 1;
		mTextureGroupMap.put(LintfordCore.CORE_ENTITY_GROUP_ID, coreTextureGroup);

		loadTexturesFromMetafile(CORE_META_FILE, LintfordCore.CORE_ENTITY_GROUP_ID);

		mTextureNotFound = loadTexture(TEXTURE_NOT_FOUND_NAME, new int[] { 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF }, 2, 2, LintfordCore.CORE_ENTITY_GROUP_ID);
		mTextureWhite = loadTexture(TEXTURE_WHITE_NAME, new int[] { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF }, 2, 2, GL11.GL_NEAREST, GL11.GL_REPEAT, GL11.GL_REPEAT, LintfordCore.CORE_ENTITY_GROUP_ID);
		mTextureBlack = loadTexture(TEXTURE_BLACK_NAME, new int[] { 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000 }, 2, 2, GL11.GL_NEAREST, GL11.GL_REPEAT, GL11.GL_REPEAT, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;

		mIsLoaded = true;
	}

	public void unloadResources() {
		if (!mIsLoaded)
			return;

		for (final var entry : mTextureGroupMap.entrySet()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("TextureGroup %s (%d)..", entry.getValue().mTextureGroupName, entry.getValue().mEntityGroupID));

			final var lTextureGroup = entry.getValue();
			final var groupMap = lTextureGroup.textureMap();
			for (final var texture : groupMap.entrySet()) {
				Texture.unloadTexture(texture.getValue());
			}

			groupMap.clear();
		}
		mTextureGroupMap.clear();

		mIsLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public int increaseReferenceCounts(int entityGroupUid) {
		var textureGroup = mTextureGroupMap.get(entityGroupUid);

		if (textureGroup == null) {
			textureGroup = new TextureGroup(entityGroupUid);
			textureGroup.mReferenceCount = 1;

			mTextureGroupMap.put(entityGroupUid, textureGroup);
		} else {
			textureGroup.mReferenceCount++;
		}

		return textureGroup.mReferenceCount;
	}

	@Override
	public int decreaseReferenceCounts(int entityGroupUid) {
		final var textureGroup = mTextureGroupMap.get(entityGroupUid);

		if (textureGroup == null) {
			return 0;
		} else {
			if (mResourceManager.isEntityGroupUidProtected(entityGroupUid)) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Cannot decrease reference count on EntityGroupUid: " + entityGroupUid + " (protected)");
				return textureGroup.mReferenceCount;
			}

			if (textureGroup.mReferenceCount > 0) {
				textureGroup.mReferenceCount--;
			}
		}

		if (textureGroup.mReferenceCount <= 0) {
			unloadEntityGroup(entityGroupUid);

			mTextureGroupMap.remove(entityGroupUid);
			return 0;
		}

		return textureGroup.mReferenceCount;
	}

	private TextureGroup getTextureGroup(int entityGroupUid) {
		return mTextureGroupMap.computeIfAbsent(entityGroupUid, v -> {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "EntityGroupID does not exist! Creating a new one");
			return new TextureGroup(entityGroupUid);
		});
	}

	public Texture loadTexture(String textureName, String textureLocation, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, GL11.GL_NEAREST, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, filter, false, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, boolean reload, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, filter, GL11.GL_REPEAT, GL11.GL_REPEAT, reload, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, int wrapModeS, int wrapModeT, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, filter, wrapModeS, wrapModeT, false, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, int wrapModeS, int wrapModeT, boolean reload, int entityGroupUid) {
		if (textureLocation == null || textureLocation.length() == 0)
			return null;

		final var textureGroup = getTextureGroup(entityGroupUid);

		Texture texture = null;
		if (textureGroup.mTextureMap.containsKey(textureName)) {
			texture = textureGroup.mTextureMap.get(textureName);

			if (!reload)
				return texture;

			Debug.debugManager().logger().v(getClass().getSimpleName(), "Unloading " + textureName + ", so it can be reloaded");
			unloadTexture(texture, entityGroupUid);
		}

		if (FileUtils.getIsFilePathAResource(textureLocation)) {

			final var embeddedResourceLocation = FileUtils.getResourceUrl(textureLocation);
			texture = Texture.loadTextureFromResource(textureName, embeddedResourceLocation, filter);

		} else {

			final var workspacePath = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
			final var cleanedResourceFileName = FileUtils.cleanFilename(textureLocation);

			if (!textureLocation.startsWith(workspacePath)) {
				textureLocation = Paths.get(workspacePath, cleanedResourceFileName).toString();
			}

			texture = Texture.loadTextureFromFile(textureName, textureLocation, filter, wrapModeS, wrapModeT);

		}

		if (texture != null)
			textureGroup.mTextureMap.put(textureName, texture);

		if (texture == null)
			return mTextureNotFound;

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TEXTURES, 1);

		return texture;
	}

	public Texture loadTexture(String textureName, int[] colorDataARGB, int width, int height, int entityGroupUid) {
		return loadTexture(textureName, colorDataARGB, width, height, GL11.GL_NEAREST, GL11.GL_REPEAT, GL11.GL_REPEAT, entityGroupUid);
	}

	public Texture loadTexture(String name, int[] colorDataARGB, int width, int height, int filter, int wrapSMode, int wrapTMode, int entityGroupUid) {
		Texture returnTexture = null;
		TextureGroup textureGroup = getTextureGroup(entityGroupUid);

		if (textureGroup.mTextureMap.containsKey(name)) {
			returnTexture = textureGroup.mTextureMap.get(name);
		}

		if (returnTexture != null && returnTexture.getTextureID() != -1) {
			returnTexture.updateGLTextureData(colorDataARGB, width, height);
			return returnTexture;

		} else {

			final var newTexture = Texture.createTexture(name, name, colorDataARGB, width, height, filter, wrapSMode, wrapTMode);
			if (newTexture != null) {
				newTexture.reloadable(false);
				textureGroup.mTextureMap.put(name, newTexture);
			}

			return newTexture;
		}
	}

	public void reloadAllTextures() {
		Debug.debugManager().logger().v(getClass().getSimpleName(), "Reloading all modified textures");

		for (final var textureGroup : mTextureGroupMap.values()) {
			for (final var texture : textureGroup.mTextureMap.values()) {
				if (texture != null) {
					texture.reload();
				}
			}
		}
	}

	/** Unloads the specified texture in the texture group, if applicable. */
	public void unloadTexture(Texture texture, int entityGroupUid) {
		if (texture == null)
			return; // already lost reference

		final var textureGroup = mTextureGroupMap.get(entityGroupUid);
		if (textureGroup.mTextureMap.containsValue(texture)) {
			final var textureName = texture.name();
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("unloading texture: %s from texture group %d", textureName, entityGroupUid));

			Texture.unloadTexture(texture);

			textureGroup.mTextureMap.remove(textureName);
		}
	}

	public void unloadEntityGroup(int entityGroupUid) {
		final var textureGroup = mTextureGroupMap.get(entityGroupUid);

		if (textureGroup == null)
			return;

		final int textureCount = textureGroup.mTextureMap.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Unloading TextureGroup %d (freeing total %d textures)", entityGroupUid, textureCount));

		final var iterator = textureGroup.mTextureMap.entrySet().iterator();
		while (iterator.hasNext()) {
			final var nextTexture = iterator.next();
			Texture.unloadTexture(nextTexture.getValue());

			iterator.remove();
		}
	}

	public void loadTexturesFromMetafile(String metaFileLocation, int entityGroupUid) {
		loadTexturesFromMetafile(metaFileLocation, null, entityGroupUid);
	}

	/***
	 * Batch loads texture files as given in a 'meta' file.
	 * 
	 * @param metaFileLocation The location of the meta file (either a relative/abolsute file path or an embedded resource location).
	 * @param baseDirectory    An optional base directory that will be prepended to each item. Can be a file path or resource location.
	 * @param entityGroupUid   The entity group uid to which each loaded texture resource will be added.
	 */
	public void loadTexturesFromMetafile(String metaFileLocation, String baseDirectory, int entityGroupUid) {

		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading textures from meta-file %s", metaFileLocation));

		final var gson = new GsonBuilder().create();

		var metaFileContentsString = (String) null;
		var textureMetaData = (TextureMetaData) null;

		metaFileContentsString = FileUtils.loadString(metaFileLocation);

		try {
			textureMetaData = gson.fromJson(metaFileContentsString, TextureMetaData.class);

			if (textureMetaData == null || textureMetaData.textureDefinitions == null || textureMetaData.textureDefinitions.length == 0) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the PObject meta file");
				return;
			}

			final var textureGroup = getTextureGroup(entityGroupUid);

			final int numberOfTextureDefinitions = textureMetaData.textureDefinitions.length;
			for (int i = 0; i < numberOfTextureDefinitions; i++) {
				final var textureDataDefinition = textureMetaData.textureDefinitions[i];

				final var textureName = textureDataDefinition.textureName;
				var filePath = textureDataDefinition.filepath;

				if (baseDirectory != null)
					filePath = baseDirectory + filePath;

				Debug.debugManager().logger().i(getClass().getSimpleName(), "  Loading texture from: " + filePath);

				final int glFilterMode = mapTextureFilterMode(textureDataDefinition.filterIndex);
				final int glWrapSFilter = mapWrapMode(textureDataDefinition.filterIndex);
				final int glWrapTFilter = mapWrapMode(textureDataDefinition.filterIndex);

				final var newTexture = loadTexture(textureName, filePath, glFilterMode, glWrapSFilter, glWrapTFilter, false, entityGroupUid);

				if (newTexture != null) {
					newTexture.reloadable(true);

					Debug.debugManager().logger().i(getClass().getSimpleName(), "Loaded texture from Meta '" + textureName + "' into EntityGroupID: " + entityGroupUid);

					textureGroup.mTextureMap.put(textureName, newTexture);
				} else {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to Load texture from meta: '" + textureName + "'");
				}
			}
		} catch (Exception e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to load textures from meta file. Exception: " + e.getMessage());
		}
	}

	/**
	 * Maps the index from the Texture Meta data file to the GL filter mode.
	 * 
	 * 1 = GL_NEAREST 2 = GL_LINEAR
	 */
	private int mapTextureFilterMode(int filterModeIndex) {
		if (filterModeIndex == 1) {
			return GL11.GL_NEAREST;
		} else {
			return GL11.GL_LINEAR;
		}
	}

	/**
	 * Maps the index from the Texture Meta data file to the GL Wrap mode.
	 * 
	 * 1 = GL_CLAMP_TO_EDGE 2 = GL_MIRRORED_REPEAT 3 = GL_REPEAT
	 */
	private int mapWrapMode(int wrapModeIndex) {
		switch (wrapModeIndex) {
		case 1:
			return GL12.GL_CLAMP_TO_EDGE;
		case 2:
			return GL14.GL_MIRRORED_REPEAT;
		case 3:
			return GL11.GL_REPEAT;
		default:
			return GL12.GL_CLAMP_TO_EDGE;
		}
	}

	public void dumpTextureInformation() {
		for (var entry : mTextureGroupMap.entrySet()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("TextureGroup %s (%d)..", entry.getValue().mTextureGroupName, entry.getValue().mEntityGroupID));
			final var textureGroup = entry.getValue();

			final var groupMap = textureGroup.textureMap();
			for (final var texture : groupMap.entrySet()) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  Texture %s (%d)", texture.getValue().name(), texture.getValue().getTextureID()));
			}
		}
	}
}

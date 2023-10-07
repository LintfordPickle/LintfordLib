package net.lintfordlib.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.EntityGroupManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.storage.FileUtils;

public class TextureManager extends EntityGroupManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class TextureDataDefinition {
		public String textureName;
		public String filepath;
		public int filterIndex;
		public int wrapSIndex;
		public int wrapTIndex;
	}

	public class TextureMetaData {
		public TextureDataDefinition[] textureDefinitions;
	}

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

	private static final String CORE_META_FILE = "/res/textures/_meta.json";

	/** When enabled, missing textures will be filled with a magenta color. */
	public static final boolean USE_DEBUG_MISSING_TEXTURES = true;

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
		if (!mTextureGroupMap.containsKey(entityGroupUid)) {
			TextureGroup lNewTextureGroup = new TextureGroup(entityGroupUid);
			mTextureGroupMap.put(entityGroupUid, lNewTextureGroup);

			return lNewTextureGroup;
		}

		return mTextureGroupMap.get(entityGroupUid);
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
		return (texture != null && texture.name().equals(TextureManager.TEXTURE_NOT_FOUND_NAME) == false);
	}

	public Texture getTextureOrLoad(String textureName, String textureFilepath, int textureFilter, int entityGroupUid) {
		final var lRetTexture = isTextureLoaded(textureName, entityGroupUid);

		if (isTextureLoaded(lRetTexture))
			return lRetTexture;

		if (textureFilepath == null || textureFilepath.length() == 0)
			return null;

		String lTextureName = textureName != null ? textureName : textureFilepath;

		return loadTexture(lTextureName, textureFilepath, textureFilter, entityGroupUid);
	}

	// returns the texture if one is found, otherwise null
	public Texture isTextureLoaded(String textureName, int entityGroupUid) {
		final var lTextureGroup = mTextureGroupMap.get(entityGroupUid);
		if (lTextureGroup == null)
			return null;

		return lTextureGroup.getTextureByName(textureName);
	}

	/** Returns the {@link Texture} with the given name. If no {@link Texture} by the given name is found, a default MAGENTA texture will be returned. */
	public Texture getTexture(String textureName, int entityGroupUid) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(entityGroupUid);

		if (lTextureGroup == null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't getTexture %s: TextureGroup %d doesn't exit", textureName, entityGroupUid));
			return mTextureNotFound;
		}

		if (lTextureGroup.mTextureMap.containsKey(textureName))
			return lTextureGroup.mTextureMap.get(textureName);

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't getTexture %s: TextureGroup %d doesn't exit", textureName, entityGroupUid));
		return mTextureNotFound;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TextureManager() {
		mTextureGroupMap = new HashMap<>();

		final var lCoreTextureGroup = new TextureGroup(LintfordCore.CORE_ENTITY_GROUP_ID);
		lCoreTextureGroup.mAutomaticUnload = false;
		lCoreTextureGroup.mTextureGroupName = "CORE";
		lCoreTextureGroup.mReferenceCount = 1;
		mTextureGroupMap.put(LintfordCore.CORE_ENTITY_GROUP_ID, lCoreTextureGroup);

		loadTexturesFromMetafile(CORE_META_FILE, LintfordCore.CORE_ENTITY_GROUP_ID);

		mTextureNotFound = loadTexture(TEXTURE_NOT_FOUND_NAME, new int[] { 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF }, 2, 2, LintfordCore.CORE_ENTITY_GROUP_ID);
		mTextureWhite = loadTexture(TEXTURE_WHITE_NAME, new int[] { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF }, 2, 2, GL11.GL_NEAREST, GL12.GL_REPEAT, GL12.GL_REPEAT, LintfordCore.CORE_ENTITY_GROUP_ID);
		mTextureBlack = loadTexture(TEXTURE_BLACK_NAME, new int[] { 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000 }, 2, 2, GL11.GL_NEAREST, GL12.GL_REPEAT, GL12.GL_REPEAT, LintfordCore.CORE_ENTITY_GROUP_ID);
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
			final var lGroupMap = lTextureGroup.textureMap();
			for (final var lTexture : lGroupMap.entrySet()) {
				Texture.unloadTexture(lTexture.getValue());
			}

			lGroupMap.clear();
		}
		mTextureGroupMap.clear();

		mIsLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public int increaseReferenceCounts(int entityGroupUid) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(entityGroupUid);

		if (lTextureGroup == null) {
			lTextureGroup = new TextureGroup(entityGroupUid);
			lTextureGroup.mReferenceCount = 1;

			mTextureGroupMap.put(entityGroupUid, lTextureGroup);
		} else {
			lTextureGroup.mReferenceCount++;
		}

		return lTextureGroup.mReferenceCount;
	}

	@Override
	public int decreaseReferenceCounts(int entityGroupUid) {
		final var lTextureGroup = mTextureGroupMap.get(entityGroupUid);

		if (lTextureGroup == null) {
			return 0;
		} else {
			if (mResourceManager.isEntityGroupUidProtected(entityGroupUid)) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Cannot decrease reference count on EntityGroupUid: " + entityGroupUid + " (protected)");
				return lTextureGroup.mReferenceCount;
			}

			if (lTextureGroup.mReferenceCount > 0) {
				lTextureGroup.mReferenceCount--;
			}
		}

		if (lTextureGroup.mReferenceCount <= 0) {
			unloadEntityGroup(entityGroupUid);

			mTextureGroupMap.remove(entityGroupUid);
			return 0;
		}

		return lTextureGroup.mReferenceCount;
	}

	public Texture loadTexture(String textureName, String textureLocation, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, GL11.GL_NEAREST, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, filter, false, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, boolean reload, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, filter, GL12.GL_REPEAT, GL12.GL_REPEAT, reload, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, int wrapModeS, int wrapModeT, int entityGroupUid) {
		return loadTexture(textureName, textureLocation, filter, wrapModeS, wrapModeT, false, entityGroupUid);
	}

	public Texture loadTexture(String textureName, String textureLocation, int filter, int wrapModeS, int wrapModeT, boolean reload, int entityGroupUid) {
		if (textureLocation == null || textureLocation.length() == 0) {
			return null;
		}

		final var lTextureGroup = getTextureGroup(entityGroupUid);

		Texture lTexture = null;
		if (lTextureGroup.mTextureMap.containsKey(textureName)) {
			lTexture = lTextureGroup.mTextureMap.get(textureName);

			if (!reload)
				return lTexture;

			Debug.debugManager().logger().v(getClass().getSimpleName(), "Unloading " + textureName + ", so it can be reloaded");
			unloadTexture(lTexture, entityGroupUid);
		}

		// Create new texture
		if (textureLocation.charAt(0) == '/') {
			lTexture = Texture.loadTextureFromResource(textureName, textureLocation, filter);
		} else {
			lTexture = Texture.loadTextureFromFile(textureName, textureLocation, filter);
		}

		if (lTexture != null) {
			lTextureGroup.mTextureMap.put(textureName, lTexture);
		}

		if (lTexture == null) {
			return mTextureNotFound;
		}

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TEXTURES, 1);

		return lTexture;
	}

	private TextureGroup getTextureGroup(int entityGroupUid) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(entityGroupUid);
		if (lTextureGroup == null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("EntityGroupID does not exist! Creating a new one", entityGroupUid));
			lTextureGroup = new TextureGroup(entityGroupUid);
			mTextureGroupMap.put(entityGroupUid, lTextureGroup);

		}
		return lTextureGroup;
	}

	public Texture loadTexture(String textureName, int[] colorDataARGB, int width, int height, int entityGroupUid) {
		return loadTexture(textureName, colorDataARGB, width, height, GL11.GL_NEAREST, GL12.GL_REPEAT, GL12.GL_REPEAT, entityGroupUid);
	}

	public Texture loadTexture(String name, int[] colorDataARGB, int width, int height, int filter, int wrapSMode, int wrapTMode, int entityGroupUid) {
		Texture lResult = null;
		TextureGroup lTextureGroup = getTextureGroup(entityGroupUid);

		if (lTextureGroup.mTextureMap.containsKey(name)) {
			lResult = lTextureGroup.mTextureMap.get(name);
		}

		if (lResult != null && lResult.getTextureID() != -1) {
			lResult.updateGLTextureData(colorDataARGB, width, height);
			return lResult;
		} else {
			Texture lTex = Texture.createTexture(name, name, colorDataARGB, width, height, filter, wrapSMode, wrapTMode);
			if (lTex != null) {
				lTex.reloadable(false);
				lTextureGroup.mTextureMap.put(name, lTex);
			}

			return lTex;
		}
	}

	public boolean saveTextureToFile(int width, int height, int[] imageData, String fileLocation) {
		BufferedImage lImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Convert our ARGB to output ABGR
		int[] lTextureData = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int a = (imageData[i] & 0xff000000) >> 24;
			int r = (imageData[i] & 0xff0000) >> 16;
			int g = (imageData[i] & 0xff00) >> 8;
			int b = (imageData[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		lImage.setRGB(0, 0, width, height, lTextureData, 0, width);

		final var lOutputfile = new File(fileLocation);
		try {
			ImageIO.write(lImage, "png", lOutputfile);
		} catch (IOException e) {
			return false;
		}

		return true;

	}

	public Texture createFontTexture(String textureName, BufferedImage image, int entityGroupUid) {
		return createFontTexture(textureName, image, GL11.GL_NEAREST, entityGroupUid);
	}

	public Texture createFontTexture(String textureName, BufferedImage image, int filter, int entityGroupUid) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(entityGroupUid);
		if (lTextureGroup == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Cannot load texture %s for EntityGroupID %d: EntityGroupID does not exist!", (textureName + " (Font)"), entityGroupUid));
			return null;

		} else if (lTextureGroup.mTextureMap.containsKey(textureName)) {
			return lTextureGroup.mTextureMap.get(textureName);
		}

		final var lNewTexture = Texture.createTexture(textureName, textureName, image, filter);
		lNewTexture.reloadable(false); // no need to reload font textures (on-the-fly)

		lTextureGroup.mTextureMap.put(textureName, lNewTexture);

		return lNewTexture;
	}

	public void reloadTextures() {
		Debug.debugManager().logger().v(getClass().getSimpleName(), "Reloading all modified files");

		for (final var lTextureGroup : mTextureGroupMap.values()) {
			for (final var lTexture : lTextureGroup.mTextureMap.values()) {
				if (lTexture != null) {
					lTexture.reload();
				}
			}
		}
	}

	/** Unloads the specified texture in the texture group, if applicable. */
	public void unloadTexture(Texture texture, int entityGroupUid) {
		if (texture == null)
			return; // already lost reference

		TextureGroup lTextureGroup = mTextureGroupMap.get(entityGroupUid);
		if (lTextureGroup == null) {
			return;

		} else if (lTextureGroup.mTextureMap.containsValue(texture)) {
			String lTextureName = texture.name();
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("unloading texture: %s from texture group %d", lTextureName, entityGroupUid));

			Texture.unloadTexture(texture);

			lTextureGroup.mTextureMap.remove(lTextureName);
			texture = null;
		}

		return;
	}

	public void unloadEntityGroup(int entityGroupUid) {
		final var lTextureGroup = mTextureGroupMap.get(entityGroupUid);

		if (lTextureGroup == null)
			return;

		final int lTextureCount = lTextureGroup.mTextureMap.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Unloading TextureGroup %d (freeing total %d textures)", entityGroupUid, lTextureCount));

		if (lTextureGroup != null) {
			final var lIterator = lTextureGroup.mTextureMap.entrySet().iterator();
			while (lIterator.hasNext()) {
				final var lNextTexture = lIterator.next();
				Texture.unloadTexture(lNextTexture.getValue());

				lIterator.remove();
			}
		}
	}

	/** Batch load textures */
	public void loadTexturesFromMetafile(String metaFileLocation, int entityGroupUid) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading textures from meta-file %s", metaFileLocation));

		final var lGson = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		TextureMetaData lTextureMetaData = null;

		lMetaFileContentsString = FileUtils.loadString(metaFileLocation);

		try {
			lTextureMetaData = lGson.fromJson(lMetaFileContentsString, TextureMetaData.class);

			if (lTextureMetaData == null || lTextureMetaData.textureDefinitions == null || lTextureMetaData.textureDefinitions.length == 0) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the PObject meta file");
				return;
			}

			final var lTextureGroup = getTextureGroup(entityGroupUid);

			final int lNumberOfTextureDefinitions = lTextureMetaData.textureDefinitions.length;
			for (int i = 0; i < lNumberOfTextureDefinitions; i++) {
				final var lTextureDataDefinition = lTextureMetaData.textureDefinitions[i];

				final var lTextureName = lTextureDataDefinition.textureName;
				final var lFilepath = lTextureDataDefinition.filepath;

				final int lGlFilterMode = mapTextureFilterMode(lTextureDataDefinition.filterIndex);
				final int lGlWrapSFilter = mapWrapMode(lTextureDataDefinition.filterIndex);
				final int lGlWrapTFilter = mapWrapMode(lTextureDataDefinition.filterIndex);

				final var lNewTexture = loadTexture(lTextureName, lFilepath, lGlFilterMode, lGlWrapSFilter, lGlWrapTFilter, true, entityGroupUid);

				if (lNewTexture != null) {
					lNewTexture.reloadable(true);

					Debug.debugManager().logger().i(getClass().getSimpleName(), "Loaded texture from Meta '" + lTextureName + "' into EntityGroupID: " + entityGroupUid);

					lTextureGroup.mTextureMap.put(lTextureName, lNewTexture);
				} else {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to Load texture from meta: '" + lTextureName + "'");
				}
			}
		} catch (Exception e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "");
		}
	}

	/**
	 * Maps the index from the Texture Meta data file to the GL filter mode.
	 * 
	 * 1 = GL_NEAREST 2 = GL_LINEAR
	 */
	private int mapTextureFilterMode(int filterModeIndex) {
		switch (filterModeIndex) {
		case 1:
			return GL11.GL_NEAREST;
		default:
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
			return GL12.GL_REPEAT;
		default:
			return GL12.GL_CLAMP_TO_EDGE;
		}
	}

	public void dumpTextureInformation() {
		for (Map.Entry<Integer, TextureGroup> entry : mTextureGroupMap.entrySet()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("TextureGroup %s (%d)..", entry.getValue().mTextureGroupName, entry.getValue().mEntityGroupID));
			TextureGroup lTextureGroup = entry.getValue();

			Map<String, Texture> lGroupMap = lTextureGroup.textureMap();
			for (Map.Entry<String, Texture> lTexture : lGroupMap.entrySet()) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  Texture %s (%d)", lTexture.getValue().name(), lTexture.getValue().getTextureID()));
			}
		}
	}
}

package net.lintford.library.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.EntityGroupManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.storage.FileUtils;

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

		Map<String, Texture> mTextureMap;

		boolean automaticUnload = true;
		int entityGroupID;
		String name = "";
		int referenceCount = 0;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public Map<String, Texture> textureMap() {
			return mTextureMap;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public TextureGroup(int pEntityGroupID) {
			mTextureMap = new HashMap<>();

			entityGroupID = pEntityGroupID;
			referenceCount = 0;

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		Texture getTextureByName(String pTextureName) {
			return mTextureMap.get(pTextureName);
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

	public TextureGroup textureGroup(int pEntityGroupID) {
		if (!mTextureGroupMap.containsKey(pEntityGroupID)) {
			TextureGroup lNewTextureGroup = new TextureGroup(pEntityGroupID);
			mTextureGroupMap.put(pEntityGroupID, lNewTextureGroup);

			return lNewTextureGroup;
		}

		return mTextureGroupMap.get(pEntityGroupID);
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

	public boolean isTextureLoaded(Texture pTexture) {
		return (pTexture != null && pTexture.name().equals(TextureManager.TEXTURE_NOT_FOUND_NAME) == false);
	}

	public Texture getTextureOrLoad(String pName, String pTextureFilepath, int pTextureFilter, int pEntityGroupID) {
		Texture lRetTexture = isTextureLoaded(pName, pEntityGroupID);

		if (isTextureLoaded(lRetTexture)) {
			return lRetTexture;
		}

		if (pTextureFilepath == null || pTextureFilepath.length() == 0) {
			return null;
		}

		String lTextureName = pName != null ? pName : pTextureFilepath;

		return loadTexture(lTextureName, pTextureFilepath, pTextureFilter, pEntityGroupID);
	}

	// returns the texture if one is found, otherwise null
	public Texture isTextureLoaded(String pName, int pEntityGroupID) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);
		if (lTextureGroup == null) {
			return null;
		}
		return lTextureGroup.getTextureByName(pName);
	}

	/** Returns the {@link Texture} with the given name. If no {@link Texture} by the given name is found, a default MAGENTA texture will be returned. */
	public Texture getTexture(String pName, int pEntityGroupID) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);

		if (lTextureGroup == null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't getTexture %s: TextureGroup %d doesn't exit", pName, pEntityGroupID));
			return mTextureNotFound;

		}

		if (lTextureGroup.mTextureMap.containsKey(pName)) {
			return lTextureGroup.mTextureMap.get(pName);

		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't getTexture %s: TextureGroup %d doesn't exit", pName, pEntityGroupID));
		return mTextureNotFound;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TextureManager() {
		mTextureGroupMap = new HashMap<>();

		TextureGroup lCoreTextureGroup = new TextureGroup(LintfordCore.CORE_ENTITY_GROUP_ID);
		lCoreTextureGroup.automaticUnload = false;
		lCoreTextureGroup.name = "CORE";
		lCoreTextureGroup.referenceCount = 1;
		mTextureGroupMap.put(LintfordCore.CORE_ENTITY_GROUP_ID, lCoreTextureGroup);

		loadTexturesFromMetafile(CORE_META_FILE, LintfordCore.CORE_ENTITY_GROUP_ID);

		mTextureNotFound = loadTexture(TEXTURE_NOT_FOUND_NAME, new int[] { 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF }, 2, 2, LintfordCore.CORE_ENTITY_GROUP_ID);
		mTextureWhite = loadTexture(TEXTURE_WHITE_NAME, new int[] { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF }, 2, 2, GL11.GL_NEAREST, GL12.GL_REPEAT, GL12.GL_REPEAT, LintfordCore.CORE_ENTITY_GROUP_ID);
		mTextureBlack = loadTexture(TEXTURE_BLACK_NAME, new int[] { 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000 }, 2, 2, GL11.GL_NEAREST, GL12.GL_REPEAT, GL12.GL_REPEAT, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		final var map = mTextureGroupMap;
		for (final var entry : map.entrySet()) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("TextureGroup %s (%d)..", entry.getValue().name, entry.getValue().entityGroupID));

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
	public int increaseReferenceCounts(int pEntityGroupID) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);

		// Create a new TextureGroup for this EntityGroupID if one doesn't exist
		if (lTextureGroup == null) {
			lTextureGroup = new TextureGroup(pEntityGroupID);
			lTextureGroup.referenceCount = 1;

			mTextureGroupMap.put(pEntityGroupID, lTextureGroup);

		} else {
			lTextureGroup.referenceCount++;

		}

		return lTextureGroup.referenceCount;

	}

	@Override
	public int decreaseReferenceCounts(int pEntityGroupID) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);

		// Create a new TextureGroup for this EntityGroupID if one doesn't exist
		if (lTextureGroup == null) {
			return 0;

		} else {
			lTextureGroup.referenceCount--;

		}

		if (lTextureGroup.referenceCount <= 0) {
			// Unload textures for this entityGroupID
			unloadEntityGroup(pEntityGroupID);

			mTextureGroupMap.remove(pEntityGroupID);
			lTextureGroup = null;

			return 0;

		}

		return lTextureGroup.referenceCount;

	}

	public Texture loadTexture(String pName, String pTextureLocation, int pEntityGroupID) {
		return loadTexture(pName, pTextureLocation, GL11.GL_NEAREST, pEntityGroupID);
	}

	public Texture loadTexture(String pName, String pTextureLocation, int pFilter, int pEntityGroupID) {
		return loadTexture(pName, pTextureLocation, pFilter, false, pEntityGroupID);

	}

	public Texture loadTexture(String pName, String pTextureLocation, int pFilter, boolean pReload, int pEntityGroupID) {
		return loadTexture(pName, pTextureLocation, pFilter, GL12.GL_REPEAT, GL12.GL_REPEAT, pReload, pEntityGroupID);
	}

	public Texture loadTexture(String pName, String pTextureLocation, int pFilter, int pWrapModeS, int pWrapModeT, int pEntityGroupID) {
		return loadTexture(pName, pTextureLocation, pFilter, pWrapModeS, pWrapModeT, false, pEntityGroupID);
	}

	public Texture loadTexture(String pName, String pTextureLocation, int pFilter, int pWrapModeS, int pWrapModeT, boolean pReload, int pEntityGroupID) {
		if (pTextureLocation == null || pTextureLocation.length() == 0) {
			return null;

		}

		final var lTextureGroup = getTextureGroup(pEntityGroupID);

		Texture lTexture = null;
		if (lTextureGroup.mTextureMap.containsKey(pName)) {
			lTexture = lTextureGroup.mTextureMap.get(pName);

			if (!pReload)
				return lTexture;

			Debug.debugManager().logger().v(getClass().getSimpleName(), "Unloading " + pName + ", so it can be reloaded");
			unloadTexture(lTexture, pEntityGroupID);

		}

		// Create new texture
		if (pTextureLocation.charAt(0) == '/') {
			lTexture = Texture.loadTextureFromResource(pName, pTextureLocation, pFilter);

		} else {
			lTexture = Texture.loadTextureFromFile(pName, pTextureLocation, pFilter);

		}

		if (lTexture != null) {
			lTextureGroup.mTextureMap.put(pName, lTexture);

		}

		if (lTexture == null) {
			return mTextureNotFound;

		}

		Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TEXTURES, 1);

		return lTexture;
	}

	private TextureGroup getTextureGroup(int pEntityGroupID) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);
		if (lTextureGroup == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("EntityGroupID does not exist! Creating a new one", pEntityGroupID));
			lTextureGroup = new TextureGroup(pEntityGroupID);
			mTextureGroupMap.put(pEntityGroupID, lTextureGroup);

		}
		return lTextureGroup;
	}

	public Texture loadTexture(String pName, int[] pColorData, int pWidth, int pHeight, int pEntityGroupID) {
		return loadTexture(pName, pColorData, pWidth, pHeight, GL11.GL_NEAREST, GL12.GL_REPEAT, GL12.GL_REPEAT, pEntityGroupID);
	}

	public Texture loadTexture(String pName, int[] pColorData, int pWidth, int pHeight, int pFilter, int pWrapSMode, int pWrapTMode, int pEntityGroupID) {
		Texture lResult = null;
		TextureGroup lTextureGroup = getTextureGroup(pEntityGroupID);

		if (lTextureGroup.mTextureMap.containsKey(pName)) {
			lResult = lTextureGroup.mTextureMap.get(pName);
			unloadTexture(lResult, pEntityGroupID);

		}

		if (lResult != null) {
			lResult.updateGLTextureData(pColorData, pWidth, pHeight);

			return lResult;
		} else {
			Texture lTex = Texture.createTexture(pName, pName, pColorData, pWidth, pHeight, pFilter, pWrapSMode, pWrapTMode);
			if (lTex != null) {
				// Can't reload rgb data on-the-fly
				lTex.reloadable(false);
				lTextureGroup.mTextureMap.put(pName, lTex);

			}

			return lTex;

		}

	}

	public boolean saveTextureToFile(int pWidth, int pHeight, int[] pData, String pFileLocation) {
		BufferedImage lImage = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_ARGB);

		// Convert our ARGB to output ABGR
		int[] lTextureData = new int[pWidth * pHeight];
		for (int i = 0; i < pWidth * pHeight; i++) {
			int a = (pData[i] & 0xff000000) >> 24;
			int r = (pData[i] & 0xff0000) >> 16;
			int g = (pData[i] & 0xff00) >> 8;
			int b = (pData[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r;
		}

		lImage.setRGB(0, 0, pWidth, pHeight, lTextureData, 0, pWidth);

		File outputfile = new File(pFileLocation);
		try {
			ImageIO.write(lImage, "png", outputfile);
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}

		return true;

	}

	public Texture createFontTexture(String pName, BufferedImage pImage, int pEntityGroupID) {
		return createFontTexture(pName, pImage, GL11.GL_NEAREST, pEntityGroupID);
	}

	public Texture createFontTexture(String pName, BufferedImage pImage, int pFilter, int pEntityGroupID) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);
		if (lTextureGroup == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Cannot load texture %s for EntityGroupID %d: EntityGroupID does not exist!", (pName + " (Font)"), pEntityGroupID));
			return null;

		} else if (lTextureGroup.mTextureMap.containsKey(pName)) {
			// This texture group already contains a texture with the same name, so return it
			return lTextureGroup.mTextureMap.get(pName);

		}

		Texture lNewTexture = Texture.createTexture(pName, pName, pImage, pFilter);
		lNewTexture.reloadable(false); // no need to reload font textures (on-the-fly)

		lTextureGroup.mTextureMap.put(pName, lNewTexture);

		return lNewTexture;
	}

	public void reloadTextures() {
		Debug.debugManager().logger().v(getClass().getSimpleName(), "Reloading all modified files");

		for (TextureGroup lTextureGroup : mTextureGroupMap.values()) {
			for (Texture lTexture : lTextureGroup.mTextureMap.values()) {
				if (lTexture != null) {
					lTexture.reload();
				}

			}

		}

	}

	/** Unloads the specified texture in the texture group, if applicable. */
	public void unloadTexture(Texture pTexture, int pEntityGroupID) {
		if (pTexture == null)
			return; // already lost reference

		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);
		if (lTextureGroup == null) {
			return;

		} else if (lTextureGroup.mTextureMap.containsValue(pTexture)) {
			String lTextureName = pTexture.name();

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("unloading texture: %s from texture group %d", lTextureName, pEntityGroupID));

			Texture.unloadTexture(pTexture);

			lTextureGroup.mTextureMap.remove(lTextureName);
			pTexture = null;

		}

		return;

	}

	public void unloadEntityGroup(int pEntityGroupID) {
		TextureGroup lTextureGroup = mTextureGroupMap.get(pEntityGroupID);

		if (lTextureGroup == null)
			return;

		final int lTextureCount = lTextureGroup.mTextureMap.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Unloading TextureGroup %d (freeing total %d textures)", pEntityGroupID, lTextureCount));

		if (lTextureGroup != null) {
			// Iterate over all the textures in the group and unload them
			Iterator<Entry<String, Texture>> it = lTextureGroup.mTextureMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Texture> lNextTexture = it.next();
				Texture.unloadTexture(lNextTexture.getValue());

				it.remove();

			}

		}

	}

	/** Batch load textures */
	public void loadTexturesFromMetafile(String pMetaFileLocation, int pEntityGroupID) {

		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading textures from meta-file %s", pMetaFileLocation));

		final var lGson = new GsonBuilder().create();

		String lMetaFileContentsString = null;
		TextureMetaData lTextureMetaData = null;

		lMetaFileContentsString = FileUtils.loadString(pMetaFileLocation);

		try {
			lTextureMetaData = lGson.fromJson(lMetaFileContentsString, TextureMetaData.class);

			if (lTextureMetaData == null || lTextureMetaData.textureDefinitions == null || lTextureMetaData.textureDefinitions.length == 0) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the PObject meta file");
				return;

			}

			final var lTextureGroup = mTextureGroupMap.get(pEntityGroupID);
			if (lTextureGroup == null) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Cannot load texture %s for EntityGroupID %d: EntityGroupID does not exist!", (pMetaFileLocation + " (META)"), pEntityGroupID));
				return;

			}

			final int lNumberOfTextureDefinitions = lTextureMetaData.textureDefinitions.length;
			for (int i = 0; i < lNumberOfTextureDefinitions; i++) {
				final var lTextureDataDefinition = lTextureMetaData.textureDefinitions[i];

				final var lTextureName = lTextureDataDefinition.textureName;
				final var lFilepath = lTextureDataDefinition.filepath;

				int lGlFilterMode = mapTextureFilterMode(lTextureDataDefinition.filterIndex);
				int lGlWrapSFilter = mapWrapMode(lTextureDataDefinition.filterIndex);
				int lGlWrapTFilter = mapWrapMode(lTextureDataDefinition.filterIndex);

				final var lNewTexture = loadTexture(lTextureName, lFilepath, lGlFilterMode, lGlWrapSFilter, lGlWrapTFilter, true, pEntityGroupID);

				if (lNewTexture != null) {
					// All textures that we manualy load can be reloaded be re-calling this method
					lNewTexture.reloadable(true);

					Debug.debugManager().logger().i(getClass().getSimpleName(), "Loaded texture from Meta '" + lTextureName + "' into EntityGroupID: " + pEntityGroupID);

					lTextureGroup.mTextureMap.put(lTextureName, lNewTexture);

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
	private int mapTextureFilterMode(int pIndex) {
		switch (pIndex) {
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
	private int mapWrapMode(int pIndex) {
		switch (pIndex) {
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

		Map<Integer, TextureGroup> map = mTextureGroupMap;
		for (Map.Entry<Integer, TextureGroup> entry : map.entrySet()) {

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("TextureGroup %s (%d)..", entry.getValue().name, entry.getValue().entityGroupID));

			TextureGroup lTextureGroup = entry.getValue();

			Map<String, Texture> lGroupMap = lTextureGroup.textureMap();
			for (Map.Entry<String, Texture> lTexture : lGroupMap.entrySet()) {

				Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("  Texture %s (%d)", lTexture.getValue().name(), lTexture.getValue().getTextureID()));

			}

		}

	}

}

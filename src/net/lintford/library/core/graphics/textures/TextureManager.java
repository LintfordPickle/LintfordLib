package net.lintford.library.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.graphics.textures.xml.TextureMetaLoader;

public class TextureManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/** When enabled, missing textures will be filled with a magenta color. */
	public static final boolean USE_DEBUG_MISSING_TEXTURES = true;

	public static final String TEXTURE_WHITE_NAME = "WHITE_W";
	public static final String TEXTURE_BLACK_NAME = "BLACK_T";
	public static final String TEXTURE_NOT_FOUND_NAME = "NOT_FOUND";

	public static final String TEXTURE_CHECKER_BOARD_NAME = "CHECKER";
	public static final String TEXTURE_CHECKER_BOARD_INDEXED_NAME = "CHECKERI";
	public static final String TEXTURE_CORE_UI_NAME = "CORE_UI";
	public static final String TEXTURE_SYSTEM_UI_NAME = "SYS_UI";

	public static final Texture TEXTURE_NOT_FOUND = TextureManager.textureManager().loadTexture(TEXTURE_NOT_FOUND_NAME, new int[] { 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF, 0xFFFF00FF }, 2, 2);
	public static final Texture TEXTURE_WHITE = TextureManager.textureManager().loadTexture(TEXTURE_WHITE_NAME, new int[] { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF }, 2, 2);
	public static final Texture TEXTURE_BLACK = TextureManager.textureManager().loadTexture(TEXTURE_BLACK_NAME, new int[] { 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000 }, 2, 2);

	public static final Texture TEXTURE_CHECKER = TextureManager.textureManager().loadTexture(TEXTURE_CHECKER_BOARD_NAME, "/res/textures/CheckerBoard.png", GL11.GL_NEAREST);
	public static final Texture TEXTURE_CHECKER_I = TextureManager.textureManager().loadTexture(TEXTURE_CHECKER_BOARD_INDEXED_NAME, "/res/textures/CheckerBoardIndexed.png", GL11.GL_NEAREST);

	/** A static texture which contains 'generic' icons which can be used for core components and debugging. */
	public static final Texture TEXTURE_CORE_UI = TextureManager.textureManager().loadTexture(TEXTURE_CORE_UI_NAME, "/res/textures/core/system.png", GL11.GL_NEAREST);
	
	/** The System texture never changes. It just makes sure LintfordCore always has a basic set of geometry to use for rendering. */
	public static final Texture TEXTURE_SYS_UI = TextureManager.textureManager().loadTexture(TEXTURE_SYSTEM_UI_NAME, "/res/textures/core/system.png", GL11.GL_NEAREST);

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static TextureManager mTextureManager;
	private Map<String, Texture> mTextures;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns an instance of the {@link TextureManager}. */
	public static TextureManager textureManager() {
		if (mTextureManager == null) {
			mTextureManager = new TextureManager();

		}

		return mTextureManager;
	}

	/** Returns the {@link Texture} with the given name. If no {@link Texture} by the given name is found, a default MAGENTA texture will be returned. */
	public Texture getTexture(String pName) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		// In case the requested texture is not found, then return a default MAGENTA texture.
		return TextureManager.TEXTURE_NOT_FOUND;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private TextureManager() {
		mTextures = new HashMap<String, Texture>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void unloadGLContent() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public Texture loadTexture(String pName, String pTextureLocation) {
		return loadTexture(pName, pTextureLocation, GL11.GL_NEAREST);
	}

	public Texture loadTexture(String pName, String pTextureLocation, int pFilter) {
		return loadTexture(pName, pTextureLocation, pFilter, false);

	}

	public Texture loadTexture(String pName, String pTextureLocation, int pFilter, boolean pReload) {
		if (pTextureLocation == null || pTextureLocation.length() == 0) {
			return null;

		}

		Texture lTex = null;

		if (mTextures.containsKey(pName)) {
			lTex = mTextures.get(pName);

			if (!pReload)
				return lTex;

			unloadTexture(lTex);

		}

		// create new texture
		if (pTextureLocation.charAt(0) == '/') {
			lTex = Texture.loadTextureFromResource(pName, pTextureLocation, pFilter);

		} else {
			lTex = Texture.loadTextureFromFile(pName, pTextureLocation, pFilter);

		}

		if (lTex != null) {
			mTextures.put(pName, lTex); // cache

		}

		if (lTex == null) {
			return TextureManager.TEXTURE_NOT_FOUND;

		}

		return lTex;
	}

	public Texture loadTexture(String pName, int[] pColorData, int pWidth, int pHeight) {
		return loadTexture(pName, pColorData, pWidth, pHeight, GL11.GL_NEAREST);
	}

	public Texture loadTexture(String pName, int[] pColorData, int pWidth, int pHeight, int pFilter) {
		Texture lResult = null;
		if (mTextures.containsKey(pName)) {
			lResult = mTextures.get(pName);
		}

		if (lResult != null) {
			lResult.updateGLTextureData(pColorData, pWidth, pHeight);

			return lResult;
		} else {
			Texture lTex = Texture.createTexture(pName, pName, pColorData, pWidth, pHeight, pFilter);
			if (lTex != null) {
				// Can't reload from rgb data
				lTex.reloadable(false);
				mTextures.put(pName, lTex); // cache

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

	public Texture createFontTexture(String pName, BufferedImage pImage) {
		return createFontTexture(pName, pImage, GL11.GL_NEAREST);
	}

	public Texture createFontTexture(String pName, BufferedImage pImage, int pFilter) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		Texture lTex = Texture.createTexture(pName, pName, pImage, pFilter);
		lTex.reloadable(false);
		mTextures.put(pName, lTex);

		return lTex;
	}

	public void reloadTextures() {
		if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
			DebugManager.DEBUG_MANAGER.logger().v(getClass().getSimpleName(), "Reloading all modified files");

		}

		for (Texture lTexture : mTextures.values()) {
			if (lTexture != null) {
				lTexture.reload();
			}

		}

	}

	/** Unloads the speicifed texture, if applicable. */
	public void unloadTexture(Texture pTexture) {
		if (pTexture == null)
			return; // already lost reference

		if (mTextures.containsValue(pTexture)) {

			String lTextureName = pTexture.name();
			Texture.unloadTexture(pTexture);

			mTextures.remove(lTextureName);

		}

		Texture.unloadTexture(pTexture);
		pTexture = null;

		return;

	}

	/** Batch load textures */
	public void loadTexturesFromMetafile(String pMetaFileLoation) {
		final TextureMetaLoader lLoader = new TextureMetaLoader();
		final ArrayList<TextureMetaItem> lItems = lLoader.loadTextureMetaFile(pMetaFileLoation);

		final int lNumTextures = lItems.size();
		for (int i = 0; i < lNumTextures; i++) {
			int GL_FILTER_MODE = GL11.GL_NEAREST;
			switch (lItems.get(i).filterType) {
			case 1:
				GL_FILTER_MODE = GL11.GL_NEAREST;
				break;
			default:
				GL_FILTER_MODE = GL11.GL_LINEAR;
				break;
			}

			Texture lTex = loadTexture(lItems.get(i).textureName, lItems.get(i).textureLocation, GL_FILTER_MODE);

			if (lTex != null) {
				lTex.reloadable(true);
				mTextures.put(lItems.get(i).textureName, lTex);

			}
		}
	}

}

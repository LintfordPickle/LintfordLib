package net.ld.library.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.graphics.textures.xml.TextureMetaLoader;

public class TextureManager {

	// =============================================
	// Variables
	// =============================================

	private static TextureManager mTextureManager;
	private Map<String, Texture> mTextures;

	// =============================================
	// Properties
	// =============================================

	/**
	 * Returns an instance of {@link TextureManager}, which can be used to load
	 * texture images.
	 */
	public static TextureManager textureManager() {
		if (mTextureManager == null) {
			mTextureManager = new TextureManager();
		}

		return mTextureManager;
	}

	/**
	 * Returns the texture with the given name, if it exists. null is returned
	 * otherwise.
	 */
	public Texture getTexture(String pName) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		return null;
	}

	// =============================================
	// Constructor
	// =============================================

	/**
	 * Creates a new instance of {@link TextureManager}. The TextureManager uses
	 * the singleton pattern, and only one instance exists at a time. Use
	 * textureManager() to retrieve an instance.
	 */
	private TextureManager() {
		mTextures = new HashMap<String, Texture>();
	}

	// =============================================
	// Core-Methods
	// =============================================

	/** Loads a list of textures from the given meta data file. */
	public void loadTexturesFromMetafile(String pMetaFileLoation) {
		final TextureMetaLoader lLoader = new TextureMetaLoader();
		final ArrayList<TextureMetaItem> lItems = lLoader.loadTextureMetaFile(pMetaFileLoation);

		final int lNumTextures = lItems.size();
		for (int i = 0; i < lNumTextures; i++) {
			Texture lTex = null;
			switch (lItems.get(i).filterType) {
			case 1:
				lTex = Texture.loadTextureFromFile(lItems.get(i).textureLocation, GL11.GL_NEAREST);
				break;
			default:
				lTex = Texture.loadTextureFromFile(lItems.get(i).textureLocation, GL11.GL_LINEAR);
				break;
			}

			mTextures.put(lItems.get(i).textureName, lTex);
		}
	}

	// =============================================
	// Methods
	// =============================================

	/**
	 * loads a texture from the given filename and assigns it the given name.
	 * The {@link Texture} instance loaded is returned (can be null).
	 */
	public Texture loadTextureFromFile(String pName, String pTextureLocation) {
		return loadTextureFromFile(pName, pTextureLocation, GL11.GL_LINEAR);
	}

	/**
	 * loads a texture from the given filename and assigns it the given name.
	 * The {@link Texture} instance loaded is returned (can be null). pFilter
	 * applies the GL11 texture filter.
	 */
	public Texture loadTextureFromFile(String pName, String pTextureLocation, int pFilter) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		Texture lTex = Texture.loadTextureFromFile(pTextureLocation, pFilter);
		mTextures.put(pName, lTex); // cache

		return lTex;
	}

	/**
	 * loads a texture from the given resource name embedded in the jar and
	 * assigns it the given name. The {@link Texture} instance loaded is
	 * returned (can be null).
	 */
	public Texture loadTextureFromResource(String pName, String pTextureLocation) {
		return loadTextureFromResource(pName, pTextureLocation, GL11.GL_LINEAR);
	}

	/**
	 * loads a texture from the given resource name embedded in the jar and
	 * assigns it the given name. The {@link Texture} instance loaded is
	 * returned (can be null). pFilter applies the GL11 texture filter.
	 */
	public Texture loadTextureFromResource(String pName, String pTextureLocation, int pFilter) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		Texture lTex = Texture.loadTextureFromResource(pTextureLocation, pFilter);
		mTextures.put(pName, lTex); // cache

		return lTex;
	}

	public Texture createFontTexture(String pName, BufferedImage pImage) {
		return createFontTexture(pName, pImage, GL11.GL_NEAREST);
	}

	public Texture createFontTexture(String pName, BufferedImage pImage, int pFilter) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		Texture lTex = Texture.createTexture(pImage, pName, pFilter);
		lTex.reloadable(false);
		mTextures.put(pName, lTex);

		return lTex;
	}

	/** Forces reload of all previously loaded textures. */
	public void reloadTextures() {
		System.out.println("Reloading all textures ..");

		for (Texture lTexture : mTextures.values()) {
			if (lTexture != null) {
				if (!lTexture.reloadable())
					continue;
				lTexture.reload();
			}
		}
	}
}

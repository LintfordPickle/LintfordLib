package net.ld.library.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class TextureManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static TextureManager mTextureManager;
	private Map<String, Texture> mTextures;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/**
	 * Creates a new instance of {@link TextureManager}. The TextureManager uses
	 * the singleton pattern, and only one instance exists at a time. Use
	 * textureManager() to retrieve an instance.
	 */
	private TextureManager() {
		mTextures = new HashMap<String, Texture>();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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

	/** Unloads the specified texture, if applicable. */
	public boolean unloadTexture(Texture pTexture) {
		if (pTexture == null)
			return false; // already lost reference

		if (mTextures.containsValue(pTexture)) {

			Texture.unloadTexture(pTexture);

			mTextures.remove(pTexture);

			return true;
		}

		return false;

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
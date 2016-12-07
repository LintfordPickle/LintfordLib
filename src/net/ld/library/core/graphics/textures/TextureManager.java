package net.ld.library.core.graphics.textures;

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

	public static TextureManager textureManager() {
		if (mTextureManager == null) {
			mTextureManager = new TextureManager();
		}

		return mTextureManager;
	}

	public Texture getTexture(String pName) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		return null;
	}

	// =============================================
	// Constructor
	// =============================================

	private TextureManager() {
		mTextures = new HashMap<String, Texture>();
	}

	// =============================================
	// Core-Methods
	// =============================================

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

	public Texture loadTextureFromFile(String pName, String pTextureLocation) {
		return loadTextureFromFile(pName, pTextureLocation, GL11.GL_LINEAR);
	}

	public Texture loadTextureFromFile(String pName, String pTextureLocation, int pFilter) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		Texture lTex = Texture.loadTextureFromFile(pTextureLocation, pFilter);
		mTextures.put(pName, lTex); // cache

		return lTex;
	}

	public Texture loadTextureFromResource(String pName, String pTextureLocation) {
		return loadTextureFromResource(pName, pTextureLocation, GL11.GL_LINEAR);
	}

	public Texture loadTextureFromResource(String pName, String pTextureLocation, int pFilter) {
		if (mTextures.containsKey(pName)) {
			return mTextures.get(pName);
		}

		Texture lTex = Texture.loadTextureFromResource(pTextureLocation, pFilter);
		mTextures.put(pName, lTex); // cache

		return lTex;
	}

	public void reloadTextures() {
		System.out.println("Reloading all textures ..");

		for (Texture lTexture : mTextures.values()) {
			lTexture.reload();
		}
	}
}

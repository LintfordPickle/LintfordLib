package net.ld.library.core.graphics.sprites;

import java.util.HashMap;
import java.util.Map;

import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;

public class SpriteSheet {

	// =============================================
	// Variables
	// =============================================

	private Texture mTexture;
	private String mFilename;

	public final String spriteSheetName;

	private Map<String, Sprite> mSpriteMap;
	public float w;
	public float h;

	// =============================================
	// Properties
	// =============================================

	public boolean isLoaded() {
		return mTexture != null;
	}

	public Texture texture() {
		return mTexture;
	}

	// =============================================
	// Constructor
	// =============================================

	public SpriteSheet(String pSpriteSheetName) {
		spriteSheetName = pSpriteSheetName;
		mSpriteMap = new HashMap<String, Sprite>();
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void loadContent() {
		if (mFilename == null || mFilename.length() == 0) {
			throw new RuntimeException("SpriteSheet filename cannot be null!"); // unchecked
		}

		mTexture = TextureManager.textureManager().loadTexture(mFilename, mFilename);

		w = mTexture.getTextureWidth();
		h = mTexture.getTextureHeight();

	}

	public void draw() {

	}

	// =============================================
	// Methods
	// =============================================

	public void setTextureFilename(String pNewFilename) {
		if (pNewFilename == null || pNewFilename.length() == 0) {
			throw new RuntimeException("SpriteSheet filename cannot be null!"); // unchecked
		}

		mFilename = pNewFilename;
	}

	public void addSpriteDefinition(String pNewName, Sprite pNewSprite) {
		if (mSpriteMap == null)
			mSpriteMap = new HashMap<String, Sprite>();

		mSpriteMap.put(pNewName, pNewSprite);
	}

	public Sprite getSprite(String pSpriteName) {

		if (mSpriteMap.containsKey(pSpriteName)) {
			return mSpriteMap.get(pSpriteName);
		}

		return null;
	}
}

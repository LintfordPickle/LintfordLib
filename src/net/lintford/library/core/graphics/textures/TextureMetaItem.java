package net.lintford.library.core.graphics.textures;

import com.google.gson.annotations.SerializedName;

public class TextureMetaItem {

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "textureName")
	private final String mTextureName;

	@SerializedName(value = "textureLocation")
	private final String mTextureLocation;

	@SerializedName(value = "filterType")
	private final int mFilterType;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String textureName() {
		return mTextureName;
	}

	public String textureLocation() {
		return mTextureLocation;
	}

	public int filterType() {
		return mFilterType;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public TextureMetaItem(final String pTextureName, final String pTextureLocation, int pFilterType) {
		mTextureName = pTextureName;
		mTextureLocation = pTextureLocation;
		mFilterType = pFilterType;
	}
}

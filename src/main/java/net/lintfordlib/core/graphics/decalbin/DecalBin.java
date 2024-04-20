package net.lintfordlib.core.graphics.decalbin;

import net.lintfordlib.core.binpacking.BinPacker;

public class DecalBin {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BinPacker _BinPacker;

	private String mBinName;
	private String mDecalAtlasTextureName;
	private int mDecalAtlasWidth;
	private int mDecalAtlasHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public BinPacker binPacker() {
		if (_BinPacker == null)
			_BinPacker = new BinPacker(mBinName, mDecalAtlasWidth, mDecalAtlasHeight);

		return _BinPacker;
	}

	public String decalAtlasName() {
		return mBinName;
	}

	public String decalAtlasFilepath() {
		return mDecalAtlasTextureName;
	}

	public int decalAtlasWidth() {
		return mDecalAtlasWidth;
	}

	public int decalAtlasHeight() {
		return mDecalAtlasHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DecalBin(String name, String textureFilename, int width, int height) {
		mBinName = name;
		mDecalAtlasTextureName = textureFilename;
		mDecalAtlasWidth = width;
		mDecalAtlasHeight = height;
	}
}

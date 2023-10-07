package net.lintfordlib.core.graphics.textures;

public class RGBTexture {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int[] mColorData;
	private boolean mIsLoaded;
	private int mWidth;
	private int mHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	public int[] colorData() {
		return mColorData;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setData(int width, int height, int[] colorData) {
		mWidth = width;
		mHeight = height;
		mColorData = colorData;
	}

	public void reset() {
		mWidth = 0;
		mHeight = 0;
		mColorData = null;
		mIsLoaded = false;
	}

	/** pSaveLocation is the path NOT including the filename */
	public void saveToDisk(String saveLocation, String filename) {
		Texture.saveTextureToFile(mWidth, mHeight, mColorData, saveLocation + System.getProperty("file.separator") + filename);
	}
}

package net.ld.library.core.graphics.fonts;

import net.ld.library.core.camera.ICamera;
import net.ld.library.core.graphics.ResourceManager;

public class FontUnit {

	private static final int MIN_POINTSIZE = 6;
	private static final int MAX_POINTSIZE = 100;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mFontName;
	private String mFontPath;
	private int mFontPointSize;
	private BitmapFont mBitmapFont;
	private AWTBitmapFontSpriteBatch mFontSpriteBatch;
	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public AWTBitmapFontSpriteBatch spriteBatch() {
		return mFontSpriteBatch;
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public String fontName() {
		return mFontName;
	}

	public String fontPath() {
		return mFontPath;
	}

	public int fontPointSize() {
		return mFontPointSize;
	}

	public BitmapFont bitmap() {
		return mBitmapFont;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontUnit(String pFontName, int pPointSize, BitmapFont pBitmapFont) {
		mFontName = pFontName;
		mBitmapFont = pBitmapFont;

		mFontPointSize = pPointSize;
		if (mFontPointSize < MIN_POINTSIZE)
			mFontPointSize = MIN_POINTSIZE;

		if (mFontPointSize > MAX_POINTSIZE)
			mFontPointSize = MAX_POINTSIZE;

		mFontSpriteBatch = new AWTBitmapFontSpriteBatch(mBitmapFont);
		
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mBitmapFont.loadGLContent(pResourceManager);
		mFontSpriteBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		mIsLoaded = false;

	}

	public void begin(ICamera pCamera) {
		mFontSpriteBatch.begin(pCamera);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pScale) {
		mFontSpriteBatch.draw(pText, pX, pY, pZ, pScale, AWTBitmapFontSpriteBatch.NO_WORD_WRAP);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pScale, float pWordWrapWidth) {
		mFontSpriteBatch.draw(pText, pX, pY, pZ, pScale, pWordWrapWidth);
	}
	
	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale) {
		mFontSpriteBatch.draw(pText, pX, pY, pZ, 1f, 1f, 1f, pA, pScale, -1);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth) {
		mFontSpriteBatch.draw(pText, pX, pY, pZ, pR, pG, pB, pA, pScale, pWordWrapWidth);
	}

	public void end() {
		mFontSpriteBatch.end();
	}

	
}

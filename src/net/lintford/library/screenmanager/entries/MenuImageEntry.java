package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.graphics.fonts.BitmapFont;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuImageEntry extends MenuEntry {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ALIGNMENT mAlignment = ALIGNMENT.center;
	private boolean mShow;
	private float mR, mG, mB;

	private boolean mImageLoaded;
	private Texture mTexture;
	private boolean mShowLabel;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean enableLabel() {
		return mShowLabel;
	}

	public void enableLabel(boolean pEnableLabel) {
		mShowLabel = pEnableLabel;
	}

	@Override
	public float getHeight() {
		if (mImageLoaded && mTexture != null) {
			return mTexture.getTextureHeight();
		} else {
			// Height of image + title + padding between
			if (!mParentScreen.isLoaded())
				return 30f;
			return height + mParentScreen.fontHeader().bitmap().fontHeight() + 10;

		}

	}

	@Override
	public float getWidth() {
		if (mImageLoaded && mTexture != null) {
			return mTexture.getTextureWidth();
		}

		return 800;

	}

	public void alignment(ALIGNMENT left) {
		mAlignment = left;
	}

	public ALIGNMENT alignment() {
		return mAlignment;
	}

	@Override
	public boolean hasFocus() {
		return super.hasFocus();
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		if (pNewValue) {

		}

		super.hasFocus(pNewValue);
	}

	public boolean show() {
		return mShow;
	}

	public void show(boolean pNewValue) {
		mShow = pNewValue;
	}

	public void label(String pNewLabel) {
		mText = pNewLabel;
	}

	public String label() {
		return mText;
	}

	public void labelColor(float pR, float pG, float pB) {
		mR = pR;
		mG = pG;
		mB = pB;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuImageEntry(ScreenManager pScreenManager, MenuScreen pParentScreen) {
		super(pScreenManager, pParentScreen, "");

		mText = "Add your message";
		mShow = true;
		mR = 1.0f;
		mG = mB = 0.1f;

		mCanHaveFocus = false;
		mCanHoverOver = false;

		mShowLabel = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		width = getWidth();
		height = MENUENTRY_HEIGHT;

	}

	@Override
	public void draw(Screen pScreen, RenderState pRenderState, boolean pIsSelected, float pParentZDepth) {

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		final float lLabelWidth = lFontBitmap.getStringWidth(mText);
		mAlignment = ALIGNMENT.center;
		float lX = x + width / 2 - lLabelWidth / 2;
		switch (mAlignment) {
		case left:
			lX = x + 5;
			break;
		case right:
			lX = x - 5 - lLabelWidth;
			break;
		default:
			lX = x + width / 2 - lLabelWidth / 2;
			break;
		}

		float lLabelHeightOffset = 0;
		if (mShowLabel) {
			final float FONT_SCALE = 1f;
			mParentScreen.font().begin(mScreenManager.HUD());
			mParentScreen.font().draw(mText, lX, y, pParentZDepth + .1f, mR * mParentScreen.r(), mG * mParentScreen.g(), mB * mParentScreen.b(), mParentScreen.a(), FONT_SCALE);
			mParentScreen.font().end();

			lLabelHeightOffset += lFontBitmap.fontHeight() + 10;

		}

		mSpriteBatch.begin(pRenderState.HUDCamera());

		height = 240;

		switch (mAlignment) {
		case left:
			lX = x + 5;
			break;
		case right:
			lX = x - 5 - width;
			break;
		default:
			lX = x + width / 2 - 320 / 2;
			break;
		}

		if (mTexture != null) {
			final int width = mTexture.getTextureWidth();
			final int height = mTexture.getTextureHeight();

			lX = -width / 2;
			mSpriteBatch.draw(0, 0, width, height, lX + 5, y + lLabelHeightOffset, pParentZDepth + .1f, width, height, 1f, mTexture);

		}

		mSpriteBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTexture(Texture pTexture) {
		mTexture = pTexture;

		mImageLoaded = mTexture != null;

	}

}

package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuImageEntry extends MenuEntry {

	private static final long serialVersionUID = 4053035578493366108L;

	private static final float DEFAULT_ASPECT_RATIO = 480f / 640f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LAYOUT_ALIGNMENT mAlignment = LAYOUT_ALIGNMENT.CENTER;

	private float mForceHeight;
	private float mFittedWidth;
	private float mFittedHeight;

	private float mDefaultWidth;
	private boolean mScaleToFitParent;

	private int mMaximumWidth = 640;

	private Texture mMissingTexture;
	private float srcX, srcY, srcWidth, srcHeight;
	private Texture mMainTexture;

	private boolean mShowMissingTextureText;
	private String mMissingTextureText;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setMissingTexturetext(String pText) {
		if (pText == null || pText.length() == 0) {
			mMissingTextureText = "";
			mShowMissingTextureText = false;

			return;

		}

		mMissingTextureText = pText;
		mShowMissingTextureText = true;

	}

	public void setMaximumImageWidth(int pWidthLimit) {
		mMaximumWidth = pWidthLimit;

	}

	@Override
	public float height() {
		if (forceHeight() < 0)
			return mFittedHeight;

		return mFittedHeight;
	}

	public void forceHeight(float pNewValue) {
		mForceHeight = pNewValue;
	}

	public float forceHeight() {
		return mForceHeight;
	}

	public void alignment(LAYOUT_ALIGNMENT left) {
		mAlignment = left;
	}

	public LAYOUT_ALIGNMENT alignment() {
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuImageEntry(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout, "");

		mText = "Add your message";

		mCanHaveFocus = false;
		mCanHoverOver = false;

		mScaleToFitParent = true;

		mLeftMargin = 20;
		mRightMargin = 20;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUITexture = pResourceManager.textureManager().textureCore();

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mUITexture = null;

	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		h = mMainTexture != null ? mMainTexture.getTextureHeight() + 20f : h;

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);
		if (mMainTexture != null) {
			float lAR = (float) mMainTexture.getTextureHeight() / (float) mMainTexture.getTextureWidth();

			float thMaxWidth = mMaxWidth;
			if (mScaleToFitParent && lAR != 0) {
				float thMaxHeight = mParentLayout.h() - mParentLayout.marginBottom() - mParentLayout.marginTop();
				thMaxWidth = thMaxHeight / lAR;

			}

			// limited by width
			mFittedWidth = mMainTexture.getTextureWidth();

			if (mFittedWidth > thMaxWidth)
				mFittedWidth = thMaxWidth;

			if (mFittedWidth > mMaximumWidth)
				mFittedWidth = mMaximumWidth;

			mFittedHeight = mFittedWidth * lAR;

		} else {
			mDefaultWidth = mParentLayout.w() - marginLeft() - marginRight();

			float thMaxWidth = mMaxWidth;
			if (mScaleToFitParent && DEFAULT_ASPECT_RATIO != 0) {
				float thMaxHeight = mParentLayout.h() - mParentLayout.marginBottom() - mParentLayout.marginTop();
				thMaxWidth = thMaxHeight / DEFAULT_ASPECT_RATIO;

			}

			mFittedWidth = mDefaultWidth;

			if (mFittedWidth > thMaxWidth)
				mFittedWidth = thMaxWidth;

			if (mFittedWidth > mMaximumWidth)
				mFittedWidth = mMaximumWidth;

			mFittedHeight = mFittedWidth * DEFAULT_ASPECT_RATIO;

		}

		x = mParentLayout.x() + mParentLayout.width() / 2f - mFittedWidth / 2;
		h = mFittedHeight;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {

		final var lParentScreen = mParentLayout.parentScreen();
		final var lTextureBatch = lParentScreen.rendererManager().uiTextureBatch();

		lTextureBatch.begin(pCore.HUD());

		if (mMainTexture != null) {
			final int lTextureWidth = mMainTexture.getTextureWidth();
			final int lTextureHeight = mMainTexture.getTextureHeight();

			lTextureBatch.draw(mMainTexture, 0, 0, lTextureWidth, lTextureHeight, x, y, mFittedWidth, mFittedHeight, pParentZDepth + .1f, 1f, 1f, 1f, 1f);

		} else if (mShowMissingTextureText) {
			final FontUnit lFontUnit = lParentScreen.rendererManager().textFont();

			final float lTextWidth = lFontUnit.bitmap().getStringWidth(mMissingTextureText);

			lFontUnit.begin(pCore.HUD());
			lFontUnit.draw(mMissingTextureText, x + mFittedWidth / 2f - lTextWidth / 2f, y + mFittedHeight / 2, 1f);
			lFontUnit.end();

		} else if (mMissingTexture != null) {
			lTextureBatch.draw(mMissingTexture, srcX, srcY, srcWidth, srcHeight, x, y, mFittedWidth, mFittedHeight, pParentZDepth + .1f, 1f, 1f, 1f, 1f);

		} else if (mUITexture != null) {
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, mFittedWidth, mFittedHeight, pParentZDepth + .1f, 1f, 1f, 1f, 1f);

		}

		lTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTexture(Texture pTexture) {
		mMainTexture = pTexture;

	}

	public void setDefaultImage(Texture pBackgroundTexture, int pSrcX, int pSrcY, int pSrcWidth, int pSrcHeight) {
		mMissingTexture = pBackgroundTexture;
		srcX = pSrcX;
		srcY = pSrcY;
		srcWidth = pSrcWidth;
		srcHeight = pSrcHeight;

	}

}

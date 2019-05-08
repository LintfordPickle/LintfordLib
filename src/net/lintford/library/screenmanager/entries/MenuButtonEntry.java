package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuButtonEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2194989174357016245L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private boolean mIsChecked;
	private final String mSeparator = " : ";
	private String mButtonLabel;
	private boolean mButtonEnabled;
	private boolean mButtonVisible;
	private boolean mIsMouseOver;

	private Texture mUITexture;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setListener(EntryInteractions pListener) {
		mClickListener = pListener;
	}

	public void setButtonEnabled(boolean pNewValue) {
		mButtonEnabled = pNewValue;
	}

	public boolean buttonEnabled() {
		return mButtonEnabled;
	}

	public void setButtonLabel(String pButtonLabel) {
		if (pButtonLabel == null || pButtonLabel.length() == 0)
			mButtonLabel = "";
		else
			mButtonLabel = pButtonLabel;

	}

	public String setButtonLabel() {
		return mButtonLabel;
	}

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean pNewValue) {
		mIsChecked = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuButtonEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, String pLabel) {
		this(pScreenManager, pParentLayout, pLabel, "No Label");

	}

	public MenuButtonEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, String pLabel, String pButtonLabel) {
		super(pScreenManager, pParentLayout, "");

		mLabel = pLabel;

		mButtonLabel = pButtonLabel;

		mButtonEnabled = true;
		mButtonVisible = true;

		mHighlightOnHover = false;
		mDrawBackground = false;

	}

	// --------------------------------------
	// Core Methods
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
	public boolean handleInput(LintfordCore pCore) {
		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		// button input handling
		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mIsMouseOver = true;
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (mEnabled) {

					// TODO: Play a menu click sound

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);
					// mParentScreen.setHoveringOn(this);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					mIsChecked = !mIsChecked;

					pCore.input().setLeftMouseClickHandled();

				}

			} else {
				hasFocus(true);
			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.time().elapseGameTimeMilli();
			}

			return true;

		} else {
			hoveredOver(false);
			mIsMouseOver = false;
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		super.draw(pCore, pScreen, pIsSelected, pParentZDepth);

		MenuScreen lParentScreen = mParentLayout.parentScreen();
		FontUnit lFontBitmap = lParentScreen.font();

		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final float lTextWidth = lFontBitmap.bitmap().getStringWidth(mLabel, luiTextScale);
		final float lTextHeight = lFontBitmap.bitmap().getStringHeight(mLabel) * luiTextScale;
		final float lSeparatorHalfWidth = lFontBitmap.bitmap().getStringWidth(mSeparator, luiTextScale) * 0.5f;

		final TextureBatch lTextureBatch = mParentLayout.parentScreen().rendererManager().uiTextureBatch();

		// Get the button area
		final float TILE_SIZE = 32;
		final float BUTTON_PADDING_W = 10f;
		final float BUTTON_PADDING_H = 5f;

		float lBW = w / 2 - BUTTON_PADDING_W * 2;
		float lBH = h - BUTTON_PADDING_H * 2;
		float lBX = x + (w / 2) + BUTTON_PADDING_W;
		float lBY = y + BUTTON_PADDING_H;

		// Draw the left/right buttons
		lTextureBatch.begin(pCore.HUD());

		float lR = mEnabled ? mAnimationTimer <= 0 ? ColorConstants.GREY_DARK.x : 0.55f : .35f;
		float lG = mEnabled ? mAnimationTimer <= 0 ? ColorConstants.GREY_DARK.y : 0.55f : .35f;
		float lB = mEnabled ? mAnimationTimer <= 0 ? ColorConstants.GREY_DARK.z : 0.55f : .35f;
		float lA = 1f;

		if (mIsMouseOver) {
			lR *= 0.6f;
			lG *= 0.6f;
			lB *= 0.6f;
		}

		lTextureBatch.draw(mUITexture, 0, 32, 32, 32, lBX, lBY, TILE_SIZE, lBH, 0f, lR, lG, lB, lA);
		lTextureBatch.draw(mUITexture, 32, 32, 32, 32, lBX + TILE_SIZE, lBY, lBW - TILE_SIZE * 2, lBH, 0f, lR, lG, lB, lA);
		lTextureBatch.draw(mUITexture, 64, 32, 32, 32, lBX + lBW - TILE_SIZE, lBY, TILE_SIZE, lBH, 0f, lR, lG, lB, lA);

		lTextureBatch.end();

		lR = lParentScreen.r();
		lG = lParentScreen.g();
		lB = lParentScreen.b();
		lA = lParentScreen.a();

		final float lButtonTextWidth = lFontBitmap.bitmap().getStringWidth(mButtonLabel);

		lFontBitmap.begin(pCore.HUD());
		lFontBitmap.draw(mLabel, x + w / 2 - 10 - lTextWidth - lSeparatorHalfWidth, y + h / 2 - lTextHeight * 0.5f, pParentZDepth, lR, lG, lB, lA, luiTextScale, -1);
		lFontBitmap.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - lTextHeight * 0.5f, pParentZDepth, lR, lG, lB, lA, luiTextScale, -1);
		lFontBitmap.draw(mButtonLabel, x + (w / 4) * 3 - lButtonTextWidth / 2, y + h / 2 - lTextHeight * 0.5f, pParentZDepth, lR, lG, lB, lA, luiTextScale, -1);
		lFontBitmap.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
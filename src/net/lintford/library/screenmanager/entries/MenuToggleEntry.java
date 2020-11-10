package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuToggleEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 51472065385268475L;
	private static final float SPACE_BETWEEN_TEXT = 15;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Texture mUITexture;
	private boolean mIsChecked;
	private final String mSeparator = " : ";

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void label(String pNewLabel) {
		mText = pNewLabel;
	}

	public String label() {
		return mText;
	}

	public void entryText(String pNewValue) {
		mText = pNewValue;
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

	public MenuToggleEntry(ScreenManager pScreenManager, BaseLayout pParentlayout) {
		super(pScreenManager, pParentlayout, "");

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

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mEnabled) {

					// TODO: Play menu click sound

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);

					mIsChecked = !mIsChecked;

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);

					}

				}

			} else {
				hasFocus(true);

			}

			return true;

		} else {
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		w = Math.min(mParentLayout.w() - 50f, mMaxWidth);

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		final double lDeltaTime = pCore.appTime().elapsedTimeMilli() / 1000.;

		// Check if tool tips are enabled.
		if (mToolTipEnabled) {
			mToolTipTimer += lDeltaTime;
		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		// super.draw(pCore, pScreen, pIsSelected, pParentZDepth);

		final var lParentScreen = mParentLayout.parentScreen();
		final var lFont = lParentScreen.font();
		if (lFont == null)
			return;

		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mText, lUiTextScale);
		final float lTextHeight = lFont.bitmap().fontHeight() * lUiTextScale;
		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		mZ = pParentZDepth;

		final var lTextureBatch = lParentScreen.rendererManager().uiTextureBatch();

		// Draw the left/right buttons
		lTextureBatch.begin(pCore.HUD());

		final float TILE_SIZE = 32;

		// Render the check box (either ticked or empty)
		if (mIsChecked)
			lTextureBatch.draw(mUITexture, 64, 128, 32, 32, centerX() + TILE_SIZE / 2, y + h / 2 - TILE_SIZE / 2, TILE_SIZE, TILE_SIZE, mZ, 1f, 1f, 1f, 1f);
		else
			lTextureBatch.draw(mUITexture, 32, 128, 32, 32, centerX() + TILE_SIZE / 2, y + h / 2 - TILE_SIZE / 2, TILE_SIZE, TILE_SIZE, mZ, 1f, 1f, 1f, 1f);

		lTextureBatch.end();

		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);
		lFont.draw(mText, x + w / 2 - lLabelWidth - SPACE_BETWEEN_TEXT - lSeparatorHalfWidth, y + h / 2 - lTextHeight * 0.5f, mZ, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale,
				-1);
		lFont.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - lTextHeight * 0.5f, mZ, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale, -1);

		if (mIsChecked) {
			lFont.draw("Enabled", x + w / 2 + lSeparatorHalfWidth + TILE_SIZE * 2, y + h / 2 - lTextHeight * 0.5f, mZ, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale, -1);

		} else {
			lFont.draw("Disabled", x + w / 2 + lSeparatorHalfWidth + TILE_SIZE * 2, y + h / 2 - lTextHeight * 0.5f, mZ, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale, -1);

		}

		// Render the items
		lFont.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;

		} else {
			mFocusLocked = false;

		}
	}
}

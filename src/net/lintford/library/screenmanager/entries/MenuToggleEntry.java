package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.BitmapFont;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuToggleEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 51472065385268475L;
	private static final float SPACE_BETWEEN_TEXT = 15;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;

	private boolean mIsChecked;

	private final String mSeparator = " : ";

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	@Override
	public float getHeight() {
		return MENUENTRY_HEIGHT + 6;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuToggleEntry(ScreenManager pScreenManager, MenuScreen pParentScreen) {
		super(pScreenManager, pParentScreen, "");

		mLabel = "Label:";

		mHighlightOnHover = false;
		mDrawBackground = false;

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		if (intersects(pCore.HUD().getMouseCameraSpace())) {
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (mEnabled) {

					// TODO: Play menu click sound

					mParentScreen.setFocusOn(pCore, this, true);

					mIsChecked = !mIsChecked;

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);

					}

					pCore.input().setLeftMouseClickHandled();

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
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		final double lDeltaTime = pCore.time().elapseGameTimeMilli() / 1000f;

		// Check if tool tips are enabled.
		if (mToolTipEnabled) {
			mToolTipTimer += lDeltaTime;
		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		super.draw(pCore, pScreen, pIsSelected, pParentZDepth);

		mZ = pParentZDepth;

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		final float lLabelWidth = lFontBitmap.getStringWidth(mLabel);
		final float TEXT_HEIGHT = lFontBitmap.getStringHeight(mLabel);
		final float lSeparatorHalfWidth = lFontBitmap.getStringWidth(mSeparator) * 0.5f;

		// Draw the left/right buttons
		mTextureBatch.begin(pCore.HUD());

		final float TILE_SIZE = 32;

		// Render the check box (either ticked or empty)
		if (mIsChecked)
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 288, 128, 32, 32, x + (w / 4 * 3) - TILE_SIZE / 2, y + h / 2 - TILE_SIZE / 2, TILE_SIZE, TILE_SIZE, mZ, 1f, 1f, 1f, 1f);
		else
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 288, 160, 32, 32, x + (w / 4 * 3) - TILE_SIZE / 2, y + h / 2 - TILE_SIZE / 2, TILE_SIZE, TILE_SIZE, mZ, 1f, 1f, 1f, 1f);

		mTextureBatch.end();

		mParentScreen.font().begin(pCore.HUD());
		mParentScreen.font().draw(mLabel, x + w / 2 - lLabelWidth - SPACE_BETWEEN_TEXT - lSeparatorHalfWidth, y + h / 2 - lFontBitmap.getStringHeight(mLabel) * 0.5f, mZ, mParentScreen.r(), mParentScreen.g(),
				mParentScreen.b(), mParentScreen.a(), 1.0f, -1);
		mParentScreen.font().draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - TEXT_HEIGHT * 0.5f, mZ, mParentScreen.r(), mParentScreen.g(), mParentScreen.b(), mParentScreen.a(), 1.0f, -1);

		// Render the items
		mParentScreen.font().end();

	}

	@Override
	public void onClick(InputState pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;

		} else {
			mFocusLocked = false;

		}
	}
}

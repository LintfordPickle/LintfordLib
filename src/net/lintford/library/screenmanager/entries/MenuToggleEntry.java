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
			if (pCore.input().mouseTimedLeftClick()) {
				if (mEnabled) {

					// TODO: Play menu click sound

					mParentScreen.setFocusOn(pCore.input(), this, true);
					// mParentScreen.setHoveringOn(this);

					mIsChecked = !mIsChecked;

					// TODO: notify somebody that this click has been handled this frame
					// pInputState.handleTimedLeftClick();
				}
			} else {
				// mParentScreen.setHoveringOn(this);
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

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		final float lLabelWidth = lFontBitmap.getStringWidth(mLabel);
		final float TEXT_HEIGHT = lFontBitmap.getStringHeight(mLabel);
		final float lSeparatorHalfWidth = lFontBitmap.getStringWidth(mSeparator) * 0.5f;

		// TODO(John): we could make this a lot more readable and save on the individual calculations of the width/height of the same strings

		// Draw the left/right buttons
		mSpriteBatch.begin(pCore.HUD());

		final float srcx = 352;
		final float srcy = 96;
		final float srcw = 32;
		final float srch = 32;

		// Render the check box (either ticked or empty)
		if (mIsChecked)
			mSpriteBatch.draw(srcx + 32, srcy, srcw, srch, x + width / 2 + 32, y + height / 2 - 8, pParentZDepth + .1f, 24, 24, 1f, TextureManager.TEXTURE_CORE_UI);
		else
			mSpriteBatch.draw(srcx, srcy, srcw, srch, x + width / 2 + 32, y + height / 2 - 8, pParentZDepth + .1f, 24, 24, 1f, TextureManager.TEXTURE_CORE_UI);

		mSpriteBatch.end();

		mParentScreen.font().begin(pCore.HUD());
		mParentScreen.font().draw(mLabel, x + width / 2 - lLabelWidth - SPACE_BETWEEN_TEXT - lSeparatorHalfWidth, y + height / 2 - lFontBitmap.getStringHeight(mLabel) * 0.5f, pParentZDepth + .1f, mParentScreen.r(), mParentScreen.g(),
				mParentScreen.b(), mParentScreen.a(), 1.0f, -1);
		mParentScreen.font().draw(mSeparator, x + width / 2 - lSeparatorHalfWidth, y + height / 2 - TEXT_HEIGHT * 0.5f, pParentZDepth + .1f, mParentScreen.r(), mParentScreen.g(), mParentScreen.b(), mParentScreen.a(), 1.0f, -1);

		// Render the items
		mParentScreen.font().end();

	}

	@Override
	public void onClick(InputState pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;
			System.out.println("locking focus");

		} else {
			mFocusLocked = false; // no lock if not focused
		}
	}
}

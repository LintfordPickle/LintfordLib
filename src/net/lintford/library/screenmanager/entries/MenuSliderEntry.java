package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuSliderEntry extends MenuEntry {

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

	public MenuSliderEntry(ScreenManager pScreenManager, MenuScreen pParentScreen) {
		super(pScreenManager, pParentScreen, "");

		mLabel = "Label:";

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

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.time().elapseGameTimeMilli();
			}

			return true;

		} else {
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		super.draw(pCore, pScreen, pIsSelected, pParentZDepth);

		// BitmapFont lFont = mParentScreen.font().bitmap();

		// final float lLabelWidth = lFont.getStringWidth(mLabel);
		// final float lSeparatorHalfWidth = lFont.getStringWidth(mSeparator) * 0.5f;

		// draw the label to the left //
		// TODO(John): Implement the pixel font here

		// (mLabel, x - lLabelWidth - SPACE_BETWEEN_TEXT, y - getTextHeight(mLabel) * 0.5f - 4, -0.9f)
		// (mSeparator, x - lSeparatorHalfWidth, y - getTextHeight(mSeparator) * 0.5f - 4, -0.9f)

		// TODO(John): Render the box and tick (if checked)

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

package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.BitmapFont;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuDropDownEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float SPACE_BETWEEN_TEXT = 15;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;

	private List<DropDownItem> mEntries;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuDropDownEntry(ScreenManager pScreenManager, MenuScreen pParentScreen) {
		super(pScreenManager, pParentScreen, "");

		mEntries = new ArrayList<>();
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

					// TODO: play the menu clicked sound

					mParentScreen.setFocusOn(pCore.input(), this, true);
					// mParentScreen.setHoveringOn(this);

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

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		final float lLabelWidth = lFontBitmap.getStringWidth(mLabel) * 0.5f;

		// draw the label to the left //
		mParentScreen.font().draw(mLabel, x - lLabelWidth - SPACE_BETWEEN_TEXT, y - lFontBitmap.getStringWidth(mLabel) * 0.5f - 4, -0.9f);

		// TODO(John): Render the drop down box stuff

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

	public void addEntry(DropDownItem pItem) {

		if (!mEntries.contains(pItem)) {
			mEntries.add(pItem);
		}

	}
}

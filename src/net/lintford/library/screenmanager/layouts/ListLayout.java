package net.lintford.library.screenmanager.layouts;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.InputState.INPUT_TYPES;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.entries.ListBox;

/**
 * The list layout lays out all the menu entries linearly down the layout.
 * 
 * @author Lintford Pickle
 */
public class ListLayout extends BaseLayout {

	private static final long serialVersionUID = -7568188688210642680L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListLayout(MenuScreen pParentScreen) {
		super(pParentScreen);

		// Set some defaults
		mAlignment = LAYOUT_ALIGNMENT.center;

	}

	public ListLayout(MenuScreen pParentScreen, float pX, float pY) {
		this(pParentScreen);

		x = pX;
		y = pY;
	}

	public ListLayout(MenuScreen pParentScreen, float pX, float pY, float pW, float pH) {
		this(pParentScreen, pX, pY);

		w = pW;
		h = pH;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().lastInputActive() == INPUT_TYPES.Mouse) {

			if (super.handleInput(pCore) || pCore.input().isMouseTimedLeftClickAvailable()) {

				// pCore.input().setLeftMouseClickHandled();
				return true;
			}

		}

		return false;

	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		float lYPos = y + mEntryOffsetFromTop + mYScrollPos;

		if (mContentArea.h < h) {
			mYScrollPos = 0;
			lYPos += 5f;
		}

		float lVertHeightTaken = marginTop() + marginBottom() + 2;

		int lEntryCount = menuEntries().size();
		for (int i = 0; i < lEntryCount; i++) {
			MenuEntry lEntry = menuEntries().get(i);

			switch (mAlignment) {
			case left:
				lEntry.x = x;
				break;
			case center:
				lEntry.x = centerX() - lEntry.w / 2;
				break;
			case right:
				lEntry.x = x + w - lEntry.w;
				break;
			}

			// Listboxes shouldn't cause the listlayout parent cotainers to grow in vertical size.
			// Instead we need to work how how much space the listbox
			// FIXME: This isn't quite correct (the -10 is wrong, but it works)
			if (lEntry instanceof ListBox) {
				lEntry.h = h - lVertHeightTaken;
			} else {
				lVertHeightTaken += lEntry.marginTop();
				lVertHeightTaken += lEntry.h;
				lVertHeightTaken += lEntry.marginBottom();
			}

			lEntry.y = lYPos;

			lYPos += lEntry.marginTop();
			lYPos += lEntry.height();
			lYPos += lEntry.marginBottom();

		}

	}

}

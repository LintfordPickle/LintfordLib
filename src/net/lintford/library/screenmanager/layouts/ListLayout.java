package net.lintford.library.screenmanager.layouts;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.InputState.INPUT_TYPES;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;

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
		// otherwise, defocus this and all children ??
		else {
			final int lEntryCount = menuEntries().size();
			for (int i = 0; i < lEntryCount; i++) {
				MenuEntry lEntry = menuEntries().get(i);

				lEntry.hoveredOver(false);

			}

		}

		return false;

	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		float lYPos = y + mEntryOffsetFromTop + mYScrollPos;

		final int lEntryCount = menuEntries().size();

		// If the height of the content is smaller than the height of this layout, disable the scroll bar
		if (mContentArea.h < h) {
			mYScrollPos = 0;
			lYPos += 5f;
		}

		final float lLayoutHeight = h - marginBottom() - marginTop();

		// See how many layouts only take what they need
		int lCountOfSharers = lEntryCount;
		int lCountOfTakers = 0;

		float lHeightTaken = marginTop() + marginBottom();

		for (int i = 0; i < lEntryCount; i++) {
			MenuEntry lEntry = menuEntries().get(i);
			if (lEntry.verticalFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lCountOfTakers++;
				lHeightTaken += lEntry.paddingTop() + lEntry.height() + lEntry.paddingBottom();

			}

		}

		lCountOfSharers -= lCountOfTakers;

		final float INNER_PADDING = 25;
		float lSizeOfEachFillElement = ((lLayoutHeight - lHeightTaken) / lCountOfSharers) - INNER_PADDING;

		if (lSizeOfEachFillElement < 0)
			lSizeOfEachFillElement = 10;

		for (int i = 0; i < lEntryCount; i++) {
			MenuEntry lEntry = menuEntries().get(i);
			float lScrollBarWidth = 0;
			if (mScrollBarsEnabled)
				lScrollBarWidth = mScrollBar.width();

			lEntry.w = w - marginLeft() - marginRight() - lScrollBarWidth;
			lEntry.x = centerX() - lEntry.w / 2 - lScrollBarWidth / 2;

			// Assign the entry height here
			if (lEntry.verticalFillType() == FILLTYPE.FILL_CONTAINER) {
				lEntry.h = lSizeOfEachFillElement; // MathHelper.clamp(lSizeOfEachFillElement, lEntry.minHeight(), lEntry.maxHeight());

			} else if (lEntry.verticalFillType() == FILLTYPE.FILL_CONTAINER) {
				lEntry.h = lSizeOfEachFillElement; // MathHelper.clamp(lSizeOfEachFillElement, lEntry.minHeight(), lEntry.maxHeight());

			} else {
				lEntry.h = 32;// lLayoutHeight - lHeightTaken;

			}

			lEntry.y = lYPos;

			lYPos += lEntry.marginTop();
			lYPos += lEntry.height();
			lYPos += lEntry.marginBottom();

		}

	}

}

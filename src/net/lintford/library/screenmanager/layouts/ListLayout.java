package net.lintford.library.screenmanager.layouts;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;

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
		mAlignment = ALIGNMENT.center;

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
	public void updateStructurePositions() {
		super.updateStructurePositions();

		if (mContentArea.h < h)
			mYScrollPos = 0;

		float lYPos = y + mYScrollPos;

		int lEntryCount = menuEntries().size();
		for (int i = 0; i < lEntryCount; i++) {
			MenuEntry lEntry = menuEntries().get(i);
//			if (!menuEntries().get(i).enabled())
//				continue;

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

			lEntry.y = lYPos;

			lYPos += lEntry.marginTop();
			lYPos += lEntry.h;
			lYPos += lEntry.marginBottom();

		}

	}

}

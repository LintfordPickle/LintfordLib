package net.lintford.library.screenmanager.layouts;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;

/**
 * The list layout lays out all the menu entries linearly down the layout.
 * 
 * @author Lintford Pickle
 */
public class DualColumnLayout extends BaseLayout {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7568188688210642680L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<MenuEntry> mSecondMenuEntries;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<MenuEntry> secondMenuEntries() {
		return mSecondMenuEntries;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DualColumnLayout(MenuScreen pParentScreen) {
		super(pParentScreen);

		// Set some defaults
		mAlignment = ALIGNMENT.center;

		mSecondMenuEntries = new ArrayList<>();

	}

	public DualColumnLayout(MenuScreen pParentScreen, float pX, float pY) {
		this(pParentScreen);

		x = pX;
		y = pY;
	}

	public DualColumnLayout(MenuScreen pParentScreen, float pX, float pY, float pW, float pH) {
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
		
		float lXPos = x;
		float lYPos = y + mYScrollPos;

		if (mContentArea.h < h) {
			mYScrollPos = 0;
			lYPos += 5f;
		}

		int lEntryCount = menuEntries().size();
		for (int i = 0; i < lEntryCount; i++) {
			MenuEntry lEntry = menuEntries().get(i);

			switch (mAlignment) {
			case left:
				lEntry.x = lXPos;
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
		
		int lSecondEntryCount = secondMenuEntries().size();
		for (int i = 0; i < lSecondEntryCount; i++) {
			MenuEntry lEntry = secondMenuEntries().get(i);

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

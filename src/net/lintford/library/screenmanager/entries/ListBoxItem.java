package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class ListBoxItem {

	public static final float LISTBOXITEM_WIDTH = 600;
	public static final float LISTBOXITEM_HEIGHT = 64;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected ListBox mParentListBox;

	public float mXPos;
	public float mYPos;

	protected int mItemIndex;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBoxItem(ScreenManager pScreenManager, ListBox pParentListBox, int pIndex) {
		mScreenManager = pScreenManager;
		mParentListBox = pParentListBox;

		mItemIndex = pIndex;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public boolean handleInput(LintfordCore pCore) {

		Vector2f lMouseMenuSpace = pCore.HUD().getMouseCameraSpace();

		float lAbsPosX = mParentListBox.x + (mParentListBox.width / 2) + mXPos;
		float lAbsPosY = mParentListBox.y + mYPos + 15;

		if ((lMouseMenuSpace.x > lAbsPosX && lMouseMenuSpace.x < lAbsPosX + LISTBOXITEM_WIDTH && lMouseMenuSpace.y > lAbsPosY && lMouseMenuSpace.y < lAbsPosY + LISTBOXITEM_HEIGHT) && pCore.input().tryAquireLeftClickOwnership(hashCode())) {
			mParentListBox.setSelectedItem(mItemIndex);

			return true;
		}

		return false;

	}

	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {

	}

	public abstract void draw(LintfordCore pCore, Screen pScreen, TextureBatch pSpriteBatch, boolean pIsSelected, float pParentZDepth);

}

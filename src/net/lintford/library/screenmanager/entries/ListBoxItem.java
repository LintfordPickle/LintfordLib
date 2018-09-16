package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class ListBoxItem extends Rectangle {

	private static final long serialVersionUID = -1093948958243532531L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected ListBox mParentListBox;

	protected int mItemIndex;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBoxItem(ScreenManager pScreenManager, ListBox pParentListBox, int pIndex) {
		mScreenManager = pScreenManager;
		mParentListBox = pParentListBox;

		mItemIndex = pIndex;

		w = 600;
		h = 64;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public boolean handleInput(LintfordCore pCore) {
		final Vector2f lMouseMenuSpace = pCore.HUD().getMouseCameraSpace();

		if (intersectsAA(lMouseMenuSpace) && pCore.input().tryAquireLeftClickOwnership(hashCode())) {
			mParentListBox.setSelectedItem(mItemIndex);
			return true;

		}

		return false;

	}

	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {

	}

	public abstract void draw(LintfordCore pCore, Screen pScreen, TextureBatch pSpriteBatch, boolean pIsSelected, float pParentZDepth);

}

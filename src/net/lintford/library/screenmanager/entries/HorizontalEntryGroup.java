package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class HorizontalEntryGroup extends MenuEntry {

	private static final long serialVersionUID = -7105496211645061681L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<MenuEntry> mChildEntries;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<MenuEntry> entries() {
		return mChildEntries;
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		if (!pNewValue) {
			int lCount = entries().size();
			for (int i = 0; i < lCount; i++) {
				entries().get(i).hasFocus(false);
			}

		}

		super.hasFocus(pNewValue);

	}

	@Override
	public void hoveredOver(boolean pNewValue) {
		if (!pNewValue) {
			int lCount = entries().size();
			for (int i = 0; i < lCount; i++) {
				entries().get(i).hoveredOver(false);
			}

		}

		super.hoveredOver(pNewValue);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public HorizontalEntryGroup(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout, "");

		mChildEntries = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).initialise();
		}

		updateEntries();

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).loadGLContent(pResourceManager);
		}
	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).unloadGLContent();
		}
	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				if (mChildEntries.get(i).handleInput(pCore)) {
					return true;
				}

			}

			return true;

		} else {
			mToolTipTimer = 0;

			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				mChildEntries.get(i).hasFocus(false);
				mChildEntries.get(i).hoveredOver(false);

			}

		}

		return false;

	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		updateEntries();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).updateStructure();

		}
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).update(pCore, pScreen, pIsSelected);
		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final float lR = mParentLayout.parentScreen().r();
			final float lG = mParentLayout.parentScreen().g();
			final float lB = mParentLayout.parentScreen().b();

			Debug.debugManager().drawers().drawRectImmediate(pCore.gameCamera(), x, y, w, h, 0.5f * lR, 0.2f * lG, lB);

		}

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).draw(pCore, pScreen, pIsSelected, pParentZDepth);

		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateEntries() {
		// Here we will use the position given to us by the parent screen and use it
		// to orientation our children (for now just horizontally).
		if (mChildEntries == null || mChildEntries.size() == 0)
			return;

		int lCount = mChildEntries.size();
		float lTotalHeight = 0;
		for (int i = 0; i < lCount; i++) {
			if (mChildEntries.get(i).h + mChildEntries.get(i).marginTop() * 2 > lTotalHeight) {
				lTotalHeight = mChildEntries.get(i).h + mChildEntries.get(i).marginTop() * 2;

			}

		}

		final float lHPadding = 10;
		final float lHSpace = w / (lCount) - lHPadding;

		for (int i = 0; i < lCount; i++) {
			float lPosX = x + mChildEntries.get(i).marginLeft() + (lHSpace + lHPadding) * i;
			float lPosY = y;

			mChildEntries.get(i).w = lHSpace;
			mChildEntries.get(i).x = lPosX;
			mChildEntries.get(i).y = lPosY + mChildEntries.get(i).marginTop();

		}

//		w = lTotalWidth;
//		h = lTotalHeight;

	}

	public void addEntry(MenuEntry pEntry) {
		mChildEntries.add(pEntry);
	}

	public void removeEntry(MenuEntry pEntry) {
		if (mChildEntries.contains(pEntry)) {
			mChildEntries.remove(pEntry);
		}
	}

}

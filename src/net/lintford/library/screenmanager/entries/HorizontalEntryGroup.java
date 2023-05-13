package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public HorizontalEntryGroup(ScreenManager pScreenManager, MenuScreen parentScreen) {
		super(pScreenManager, parentScreen, "");

		mChildEntries = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).initialize();
		}

		updateEntries();

	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).loadResources(pResourceManager);
		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).unloadResources();
		}
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {
		// TODO Auto-generated method stub
		return super.onHandleGamepadInput(core);
	}

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {
		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				if (mChildEntries.get(i).onHandleMouseInput(core)) {
					return true;
				}
			}

			return true;

		} else {
			mToolTipTimer = 0;

			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				mChildEntries.get(i).hasFocus(false);
				// mChildEntries.get(i).hoveredOver(false);
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
	public void update(LintfordCore pCore, MenuScreen pScreen) {
		super.update(pCore, pScreen);

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).update(pCore, pScreen);
		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, float pParentZDepth) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final float lR = parentScreen().screenColor.r;
			final float lG = parentScreen().screenColor.g;
			final float lB = parentScreen().screenColor.b;

			Debug.debugManager().drawers().drawRectImmediate(pCore.gameCamera(), mX, mY, mW, mH, 0.5f * lR, 0.2f * lG, lB);

		}

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).draw(pCore, pScreen, pParentZDepth);

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
			if (mChildEntries.get(i).height() + mChildEntries.get(i).marginTop() * 2 > lTotalHeight) {
				lTotalHeight = mChildEntries.get(i).height() + mChildEntries.get(i).marginTop() * 2;
			}
		}

		final float lHPadding = 10;
		final float lHSpace = (mW - lHPadding) / lCount;

		for (int i = 0; i < lCount; i++) {
			final var MenuEntry = mChildEntries.get(i);
			float lPosX = mX + (lHSpace + lHPadding) * i;
			float lPosY = mY;

			MenuEntry.width(lHSpace);
			MenuEntry.setPosition(lPosX, lPosY + mChildEntries.get(i).marginTop());
		}

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

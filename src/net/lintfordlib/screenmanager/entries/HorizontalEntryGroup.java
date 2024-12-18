package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class HorizontalEntryGroup extends MenuEntry {

	private static final long serialVersionUID = -7105496211645061681L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<MenuEntry> mChildEntries;
	private int mSelectedEntryUid;

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
				// entries().get(i).hasFocus(false);
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
	public boolean onHandleMouseInput(LintfordCore core) {
		if (intersectsAA(core.HUD().getMouseCameraSpace())) {

			final var lChildEntryCount = mChildEntries.size();
			for (int i = 0; i < lChildEntryCount; i++) {
				if (mChildEntries.get(i).onHandleMouseInput(core)) {
					return true;
				}
			}

			return true;

		} else {
			mToolTipTimer = 0;

			final var lChildEntryCount = mChildEntries.size();
			for (int i = 0; i < lChildEntryCount; i++) {
				mChildEntries.get(i).hasFocus(false);
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

		final var lChildEntryCount = mChildEntries.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildEntries.get(i).update(pCore, pScreen);
		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, float pParentZDepth) {
		final var lChildEntryCount = mChildEntries.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildEntries.get(i).draw(pCore, pScreen, pParentZDepth);
		}
		
		if(mHasFocus) {
			System.out.println("HorizontalEntryGroup: mHasFocus is  true");
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final var lR = parentScreen().screenColor.r;
			final var lG = parentScreen().screenColor.g;
			final var lB = parentScreen().screenColor.b;

			Debug.debugManager().drawers().drawRectImmediate(pCore.gameCamera(), mX, mY, mW, mH, 0.5f * lR, 0.2f * lG, lB);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		if (mChildEntries.isEmpty())
			return;

		final var lSelectedItem = mChildEntries.get(mSelectedEntryUid);
		if (lSelectedItem != null)
			lSelectedItem.onClick(inputManager);

	}

	public void navigatePrev() {
		mSelectedEntryUid--;
		if (mSelectedEntryUid < 0)
			mSelectedEntryUid = mChildEntries.size();
	}

	public void navigateNext() {
		mSelectedEntryUid++;
		if (mSelectedEntryUid >= mChildEntries.size())
			mSelectedEntryUid = 0;
	}

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

		final float lHPadding = 10.f;
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
		if (mChildEntries.contains(pEntry))
			mChildEntries.remove(pEntry);

	}

}

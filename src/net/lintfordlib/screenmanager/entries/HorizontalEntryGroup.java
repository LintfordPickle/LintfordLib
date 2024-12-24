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
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;

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

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final var lR = parentScreen().screenColor.r;
			final var lG = parentScreen().screenColor.g;
			final var lB = parentScreen().screenColor.b;

			Debug.debugManager().drawers().drawRectImmediate(pCore.gameCamera(), pScreen.screenPositionOffset().x + mX, pScreen.screenPositionOffset().y + mY, mW, mH, 0.5f * lR, 0.2f * lG, lB);
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
		if (mChildEntries == null || mChildEntries.isEmpty())
			return;

		int lCount = mChildEntries.size();
		float lTotalHeight = 0;
		for (int i = 0; i < lCount; i++) {
			if (mChildEntries.get(i).height() + mChildEntries.get(i).marginTop() * 2 > lTotalHeight) {
				lTotalHeight = mChildEntries.get(i).height() + mChildEntries.get(i).marginTop() * 2;
			}
		}

		// how much horizontal h_space can be *shared* and how much h_space should be pre-allocated
		var takenSpace = 0.f;
		for (int i = 0; i < lCount; i++) {
			final var lEntry = mChildEntries.get(i);
			if (lEntry.horizontalFillType() == FILLTYPE.TAKE_DESIRED_SIZE)
				takenSpace += lEntry.desiredWidth();

			takenSpace += lEntry.marginLeft() + lEntry.marginRight();

		}

		final float lHPadding = 0.f; // the padding of *this* control, that is, left and right
		final float lHInnerSpacing = 0.f;
		final float lHSpace = (mW - takenSpace) / lCount;

		var neededWidth = lHPadding * 2.f;
		for (int i = 0; i < lCount; i++) {
			final var lEntry = mChildEntries.get(i);
			final var lEWidth = lEntry.horizontalFillType() == FILLTYPE.TAKE_DESIRED_SIZE ? lEntry.desiredWidth() : lHSpace;
			neededWidth += lEntry.marginLeft() + lEWidth + lEntry.marginRight();
			if (i < lCount - 1)
				neededWidth += lHInnerSpacing;
		}

		final var lCenterX = mX + mW / 2;
		float lPosX = lCenterX - neededWidth / 2 + lHPadding;
		for (int i = 0; i < lCount; i++) {
			final var lEntry = mChildEntries.get(i);

			final var lEWidth = lEntry.horizontalFillType() == FILLTYPE.TAKE_DESIRED_SIZE ? lEntry.desiredWidth() : lHSpace;

			final var lPosY = mY;

			lPosX += lEntry.marginLeft();

			lEntry.width(50);
			lEntry.setPosition(lPosX, lPosY);
			lEntry.width(lEWidth);

			lPosX += lHInnerSpacing + lEWidth;
			lPosX += lEntry.marginRight();
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

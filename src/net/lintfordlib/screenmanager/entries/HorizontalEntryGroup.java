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
	private boolean mWrapInputAround = false;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** should the left/right input wrap the index around to the beginning..? */
	public boolean inputWrap() {
		return mWrapInputAround;
	}

	/** should the left/right input wrap the index around to the beginning..? */
	public void inputWrap(boolean wrapInputAround) {
		mWrapInputAround = wrapInputAround;
	}

	public List<MenuEntry> entries() {
		return mChildEntries;
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		if (!mHasFocus && pNewValue) {
			// try to set the index to the first available selectable child entry
			final var numEntries = entries().size();
			for (int i = 0; i < numEntries; i++) {
				final var entry = entries().get(i);
				if (entry != null && entry.enabled() && entry != MenuEntry.menuSeparator()) {
					mSelectedEntryUid = i;
					break;
				}
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

			if (mHasFocus && mSelectedEntryUid == i) {
				mChildEntries.get(i).hasFocus(true);
			} else {
				mChildEntries.get(i).hasFocus(false);
			}

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
	public boolean resolveChildEntry(MenuEntry entry) {
		final var numEntries = mChildEntries.size();
		for (int i = 0; i < numEntries; i++) {
			final var childEntry = mChildEntries.get(i);
			if (childEntry == entry || childEntry.resolveChildEntry(entry)) {
				mSelectedEntryUid = i;
				return true;
			}
		}

		return false;
	}

	public boolean setFocusOnChildEntry(MenuEntry entry) {
		boolean found = false; // cannot early escape, we need to update all entries either way
		final var numEntries = mChildEntries.size();
		for (int i = 0; i < numEntries; i++) {
			final var childEntry = mChildEntries.get(i);
			if (childEntry == entry || childEntry.setFocusOnChildEntry(entry)) {
				childEntry.hasFocus(true);
				mSelectedEntryUid = i;
				found = true;
			} else {
				childEntry.hasFocus(false);
			}
		}

		return found;
	}

	@Override
	public void onClick(InputManager inputManager) {
		if (mChildEntries.isEmpty())
			return;

		final var lSelectedItem = mChildEntries.get(mSelectedEntryUid);
		if (lSelectedItem != null)
			lSelectedItem.onClick(inputManager);

	}

	@Override
	public boolean leftMostChildSelected() {
		var isMultipleChildren = getChildCount(true) > 1;
		if (!isMultipleChildren)
			return true;
		return mSelectedEntryUid > 0;
	}

	@Override
	public boolean rightMostChildSelected() {
		var isMultipleChildren = getChildCount(true) > 1;
		if (!isMultipleChildren)
			return true;
		return mSelectedEntryUid < mChildEntries.size();
	}

	public int getChildCount(boolean filterOutDisabled) {
		final var numEntries = mChildEntries.size();
		int result = 0;
		for (int i = 0; i < numEntries; i++) {
			final var entry = mChildEntries.get(i);
			if (entry == null || !entry.enabled() || entry == MenuEntry.menuSeparator())
				continue;

			result++;
		}
		return result;

	}

	@Override
	public boolean onNavigationGainFocus(LintfordCore core) {
		return onNavigationRight(core);
	}

	@Override
	public boolean onNavigationLeft(LintfordCore core) {

		if (mChildEntries.size() <= 1)
			return true;

		final var startIndex = mSelectedEntryUid;
		mSelectedEntryUid--;

		while (mSelectedEntryUid != startIndex) {
			if (mSelectedEntryUid < 0) {
				if (!mWrapInputAround) {
					mSelectedEntryUid = startIndex;
					return false;
				}

				mSelectedEntryUid = mChildEntries.size() - 1;
			}

			if (mChildEntries.get(mSelectedEntryUid) != MenuEntry.menuSeparator())
				return true;

			mSelectedEntryUid--;

		}

		return false;

	}

	@Override
	public boolean onNavigationRight(LintfordCore core) {
		if (mChildEntries.size() <= 1)
			return true;

		final var startIndex = mSelectedEntryUid;
		mSelectedEntryUid++;

		while (mSelectedEntryUid != startIndex) {
			if (mSelectedEntryUid >= mChildEntries.size()) {
				if (!mWrapInputAround) {
					mSelectedEntryUid = startIndex;
					return false;
				}

				mSelectedEntryUid = 0;
			}

			if (mChildEntries.get(mSelectedEntryUid) != MenuEntry.menuSeparator())
				return true;

			mSelectedEntryUid++;
		}

		return false;
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

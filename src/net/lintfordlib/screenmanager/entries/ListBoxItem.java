package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public abstract class ListBoxItem extends Rectangle {

	private static final long serialVersionUID = -1093948958243532531L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected ListBox mParentListBox;
	public final Color textColor = new Color();
	public final Color entryColor = new Color();
	protected int mEntityGroupID;
	protected int mItemIndex;
	protected float mDoubleClickTimer;
	protected int mDoubleClickLogicalCounter;
	protected int mEntryWidth;
	protected int mEntryHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int itemIndex() {
		return mItemIndex;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBoxItem(ScreenManager screenManager, ListBox parentListBox, int index, int entityGroupUid) {
		mScreenManager = screenManager;
		mParentListBox = parentListBox;

		mEntityGroupID = entityGroupUid;

		mItemIndex = index;

		mEntryWidth = 600;
		mEntryHeight = 64;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public boolean handleInput(LintfordCore core) {
		final var lMouseMenuSpace = core.HUD().getMouseCameraSpace();

		final var intersectsUs = intersectsAA(lMouseMenuSpace);
		final var areWeFreeToUseMouse = core.input().mouse().isMouseOverThisComponent(hashCode());
		final var canWeAcquireLeftMouse = intersectsUs && core.input().mouse().tryAcquireMouseLeftClick(hashCode());

		if (canWeAcquireLeftMouse && mDoubleClickLogicalCounter == -1) {
			mDoubleClickLogicalCounter = core.input().mouse().mouseLeftButtonLogicalTimer();
			mDoubleClickTimer = 200; // 200 ms to double click
		}

		if (intersectsUs && areWeFreeToUseMouse && canWeAcquireLeftMouse) {
			mParentListBox.selectedIndex(mItemIndex);

			return true;
		}
	
		if (intersectsUs && mDoubleClickLogicalCounter != -1) {
			mDoubleClickTimer -= core.appTime().elapsedTimeMilli();

			if (mDoubleClickTimer < 0) {
				mDoubleClickLogicalCounter = -1;
				mDoubleClickTimer = 0.f;
			} else if (mDoubleClickLogicalCounter != core.input().mouse().mouseLeftButtonLogicalTimer()) {
				mDoubleClickLogicalCounter = core.input().mouse().mouseLeftButtonLogicalTimer();
				mParentListBox.itemDoubleClicked(mItemIndex);

				return true;
			}

		} else {
			mDoubleClickLogicalCounter = -1;
			mDoubleClickTimer = 0.f;
		}

		return false;
	}

	public void update(LintfordCore core, MenuScreen screen) {
		mW = mEntryWidth;
		mH = mEntryHeight;
	}

	public abstract void draw(LintfordCore core, Screen screen, SpriteBatch spriteBatch, float parentZDepth);

}
package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
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

	public final Color textColor = new Color();
	public final Color entryColor = new Color();

	protected int mEntityGroupID;
	protected int mItemIndex;
	protected float mDoubleClickTimer;
	protected int mDoubleClickLogicalCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int itemIndex() {
		return mItemIndex;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBoxItem(ScreenManager pScreenManager, ListBox pParentListBox, int pIndex, int pEntityGroupID) {
		mScreenManager = pScreenManager;
		mParentListBox = pParentListBox;

		mEntityGroupID = pEntityGroupID;

		mItemIndex = pIndex;

		w = 600;
		h = 64;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public void loadGLContent(ResourceManager pResourceManager) {

	}

	public void unloadGLContent() {

	}

	public boolean handleInput(LintfordCore pCore) {
		final Vector2f lMouseMenuSpace = pCore.HUD().getMouseCameraSpace();

		final var intersectsUs = intersectsAA(lMouseMenuSpace);
		final var areWeFreeToUseMouse = pCore.input().mouse().isMouseOverThisComponent(hashCode());
		final var canWeAcquireLeftMouse = intersectsUs && pCore.input().mouse().tryAcquireMouseLeftClick(hashCode());

		if (canWeAcquireLeftMouse && mDoubleClickLogicalCounter == -1) {
			mDoubleClickLogicalCounter = pCore.input().mouse().mouseLeftButtonLogicalTimer();
			mDoubleClickTimer = 200; // 200 ms to double click
		}

		if (intersectsUs && areWeFreeToUseMouse && canWeAcquireLeftMouse) {
			mParentListBox.selectedIndex(mItemIndex);

			return true;

		}

		if (intersectsUs && mDoubleClickLogicalCounter != -1) {
			mDoubleClickTimer -= pCore.appTime().elapsedTimeMilli();

			if (mDoubleClickTimer < 0) {
				mDoubleClickLogicalCounter = -1;
				mDoubleClickTimer = 0.f;
			}

			else if (mDoubleClickLogicalCounter != pCore.input().mouse().mouseLeftButtonLogicalTimer()) {
				mDoubleClickLogicalCounter = pCore.input().mouse().mouseLeftButtonLogicalTimer();
				mParentListBox.itemDoubleClicked(mItemIndex);

				return true;

			}

		} else {
			mDoubleClickLogicalCounter = -1;
			mDoubleClickTimer = 0.f;
		}

		return false;

	}

	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {

	}

	public abstract void draw(LintfordCore pCore, Screen pScreen, TextureBatchPCT pSpriteBatch, boolean pIsSelected, float pParentZDepth);

}

package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.renderers.windows.components.StencilHelper;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.screenmanager.IListBoxItemDoubleClick;
import net.lintfordlib.screenmanager.IListBoxItemSelected;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;

public class MenuListBox extends MenuEntry implements IScrollBarArea {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6606453352329315889L;

	public static float LISTBOX_ITEM_VPADDING = 15;

	public static final float LISTBOX_MIN_WIDTH = 400;
	public static final float LISTBOX_MAX_WIDTH = 1024;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<MenuListBoxItem> mItems;
	protected ScrollBarContentRectangle mFullContentArea;
	protected ScrollBar mScrollBar;
	protected float mLastMouseYPos;
	protected IListBoxItemSelected mSelecterListener;
	protected IListBoxItemDoubleClick mItemDoubleClickListener;
	protected int mSelectedItemIndex = -1;
	protected boolean mIsInputActive;
	protected boolean mClickActive;
	protected int mItemHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int selectedIndex() {
		return mSelectedItemIndex;
	}

	public void selectedIndex(int i) {
		if (mSelecterListener != null && (i >= 0 && i < mItems.size())) {
			MenuListBoxItem litem = mItems.get(i);
			mSelecterListener.onListBoxItemSelected(litem, i);
		}

		mSelectedItemIndex = i;
	}

	public void selectedItem(MenuListBoxItem item) {
		if (!mItems.contains(item))
			return;

		final var indexOfItem = mItems.indexOf(item);
		if (indexOfItem == -1)
			return;

		mSelectedItemIndex = indexOfItem;
		if (mSelecterListener != null)
			mSelecterListener.onListBoxItemSelected(item, mSelectedItemIndex);

	}

	public void itemDoubleClicked(int i) {
		if (mItemDoubleClickListener != null && (i >= 0 && i < mItems.size())) {
			final var lItem = mItems.get(i);
			if (mSelecterListener != null) {
				mSelecterListener.onListBoxItemSelected(lItem, i);
			}

			mItemDoubleClickListener.onListItemDoubleClicked(lItem);
		}

		mSelectedItemIndex = i;
	}

	public void itemDoubleClicked(MenuListBoxItem item) {
		if (!mItems.contains(item))
			return;

		final var indexOfItem = mItems.indexOf(item);
		if (indexOfItem == -1)
			return;

		mSelectedItemIndex = indexOfItem;

		if (mItemDoubleClickListener != null) {
			if (mSelecterListener != null) {
				mSelecterListener.onListBoxItemSelected(item, indexOfItem);
			}

			mItemDoubleClickListener.onListItemDoubleClicked(item);
		}
	}

	public List<MenuListBoxItem> items() {
		return mItems;
	}

	public int itemHeight() {
		return mItemHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuListBox(ScreenManager screenManager, MenuScreen parentScreen) {
		this(screenManager, parentScreen, 25, null);
	}

	public MenuListBox(ScreenManager screenManager, MenuScreen parentScreen, int itemHeight) {
		this(screenManager, parentScreen, itemHeight, null);
	}

	public MenuListBox(ScreenManager screenManager, MenuScreen parentScreen, int itemHeight, String menuEntryLabel) {
		super(screenManager, parentScreen, menuEntryLabel);

		mItems = new ArrayList<>();
		mItemHeight = itemHeight;

		mFullContentArea = new ScrollBarContentRectangle(this);

		mScrollBar = new ScrollBar(this, new Rectangle(mX + mW - ScrollBar.BAR_WIDTH, mY, 20, mH));

		mLeftMargin = 10f;
		mRightMargin = 10f;
		mTopMargin = 10f;
		mBottomMargin = 10f;

		mMinWidth = LISTBOX_MIN_WIDTH;
		mMaxWidth = LISTBOX_MAX_WIDTH;
		mMinHeight = 200;
		mMaxHeight = 600;

		mVerticalFillType = FILLTYPE.FILL_CONTAINER;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {
		if (mScrollBar.scrollBarEnabled() && mScrollBar.handleInput(core, mScreenManager))
			return true;

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			boolean itemSelected = false;
			final var lNumitems = mItems.size();
			for (int i = 0; i < lNumitems; i++) {
				itemSelected |= mItems.get(i).handleInput(core);
			}

			if (itemSelected || core.input().mouse().isMouseLeftButtonDownTimed(this) && core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				if (mClickListener != null)
					mClickListener.onMenuEntryChanged(this);

				return true;
			}
		}

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = false;
			return false;
		}

		return super.onHandleMouseInput(core);
	}

	@Override
	public boolean onHandleKeyboardInput(LintfordCore core) {
		if (!mIsActive || !mIsInputActive)
			return false;

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this)) {
			mSelectedItemIndex--;

			if (mSelectedItemIndex < 0)
				mSelectedItemIndex = 0;

			scrollContentItemIntoView(mSelectedItemIndex);
			return true;
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this)) {
			mSelectedItemIndex++;

			if (mSelectedItemIndex >= mItems.size())
				mSelectedItemIndex = mItems.size() - 1;

			scrollContentItemIntoView(mSelectedItemIndex);
			return true;
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this)) {

			// The capture is managed in the onClick() method
			// This is bad, but we need to 'deactivate' this entry so the onCLick is even called...

			mParentScreen.onMenuEntryDeactivated(this);
		}

		return false;
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {
		if (!mIsActive || !mIsInputActive)
			return false;

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, this)) {
			mSelectedItemIndex--;

			if (mSelectedItemIndex < 0)
				mSelectedItemIndex = 0;

			// scrollContentItemIntoView(mHighlightedIndex);
			return true;
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, this)) {
			mSelectedItemIndex++;

			if (mSelectedItemIndex >= mItems.size())
				mSelectedItemIndex = mItems.size() - 1;

			// scrollContentItemIntoView(mHighlightedIndex);
			return true;
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_CROSS, this)) {

			// The capture is managed in the onClick() method
			// This is bad, but we need to 'deactivate' this entry so the onCLick is even called...

			mParentScreen.onMenuEntryDeactivated(this);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_CIRCLE, this)) {

			// The capture is managed in the onClick() method
			// This is bad, but we need to 'deactivate' this entry so the onCLick is even called...

			mParentScreen.onMenuEntryDeactivated(this);
			mIsInputActive = false;
		}

		return false;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		if (!mIsActive || !mAffectParentStructure && !mEnableUpdateDraw)
			return;

		final var lDeltaTime = (float) core.appTime().elapsedTimeMilli();

		if (mInputTimer >= 0)
			mInputTimer -= lDeltaTime;

		final int lCount = mItems.size();
		float mItemYPos = 0;

		float lTotalContentHeight = marginTop() + marginBottom();
		for (int i = 0; i < lCount; i++) {
			final var lItem = mItems.get(i);

			if (i == mSelectedItemIndex) {
				lItem.entryColor.setFromColor(ColorConstants.MenuEntrySelectedColor);
				lItem.entryColor.a = .3f;
			} else {
				lItem.entryColor.setRGBA(.3f, .3f, .3f, 0.2f);
			}

			lItem.update(core, screen);

			final var lTransitionOffset = screen.screenPositionOffset();

			final float lInnerPadding = mScrollBar.scrollBarEnabled() ? mScrollBar.width() : 0;
			mItems.get(i).width(mW - marginLeft() - marginRight() - lInnerPadding);
			mItems.get(i).setPosition(lTransitionOffset.x + mX + marginLeft(), lTransitionOffset.y + mY + marginTop() + mScrollBar.currentYPos() + mItemYPos);

			mItemYPos += lItem.height() + LISTBOX_ITEM_VPADDING;
			lTotalContentHeight += lItem.height() + LISTBOX_ITEM_VPADDING;
		}

		mFullContentArea.set(this);
		mFullContentArea.height(lTotalContentHeight);

		mScrollBar.scrollBarEnabled(mFullContentArea.height() > mH);
		mScrollBar.update(core);
	}

	public void scrollContentItemIntoView(int itemIndex) {

		if (contentDisplayArea().height() > mFullContentArea.height()) {
			mScrollBar.AbsCurrentYPos(0);
			return; // no need to scroll, the content fits within the display area
		}

		if (itemIndex == 0) {
			mScrollBar.AbsCurrentYPos(0);
			return;
		}

		float itopPos = 0.f;
		for (int i = 1; i <= itemIndex - 1; i++) {
			final var menuEntry = mItems.get(i);
			itopPos += menuEntry.height();
		}

		final var topPosition = MathHelper.clamp(-itopPos, -(mFullContentArea.height() - contentDisplayArea().height()), 0);
		mScrollBar.AbsCurrentYPos(topPosition);
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var lSpriteBatch = mParentScreen.spriteBatch();
		final var lFontUnit = mParentScreen.font();
		final var lScreenOffset = screen.screenPositionOffset();

		lSpriteBatch.begin(core.HUD());
		final var lTileSize = 32;
		lSpriteBatch.setColorRGBA(.15f, .15f, .65f, 0.74f);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY, lTileSize, lTileSize, parentZDepth);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, lScreenOffset.x + mX + lTileSize, lScreenOffset.y + mY, mW - lTileSize * 2, lTileSize, parentZDepth);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, lScreenOffset.x + mX + mW - lTileSize, lScreenOffset.y + mY, lTileSize, lTileSize, parentZDepth);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY + lTileSize, lTileSize, mH - lTileSize * 2, parentZDepth);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER, lScreenOffset.x + mX + lTileSize, lScreenOffset.y + mY + lTileSize, mW - lTileSize * 2, mH - 64, parentZDepth);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT, lScreenOffset.x + mX + mW - lTileSize, lScreenOffset.y + mY + lTileSize, lTileSize, mH - lTileSize * 2, parentZDepth);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY + mH - lTileSize, lTileSize, lTileSize, parentZDepth);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, lScreenOffset.x + mX + lTileSize, lScreenOffset.y + mY + mH - lTileSize, mW - lTileSize * 2, lTileSize, parentZDepth);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, lScreenOffset.x + mX + mW - lTileSize, lScreenOffset.y + mY + mH - lTileSize, lTileSize, lTileSize, parentZDepth);
		lSpriteBatch.end();

		Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mX, mY + 2, mW, mH - 4);

		StencilHelper.preDraw(core, lSpriteBatch, mX, mY + 2, mW, mH - 4, -0, 55);

		lFontUnit.begin(core.HUD());
		lSpriteBatch.begin(core.HUD());
		for (int i = 0; i < mItems.size(); i++) {
			mItems.get(i).draw(core, screen, lSpriteBatch, mCoreSpritesheet, lFontUnit, parentZDepth, mSelectedItemIndex == i, mIsInputActive && mSelectedItemIndex == i);
		}

		lSpriteBatch.end();
		lFontUnit.end();

		drawDebugCollidableBounds(core, lSpriteBatch);
		if (mScrollBar.scrollBarEnabled()) {
			lSpriteBatch.begin(core.HUD());

			mScrollBar.positionOffset.x = lScreenOffset.x;
			mScrollBar.positionOffset.y = lScreenOffset.y;

			mScrollBar.scrollBarAlpha(1.f);

			mScrollBar.draw(core, lSpriteBatch, mCoreSpritesheet, parentZDepth);
			lSpriteBatch.end();
		}

		if (mHasFocus && !mIsInputActive)
			renderHighlight(core, screen, lSpriteBatch);

		StencilHelper.postDraw(core);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			for (int i = 0; i < mItems.size(); i++) {
				Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mItems.get(i));
			}
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mIsInputActive = !mIsInputActive;
		if (mIsInputActive) {
			mParentScreen.onMenuEntryActivated(this);
			resetCoolDownTimer();
		} else {
			mParentScreen.onMenuEntryDeactivated(this);
		}

	}

	@Override
	public void onDeactivation(InputManager inputManager) {
		super.onDeactivation(inputManager);

		mIsInputActive = false;
	}

	public void addEntry(MenuListBoxItem item) {
		if (!mItems.contains(item)) {
			mItems.add(item);
		}

		if (mSelectedItemIndex < 0)
			mSelectedItemIndex = 0; // Select the first item by default
	}

	public void removeEntry(MenuListBoxItem item) {
		if (mItems.contains(item)) {
			mItems.remove(item);
		}
	}

	public MenuListBoxItem getSelectedItem() {
		if (mSelectedItemIndex < 0 || mSelectedItemIndex >= mItems.size())
			return null;

		return mItems.get(mSelectedItemIndex);
	}

	public boolean isItemSelected() {
		return mSelectedItemIndex < 0 || mSelectedItemIndex >= mItems.size();
	}

	public void clearListBox() {
		mItems.clear();
	}

	public void setItemSelectedListener(IListBoxItemSelected listBoxItemSelected) {
		mSelecterListener = listBoxItemSelected;
	}

	public void setItemDoubleClickListener(IListBoxItemDoubleClick listBoxItemDoubleClick) {
		mItemDoubleClickListener = listBoxItemDoubleClick;
	}

	// --------------------------------------
	// IScrollBarArea Methods
	// --------------------------------------

	@Override
	public Rectangle contentDisplayArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mFullContentArea;
	}

	@Override
	public void onViewportChange(float width, float height) {
		super.onViewportChange(width, height);

		if (mScrollBar != null) {
			mScrollBar.resetBarTop();
		}
	}
}

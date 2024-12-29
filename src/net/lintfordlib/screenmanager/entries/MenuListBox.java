package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
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
	protected ScrollBarContentRectangle mContentArea;
	protected ScrollBar mScrollBar;
	protected float mLastMouseYPos;
	protected IListBoxItemSelected mSelecterListener;
	protected IListBoxItemDoubleClick mItemDoubleClickListener;
	protected int mSelectedItemIndex = -1;
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

	@Override
	public float height() {
		return mH;
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

		mContentArea = new ScrollBarContentRectangle(this);

		mScrollBar = new ScrollBar(this, new Rectangle(mX + mW - ScrollBar.BAR_WIDTH, mY, 20, mH));

		mLeftMargin = 10f;
		mRightMargin = 10f;
		mTopMargin = 10f;
		mBottomMargin = 10f;

		mMinWidth = LISTBOX_MIN_WIDTH;
		mMaxWidth = LISTBOX_MAX_WIDTH;
		mMaxHeight = 400;
		mMaxHeight = 1000;

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
	public void update(LintfordCore core, MenuScreen screen) {
		final int lCount = mItems.size();
		float mItemYPos = 0;

		float lTotalContentHeight = marginTop() + marginBottom();
		for (int i = 0; i < lCount; i++) {
			final var lItem = mItems.get(i);

			if (i == mSelectedItemIndex) {
				lItem.entryColor.setRGBA(1.f, .44f, .1f, 0.4f);
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

		mContentArea.set(this);

		if (mVerticalFillType == FILLTYPE.FILL_CONTAINER || mVerticalFillType == FILLTYPE.TAKE_WHATS_NEEDED)
			mContentArea.height(lTotalContentHeight);

		mScrollBar.scrollBarEnabled(mContentArea.height() - mH > 0);
		mScrollBar.update(core);
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var lSpriteBatch = mParentScreen.spriteBatch();
		final var lFontUnit = mParentScreen.font();
		final var lScreenOffset = screen.screenPositionOffset();

		lSpriteBatch.begin(core.HUD());
		final var lTileSize = 32;
		final var lBackgroundColor = ColorConstants.getColor(.15f, .15f, .65f, 0.74f);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY, lTileSize, lTileSize, parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, lScreenOffset.x + mX + lTileSize, lScreenOffset.y + mY, mW - lTileSize * 2, lTileSize, parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, lScreenOffset.x + mX + mW - lTileSize, lScreenOffset.y + mY, lTileSize, lTileSize, parentZDepth, lBackgroundColor);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY + lTileSize, lTileSize, mH - lTileSize * 2, parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER, lScreenOffset.x + mX + lTileSize, lScreenOffset.y + mY + lTileSize, mW - lTileSize * 2, mH - 64, parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT, lScreenOffset.x + mX + mW - lTileSize, lScreenOffset.y + mY + lTileSize, lTileSize, mH - lTileSize * 2, parentZDepth, lBackgroundColor);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY + mH - lTileSize, lTileSize, lTileSize, parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, lScreenOffset.x + mX + lTileSize, lScreenOffset.y + mY + mH - lTileSize, mW - lTileSize * 2, lTileSize, parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, lScreenOffset.x + mX + mW - lTileSize, lScreenOffset.y + mY + mH - lTileSize, lTileSize, lTileSize, parentZDepth, lBackgroundColor);
		lSpriteBatch.end();

		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0x0F); // Write to stencil buffer

		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		// Fill in the renderable parts of the list box (this is needed!)
		lSpriteBatch.begin(core.HUD());
		final var lColor = ColorConstants.getWhiteWithAlpha(0.f);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_BLACK, mX, mY + 2, mW, mH - 4, parentZDepth, lColor);
		lSpriteBatch.end();

		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

		lFontUnit.begin(core.HUD());
		lSpriteBatch.begin(core.HUD());
		for (int i = 0; i < mItems.size(); i++)
			mItems.get(i).draw(core, screen, lSpriteBatch, mCoreSpritesheet, lFontUnit, parentZDepth, mSelectedItemIndex == i);

		lSpriteBatch.end();
		lFontUnit.end();

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		drawDebugCollidableBounds(core, lSpriteBatch);
		if (mScrollBar.scrollBarEnabled()) {
			lSpriteBatch.begin(core.HUD());

			mScrollBar.positionOffset.x = lScreenOffset.x;
			mScrollBar.positionOffset.y = lScreenOffset.y;

			mScrollBar.scrollBarAlpha(1.f);

			mScrollBar.draw(core, lSpriteBatch, mCoreSpritesheet, parentZDepth);
			lSpriteBatch.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			for (int i = 0; i < mItems.size(); i++) {
				Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mItems.get(i));
			}
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addEntry(MenuListBoxItem item) {
		if (!mItems.contains(item)) {
			mItems.add(item);
		}
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
		return mContentArea;
	}

	@Override
	public void onViewportChange(float width, float height) {
		super.onViewportChange(width, height);

		if (mScrollBar != null) {
			mScrollBar.resetBarTop();
		}
	}
}

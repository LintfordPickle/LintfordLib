package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.IListBoxItemDoubleClick;
import net.lintford.library.screenmanager.IListBoxItemSelected;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class ListBox extends MenuEntry implements IScrollBarArea {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6606453352329315889L;

	// TODO: Replace this with the value from the IListBoxItem instances
	public static int LISTBOX_HEIGHT = 350;
	public static float LISTBOX_ITEM_VPADDING = 15; // The amound of space vertically between items

	public static final float LISTBOX_MIN_WIDTH = 400;
	public static final float LISTBOX_MAX_WIDTH = 1024;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<ListBoxItem> mItems;
	protected ScrollBarContentRectangle mContentArea;
	protected ScrollBar mScrollBar;
	protected float mLastMouseYPos;
	protected IListBoxItemSelected mSelecterListener;
	protected IListBoxItemDoubleClick mItemDoubleClickListener;
	protected int mSelectedItem = -1;
	protected boolean mClickActive;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int selectedIndex() {
		return mSelectedItem;
	}

	public void selectedIndex(int i) {
		if (mSelecterListener != null && (i >= 0 && i < mItems.size())) {
			ListBoxItem litem = mItems.get(i);
			mSelecterListener.onListBoxItemSelected(litem, i);

		}

		mSelectedItem = i;
	}

	public void itemDoubleClicked(int i) {
		if (mItemDoubleClickListener != null && (i >= 0 && i < mItems.size())) {
			final var lItem = mItems.get(i);
			if (mSelecterListener != null) {
				mSelecterListener.onListBoxItemSelected(lItem, i);
			}

			mItemDoubleClickListener.onListItemDoubleClicked(lItem);
		}

		mSelectedItem = i;
	}

	public List<ListBoxItem> items() {
		return mItems;
	}

	@Override
	public float height() {
		return mH;// mContentArea.h;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBox(ScreenManager screenManager, BaseLayout parentLayout, String menuEntryLabel) {
		super(screenManager, parentLayout, menuEntryLabel);

		mItems = new ArrayList<>();

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
	public boolean handleInput(LintfordCore core) {
		final var lMouseHUDCoords = core.HUD().getMouseCameraSpace();

		if (!mScrollBar.clickAction() && !intersectsAA(lMouseHUDCoords) && core.input().mouse().isMouseLeftClick(hashCode()))
			return false;

		if (mScrollBar.handleInput(core, mScreenManager)) {
			mClickActive = false;
			return true;
		}

		// Check each of the items if they havae captured mouse input
		for (int i = 0; i < mItems.size(); i++) {
			boolean lResult = mItems.get(i).handleInput(core);
			// FIXME: Double check - if an item was click
			if (lResult)
				return true;

			if (core.input().mouse().isMouseLeftButtonDown())
				mSelectedItem = -1;

		}

		if (!core.input().mouse().isMouseLeftButtonDown()) {
			mClickActive = false;
			return false;
		}

		if (!core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
			mClickActive = false;
			return false;
		}

		if (!mClickActive) {
			mClickActive = true;
			mLastMouseYPos = lMouseHUDCoords.y;
			return true;
		}

		return false;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen, boolean isSelected) {
		final int lCount = mItems.size();
		float mItemYPos = 0;

		float lTotalContentHeight = marginTop() + marginBottom();
		for (int i = 0; i < lCount; i++) {
			ListBoxItem lItem = mItems.get(i);
			mItems.get(i).update(core, screen, isSelected);

			final float lInnerPadding = mScrollBar.scrollBarEnabled() ? mScrollBar.width() : 0;
			mItems.get(i).width(mW - marginLeft() - marginRight() - lInnerPadding);
			mItems.get(i).setPosition(mX + marginLeft(), mY + marginTop() + mScrollBar.currentYPos() + mItemYPos);

			mItemYPos += lItem.height() + LISTBOX_ITEM_VPADDING;
			lTotalContentHeight += lItem.height() + LISTBOX_ITEM_VPADDING;
		}

		if (mVerticalFillType == FILLTYPE.FILL_CONTAINER || mVerticalFillType == FILLTYPE.TAKE_WHATS_NEEDED)
			mContentArea.height(lTotalContentHeight);

		mScrollBar.scrollBarEnabled(mContentArea.height() - mH > 0);
		mScrollBar.update(core);
	}

	@Override
	public void draw(LintfordCore core, Screen screen, boolean isSelected, float parentZDepth) {
		final var lSpriteBatch = mParentLayout.parentScreen.spriteBatch();

		final var lScreenOffset = screen.screenPositionOffset();
		final float lTileSize = 32;

		lSpriteBatch.begin(core.HUD());
		final var lBackgroundColor = ColorConstants.getColor(.15f, .15f, .15f, 0.4f);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, (int) (lScreenOffset.x + mX), (int) (lScreenOffset.y + mY), lTileSize, lTileSize, parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, (int) (lScreenOffset.x + mX + lTileSize), (int) (lScreenOffset.y + mY), (int) mW - lTileSize * 2, lTileSize, parentZDepth,
				lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, (int) (lScreenOffset.x + mX + (int) mW - lTileSize), (int) (lScreenOffset.y + mY), lTileSize, lTileSize, parentZDepth,
				lBackgroundColor);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT, (int) (lScreenOffset.x + mX), (int) (lScreenOffset.y + mY + lTileSize), lTileSize, (int) mH - lTileSize * 2, parentZDepth,
				lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER, (int) (lScreenOffset.x + mX + lTileSize), (int) (lScreenOffset.y + mY + lTileSize), (int) mW - lTileSize * 2, (int) mH - 64,
				parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT, (int) (lScreenOffset.x + mX + (int) mW - lTileSize), (int) (lScreenOffset.y + mY + lTileSize), lTileSize,
				(int) mH - lTileSize * 2, parentZDepth, lBackgroundColor);

		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, (int) (lScreenOffset.x + mX), (int) (lScreenOffset.y + (int) mY + (int) mH - lTileSize), lTileSize, lTileSize, parentZDepth,
				lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, (int) (lScreenOffset.x + mX + lTileSize), (int) (lScreenOffset.y + mY + (int) mH - lTileSize), mW - lTileSize * 2, lTileSize,
				parentZDepth, lBackgroundColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, (int) (lScreenOffset.x + mX + (int) mW - lTileSize), (int) (lScreenOffset.y + (int) mY + (int) mH - lTileSize), lTileSize,
				lTileSize, parentZDepth, lBackgroundColor);
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

		for (int i = 0; i < mItems.size(); i++)
			mItems.get(i).draw(core, screen, lSpriteBatch, mSelectedItem == mItems.get(i).mItemIndex, parentZDepth);

		if (mScrollBar.scrollBarEnabled()) {
			lSpriteBatch.begin(core.HUD());
			mScrollBar.draw(core, lSpriteBatch, mCoreSpritesheet, parentZDepth);
			lSpriteBatch.end();
		}

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addEntry(ListBoxItem item) {
		if (!mItems.contains(item)) {
			mItems.add(item);
		}
	}

	public void removeEntry(ListBoxItem item) {
		if (mItems.contains(item)) {
			mItems.remove(item);
		}
	}

	@Override
	public void onClick(InputManager inputManager) {

	}

	public ListBoxItem getSelectedItem() {
		for (int i = 0; i < mItems.size(); i++) {
			if (mItems.get(i).mItemIndex == mSelectedItem)
				return mItems.get(i);
		}

		return null;
	}

	public boolean isItemSelected() {
		if (mSelectedItem == -1)
			return false;

		for (int i = 0; i < mItems.size(); i++) {
			if (mItems.get(i).mItemIndex == mSelectedItem)
				return true;
		}

		return false;
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

package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.core.maths.Vector2f;
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
	protected float mZScrollAcceleration;
	protected float mZScrollVelocity;
	protected float mYScrollPos;

	protected float mLastMouseYPos;
	protected boolean mScrollBarsEnabled;
	protected IListBoxItemSelected mSelecterListener;
	protected IListBoxItemDoubleClick mItemDoubleClickListener;

	protected int mSelectedItem = -1;

	protected boolean mClickActive; // Clicked within the listbox (and dragging, i.e. for scrolling)

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

	public float getYScrollPosition() {
		return mYScrollPos;
	}

	public List<ListBoxItem> items() {
		return mItems;
	}

	@Override
	public float height() {
		return h;// mContentArea.h;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBox(ScreenManager pScreenManager, BaseLayout pParentLayout, String pMenuEntryLabel) {
		super(pScreenManager, pParentLayout, pMenuEntryLabel);

		mItems = new ArrayList<>();

		mContentArea = new ScrollBarContentRectangle(this);

		mScrollBar = new ScrollBar(this, new Rectangle(x + w - ScrollBar.BAR_WIDTH, y, 20, h));

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
	public boolean handleInput(LintfordCore pCore) {
		final Vector2f lMouseHUDCoords = pCore.HUD().getMouseCameraSpace();

		// Check if the mouse is clicked outside of this list box
		// In this case, we shouldn't un-select the current selected entry, otherwise we'd never be able to click a button outside of this list box
		if (!mScrollBar.clickAction() && !intersectsAA(lMouseHUDCoords) && pCore.input().mouse().isMouseLeftClick(hashCode())) {
			return false;
		}

		if (mScrollBar.handleInput(pCore)) {
			mClickActive = false;
			return true;
		}

		// Check each of the items if they havae captured mouse input
		for (int i = 0; i < mItems.size(); i++) {
			boolean lResult = mItems.get(i).handleInput(pCore);
			// FIXME: Double check - if an item was click
			if (lResult) {
				return true;
			}

			if (pCore.input().mouse().isMouseLeftButtonDown()) {
				mSelectedItem = -1;
			}
		}

		/// Scrolling ///

		if (intersectsAA(lMouseHUDCoords) && pCore.input().mouse().tryAcquireMouseMiddle(hashCode())) {
			mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

		}

		if (!pCore.input().mouse().isMouseLeftButtonDown()) {
			mClickActive = false;

			return false;
		}

		if (!pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
			mClickActive = false;
			return false;

		}

		if (!mClickActive) {
			mClickActive = true;
			mLastMouseYPos = lMouseHUDCoords.y;
			return true;
		}

		// Allow us to scroll the listbox by clicking and dragging within its bounds
		final float lMaxDiff = mContentArea.h() - h;

		// if()

		if (mClickActive) {
			if (lMaxDiff > 0) {
				float lDiffY = lMouseHUDCoords.y - mLastMouseYPos;
				mYScrollPos += lDiffY;

				if (mYScrollPos < -lMaxDiff - LISTBOX_ITEM_VPADDING)
					mYScrollPos = -lMaxDiff - LISTBOX_ITEM_VPADDING;
				if (mYScrollPos > 0)
					mYScrollPos = 0;

				mLastMouseYPos = lMouseHUDCoords.y;

				return true;
			}

		}

		if (lMaxDiff <= 0) {
			mYScrollPos = 0;
		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		mScrollBarsEnabled = mContentArea.h() - h > 0;

		if (mContentArea.h() < h)
			mYScrollPos = 0;

		int lCount = mItems.size();
		float mItemYPos = 0;

		float lTotalContentHeight = marginTop() + marginBottom();
		for (int i = 0; i < lCount; i++) {
			ListBoxItem lItem = mItems.get(i);
			mItems.get(i).update(pCore, pScreen, pIsSelected);

			// We need an innerpadding for the case when the scrollbar is enabled. In that case
			// we narrow the size of the WorldListItem.
			final float lInnerPadding = mScrollBarsEnabled ? 25 : 0;
			mItems.get(i).w(w - marginLeft() - marginRight() - lInnerPadding);

			mItems.get(i).setPosition(x + marginLeft(), y + marginTop() + mYScrollPos + mItemYPos);

			mItemYPos += lItem.h() + LISTBOX_ITEM_VPADDING;
			lTotalContentHeight += lItem.h() + LISTBOX_ITEM_VPADDING;
		}

		// mContentArea.w = w;
		if (mVerticalFillType == FILLTYPE.FILL_CONTAINER || mVerticalFillType == FILLTYPE.TAKE_WHATS_NEEDED)
			mContentArea.h(lTotalContentHeight);

		final float lDeltaTime = (float) pCore.appTime().elapsedTimeMilli() / 1000f;
		float lScrollSpeedFactor = mYScrollPos;

		mZScrollVelocity += mZScrollAcceleration;
		lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
		mZScrollVelocity *= 0.85f;
		mZScrollAcceleration = 0.0f;
		mYScrollPos = lScrollSpeedFactor;

		// Constrain
		if (mYScrollPos > 0)
			mYScrollPos = 0;
		if (mYScrollPos < -(mContentArea.h() - this.h)) {
			mYScrollPos = -(mContentArea.h() - this.h);
		}

		mScrollBar.update(pCore);

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		final var lSpriteBatch = mParentLayout.parentScreen.spriteBatch();

		// We need to use a stencil buffer to clip the list box items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0x0F); // Write to stencil buffer

		// Make sure we are starting with a fresh stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		// Fill in the renderable parts of the list box (this is needed!)
		lSpriteBatch.begin(pCore.HUD());
		final var lColor = ColorConstants.getWhiteWithAlpha(0.f);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_BLACK, x, y + marginTop(), w, h - marginTop() - marginBottom(), pParentZDepth, lColor);
		lSpriteBatch.end();

		// Start the stencil buffer test to filter out everything outside of the scroll view
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

		lSpriteBatch.begin(pCore.HUD());
		final var lBackgroundColor = ColorConstants.getColor(.15f, .15f, .15f, 0.4f);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, this, pParentZDepth, lBackgroundColor);
		lSpriteBatch.end();

		for (int i = 0; i < mItems.size(); i++) {
			mItems.get(i).draw(pCore, pScreen, lSpriteBatch, mSelectedItem == mItems.get(i).mItemIndex, pParentZDepth);
		}

		if (mScrollBarsEnabled) {
			lSpriteBatch.begin(pCore.HUD());
			mScrollBar.draw(pCore, lSpriteBatch, mCoreSpritesheet, pParentZDepth);
			lSpriteBatch.end();
		}

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		drawDebugCollidableBounds(pCore, lSpriteBatch);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addEntry(ListBoxItem pItem) {
		if (!mItems.contains(pItem)) {
			mItems.add(pItem);
		}
	}

	public void removeEntry(ListBoxItem pItem) {
		if (mItems.contains(pItem)) {
			mItems.remove(pItem);
		}
	}

	@Override
	public void onClick(InputManager pInputState) {

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

	public void setItemSelectedListener(IListBoxItemSelected pListBoxItemSelected) {
		mSelecterListener = pListBoxItemSelected;

	}

	public void setItemDoubleClickListener(IListBoxItemDoubleClick pListBoxItemDoubleClick) {
		mItemDoubleClickListener = pListBoxItemDoubleClick;

	}

	// --------------------------------------
	// IScrollBarArea Methods
	// --------------------------------------

	@Override
	public float currentYPos() {
		return mYScrollPos;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mYScrollPos += pAmt;

	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mYScrollPos = pValue;

	}

	@Override
	public Rectangle contentDisplayArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentArea;
	}

	@Override
	public void onViewportChange(float pWidth, float pHeight) {
		super.onViewportChange(pWidth, pHeight);

		if (mScrollBar != null) {
			mScrollBar.resetBarTop();

		}

	}

}

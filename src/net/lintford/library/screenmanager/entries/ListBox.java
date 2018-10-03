package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.BaseLayout.FILL_TYPE;

public class ListBox extends MenuEntry implements IScrollBarArea {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6606453352329315889L;

	public static int LISTBOX_HEIGHT = 350;
	public static float LISTBOX_ITEM_VPADDING = 15; // The amound of space vertically between items

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<ListBoxItem> mItems;
	protected TextureBatch mSpriteBatch;
	protected ScrollBar mScrollBar;

	protected ScrollBarContentRectangle mContentArea;
	protected float mYScrollPos;
	protected float mLastMouseYPos;
	protected boolean mScrollBarsEnabled;

	protected int mSelectedItem = -1;

	protected boolean mClickActive; // Clicked within the listbox (and dragging, i.e. for scrolling)

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int selectedIndex() {
		return mSelectedItem;
	}
	
	public void selectedIndex(int i) {
		mSelectedItem = i;
	}

	public float getYScrollPosition() {
		return mYScrollPos;
	}

	public List<ListBoxItem> items() {
		return mItems;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBox(ScreenManager pScreenManager, BaseLayout pParentLayout, String pMenuEntryLabel) {
		super(pScreenManager, pParentLayout, pMenuEntryLabel);

		mItems = new ArrayList<>();
		mSpriteBatch = new TextureBatch();

		mContentArea = new ScrollBarContentRectangle(this);

		mScrollBar = new ScrollBar(this, new Rectangle(x + w - ScrollBar.BAR_WIDTH, y, 20, h));

		mLeftMargin = 10f;
		mRightMargin = 10f;
		mTopMargin = 10f;
		mBottomMargin = 10f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		// TODO Auto-generated method stub
		super.initialise();
	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		final Vector2f lMouseHUDCoords = pCore.HUD().getMouseCameraSpace();

		// Check if the mouse is clicked outside of this list box
		// In this case, we shouldn't un-select the current selected entry, otherwise we'd never be able to click a button outside of this list box
		if (!mScrollBar.clickAction() && pCore.input().mouseLeftClick() && (lMouseHUDCoords.x < x || lMouseHUDCoords.x > x + w || lMouseHUDCoords.y < y || lMouseHUDCoords.y > y + h)) {
			return false;
		}

		for (int i = 0; i < mItems.size(); i++) {
			boolean lResult = mItems.get(i).handleInput(pCore);
			// Was this item clicked on?
			if (lResult) {
				return true;
			}

			if (pCore.input().mouseLeftClick()) {
				mSelectedItem = -1;
			}
		}

		if (mScrollBar.handleInput(pCore)) {
			mClickActive = false;
			return true;
		}

		if (!pCore.input().mouseLeftClick()) {
			mClickActive = false;

			return false;
		}

		if (!pCore.input().tryAquireLeftClickOwnership(hashCode())) {
			mClickActive = false;
			return false;

		}

		if (!mClickActive) {
			mClickActive = true;
			mLastMouseYPos = lMouseHUDCoords.y;
			return true;
		}

		// Allow us to scroll the listbox by clicking and dragging within its bounds
		final float lMaxDiff = mContentArea.h - h;

		// Scrolling
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
	public void updateStructureDimensions() {
		super.updateStructureDimensions();

		// Fill the width and height of the parent layout
		w = mParentLayout.w - marginLeft() - marginRight();
		if (mParentLayout.fillType() == FILL_TYPE.DYNAMIC)
			h = mParentLayout.h - marginTop() - marginBottom();
		else
			h = Math.min(LISTBOX_HEIGHT, (mItems.size() > 0 ? mItems.size() : 10f) * 70f);

	}

	public void update(LintfordCore pCore) {
		mScrollBarsEnabled = mContentArea.h - h > 0;

		if (mContentArea.h < h)
			mYScrollPos = 0;

		int lCount = mItems.size();
		float mItemYPos = 0;

		for (int i = 0; i < lCount; i++) {
			ListBoxItem lItem = mItems.get(i);

			lItem.w = 550;

			mItems.get(i).x = x + w / 2 - lItem.w / 2;
			mItems.get(i).y = y + 15f + mYScrollPos + mItemYPos;

			mItemYPos += lItem.h + LISTBOX_ITEM_VPADDING;

		}

		mContentArea.w = w;
		mContentArea.h = mItems.size() * (64f + LISTBOX_ITEM_VPADDING) + LISTBOX_ITEM_VPADDING;

		mScrollBar.update(pCore);
//		mScrollBar.x = 0;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {

		final float TILE_SIZE = 32f;

		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 64, TILE_SIZE, TILE_SIZE, x, y, TILE_SIZE, TILE_SIZE, pParentZDepth, 1, 1, 1, 0.85f);
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 64, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y, w - 64, TILE_SIZE, pParentZDepth, 1, 1, 1, 0.85f);
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 64, TILE_SIZE, TILE_SIZE, x + w - 32, y, TILE_SIZE, TILE_SIZE, pParentZDepth, 1, 1, 1, 0.85f);

		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 96, TILE_SIZE, TILE_SIZE, x, y + 32, TILE_SIZE, h - 64, pParentZDepth, 1, 1, 1, 0.85f);
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 96, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + 32, w - 64, h - 64, pParentZDepth, 1, 1, 1, 0.85f);
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 96, TILE_SIZE, TILE_SIZE, x + w - 32, y + 32, TILE_SIZE, h - 64, pParentZDepth, 1, 1, 1, 0.85f);

		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 128, TILE_SIZE, TILE_SIZE, x, y + h - 32, TILE_SIZE, TILE_SIZE, pParentZDepth, 1, 1, 1, 0.85f);
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 128, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, pParentZDepth, 1, 1, 1, 0.85f);
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 128, TILE_SIZE, TILE_SIZE, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, pParentZDepth, 1, 1, 1, 0.85f);
		mSpriteBatch.end();

		// We need to use a stencil buffer to clip the list box items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0xFF); // Write to stencil buffer

		// Make sure we are starting with a fresh stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		final float DEPTH_PADDING = 6f;

		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 32, 0, 32, 32, x + DEPTH_PADDING, y + DEPTH_PADDING, w - DEPTH_PADDING * 2, h - DEPTH_PADDING * 2, pParentZDepth, 1, 1, 1, 0f);
		mSpriteBatch.end();

		// Start the stencil buffer test to filter out everything outside of the scroll view
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

		for (int i = 0; i < mItems.size(); i++) {
			mItems.get(i).draw(pCore, pScreen, mSpriteBatch, mSelectedItem == mItems.get(i).mItemIndex, pParentZDepth);
		}

		if (mScrollBarsEnabled) {
			mScrollBar.draw(pCore, mSpriteBatch, pParentZDepth);

		}

		GL11.glDisable(GL11.GL_STENCIL_TEST);

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
	public void onClick(InputState pInputState) {

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

}

package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.renderers.windows.UIRectangle;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class ListBox extends MenuEntry implements IScrollBarArea {

	// --------------------------------------
	// Constants
	// --------------------------------------

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

	public void setSelectedItem(int i) {
		mSelectedItem = i;
	}

	public float getYScrollPosition() {
		return mYScrollPos;
	}

	@Override
	public float getWidth() {
		return 800;
	}

	@Override
	public float getHeight() {
		return LISTBOX_HEIGHT;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ListBox(ScreenManager pScreenManager, MenuScreen pParentScreen, String pMenuEntryLabel) {
		super(pScreenManager, pParentScreen, pMenuEntryLabel);

		mItems = new ArrayList<>();
		mSpriteBatch = new TextureBatch();

		mContentArea = new ScrollBarContentRectangle(this);

		mScrollBar = new ScrollBar(this, new UIRectangle(x + getWidth() - ScrollBar.BAR_WIDTH, y, 20, getHeight()));

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
		if (!mScrollBar.clickAction() && pCore.input().mouseLeftClick() && (lMouseHUDCoords.x < x || lMouseHUDCoords.x > x + getWidth() || lMouseHUDCoords.y < y || lMouseHUDCoords.y > y + getHeight())) {
			return false;
		}

		for (int i = 0; i < mItems.size(); i++) {
			// TODO(John): Don't like how the item.handleInput is used explicitly (only) for selecting the item and NOT
			// handling the input
			boolean lResult = mItems.get(i).handleInput(pCore);
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
		final float lMaxDiff = mContentArea.height - getHeight();

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

	public void update(LintfordCore pCore) {
		mScrollBar.update(pCore);

		mContentArea.width = width;
		mContentArea.height = mItems.size() * (ListBoxItem.LISTBOXITEM_HEIGHT + LISTBOX_ITEM_VPADDING) + LISTBOX_ITEM_VPADDING;

		mScrollBarsEnabled = mContentArea.height - getHeight() > 0;

		int lCount = mItems.size();
		float mItemYPos = 0;
		for (int i = 0; i < lCount; i++) {
			mItems.get(i).mXPos = x + width / 2 - ListBoxItem.LISTBOXITEM_WIDTH / 2;
			mItems.get(i).mYPos = mYScrollPos + mItemYPos;

			mItemYPos += ListBoxItem.LISTBOXITEM_HEIGHT + LISTBOX_ITEM_VPADDING;
		}

		width = 800;
		height = LISTBOX_HEIGHT;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {

		// We need to use a stencil buffer to clip the list box items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0xFF); // Write to stencil buffer

		// Make sure we are starting with a fresh stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		Texture lTexture = TextureManager.TEXTURE_CORE_UI;
		
		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(32, 0, 32, 32, x, y, -8f, getWidth(), getHeight(), 1.0f, 1, 1, 1, 0.75f, lTexture);
		mSpriteBatch.end();

		// Start the stencil buffer test to filter out everything outside of the scroll view
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

		mParentScreen.font().begin(pCore.HUD());

		for (int i = 0; i < mItems.size(); i++) {
			mItems.get(i).draw(pCore, pScreen, mSpriteBatch, mSelectedItem == mItems.get(i).mItemIndex, pParentZDepth);
		}

		mParentScreen.font().end();

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		if (mScrollBarsEnabled) {
			mScrollBar.draw(pCore, mSpriteBatch, pParentZDepth);
		}

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
	public UIRectangle windowArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle contentArea() {
		return mContentArea;
	}

}

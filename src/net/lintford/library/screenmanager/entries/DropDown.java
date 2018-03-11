package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class DropDown extends MenuEntry {

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

	protected float mListBoxInnerHeight; // The height of all items + vpadding
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
		return 250;
	}

	@Override
	public float getHeight() {
		return 20;
	}

	public float getContentHeight() {
		return mListBoxInnerHeight + DropDown.LISTBOX_ITEM_VPADDING;

	}

	public float getViewportHeight() {
		return getHeight();

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DropDown(ScreenManager pScreenManager, MenuScreen pParentScreen, String pMenuEntryLabel) {
		super(pScreenManager, pParentScreen, pMenuEntryLabel);

		mItems = new ArrayList<>();
		mSpriteBatch = new TextureBatch();

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
		final float lMouseX = pCore.input().mouseWindowCoords().x;
		final float lMouseY = pCore.input().mouseWindowCoords().y;

		// Culling
		if (pCore.input().mouseLeftClick() && (lMouseX < x || lMouseX > x + getWidth() || lMouseY < y || lMouseY > y + getHeight())) {
			return false;
		}

		// TODO: Selection
		for (int i = 0; i < mItems.size(); i++) {
			boolean lResult = mItems.get(i).handleInput(pCore);
			if (lResult)
				return true;

			if (pCore.input().mouseLeftClick()) {
				mSelectedItem = -1;
			}
		}

		if (!pCore.input().mouseLeftClick())
			return false;
		if (!pCore.input().tryAquireLeftClickOwnership(hashCode()))
			return false;

		if (!mClickActive) {
			mClickActive = true;
			mLastMouseYPos = lMouseY;
		}

		// Allow us to scroll the listbox by clicking and dragging within its bounds
		final float lMaxDiff = mListBoxInnerHeight - getHeight();

		// Scrolling
		if (mClickActive) {
			if (lMaxDiff > 0) {
				float lDiffY = lMouseY - mLastMouseYPos;
				mYScrollPos += lDiffY;

				if (mYScrollPos < -lMaxDiff - LISTBOX_ITEM_VPADDING)
					mYScrollPos = -lMaxDiff - LISTBOX_ITEM_VPADDING;
				if (mYScrollPos > 0)
					mYScrollPos = 0;

				mLastMouseYPos = lMouseY;

				return true;
			}

		} else {
			mClickActive = false;

		}

		if (lMaxDiff <= 0) {
			mYScrollPos = 0;
		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {

		mListBoxInnerHeight = mItems.size() * (ListBoxItem.LISTBOXITEM_HEIGHT + LISTBOX_ITEM_VPADDING);
		mScrollBarsEnabled = mListBoxInnerHeight - getHeight() > 0;

		updateListBoxItemPositions();

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {

		// We need to use a stencil buffer to clip the listbox items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0xFF); // Write to stencil buffer

		// Make sure we are starting with a fresh stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, getWidth(), getHeight(), pParentZDepth + .1f, 0.23f, 0.12f, 0.12f, 0.5f);
		mSpriteBatch.end();

		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

		for (int i = 0; i < mItems.size(); i++) {
			mItems.get(i).draw(pCore, pScreen, mSpriteBatch, mSelectedItem == mItems.get(i).mItemIndex, pParentZDepth);
		}

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		if (mScrollBarsEnabled) {
			// TODO(John): Need to implement scroll bars
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateListBoxItemPositions() {
		int lCount = mItems.size();
		float mItemYPos = 0;
		for (int i = 0; i < lCount; i++) {
			mItems.get(i).mYPos = mYScrollPos + mItemYPos;

			mItemYPos += ListBoxItem.LISTBOXITEM_HEIGHT + LISTBOX_ITEM_VPADDING;
		}
	}

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

}

package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.BitmapFont;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

/**  */
public class MenuDropDownEntry<T> extends MenuEntry implements IScrollBarArea {

	private static final long serialVersionUID = -2874418532803740656L;

	public class MenuEnumEntryItem {
		public String name;
		public T value;

		public MenuEnumEntryItem(String pName, T pValue) {
			name = pName;
			value = pValue;

		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float OPEN_HEIGHT = 200;
	private static final float SPACE_BETWEEN_TEXT = 15;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;

	private int mSelectedIndex;
	private List<MenuEnumEntryItem> mItems;
	private TextureBatch mTextureBatch;

	private transient boolean mOpen;

	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBarContentRectangle mWindowRectangle;
	private transient ScrollBar mScrollBar;
	private transient float mScrollYPosition;

	private Rectangle mTopEntry;
	private boolean mAllowDuplicates;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean allowDuplicates() {
		return mAllowDuplicates;
	}

	public void allowDuplicates(boolean pNewValue) {
		mAllowDuplicates = pNewValue;
	}

	public List<MenuEnumEntryItem> items() {
		return mItems;
	}

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	@Override
	public float getHeight() {
		return mOpen ? MENUENTRY_HEIGHT : MENUENTRY_HEIGHT;
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		super.hasFocus(pNewValue);

		if (!mFocusLocked && !pNewValue)
			mOpen = pNewValue;

	}

	public MenuEnumEntryItem selectedItem() {
		if (mItems == null || mItems.size() == 0)
			return null;

		return mItems.get(mSelectedIndex);

	}

	public void setSelectedEntry(int pIndex) {
		if (pIndex < 0)
			pIndex = 0;
		if (pIndex >= mItems.size())
			pIndex = mItems.size() - 1;

		mSelectedIndex = pIndex;

	}

	public void setSelectedEntry(String pName) {
		final int COUNT = mItems.size();
		for (int i = 0; i < COUNT; i++) {
			if (mItems.get(i).name.equals(pName)) {
				mSelectedIndex = i;
				return;
			}
		}

	}

	public void setSelectEntry(T pValue) {
		final int COUNT = mItems.size();
		for (int i = 0; i < COUNT; i++) {
			if (mItems.get(i).value.equals(pValue)) {
				mSelectedIndex = i;
				return;
			}
		}
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuDropDownEntry(ScreenManager pScreenManager, MenuScreen pParentScreen, String pLabel) {
		super(pScreenManager, pParentScreen, "");

		mItems = new ArrayList<>();
		mLabel = pLabel;

		mActive = true;
		mOpen = false;

		mContentRectangle = new ScrollBarContentRectangle(this);
		mWindowRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentRectangle);
		mTopEntry = new Rectangle();

		mTextureBatch = new TextureBatch();

		mSelectedIndex = 0;

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTextureBatch.unloadGLContent();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mItems == null || mItems.size() == 0)
			return false;

		if (mScrollBar.handleInput(pCore)) {

		} else if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {

			//
			mParentScreen.setHoveringOn(this);

			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (mEnabled) {

					if (mOpen) {
						// TODO: play the menu clicked sound
						final int lConsoleLineHeight = 25;
						// Something inside the dropdown was select
						float lRelativeheight = pCore.HUD().getMouseCameraSpace().y - y - mScrollYPosition;

						int lRelativeIndex = (int) (lRelativeheight / lConsoleLineHeight);
						int lSelectedIndex = lRelativeIndex - 1;

						if (lSelectedIndex < 0)
							lSelectedIndex = 0;
						if (lSelectedIndex >= mItems.size())
							lSelectedIndex = mItems.size() - 1;

						mSelectedIndex = lSelectedIndex;

						if (mClickListener != null) {
							mClickListener.menuEntryChanged(this);
						}

					}

					mOpen = !mOpen;

					mParentScreen.setFocusOn(pCore, this, true);

					pCore.input().setLeftMouseClickHandled();

				}

			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.time().elapseGameTimeMilli();
				
			}

			return true;

		} else {
			mToolTipTimer = 0;

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		if (mItems == null || mItems.size() == 0)
			return;

		super.update(pCore, pScreen, pIsSelected);

		mTopEntry.setCenter(x + w / 2, y, w / 2, MENUENTRY_HEIGHT);

		// w = MENUENTRY_DEF_BUTTON_WIDTH;
		h = mOpen ? OPEN_HEIGHT : MENUENTRY_HEIGHT;

		mContentRectangle.set(x, y + mScrollYPosition, w, mItems.size() * 25);
		// We need to offset the window rectangle so it doesn't obscure the current selected item
		mWindowRectangle.set(x + w / 2, y + 25, w / 2, h - 50);

		mScrollBar.update(pCore);
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pComponentDepth) {

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		// TITLE BAR
		mZ = mOpen ? ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_ACTIVE : ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_PASSIVE;

		// Render the label
		float lTextR = mEnabled ? mParentScreen.r() : 0.24f;
		float lTextG = mEnabled ? mParentScreen.g() : 0.24f;
		float lTextB = mEnabled ? mParentScreen.b() : 0.24f;

		final String lSeparator = " : ";

		final float lLabelWidth = lFontBitmap.getStringWidth(mLabel);
		final float lSeparatorHalfWidth = lFontBitmap.getStringWidth(lSeparator) * 0.5f;

		mParentScreen.font().begin(pCore.HUD());
		mParentScreen.font().draw(mLabel, x + w / 2 - 10 - lLabelWidth - lSeparatorHalfWidth, y, mZ, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);
		mParentScreen.font().draw(lSeparator, x + w / 2 - lSeparatorHalfWidth, y, mZ, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);

		if (mItems == null || mItems.size() == 0) {
			// LOCALIZATION: No entries added to dropdown list
			final String lNoEntriesText = "No items found";
			mParentScreen.font().draw(lNoEntriesText, x + w / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT, y, mZ, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);
			mParentScreen.font().end();
			return;
		}

		MenuEnumEntryItem lSelectItem = mItems.get(mSelectedIndex);
		float lYPos = y + mScrollYPosition;
		// Render the selected item in the 'top spot'
		final String lCurItem = lSelectItem.name;
		final float lSelectedTextWidth = mParentScreen.font().bitmap().getStringWidth(lCurItem);
		mParentScreen.font().draw(lCurItem, x + (w / 4 * 3) + -lSelectedTextWidth / 2, y, mZ, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);
		lYPos += 25;

		mParentScreen.font().end();

		mParentScreen.font().begin(pCore.HUD());

		// CONTENT PANE

		if (mOpen) {
			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 96, 0, 32, 32, mWindowRectangle, mZ, 1, 1, 1, 1);
			mTextureBatch.end();

		}

		// We need to use a stencil buffer to clip the list box items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0xFF); // Write to stencil buffer

		// Make sure we are starting with a fresh stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 32, 0, 32, 32, mWindowRectangle, -8f, 1, 1, 1, 0);
		mTextureBatch.end();

		// Start the stencil buffer test to filter out everything outside of the scroll view
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

		if (mOpen) {
			for (int i = 0; i < mItems.size(); i++) {
				MenuEnumEntryItem lItem = mItems.get(i);
				final float lItemTextWidth = mParentScreen.font().bitmap().getStringWidth(lItem.name);
				mParentScreen.font().draw(lItem.name, x + (w / 4 * 3) - lItemTextWidth / 2, lYPos, mZ + 0.1f, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);
				lYPos += 25;

			}

		}

		mParentScreen.font().end();

		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		if (mOpen && mScrollBar.areaNeedsScrolling())
			mScrollBar.draw(pCore, mTextureBatch, -0.1f);

	}

	@Override
	public void onClick(InputState pInputState) {
		super.onClick(pInputState);

	}

	public void addItem(MenuEnumEntryItem pItem) {
		if (mAllowDuplicates) {
			mItems.add(pItem);

			return;
		}

		// Proceed in the case that duplicates are not allowed
		final int COUNT = mItems.size();
		if (mItems.contains(pItem)) {
			return;

		}

		for (int i = 0; i < COUNT; i++) {
			if (mItems.get(i).name.equals(pItem.name)) {
				return;
			}

		}

		mItems.add(pItem);

	}

	// --------------------------------------
	// Implemented Methods
	// --------------------------------------

	@Override
	public float currentYPos() {
		return mScrollYPosition;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mScrollYPosition += pAmt;

	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mScrollYPosition = pValue;

	}

	@Override
	public Rectangle contentDisplayArea() {
		return mWindowRectangle;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		// mContentRectangle.set(x, y, w, mEntries.size() * 25);
		return mContentRectangle;

	}

	public void clearItems() {
		mSelectedIndex = 0;
		mItems.clear();

	}

}
package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.fonts.BitmapFont;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuEnumEntryIndexed<T> extends MenuEntry {

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

	private static final float SPACE_BETWEEN_TEXT = 15;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private boolean mIsChecked;
	private final String mSeparator = " : ";
	private List<MenuEnumEntryItem> mItems;
	private int mSelectedIndex;

	private boolean mButtonsEnabled;
	private AARectangle mLeftButtonRectangle;
	private AARectangle mRightButtonRectangle;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<MenuEnumEntryItem> items() {
		return mItems;
	}

	public void setListener(EntryInteractions pListener) {
		mClickListener = pListener;
	}

	public void setButtonsEnabled(boolean pNewValue) {
		mButtonsEnabled = pNewValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public MenuEnumEntryItem selectedItem() {
		return mItems.get(mSelectedIndex);

	}

	public int selectedEntry() {
		return mSelectedIndex;
	}

	public String selectedEntryName() {
		return mItems.get(mSelectedIndex).name;

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

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean pNewValue) {
		mIsChecked = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEnumEntryIndexed(ScreenManager pScreenManager, MenuScreen pParentScreen, String pLabel) {
		super(pScreenManager, pParentScreen, "");

		mLabel = pLabel;
		mItems = new ArrayList<>();
		mSelectedIndex = 0;

		mLeftButtonRectangle = new AARectangle(0, 0, 25, 25);
		mRightButtonRectangle = new AARectangle(0, 0, 25, 25);

		mHighlightOnHover = false;
		mDrawBackground = false;

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		if (mButtonsEnabled) {
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (mLeftButtonRectangle.intersects(pCore.HUD().getMouseCameraSpace())) {
					mSelectedIndex--;
					if (mSelectedIndex < 0) {
						mSelectedIndex = mItems.size() - 1;
					}

					mParentScreen.setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					pCore.input().setLeftMouseClickHandled();

					return true;
				}

				else if (mRightButtonRectangle.intersects(pCore.HUD().getMouseCameraSpace())) {

					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					mParentScreen.setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					pCore.input().setLeftMouseClickHandled();

					return true;
				}

			}

		}

		if (intersects(pCore.HUD().getMouseCameraSpace())) {
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				if (mEnabled) {

					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					// TODO: Play a menu click sound

					mParentScreen.setFocusOn(pCore, this, true);
					// mParentScreen.setHoveringOn(this);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					mIsChecked = !mIsChecked;

					pCore.input().setLeftMouseClickHandled();

				}
			} else {
				// mParentScreen.setHoveringOn(this);
				hasFocus(true);
			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.time().elapseGameTimeMilli();
			}

			return true;

		} else {
			hoveredOver(false);
			mToolTipTimer = 0;

		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		// Update the button positions to line up with this entry
		// TODO(John): Need to implement left/right buttons for MenuEnumEntries.
		mLeftButtonRectangle.x = x + w / 2 + 16;
		mLeftButtonRectangle.y = y;
		mLeftButtonRectangle.h = h;

		mRightButtonRectangle.x = x + w + 64;
		mRightButtonRectangle.y = y;
		mRightButtonRectangle.h = h;
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pComponentDepth) {
		super.draw(pCore, pScreen, pIsSelected, pComponentDepth);

		mZ = pComponentDepth;

		// Render the two arrows either side of the enumeration options
		if (mButtonsEnabled) {
			// Draw the left/right buttons
			mTextureBatch.begin(pCore.HUD());
			final float ARROW_BUTTON_SIZE = 16;
			final float ARROW_PADDING_X = mLeftButtonRectangle.w - ARROW_BUTTON_SIZE;
			final float ARROW_PADDING_Y = mLeftButtonRectangle.h - ARROW_BUTTON_SIZE;

			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 384, 64, 32, 32, mLeftButtonRectangle.x + ARROW_BUTTON_SIZE + ARROW_PADDING_X, mLeftButtonRectangle.y + ARROW_PADDING_Y, -ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, mZ, 1f, 1f, 1f, 1f);
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 384, 64, 32, 32, mRightButtonRectangle.x + ARROW_PADDING_X, mRightButtonRectangle.y + ARROW_PADDING_Y, ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, mZ, 1f, 1f, 1f, 1f);

			mTextureBatch.end();
		}

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		final float lLabelWidth = lFontBitmap.getStringWidth(mLabel);
		final float TEXT_HEIGHT = lFontBitmap.getStringHeight(mLabel);
		final float lSeparatorHalfWidth = lFontBitmap.getStringWidth(mSeparator) * 0.5f;

		float lTextR = mEnabled ? mParentScreen.r() : 0.24f;
		float lTextG = mEnabled ? mParentScreen.g() : 0.24f;
		float lTextB = mEnabled ? mParentScreen.b() : 0.24f;

		mParentScreen.font().begin(pCore.HUD());
		mParentScreen.font().draw(mLabel, x + w / 2 - 10 - lLabelWidth - lSeparatorHalfWidth, y + h / 2 - TEXT_HEIGHT * 0.5f, mZ, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);
		mParentScreen.font().draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - TEXT_HEIGHT * 0.5f, mZ, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);

		// Render the items
		if (mSelectedIndex >= 0 && mSelectedIndex < mItems.size()) {
			String lCurItem = mItems.get(mSelectedIndex).name;
			mParentScreen.font().draw(lCurItem, x + w / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT, y + h / 2 - TEXT_HEIGHT * 0.5f, mZ, lTextR, lTextG, lTextB, mParentScreen.a(), 1.0f, -1);
		}

		mParentScreen.font().end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputState pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;

		} else {
			mFocusLocked = false; // no lock if not focused

		}
	}

	public void addItem(MenuEnumEntryItem pItem) {
		if (pItem == null)
			return;

		if (!mItems.contains(pItem)) {
			mItems.add(pItem);
		}
	}

	public void addItems(MenuEnumEntryItem[] pItems) {
		if (pItems == null)
			return;

		int pSize = pItems.length;
		for (int i = 0; i < pSize; i++) {
			if (!mItems.contains(pItems[i])) {
				mItems.add(pItems[i]);
			}
		}
	}

	public void clearItems() {
		mItems.clear();

	}
	
}

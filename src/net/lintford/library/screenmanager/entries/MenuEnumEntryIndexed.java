package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuEnumEntryIndexed<T> extends MenuEntry {

	private static final long serialVersionUID = -4902595949146396834L;

	public class MenuEnumEntryItem {
		public String name;
		public T value;

		public MenuEnumEntryItem(String pName, T pValue) {
			name = pName;
			value = pValue;

		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private boolean mIsChecked;
	private final String mSeparator = " : ";
	private List<MenuEnumEntryItem> mItems;
	private int mSelectedIndex;
	private boolean mEnableScaleTextToWidth;

	private boolean mButtonsEnabled;
	private Rectangle mLeftButtonRectangle;
	private Rectangle mRightButtonRectangle;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean scaleTextToWidth() {
		return mEnableScaleTextToWidth;
	}

	public void scaleTextToWidth(boolean pNewValue) {
		mEnableScaleTextToWidth = pNewValue;
	}

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

	public MenuEnumEntryIndexed(ScreenManager pScreenManager, BaseLayout pParentLayout, String pLabel) {
		super(pScreenManager, pParentLayout, "");

		mLabel = pLabel;
		mItems = new ArrayList<>();
		mSelectedIndex = 0;

		mLeftButtonRectangle = new Rectangle(0, 0, 25, 25);
		mRightButtonRectangle = new Rectangle(0, 0, 25, 25);

		mHighlightOnHover = false;
		mDrawBackground = false;

		mEnableScaleTextToWidth = true;

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

			if (mLeftButtonRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
				if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mSelectedIndex--;
					if (mSelectedIndex < 0) {
						mSelectedIndex = mItems.size() - 1;
					}

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					return true;
				}
			}

			else if (mRightButtonRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
				if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					return true;
				}

			}

		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mEnabled) {

					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					// TODO: Play a menu click sound

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					mIsChecked = !mIsChecked;

				}
			} else {
				hasFocus(true);
			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.appTime().elapsedTimeMilli();
			}

			return true;

		} else {
			hoveredOver(false);
			mToolTipTimer = 0;

		}

		return false;
	}

	@Override
	public void updateStructure() {
		super.updateStructure();
		// TODO: This -50 is because of the scrollbar - this is why I needed to keep the padding :(
		w = Math.min(mParentLayout.w() - 50f, mMaxWidth);

		if (mShowInfoIcon) {
			mInfoIconDstRectangle.set(x, y, 32f, 32f);

		}

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		// Update the button positions to line up with this entry
		// TODO(John): Need to implement left/right buttons for MenuEnumEntries.
		mLeftButtonRectangle.x(x + w / 2 + 16);
		mLeftButtonRectangle.y(y);
		mLeftButtonRectangle.h(h);

		mRightButtonRectangle.x(x + w - 32);
		mRightButtonRectangle.y(y);
		mRightButtonRectangle.h(h);
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pComponentDepth) {
		super.draw(pCore, pScreen, pIsSelected, pComponentDepth);

		mZ = pComponentDepth;

		final var lParentScreen = mParentLayout.parentScreen();
		final var lTextureBatch = lParentScreen.rendererManager().uiTextureBatch();
		final float lUiTextScale = lParentScreen.uiTextScale();

		// Render the two arrows either side of the enumeration options
		if (mButtonsEnabled) {
			// Draw the left/right buttons
			lTextureBatch.begin(pCore.HUD());
			final float ARROW_BUTTON_SIZE = 32;
			final float ARROW_PADDING_X = mLeftButtonRectangle.w() - ARROW_BUTTON_SIZE;
			final float ARROW_PADDING_Y = mLeftButtonRectangle.h() - ARROW_BUTTON_SIZE;

			lTextureBatch.draw(mUITexture, 0, 224, 32, 32, mLeftButtonRectangle.x() + ARROW_PADDING_X, mLeftButtonRectangle.y() + ARROW_PADDING_Y, ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.draw(mUITexture, 32, 224, 32, 32, mRightButtonRectangle.x() + ARROW_PADDING_X, mRightButtonRectangle.y() + ARROW_PADDING_Y, ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, mZ, 1f, 1f, 1f, 1f);

			lTextureBatch.end();

		}

		final var lFont = mParentLayout.parentScreen().font();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mLabel, lUiTextScale);
		float lAdjustedLabelScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && w * 0.4f < lLabelWidth && lLabelWidth > 0)
			lAdjustedLabelScaleW = (w * 0.4f) / lLabelWidth;

		final float lFontHeight = lFont.bitmap().fontHeight() * lUiTextScale;
		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		// Change text color depending on enabled or not
		final float lTextR = mEnabled ? mParentLayout.parentScreen().r() : 0.24f;
		final float lTextG = mEnabled ? mParentLayout.parentScreen().g() : 0.24f;
		final float lTextB = mEnabled ? mParentLayout.parentScreen().b() : 0.24f;

		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);
		lFont.draw(mLabel, x + w / 2 - 10 - (lLabelWidth * lAdjustedLabelScaleW) - lSeparatorHalfWidth, y + h / 2 - lFontHeight * 0.5f, mZ, lTextR, lTextG, lTextB, lParentScreen.a(), lAdjustedLabelScaleW, -1);
		lFont.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - lFontHeight * 0.5f, mZ, lTextR, lTextG, lTextB, lParentScreen.a(), lUiTextScale, -1);

		// Render the items
		if (mSelectedIndex >= 0 && mSelectedIndex < mItems.size()) {
			String lCurItem = mItems.get(mSelectedIndex).name;
			final float EntryWidth = lFont.bitmap().getStringWidth(lCurItem);
			float lAdjustedEntryScaleW = lUiTextScale;
			if (mEnableScaleTextToWidth && w * 0.35f < EntryWidth && EntryWidth > 0)
				lAdjustedEntryScaleW = (w * 0.35f) / EntryWidth;

			lFont.draw(lCurItem, x + (w / 4 * 3) - (EntryWidth * lAdjustedEntryScaleW) / 2, y + h / 2 - lFontHeight * 0.5f, pComponentDepth, lTextR, lTextG, lTextB, mParentLayout.parentScreen().a(), lAdjustedEntryScaleW,
					-1);

		}

		lFont.end();

		if (mShowInfoIcon) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 544, 0, 32, 32, mInfoIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.end();
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager pInputState) {
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

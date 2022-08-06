package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuEnumEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2194989174357016245L;

	public class EnumEntryItem implements Comparable<EnumEntryItem> {

		// --------------------------------------
		// Variables
		// --------------------------------------

		private String mName;
		private int mOrder;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public String name() {
			return mName;
		}

		public int order() {
			return mOrder;
		}

		// --------------------------------------
		// COnstructor
		// --------------------------------------

		public EnumEntryItem(String name, int order) {
			mName = name;
			mOrder = order;
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		@Override
		public int compareTo(EnumEntryItem o) {
			if (mOrder == o.order())
				return 0;
			return mOrder < o.order() ? -1 : 1;
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private boolean mIsChecked;
	private final String mSeparator = " : ";
	private List<EnumEntryItem> mItems;
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

	public void setListener(EntryInteractions pListener) {
		mClickListener = pListener;
	}

	public void setButtonsEnabled(boolean pNewValue) {
		mButtonsEnabled = pNewValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public int selectedEntry() {
		return mSelectedIndex;
	}

	public String selectedEntryName() {
		final var lSelectedItem = mItems.get(mSelectedIndex);
		return lSelectedItem.name();
	}

	public void setSelectedEntry(int pIndex) {
		if (pIndex < 0)
			pIndex = 0;
		if (pIndex >= mItems.size())
			pIndex = mItems.size() - 1;
		mSelectedIndex = pIndex;
	}

	public void setSelectedEntry(String pName) {
		final int lItemCount = mItems.size();
		for (int i = 0; i < lItemCount; i++) {
			if (mItems.get(i).equals(pName)) {
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

	public MenuEnumEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, String pLabel) {
		super(pScreenManager, pParentLayout, "");

		mLabel = pLabel;
		mItems = new ArrayList<>();
		mSelectedIndex = 0;

		mLeftButtonRectangle = new Rectangle(0, 0, 32, 32);
		mRightButtonRectangle = new Rectangle(0, 0, 32, 32);

		mHighlightOnHover = false;
		mDrawBackground = false;

		mEnableScaleTextToWidth = true;

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		if (mButtonsEnabled) { // left and right buttons
			if (mLeftButtonRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
				if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mSelectedIndex--;
					if (mSelectedIndex < 0) {
						mSelectedIndex = mItems.size() - 1;
					}

					mParentLayout.parentScreen.setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					return true;
				}

				else if (mRightButtonRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {

					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					mParentLayout.parentScreen.setFocusOn(pCore, this, true);

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

					mParentLayout.parentScreen.setFocusOn(pCore, this, true);

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
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		// Update the button positions to line up with this entry
		mLeftButtonRectangle.setPosition(x + w / 2 + 16, y);
		mRightButtonRectangle.setPosition(x + w - 32, y);
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		super.draw(pCore, pScreen, pIsSelected, pParentZDepth);

		final var lParentScreen = mParentLayout.parentScreen;
		final var lTextBoldFont = lParentScreen.fontBold();

		final var lUiTextScale = lParentScreen.uiTextScale();
		final var lTextWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);
		final var lParentScreenAlpha = pScreen.screenColor.a;
		final var lScreenOffset = pScreen.screenPositionOffset();

		float lAdjustedScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && w / 2 < lTextWidth && lTextWidth > 0)
			lAdjustedScaleW = (w / 2) / lTextWidth;

		final float lTextHeight = lTextBoldFont.getStringHeight(mLabel, lUiTextScale);
		final float lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		final var lTextureBatch = lParentScreen.spriteBatch();

		lTextureBatch.begin(pCore.HUD());
		final float lArrowButtonSize = 32;
		final float lArrowButtonPaddingY = mLeftButtonRectangle.h() - lArrowButtonSize;
		if (mButtonsEnabled) {
			final var lColorWhiteWithAlpha = ColorConstants.getWhiteWithAlpha(lParentScreenAlpha);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, lScreenOffset.x + mLeftButtonRectangle.x(), lScreenOffset.y + mLeftButtonRectangle.y() + lArrowButtonPaddingY, lArrowButtonSize, lArrowButtonSize, 0f,
					lColorWhiteWithAlpha);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, lScreenOffset.x + mRightButtonRectangle.x(), lScreenOffset.y + mRightButtonRectangle.y() + lArrowButtonPaddingY, lArrowButtonSize, lArrowButtonSize, 0f,
					lColorWhiteWithAlpha);
		}
		lTextureBatch.end();

		lTextBoldFont.begin(pCore.HUD());
		final float lStringWidth = lTextBoldFont.getStringWidth(mLabel, lAdjustedScaleW);
		lTextBoldFont.drawText(mLabel, lScreenOffset.x + (x + w / 2 - 10) - lStringWidth - lSeparatorHalfWidth, lScreenOffset.y + y + h / 2.f - lTextBoldFont.getStringHeight(mLabel, lAdjustedScaleW) * 0.5f, pParentZDepth, textColor, lAdjustedScaleW, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffset.x + x + w / 2 - lSeparatorHalfWidth, lScreenOffset.y + y + h / 2 - lTextHeight * 0.5f, pParentZDepth, textColor, lUiTextScale, -1);

		// Render the items
		if (mItems.size() > 0) {
			final var lCurItem = mItems.get(mSelectedIndex).name();
			final var EntryWidth = lTextBoldFont.getStringWidth(lCurItem, lUiTextScale);

			lTextBoldFont.drawText(lCurItem, lScreenOffset.x + x + (w / 6 * 4.65f) - EntryWidth / 2, lScreenOffset.y + y + h / 2 - lTextHeight * 0.5f, pParentZDepth, textColor, lUiTextScale, -1);
		}
		lTextBoldFont.end();
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

	public void addItem(String itemName) {
		addItem(itemName, 0);
	}

	public void addItem(String itemName, int itemOrder) {
		if (itemName == null)
			return;

		mItems.add(new EnumEntryItem(itemName, itemOrder));
	}

	public void addItems(String... pItems) {
		if (pItems == null)
			return;

		int pOrder = 0;
		int pSize = pItems.length;
		for (int i = 0; i < pSize; i++) {
			mItems.add(new EnumEntryItem(pItems[i], pOrder++));
		}
	}

	public void sortItems() {
		mItems.sort(Comparator.naturalOrder());
	}
}
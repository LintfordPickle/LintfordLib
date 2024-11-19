package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuEnumEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2194989174357016245L;

	private static final String SEPARATOR = ":";

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

	public void scaleTextToWidth(boolean newValue) {
		mEnableScaleTextToWidth = newValue;
	}

	public void setListener(EntryInteractions listener) {
		mClickListener = listener;
	}

	public void setButtonsEnabled(boolean newValue) {
		mButtonsEnabled = newValue;
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

	public void setSelectedEntry(int index) {
		if (index < 0)
			index = 0;

		if (index >= mItems.size())
			index = mItems.size() - 1;

		mSelectedIndex = index;
	}

	public void setSelectedEntry(String name) {
		final int lItemCount = mItems.size();
		for (int i = 0; i < lItemCount; i++) {
			if (mItems.get(i).name().equals(name)) {
				mSelectedIndex = i;
				return;
			}
		}
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean newValue) {
		mIsChecked = newValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEnumEntry(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, "");

		mLabel = label;
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
	public boolean onHandleMouseInput(LintfordCore core) {
		if (!mEnableUpdateDraw || !mEnabled)
			return false;

		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = true;
			core.input().mouse().isMouseMenuSelectionEnabled(true);

			if (!mHasFocus)
				mParentScreen.setFocusOnEntry(this);

			if (mToolTipEnabled)
				mToolTipTimer += core.appTime().elapsedTimeMilli();

			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				if (mLeftButtonRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
					mSelectedIndex--;
					if (mSelectedIndex < 0) {
						mSelectedIndex = mItems.size() - 1;
					}
					return true;
				}

				if (mRightButtonRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}
					return true;
				}

				onClick(core.input());
				return true;
			}

		} else {
			mIsMouseOver = false;
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		// Update the button positions to line up with this entry
		mLeftButtonRectangle.setPosition(mX + mW / 2 + 16, mY);
		mRightButtonRectangle.setPosition(mX + mW - 32, mY);

	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		super.draw(core, screen, parentZDepth);

		final var lTextBoldFont = mParentScreen.fontBold();

		final var lUiTextScale = mParentScreen.uiTextScale();
		final var lTextWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);
		final var lParentScreenAlpha = screen.screenColor.a;
		final var lScreenOffset = screen.screenPositionOffset();

		float lAdjustedScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && mW / 2 < lTextWidth && lTextWidth > 0)
			lAdjustedScaleW = (mW / 2) / lTextWidth;

		final float lTextHeight = lTextBoldFont.getStringHeight(mLabel, lUiTextScale);
		final float lSeparatorHalfWidth = lTextBoldFont.getStringWidth(SEPARATOR, lUiTextScale) * 0.5f;

		final var lTextureBatch = mParentScreen.spriteBatch();

		lTextureBatch.begin(core.HUD());
		final float lArrowButtonSize = 32;
		final float lArrowButtonPaddingY = mLeftButtonRectangle.height() - lArrowButtonSize;
		if (mButtonsEnabled) {
			final var lColorWhiteWithAlpha = ColorConstants.getWhiteWithAlpha(lParentScreenAlpha);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, lScreenOffset.x + mLeftButtonRectangle.x(), lScreenOffset.y + mLeftButtonRectangle.y() + lArrowButtonPaddingY, lArrowButtonSize, lArrowButtonSize, 0f, lColorWhiteWithAlpha);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, lScreenOffset.x + mRightButtonRectangle.x(), lScreenOffset.y + mRightButtonRectangle.y() + lArrowButtonPaddingY, lArrowButtonSize, lArrowButtonSize, 0f, lColorWhiteWithAlpha);
		}

		lTextureBatch.end();

		lTextBoldFont.begin(core.HUD());
		final float lStringWidth = lTextBoldFont.getStringWidth(mLabel, lAdjustedScaleW);
		lTextBoldFont.drawText(mLabel, lScreenOffset.x + (mX + mW / 2 - 10) - lStringWidth - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2.f - lTextBoldFont.getStringHeight(mLabel, lAdjustedScaleW) * 0.5f, parentZDepth, textColor, lAdjustedScaleW, -1);
		lTextBoldFont.drawText(SEPARATOR, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, parentZDepth, textColor, lUiTextScale, -1);

		if (mItems.size() > 0) {
			final var lCurItem = mItems.get(mSelectedIndex).name();
			final var EntryWidth = lTextBoldFont.getStringWidth(lCurItem, lUiTextScale);

			lTextBoldFont.drawText(lCurItem, lScreenOffset.x + mX + (mW / 6 * 4.65f) - EntryWidth / 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, parentZDepth, textColor, lUiTextScale, -1);
		}

		lTextBoldFont.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mSelectedIndex++;
		if (mSelectedIndex >= mItems.size()) {
			mSelectedIndex = 0;
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

	public void addItems(String... items) {
		if (items == null)
			return;

		int pOrder = 0;
		int pSize = items.length;
		for (int i = 0; i < pSize; i++) {
			mItems.add(new EnumEntryItem(items[i], pOrder++));
		}
	}

	public void sortItems() {
		mItems.sort(Comparator.naturalOrder());
	}
}
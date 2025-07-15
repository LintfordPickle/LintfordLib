package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
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

		final var lDoesIntersect = intersectsAA(core.HUD().getMouseCameraSpace());
		if (lDoesIntersect && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = true;
			core.input().mouse().isMouseMenuSelectionEnabled(true);

			if (!mHasFocus)
				mParentScreen.setFocusOnEntry(this);

			if (mToolTipEnabled)
				mToolTipTimer += core.appTime().elapsedTimeMilli();

			final var lLeftMouseTimed = core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this);
			if (lLeftMouseTimed) {
				if (mButtonsEnabled && mLeftButtonRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
					mSelectedIndex--;
					if (mSelectedIndex < 0) {
						mSelectedIndex = mItems.size() - 1;
					}

					if (mClickListener != null)
						mClickListener.onMenuEntryChanged(this);

					return true;
				}

				if (mButtonsEnabled && mRightButtonRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					if (mClickListener != null)
						mClickListener.onMenuEntryChanged(this);

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

		if (mButtonsEnabled) {
			final var lArrowButtonSize = Math.min(mH, 32.f);
			mLeftButtonRectangle.setPosition(mX + mW / 2 + lArrowButtonSize * .5f, mY);
			mRightButtonRectangle.setPosition(mX + mW - lArrowButtonSize, mY);
		}
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

		final var lTextHeight = lTextBoldFont.getStringHeight(mLabel, lUiTextScale);
		final var lSeparatorHalfWidth = lTextBoldFont.getStringWidth(SEPARATOR, lUiTextScale) * 0.5f;

		final var lTextureBatch = mParentScreen.spriteBatch();

		lTextureBatch.begin(core.HUD());
		final var lArrowButtonSize = Math.min(mH, 32.f);

		if (mButtonsEnabled) {
			lTextureBatch.setColorRGBA(1.f, 1.f, 1.f, lParentScreenAlpha);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, lScreenOffset.x + mLeftButtonRectangle.x(), lScreenOffset.y + mLeftButtonRectangle.y(), lArrowButtonSize, lArrowButtonSize, 0f);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, lScreenOffset.x + mRightButtonRectangle.x(), lScreenOffset.y + mRightButtonRectangle.y(), lArrowButtonSize, lArrowButtonSize, 0f);
		}

		lTextureBatch.end();

		final var lStringWidth = lTextBoldFont.getStringWidth(mLabel, lAdjustedScaleW);

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.setTextColor(textColor);
		lTextBoldFont.drawText(mLabel, lScreenOffset.x + mX + mW / 2 - 10 - lStringWidth - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2.f - lTextBoldFont.getStringHeight(mLabel, lAdjustedScaleW) * 0.5f, parentZDepth, lAdjustedScaleW, -1);
		lTextBoldFont.drawText(SEPARATOR, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, parentZDepth, lUiTextScale, -1);

		if (!mItems.isEmpty()) {
			final var lCurItem = mItems.get(mSelectedIndex).name();
			final var EntryWidth = lTextBoldFont.getStringWidth(lCurItem, lUiTextScale);

			lTextBoldFont.drawText(lCurItem, lScreenOffset.x + mX + (mW / 6 * 4.65f) - EntryWidth / 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, parentZDepth, lUiTextScale, -1);
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
		if (mSelectedIndex >= mItems.size())
			mSelectedIndex = 0;

		if (mClickListener != null)
			mClickListener.onMenuEntryChanged(this);
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

	public void clearItems() {
		mItems.clear();

	}

	@Override
	public boolean onNavigationLeft(LintfordCore core) {
		mSelectedIndex--;
		if (mSelectedIndex < 0) {
			mSelectedIndex = mItems.size() - 1;
		}

		if (mClickListener != null)
			mClickListener.onMenuEntryChanged(this);

		// by always returning true, we are repressing the layout left/right selection.
		// this is probably what we want

		return true;
	}

	@Override
	public boolean onNavigationRight(LintfordCore core) {
		mSelectedIndex++;
		if (mSelectedIndex >= mItems.size()) {
			mSelectedIndex = 0;
		}

		if (mClickListener != null)
			mClickListener.onMenuEntryChanged(this);

		// by always returning true, we are repressing the layout left/right selection.
		// this is probably what we want

		return true;
	}
}
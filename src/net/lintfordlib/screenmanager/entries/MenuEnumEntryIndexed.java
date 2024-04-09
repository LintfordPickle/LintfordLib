package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
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
	private final String mSeparator = ":";
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

	public void scaleTextToWidth(boolean newValue) {
		mEnableScaleTextToWidth = newValue;
	}

	public List<MenuEnumEntryItem> items() {
		return mItems;
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

	public MenuEnumEntryItem selectedItem() {
		return mItems.get(mSelectedIndex);

	}

	public int selectedEntry() {
		return mSelectedIndex;
	}

	public String selectedEntryName() {
		return mItems.get(mSelectedIndex).name;

	}

	public void setSelectedEntry(int index) {
		if (index < 0)
			index = 0;
		if (index >= mItems.size())
			index = mItems.size() - 1;

		mSelectedIndex = index;
	}

	public void setSelectedEntry(String name) {
		final int lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).name.equals(name)) {
				mSelectedIndex = i;
				return;
			}
		}
	}

	public void setSelectEntry(T value) {
		final int lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).value.equals(value)) {
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

	public MenuEnumEntryIndexed(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, "");

		mLabel = label;
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
	public boolean onHandleMouseInput(LintfordCore core) {
		if (!mEnableUpdateDraw || !mEnabled || isAnimating)
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

					if (mClickListener != null)
						mClickListener.onMenuEntryChanged(this);

					return true;
				}

				if (mRightButtonRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
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
		if (mEnableUpdateDraw == false)
			return;

		super.update(core, screen);

		mLeftButtonRectangle.x(mX + mW / 2 + 16);
		mLeftButtonRectangle.y(mY);
		mLeftButtonRectangle.height(mH);

		mRightButtonRectangle.x(mX + mW - 32);
		mRightButtonRectangle.y(mY);
		mRightButtonRectangle.height(mH);
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float componentDepth) {
		if (mEnableUpdateDraw == false)
			return;

		super.draw(core, screen, componentDepth);

		mZ = componentDepth;

		final var lTextureBatch = mParentScreen.spriteBatch();
		final float lUiTextScale = mParentScreen.uiTextScale();

		textColor.a = mParentScreen.screenColor.a;
		entryColor.a = mParentScreen.screenColor.a;

		if (mButtonsEnabled) {
			lTextureBatch.begin(core.HUD());

			final var lButtonSize = mH;
			final var lButtonColor = ColorConstants.getWhiteWithAlpha((mEnabled ? 1.f : 0.5f) * mParentScreen.screenColor.a);

			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, mLeftButtonRectangle.x(), mLeftButtonRectangle.y(), lButtonSize, lButtonSize, mZ, lButtonColor);
			lTextureBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, mRightButtonRectangle.x(), mRightButtonRectangle.y(), lButtonSize, lButtonSize, mZ, lButtonColor);

			lTextureBatch.end();
		}

		final var lTextBoldFont = mParentScreen.fontBold();

		final float lLabelWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);
		float lAdjustedLabelScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && mW * 0.4f < lLabelWidth && lLabelWidth > 0)
			lAdjustedLabelScaleW = (mW * 0.4f) / lLabelWidth;

		final float lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final float lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mLabel, mX + mW / 2 - 10 - (lLabelWidth * lAdjustedLabelScaleW) - lSeparatorHalfWidth, mY + mH / 2 - lFontHeight * 0.5f, mZ, textColor, lAdjustedLabelScaleW, -1);
		lTextBoldFont.drawText(mSeparator, mX + mW / 2 - lSeparatorHalfWidth, mY + mH / 2 - lFontHeight * 0.5f, mZ, textColor, lUiTextScale, -1);

		if (mSelectedIndex >= 0 && mSelectedIndex < mItems.size()) {
			String lCurItem = mItems.get(mSelectedIndex).name;
			final float EntryWidth = lTextBoldFont.getStringWidth(lCurItem);
			float lAdjustedEntryScaleW = lUiTextScale;
			if (mEnableScaleTextToWidth && mW * 0.35f < EntryWidth && EntryWidth > 0)
				lAdjustedEntryScaleW = (mW * 0.35f) / EntryWidth;

			lTextBoldFont.drawText(lCurItem, mX + (mW / 4 * 3) - (EntryWidth * lAdjustedEntryScaleW) / 2, mY + mH / 2 - lFontHeight * 0.5f, componentDepth, textColor, lAdjustedEntryScaleW, -1);
		}

		lTextBoldFont.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mHasFocus = !mHasFocus;
	}

	public void addItem(MenuEnumEntryItem item) {
		if (item == null)
			return;

		if (!mItems.contains(item)) {
			mItems.add(item);
		}
	}

	public void addItems(MenuEnumEntryItem[] items) {
		if (items == null)
			return;

		int pSize = items.length;
		for (int i = 0; i < pSize; i++) {
			if (!mItems.contains(items[i])) {
				mItems.add(items[i]);
			}
		}
	}

	public void clearItems() {
		mItems.clear();
	}

}

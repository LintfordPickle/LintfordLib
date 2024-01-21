package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.IInputClickedFocusTracker;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.screenmanager.IInputClickedFocusManager;

public class UiDropDownBox<T> extends UIWidget implements IInputClickedFocusManager, IScrollBarArea {

	// --------------------------------------
	// Innerclass
	// --------------------------------------

	public class UiDropDownBoxItem {
		public String name;
		public T value;

		public UiDropDownBoxItem(String pName, T pValue) {
			name = pName;
			value = pValue;
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 880220262639547746L;

	private static final float OPEN_HEIGHT = 256.f;
	private static final float ITEM_HEIGHT = 25.f;

	private static final String NO_ITEMS_FOUND_TEXT = "No items found";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private int mSelectedIndex;
	private int mHighlightedIndex;
	private float mContentItemOffsetTop;
	private final List<UiDropDownBoxItem> mItems = new ArrayList<>();
	private transient boolean mOpen;
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBarContentRectangle mWindowRectangle;
	private transient ScrollBar mScrollBar;
	private boolean mAllowDuplicateNames;
	private String mNoItemsFoundText = NO_ITEMS_FOUND_TEXT;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String label() {
		return mLabel;
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public void noItemsFoundText(String newText) {
		if (newText == null)
			mNoItemsFoundText = NO_ITEMS_FOUND_TEXT;

		mNoItemsFoundText = newText;
	}

	public boolean allowDuplicateNames() {
		return mAllowDuplicateNames;
	}

	public void allowDuplicateNames(boolean newValue) {
		mAllowDuplicateNames = newValue;
	}

	public List<UiDropDownBoxItem> items() {
		return mItems;
	}

	public UiDropDownBoxItem selectedItem() {
		if (mItems == null || mItems.size() == 0)
			return null;

		return mItems.get(mSelectedIndex);

	}

	public void setSelectedEntry(int index) {
		if (index < 0)
			index = 0;

		if (index >= mItems.size())
			index = mItems.size() - 1;

		mSelectedIndex = index;
	}

	public void setSelectedEntry(String name) {
		final int lNumDropDownItems = mItems.size();
		for (int i = 0; i < lNumDropDownItems; i++) {
			final var lDropDownItem = mItems.get(i);
			if (lDropDownItem == null)
				continue;

			if (lDropDownItem.name.equals(name)) {
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

	public void clearItems() {
		mItems.clear();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiDropDownBox(final UiWindow parentWindow) {
		this(parentWindow, null);
	}

	public UiDropDownBox(final UiWindow parentWindow, String label) {
		super(parentWindow);

		mOpen = false;
		mLabel = label;

		mWindowRectangle = new ScrollBarContentRectangle(parentWindow);
		mContentRectangle = new ScrollBarContentRectangle(parentWindow);
		mScrollBar = new ScrollBar(this, mContentRectangle);

		mContentItemOffsetTop = 32.f;
		mSelectedIndex = 0;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		var lResult = super.handleInput(core);

		if (!core.input().mouse().isMouseMenuSelectionEnabled())
			return false;

		if (!mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace()))
			return false;

		if (!core.input().mouse().isMouseOverThisComponent(hashCode()))
			return false;

		if (mOpen && mScrollBar.handleInput(core, this))
			return true;

		else {
			final var intersects = mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace());
			if (intersects && core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
				if (mOpen) {
					// Something inside the dropdown was highlighted / hovered over
					final float lConsoleLineHeight = ITEM_HEIGHT;
					float lRelativeheight = core.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollBar.currentYPos() - mContentItemOffsetTop;

					int lRelativeIndex = (int) (lRelativeheight / lConsoleLineHeight);
					int lSelectedIndex = lRelativeIndex;

					if (lSelectedIndex < 0)
						lSelectedIndex = 0;

					if (lSelectedIndex >= mItems.size())
						lSelectedIndex = mItems.size() - 1;

					mHighlightedIndex = lSelectedIndex;
				}

				if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					if (mOpen) {
						final float lConsoleLineHeight = ITEM_HEIGHT;
						float lRelativeheight = core.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollBar.currentYPos() - mContentItemOffsetTop;

						int lRelativeIndex = (int) (lRelativeheight / lConsoleLineHeight);
						int lSelectedIndex = lRelativeIndex;

						if (lSelectedIndex < 0)
							lSelectedIndex = 0;

						if (lSelectedIndex >= mItems.size())
							lSelectedIndex = mItems.size() - 1;

						mSelectedIndex = lSelectedIndex;

						if (mUiWidgetListenerCallback != null) {
							mUiWidgetListenerCallback.widgetOnClick(core.input(), mUiWidgetListenerUid);
							mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);
						}

						mOpen = false;

					} else {
						mOpen = true;
					}
				}
			}
		}

		return lResult;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mOpen) {

			mWindowRectangle.set(mX, mY + 25.f, mW, OPEN_HEIGHT);
		} else {
			if (mLabel != null) {
				desiredHeight(50.f);
			} else {
				desiredHeight(0.f);
			}

			mWindowRectangle.set(this);
			mWindowRectangle.expand(1);
		}

		mContentRectangle.height((mItems.size() + 1) * ITEM_HEIGHT);
		mScrollBar.update(core);
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		final var lFontHeight = textFont.fontHeight();

		if (mItems == null || mItems.size() == 0) {
			textFont.begin(core.HUD());
			textFont.drawText(mNoItemsFoundText, mX + HorizontalPadding, mY + mH / 2f - lFontHeight / 2f, componentZDepth, ColorConstants.WHITE, 1.f, -1);
			textFont.end();
			return;
		}

		// CONTENT PANE

		if (mLabel != null) {
			textFont.begin(core.HUD());
			textFont.drawText(mLabel, mX, mY + UIWidget.DefaultWidthHeight * .5f - lFontHeight / 2f, componentZDepth, ColorConstants.WHITE, 1.f, -1);
			textFont.end();
		}

		if (mOpen == false) {
			drawSelectedItem(core, spriteBatch, textFont, coreSpritesheet, componentZDepth);

		} else {
			drawSelectedItem(core, spriteBatch, textFont, coreSpritesheet, componentZDepth);

			mWindowRectangle.preDraw(core, spriteBatch, mWindowRectangle, 1);
			textFont.begin(core.HUD());
			spriteBatch.begin(core.HUD());
			final var lBlackWithAlpha = ColorConstants.getBlackWithAlpha(1.f);

			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, componentZDepth, lBlackWithAlpha);
			spriteBatch.end();

			float lYPos = mWindowRectangle.y() + mScrollBar.currentYPos() + mContentItemOffsetTop;

			final int lItemCount = mItems.size();
			for (int i = 0; i < lItemCount; i++) {
				final var lItem = mItems.get(i);

				if (i == mHighlightedIndex)
					entityColor.setFromColor(ColorConstants.GREEN);
				else
					entityColor.setFromColor(ColorConstants.TextEntryColor);

				textFont.drawText(lItem.name, mX + 5.f, lYPos, componentZDepth, entityColor, 1.f, -1);
				lYPos += ITEM_HEIGHT;
			}

			textFont.end();

			mWindowRectangle.postDraw(core);
			mScrollBar.draw(core, spriteBatch, coreSpritesheet, componentZDepth, 1.f);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());

			if (mOpen)
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, componentZDepth, ColorConstants.Debug_Transparent_Magenta);
			else
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, this, componentZDepth, ColorConstants.Debug_Transparent_Magenta);

			spriteBatch.end();
		}
	}

	private void drawSelectedItem(LintfordCore core, SpriteBatch spriteBatch, FontUnit textFont, SpriteSheetDefinition coreSpritesheet, float componentZDepth) {
		var xx = mX;
		var yy = mY + mH - UIWidget.DefaultWidthHeight;
		var ww = mW;

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, (int) xx, yy, 32, UIWidget.DefaultWidthHeight, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
		if (mW > 32) {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, (int) xx + 32, yy, ww - 64, UIWidget.DefaultWidthHeight, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, (int) xx + ww - 32, yy, 32, UIWidget.DefaultWidthHeight, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
		}
		spriteBatch.end();

		if (mSelectedIndex > -1) {
			final var lSelectedMenuEnumEntryItem = mItems.get(mSelectedIndex);
			final var lCurItemName = lSelectedMenuEnumEntryItem.name;

			textFont.begin(core.HUD());
			textFont.drawText(lCurItemName, mX + HorizontalPadding, yy + UIWidget.DefaultWidthHeight * .5f - textFont.fontHeight() / 2f, componentZDepth, ColorConstants.WHITE, 1.f, -1);
			textFont.end();
		} else {
			textFont.begin(core.HUD());
			textFont.drawText("No item selected", mX + HorizontalPadding, yy + UIWidget.DefaultWidthHeight * .5f - textFont.fontHeight() / 2f, componentZDepth, ColorConstants.WHITE, 1.f, -1);
			textFont.end();
		}

		// Draw the down arrow
		spriteBatch.begin(core.HUD());
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_DOWN, right() - 25, yy, 25, 25, componentZDepth, ColorConstants.WHITE);
		spriteBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void setTrackedClickedFocusControl(IInputClickedFocusTracker controlToTrack) {
		// TODO Auto-generated method stub

	}

	@Override
	public IInputClickedFocusTracker getTrackedClickedFocusControl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle contentDisplayArea() {
		return mWindowRectangle;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentRectangle;
	}

}

package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.Collections;
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
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.rendering.SharedResources;
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

	private static final float MAX_ITEMS_TO_DISPLAY = 6;
	private static final float ITEM_HEIGHT = 25.f;

	private static final int NO_ITEM_INDEX = -1;

	private static final String NO_SELECTION_TEXT = "No Selection";
	private static final String NO_ITEMS_FOUND_TEXT = "No items found";

	public static final String NO_LABEL_TEXT = "unlabelled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private int mSelectedIndex;
	private int mHighlightedIndex;
	private final List<UiDropDownBoxItem> mItems = new ArrayList<>();
	private transient boolean mOpen;
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBarContentRectangle mWindowRectangle;
	private transient ScrollBar mScrollBar;
	private boolean mAllowDuplicateNames;
	private String mNoItemsFoundText = NO_ITEMS_FOUND_TEXT;
	private final Rectangle mDownArrowRectangle = new Rectangle();
	private boolean mDownArrowHovered;

	private boolean mUseEmptyItemPlaceholder;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** If set, an empty item will be automatically added at the top of the dropdown box (even after clearing) to be used to deselect the current item. */
	public boolean useEmptyItemPlaceholder() {
		return mUseEmptyItemPlaceholder;
	}

	/** If set, an empty item will be automatically added at the top of the dropdown box (even after clearing) to be used to deselect the current item. */
	public void useEmptyItemPlaceholder(boolean newValue) {
		if (newValue && !mUseEmptyItemPlaceholder) {
			mItems.add(0, new UiDropDownBoxItem(NO_SELECTION_TEXT, null));
		} else if (!newValue && mUseEmptyItemPlaceholder) {
			final var numItems = mItems.size();
			for (int i = 0; i < numItems; i++) {
				if (mItems.get(i).name.equals(NO_SELECTION_TEXT)) {
					mItems.remove(i);
					break;
				}
			}
		}

		mUseEmptyItemPlaceholder = newValue;
	}

	public String label() {
		return mLabel;
	}

	public void label(String newLabel) {
		mLabel = newLabel;
		mIsDoubleHeight = mLabel != null && !mLabel.isEmpty();
		desiredHeight(mIsDoubleHeight ? UIWidget.DefaultWidthHeight * 2.f : UIWidget.DefaultWidthHeight);
		mH = desiredHeight();
	}

	public boolean isOpen() {
		return mOpen;
	}

	public void isOpen(boolean open) {
		mOpen = open;
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
		return Collections.unmodifiableList(mItems);
	}

	public UiDropDownBoxItem selectedItem() {
		if (mItems == null || mItems.isEmpty())
			return null;

		if (mSelectedIndex == NO_ITEM_INDEX || mSelectedIndex >= mItems.size())
			return null;

		return mItems.get(mSelectedIndex);
	}

	public T getObjectByIndex(int index) {
		if (index < 0 || index >= mItems.size())
			return null;

		return mItems.get(index).value;
	}

	public T getObjectByName(String name) {
		final var lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).name.equals(name))
				return mItems.get(i).value;
		}
		return null;
	}

	public void setSelectedEntry(int index) {
		if (index < -1)
			index = NO_ITEM_INDEX;

		if (index >= mItems.size())
			index = mItems.size() - 1;

		mSelectedIndex = index;
	}

	public void setSelectedEntry(String name) {
		if (name == null) {
			mSelectedIndex = NO_ITEM_INDEX;
			return;
		}

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

		mSelectedIndex = NO_ITEM_INDEX;
	}

	public void setSelectEntry(T value) {
		final int lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).value.equals(value)) {
				mSelectedIndex = i;
				return;
			}
		}

		mSelectedIndex = NO_ITEM_INDEX;
	}

	public void clearItems() {
		mItems.clear();
		mSelectedIndex = NO_ITEM_INDEX;

		if (mUseEmptyItemPlaceholder) {
			mItems.add(0, new UiDropDownBoxItem(NO_SELECTION_TEXT, null));
		}
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiDropDownBox() {
		this(null);
	}

	public UiDropDownBox(String label) {
		mOpen = false;
		mLabel = label;
		mIsDoubleHeight = mLabel != null && !mLabel.isEmpty();
		desiredHeight(mIsDoubleHeight ? UIWidget.DefaultWidthHeight * 2.f : UIWidget.DefaultWidthHeight);
		mH = desiredHeight();

		mWindowRectangle = new ScrollBarContentRectangle(this);
		mContentRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentRectangle);

		mSelectedIndex = 0;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		var lResult = super.handleInput(core);

		final var lMouseCamera = core.HUD().getMouseCameraSpace();
		mDownArrowHovered = mDownArrowRectangle.intersectsAA(lMouseCamera.x, lMouseCamera.y);

		if (!core.input().mouse().isMouseMenuSelectionEnabled())
			return false;

		if (mOpen) {
			// 1. Mouse inside popup window
			if (mWindowRectangle.intersectsAA(lMouseCamera)) {
				if (mScrollBar.scrollBarEnabled() && mScrollBar.handleInput(core, this)) {
					return true;
				}

				if (core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
					final float lRelativeY = lMouseCamera.y - mWindowRectangle.y() - mScrollBar.currentYPos();
					final int lIndex = (int) (lRelativeY / ITEM_HEIGHT);

					if (lIndex >= 0 && lIndex < mItems.size()) {
						mHighlightedIndex = lIndex;
					} else {
						mHighlightedIndex = NO_ITEM_INDEX;
					}

					if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
						if (lIndex >= 0 && lIndex < mItems.size()) {
							mSelectedIndex = lIndex;

							if (mUiWidgetListenerCallback != null) {
								mUiWidgetListenerCallback.widgetOnClick(core.input(), mUiWidgetListenerUid);
								mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);
							}
						}

						mOpen = false;
						return true;
					}
				}
				return true;
			}

			// 2. Clicked on the header box (toggle closed)
			if (intersectsAA(lMouseCamera)) {
				if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mOpen = false;
					return true;
				}
				return true;
			}

			// 3. Clicked outside both popup and header
			if (core.input().mouse().isMouseLeftButtonDownTimed(this)) {
				mOpen = false;
				return false;
			}

			return false;
		}

		// When closed: clicking on the widget opens it
		if (intersectsAA(lMouseCamera) && core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				mOpen = true;
				return true;
			}
		}

		return lResult;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mOpen) {
			final float lPopupHeight = Math.max(1, Math.min(mItems.size(), (int) MAX_ITEMS_TO_DISPLAY)) * ITEM_HEIGHT;
			float lPopupY = mY + mH;

			if (core.HUD() != null && core.HUD().boundingRectangle() != null) {
				final float lHudBottom = core.HUD().boundingRectangle().bottom();
				if (lPopupY + lPopupHeight > lHudBottom) {
					lPopupY = mY - lPopupHeight;
				}
			}

			mWindowRectangle.set(mX, lPopupY, mW, lPopupHeight);
			final float lContentHeight = Math.max(mItems.size() * ITEM_HEIGHT, lPopupHeight);
			mContentRectangle.set(mX, lPopupY, mW, lContentHeight);
			mScrollBar.update(core);
		} else {
			mIsDoubleHeight = mLabel != null && !mLabel.isEmpty();
			desiredHeight(mIsDoubleHeight ? UIWidget.DefaultWidthHeight * 2.f : UIWidget.DefaultWidthHeight);

			mWindowRectangle.set(this);
			mContentRectangle.set(this);
			mScrollBar.update(core);
		}

		mDownArrowRectangle.set(right() - 25, mY + mH - UIWidget.DefaultWidthHeight, 25, 25);
	}

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		final var lFontHeight = textFont.fontHeight();
		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		// Handle Empty State when closed
		if (mItems.isEmpty() && !mOpen) {
			textFont.begin(core.HUD());
			textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			textFont.drawText(mNoItemsFoundText, mX + HorizontalPadding, mY + mH / 2f - lFontHeight / 2f, componentZDepth, 1.f, -1);
			textFont.end();
			return;
		}

		// Draw Main Label (if exists)
		if (mLabel != null && !mLabel.isEmpty()) {
			textFont.begin(core.HUD());
			textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			textFont.drawText(mLabel, mX, mY + UIWidget.DefaultWidthHeight * .5f - lFontHeight / 2f, componentZDepth, 1.f, -1);
			textFont.end();
		}

		if (!mOpen) {
			drawSelectedItem(core, lSpriteBatch, textFont, coreSpritesheet, componentZDepth);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());

			lSpriteBatch.setColorRGBA(2.f, .8f, .2f, .4f);
			lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, UIWidget.DefaultWidthHeight, componentZDepth);

			lSpriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			if (mOpen)
				lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, componentZDepth);
			else
				lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, this, componentZDepth);

			lSpriteBatch.end();
		}
	}

	@Override
	public void drawPost(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		super.drawPost(core, sharedResources, coreSpritesheet, textFont, componentZDepth);

		if (!mOpen)
			return;

		final var spriteBatch = sharedResources.uiSpriteBatch();

		drawSelectedItem(core, spriteBatch, textFont, coreSpritesheet, componentZDepth);

		// 1. Draw dropdown background panel
		spriteBatch.begin(core.HUD());
		spriteBatch.setColorRGBA(0.08f, 0.08f, 0.08f, 1.f);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, componentZDepth - 0.1f);

		// Border around popup
		spriteBatch.setColor(ColorConstants.MenuPanelPrimaryColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle.x(), mWindowRectangle.y(), mWindowRectangle.width(), 1.f, componentZDepth - 0.12f);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle.x(), mWindowRectangle.bottom() - 1.f, mWindowRectangle.width(), 1.f, componentZDepth - 0.12f);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle.x(), mWindowRectangle.y(), 1.f, mWindowRectangle.height(), componentZDepth - 0.12f);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle.right() - 1.f, mWindowRectangle.y(), 1.f, mWindowRectangle.height(), componentZDepth - 0.12f);
		spriteBatch.end();

		final boolean hasScrollBar = mScrollBar.scrollBarEnabled();

		if (mItems.isEmpty()) {
			textFont.begin(core.HUD());
			textFont.setTextColorRGBA(0.7f, 0.7f, 0.7f, 1.f);
			textFont.drawText(mNoItemsFoundText, mWindowRectangle.x() + HorizontalPadding, mWindowRectangle.y() + (ITEM_HEIGHT - textFont.fontHeight()) / 2f, componentZDepth - 0.2f, 1.f, -1);
			textFont.end();
			return;
		}

		// 2. Stencil clipping for scrollable area
		if (hasScrollBar) {
			mWindowRectangle.preDraw(core, spriteBatch, mWindowRectangle, mStencilId, true);
		}

		// 3. Draw items
		textFont.begin(core.HUD());
		float lYPos = mWindowRectangle.y() + mScrollBar.currentYPos();

		final int lItemCount = mItems.size();
		for (int i = 0; i < lItemCount; i++) {
			final var lItem = mItems.get(i);

			if (i == mHighlightedIndex)
				entityColor.setFromColor(ColorConstants.GREEN());
			else
				entityColor.setFromColor(ColorConstants.TextEntryColor);

			textFont.setTextColor(entityColor);
			textFont.drawText(lItem.name, mWindowRectangle.x() + 5.f, lYPos + (ITEM_HEIGHT - textFont.fontHeight()) / 2f, componentZDepth - 0.2f, 1.f, -1);
			lYPos += ITEM_HEIGHT;
		}

		textFont.end();
		textFont.setTextColorWhite();

		if (hasScrollBar) {
			mWindowRectangle.postDraw(core);
		}

		// 4. Draw scrollbar (wrapped in spriteBatch.begin/end and closer in Z depth)
		if (hasScrollBar) {
			mScrollBar.scrollBarAlpha(1.f);
			spriteBatch.begin(core.HUD());
			mScrollBar.draw(core, spriteBatch, coreSpritesheet, componentZDepth - 0.25f);
			spriteBatch.end();
		}
	}

	private void drawSelectedItem(LintfordCore core, SpriteBatch spriteBatch, FontUnit textFont, SpriteSheetDefinition coreSpritesheet, float componentZDepth) {
		var xx = mX;
		var yy = mY + mH - UIWidget.DefaultWidthHeight;
		var ww = mW;

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(ColorConstants.MenuPanelPrimaryColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, xx, yy, 32, UIWidget.DefaultWidthHeight, componentZDepth);
		if (mW > 32) {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, xx + 32.f, yy, ww - 64, UIWidget.DefaultWidthHeight, componentZDepth);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, xx + ww - 32.f, yy, 32, UIWidget.DefaultWidthHeight, componentZDepth);
		}
		spriteBatch.end();

		textFont.begin(core.HUD());
		textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);

		if (mSelectedIndex > -1 && mSelectedIndex < mItems.size()) {
			final var lSelectedMenuEnumEntryItem = mItems.get(mSelectedIndex);
			final var lCurItemName = lSelectedMenuEnumEntryItem != null ? lSelectedMenuEnumEntryItem.name : NO_SELECTION_TEXT;

			textFont.drawText(lCurItemName, mX + HorizontalPadding, yy + UIWidget.DefaultWidthHeight * .5f - textFont.fontHeight() / 2f, componentZDepth - .01f, 1.f, -1);
		} else {
			textFont.drawText("No item selected", mX + HorizontalPadding, yy + UIWidget.DefaultWidthHeight * .5f - textFont.fontHeight() / 2f, componentZDepth - .01f, 1.f, -1);
		}

		textFont.end();

		final var lIconColor = mDownArrowHovered ? ColorConstants.WHITE() : ColorConstants.GREY_LIGHT();

		// Draw the down arrow
		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(lIconColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_DOWN, mDownArrowRectangle, componentZDepth - .01f);
		spriteBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addNewItem(String name, T object) {
		if (mItems.contains(object))
			return;

		mItems.add(new UiDropDownBoxItem(name, object));
	}

	@Override
	public void setTrackedClickedFocusControl(IInputClickedFocusTracker controlToTrack) {
		//
	}

	@Override
	public IInputClickedFocusTracker getTrackedClickedFocusControl() {
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

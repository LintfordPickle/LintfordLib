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

public class UiDropDownBox2<T> extends UIWidget implements IInputClickedFocusManager, IScrollBarArea {

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

	private UiInputText mSearchInput;
	private final List<UiDropDownBoxItem> mFilteredItems = new ArrayList<>();
	private boolean mEnableSearch = true; // Toggleable feature

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
		if (mItems == null || mItems.size() == 0)
			return null;

		if (mSelectedIndex == NO_ITEM_INDEX)
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

	public UiDropDownBox2() {
		this(NO_LABEL_TEXT);
	}

	public UiDropDownBox2(String label) {
		mOpen = false;
		mLabel = label;

		mWindowRectangle = new ScrollBarContentRectangle(this);
		mContentRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentRectangle);

		mSelectedIndex = 0;

		mSearchInput = new UiInputText();
		mSearchInput.emptyString("Search...");
		mSearchInput.singleLine(true);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mOpen) {
			// 1. Handle Search Input first
			mSearchInput.handleInput(core);
			mSearchInput.onClick(core.input());
			updateFilteredItems(); // Update list based on new keystrokes

			// 2. Adjust item selection logic to use mFilteredItems instead of mItems
			if (mFilteredItems.size() > 0) {
				final var lOffsetY = (mLabel != null ? UIWidget.DefaultWidthHeight : 0.f) + ITEM_HEIGHT;
				// Note: Added ITEM_HEIGHT to offset for the search bar itself

				final var lLabelOffsetY = mLabel != null ? UIWidget.DefaultWidthHeight : 0.f;

				final var lMouseY = core.HUD().getMouseCameraSpace().y;
				final var lRelativeHeight = lMouseY - mWindowRectangle.y() - mScrollBar.currentYPos() - lOffsetY - lLabelOffsetY;

				// Only select if we are below the search bar area
				if (lRelativeHeight > 0) {
					int lIndex = MathHelper.clampi((int) (lRelativeHeight / ITEM_HEIGHT), 0, mFilteredItems.size()) + 1;
					mHighlightedIndex = lIndex;

					if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
						// Find the actual item in the master list to set the index correctly
						var selectedFilteredItem = mFilteredItems.get(lIndex);
						setSelectedEntry(selectedFilteredItem.name);

						if (mUiWidgetListenerCallback != null) {
							mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);
						}
						mOpen = false;

						mSearchInput.stopInputCapture(core.input());
						mSearchInput.inputString(""); // Clear search on close
					}
				}
			}
		} else {

		}

		var lResult = super.handleInput(core);

		mDownArrowHovered = mDownArrowRectangle.intersectsAA(core.HUD().getMouseCameraSpace().x, core.HUD().getMouseCameraSpace().y);

		if (!core.input().mouse().isMouseMenuSelectionEnabled())
			return false;

		if (!mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (mOpen) {
				if (core.input().mouse().isMouseLeftButtonDownTimed(this)) {
					mSearchInput.stopInputCapture(core.input());
					mOpen = false;
				}
			}

			return false;
		}

		if (!core.input().mouse().isMouseOverThisComponent(hashCode()))
			return false;

		if (mOpen && mScrollBar.handleInput(core, this))
			return true;

		else {
			final var intersectsDropDown = mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace());
			if (intersectsDropDown && core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
				final var lOffsetY = mLabel != null ? UIWidget.DefaultWidthHeight : 0.f;
				if (mOpen) {

					final var lLabelOffsetY = mLabel != null ? UIWidget.DefaultWidthHeight : 0.f;

					final var lConsoleLineHeight = ITEM_HEIGHT;
					final var lRelativeHeight = core.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollBar.currentYPos() - lOffsetY - lLabelOffsetY;
					final var lSelectedIndex = MathHelper.clampi((int) (lRelativeHeight / lConsoleLineHeight), 0, mItems.size() - 1);

					mHighlightedIndex = lSelectedIndex;

					if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

						mSelectedIndex = lSelectedIndex;

						if (mUiWidgetListenerCallback != null) {
							mUiWidgetListenerCallback.widgetOnClick(core.input(), mUiWidgetListenerUid);
							mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);
						}

						mSearchInput.stopInputCapture(core.input());
						mOpen = false;
					}
				}

				if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mOpen = true;
				}
			}
		}

		return lResult;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lLabelOffset = mLabel != null ? 1 : 0;

		if (mOpen) {
			final var lNumItemsToRender = Math.min(mItems.size(), MAX_ITEMS_TO_DISPLAY);
			final var lHeight = lNumItemsToRender * ITEM_HEIGHT + lLabelOffset * UIWidget.DefaultWidthHeight;

			mWindowRectangle.set(mX, mY + UIWidget.DefaultWidthHeight, mW, lHeight);
		} else {
			if (mLabel != null) {
				desiredHeight(UIWidget.DefaultWidthHeight * 2.f);
			} else {
				desiredHeight(UIWidget.DefaultWidthHeight);
			}

			mWindowRectangle.set(this);
			mWindowRectangle.expand(1);
		}

		mDownArrowRectangle.set(right() - 25, mY + mH - UIWidget.DefaultWidthHeight, 25, 25);
		mContentRectangle.height((mItems.size() + lLabelOffset) * ITEM_HEIGHT);
		mScrollBar.update(core);
	}

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {

		final var lFontHeight = textFont.fontHeight();
		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		// 1. Handle Empty State
		if (mItems.isEmpty()) {
			textFont.begin(core.HUD());
			textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			textFont.drawText(mNoItemsFoundText, mX + HorizontalPadding, mY + mH / 2f - lFontHeight / 2f, componentZDepth, 1.f, -1);
			textFont.end();
			return;
		}

		// Draw Main Label (if exists)
		if (mLabel != null) {
			textFont.begin(core.HUD());
			textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			textFont.drawText(mLabel, mX, mY + UIWidget.DefaultWidthHeight * .5f - lFontHeight / 2f, componentZDepth, 1.f, -1);
			textFont.end();
		}

		// Draw the "Collapsed" view (the box that shows the current selection)
		if (!mOpen)
			drawSelectedItem(core, lSpriteBatch, textFont, coreSpritesheet, componentZDepth);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorRGBA(2.f, .8f, .2f, .4f);
			lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, UIWidget.DefaultWidthHeight, componentZDepth);
			lSpriteBatch.end();
		}

	}

	@Override
	public void drawPost(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		super.drawPost(core, sharedResources, coreSpritesheet, textFont, componentZDepth);

		final var spriteBatch = sharedResources.uiSpriteBatch();

		if (!mOpen)
			return;

		// Position the search bar at the top of the dropdown window
		final var lLabelOffsetY = mLabel != null ? UIWidget.DefaultWidthHeight : 0.f;

		// Prepare the stencil/clipping area for the dropdown window

		mWindowRectangle.preDraw(core, spriteBatch, mWindowRectangle.x(), mWindowRectangle.y() + lLabelOffsetY, mWindowRectangle.width(), mWindowRectangle.height() - lLabelOffsetY, stencilId(), false, 0xFFFFFFFF);

		// Draw the background panel for the list
		spriteBatch.begin(core.HUD());
		spriteBatch.setColorRGBA(0.05f, 0.05f, 0.05f, 1.f); // Darker background for the list
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, componentZDepth - 0.1f);
		spriteBatch.end();

		// --- DRAW FILTERED ITEMS ---
		textFont.begin(core.HUD());

		// Start drawing items below the search bar, adjusted by the scrollbar position
		float lYPos = mWindowRectangle.y() + lLabelOffsetY + ITEM_HEIGHT + mScrollBar.currentYPos();

		final int lFilteredCount = mFilteredItems.size();
		for (int i = 0; i < lFilteredCount; i++) {
			final var lItem = mFilteredItems.get(i);

			// Highlight the hovered/selected index
			if (i == mHighlightedIndex) {
				entityColor.setFromColor(ColorConstants.GREEN());
			} else {
				entityColor.setFromColor(ColorConstants.TextEntryColor);
			}

			textFont.setTextColor(entityColor);
			textFont.drawText(lItem.name, mX + 5.f, lYPos, componentZDepth - 0.2f, 1.f, -1);

			lYPos += ITEM_HEIGHT;
		}

		textFont.end();

		// End clipping
		mWindowRectangle.postDraw(core);

		// --- DRAW SEARCH BAR ---

		mSearchInput.set(mX, mWindowRectangle.y(), mW, ITEM_HEIGHT);
		mSearchInput.draw(core, sharedResources, coreSpritesheet, textFont, componentZDepth - .2f);

		// 5. Draw Scrollbar (on top of the window but outside the stencil if preferred)
		if (mScrollBar.scrollBarEnabled()) {
			mScrollBar.scrollBarAlpha(1.f);
			mScrollBar.draw(core, spriteBatch, coreSpritesheet, componentZDepth - 1);
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

		if (mSelectedIndex > -1) {
			final var lSelectedMenuEnumEntryItem = mItems.get(mSelectedIndex);
			final var lCurItemName = lSelectedMenuEnumEntryItem.name;

			textFont.drawText(lCurItemName, mX + HorizontalPadding, yy + UIWidget.DefaultWidthHeight * .5f - textFont.fontHeight() / 2f, componentZDepth - 0.01f, 1.f, -1);

		} else {
			textFont.drawText("No item selected", mX + HorizontalPadding, yy + UIWidget.DefaultWidthHeight * .5f - textFont.fontHeight() / 2f, componentZDepth - 0.01f, 1.f, -1);
		}

		textFont.end();

		final var lIconColor = mDownArrowHovered ? ColorConstants.WHITE() : ColorConstants.GREY_LIGHT();

		// Draw the down arrow
		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(lIconColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_DOWN, mDownArrowRectangle, componentZDepth - 0.01f);
		spriteBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private String mLastFilteredString;

	private void updateFilteredItems() {
		mFilteredItems.clear();

		String searchTerms = mSearchInput.inputString().toString().toLowerCase();
		if (searchTerms == null || searchTerms.length() == 0) {
			mFilteredItems.addAll(mItems);

			return;
		} else {
			for (var item : mItems) {
				if (searchTerms.isEmpty() || item.name.toLowerCase().contains(searchTerms)) {
					mFilteredItems.add(item);
				}
			}
		}

		if (mLastFilteredString != null && !mLastFilteredString.equals(searchTerms))
			mScrollBar.resetBarTop();

		mLastFilteredString = searchTerms;
	}

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

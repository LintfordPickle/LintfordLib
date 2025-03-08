package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiEnumSelection extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7704116387039308007L;

	public static final int NO_UID = -1;
	private static final String NO_LABEL_TEXT = "";

	public static final float ARROW_SIZE = 16.f;
	public static final float ARROW_PADDING = 8.f;
	public static final float WIDGET_HEIGHT = 100.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<UiIndexedEnum> mItems = new ArrayList<>();
	private String mButtonLabel;
	private boolean mIsClicked;
	private float mClickTimer;
	private int mSelectedIndex;

	private final Rectangle mLeftRectangle = new Rectangle();
	private final Rectangle mRightRectangle = new Rectangle();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void addItem(UiIndexedEnum newItem) {
		mItems.add(newItem);
	}

	public void addItems(List<UiIndexedEnum> newItems) {
		mItems.addAll(newItems);
	}

	public int getUidByName(String name) {
		final int lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).displayName != null && mItems.get(i).displayName.equals(name)) {
				return mItems.get(i).uid;
			}
		}
		return NO_UID;
	}

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(String pNewLabel) {
		mButtonLabel = pNewLabel;
	}

	public int buttonListenerID() {
		return mUiWidgetListenerUid;
	}

	public void buttonListenerID(int pNewLabel) {
		mUiWidgetListenerUid = pNewLabel;
	}

	public UiIndexedEnum selectedItem() {
		return mItems.get(mSelectedIndex);
	}

	public void setSelectedItemByUid(int uid) {
		final var lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).uid == uid) {
				mSelectedIndex = i;
				return;
			}
		}
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiEnumSelection(UiWindow pParentWindow) {
		super(pParentWindow);

		mButtonLabel = NO_LABEL_TEXT;
		mW = 200;
		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!isEnabled())
			return false;

		final var MINIMUM_CLICK_TIMER = 200;

		if (!mIsClicked && mLeftRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mIsHoveredOver = true;

			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {

				if (mClickTimer > MINIMUM_CLICK_TIMER) {
					mIsClicked = true;

					mSelectedIndex--;
					if (mSelectedIndex < 0)
						mSelectedIndex = mItems.size() - 1;

					mClickTimer = 0;

					if (mUiWidgetListenerCallback != null)
						mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);

					return true;
				}
			}
		} else if (!mIsClicked && mRightRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mIsHoveredOver = true;

			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				if (mClickTimer > MINIMUM_CLICK_TIMER) {
					mIsClicked = true;

					mSelectedIndex++;
					if (mSelectedIndex > mItems.size() - 1)
						mSelectedIndex = 0;

					mClickTimer = 0;

					if (mUiWidgetListenerCallback != null)
						mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);

					return true;
				}
			}
		} else {
			mIsHoveredOver = false;
		}

		if (mIsClicked && !core.input().mouse().tryAcquireMouseLeftClick(hashCode()))
			mIsClicked = false;

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		mClickTimer += core.appTime().elapsedTimeMilli();

		mLeftRectangle.set(mX + ARROW_PADDING, mY + mH - ARROW_SIZE - ARROW_PADDING, ARROW_SIZE, ARROW_SIZE);
		mRightRectangle.set(mX + mW - ARROW_SIZE - ARROW_PADDING, mY + mH - ARROW_SIZE - ARROW_PADDING, ARROW_SIZE, ARROW_SIZE);
	}

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		if (!mIsVisible)
			return;

		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, 1.f);
		final var lTileSize = 32;

		final var lSelectionText = (!mItems.isEmpty() ? (mSelectedIndex + 1) + "/" + mItems.size() : "0");
		final var lSelectionTextWidth = textFont.getStringWidth(lSelectionText);
		final var lScale = 1.f;

		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.setColor(lColor);

		lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_LEFT, mX, mY, lTileSize, mH, componentZDepth);
		lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, mX + lTileSize, mY, mW - lTileSize * 2, mH, componentZDepth);
		lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_RIGHT, mX + mW - lTileSize, mY, lTileSize, mH, componentZDepth);

		lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_LEFT, mLeftRectangle, componentZDepth);
		lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_RIGHT, mRightRectangle, componentZDepth - 0.01f);
		lSpriteBatch.end();

		textFont.begin(core.HUD());
		textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		final var lLabelText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;

		textFont.drawText(lSelectionText, mX + mW - lSelectionTextWidth - ARROW_PADDING, mY + ARROW_PADDING, componentZDepth, lScale);
		textFont.drawText(lLabelText, mX + ARROW_PADDING, mY + ARROW_PADDING, componentZDepth, lScale);

		if (mSelectedIndex >= 0 && mSelectedIndex < mItems.size()) {
			final var lItem = mItems.get(mSelectedIndex);
			final var lItemDisplayName = lItem.displayName;
			final var lTextItemWidth = textFont.getStringWidth(lItemDisplayName);
			textFont.drawText(lItemDisplayName, mX + mW - mW / 3.f - lTextItemWidth / 2f, mY + mH / 2 - textFont.fontHeight() / 2, componentZDepth, lScale);
		} else {
			final var lNoItemSelectedText = "";
			final var lNoTextWidth = textFont.getStringWidth(lNoItemSelectedText);
			textFont.drawText(lNoItemSelectedText, mX + mW / 2f - lNoTextWidth / 2f, mY + mH - ARROW_SIZE - ARROW_PADDING, componentZDepth, lScale);
		}
		textFont.end();
	}
}

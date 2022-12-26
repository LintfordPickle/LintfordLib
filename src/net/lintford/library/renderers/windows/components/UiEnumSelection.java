package net.lintford.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;

public class UiEnumSelection extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7704116387039308007L;

	public static final int NO_UID = -1;
	private static final String NO_LABEL_TEXT = "unlabled";

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

	public void buttonLabel(final String pNewLabel) {
		mButtonLabel = pNewLabel;
	}

	public int buttonListenerID() {
		return mUiWidgetUid;
	}

	public void buttonListenerID(final int pNewLabel) {
		mUiWidgetUid = pNewLabel;
	}

	public UiIndexedEnum selectedItem() {
		return mItems.get(mSelectedIndex);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiEnumSelection(final UiWindow pParentWindow) {
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

					if (mCallback != null)
						mCallback.widgetOnClick(core.input(), mUiWidgetUid);

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

					if (mCallback != null)
						mCallback.widgetOnClick(core.input(), mUiWidgetUid);

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
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		if (!mIsVisible)
			return;

		final var lCanvasScale = mParentWindow != null ? mParentWindow.uiStructureController().uiCanvasWScaleFactor() : 1.0f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, 1.f);

		final var lTileSize = 32;
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_LEFT, mX, mY, lTileSize, mH, componentZDepth, lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, mX + lTileSize, mY, mW - lTileSize * 2, mH, componentZDepth, lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_RIGHT, mX + mW - lTileSize, mY, lTileSize, mH, componentZDepth, lColor);

		final var lSelectionText = (mItems.size() > 0 ? (mSelectedIndex + 1) + "/" + mItems.size() : "0");
		final var lSelectionTextWidth = textFont.getStringWidth(lSelectionText, lCanvasScale);
		textFont.drawText(lSelectionText, mX + mW - lSelectionTextWidth - ARROW_PADDING, mY + ARROW_PADDING, componentZDepth, ColorConstants.WHITE, lCanvasScale);

		final var lLabelText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;

		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_LEFT, mLeftRectangle, componentZDepth, lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_SCROLLBAR_RIGHT, mRightRectangle, componentZDepth - 0.01f, lColor);

		textFont.drawText(lLabelText, mX + ARROW_PADDING, mY + ARROW_PADDING, componentZDepth, ColorConstants.WHITE, lCanvasScale);

		if (mSelectedIndex >= 0 && mSelectedIndex < mItems.size()) {
			final var lItem = mItems.get(mSelectedIndex);
			final var lItemDisplayName = lItem.displayName;
			final var lNoTextWidth = textFont.getStringWidth(lItemDisplayName, lCanvasScale);
			textFont.drawText(lItemDisplayName, mX + mW / 2f - lNoTextWidth / 2f, mY + mH - ARROW_SIZE - ARROW_PADDING, componentZDepth, ColorConstants.WHITE, lCanvasScale);
		} else {
			final var lNoItemSelectedText = "nothing";
			final var lNoTextWidth = textFont.getStringWidth(lNoItemSelectedText, lCanvasScale);
			textFont.drawText(lNoItemSelectedText, mX + mW / 2f - lNoTextWidth / 2f, mY + mH - ARROW_SIZE - ARROW_PADDING, componentZDepth, ColorConstants.WHITE, lCanvasScale);
		}

	}
}

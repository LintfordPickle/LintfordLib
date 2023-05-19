package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;

public class UiButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7704116387039308007L;

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mButtonLabel;
	private boolean mIsClicked;
	private float mClickTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiButton(final UiWindow pParentWindow) {
		super(pParentWindow);

		mButtonLabel = NO_LABEL_TEXT;
		mW = 200;
		mH = 25;
	}
	
	public UiButton(final UiWindow pParentWindow, String labelText) {
		this(pParentWindow);

		mButtonLabel = labelText;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!isEnabled())
			return false;

		if (!mIsClicked && intersectsAA(core.HUD().getMouseCameraSpace())) {
			mIsHoveredOver = true;

			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				mIsClicked = true;
				final var MINIMUM_CLICK_TIMER = 200;

				if (mCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
					mClickTimer = 0;
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
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		if (!mIsVisible)
			return;

		final float lColorMod = !mIsEnabled ? .4f : mIsHoveredOver ? .9f : 1.f;

		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		final var lTileSize = 32;
		if(mW < lTileSize) {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, mX, mY, mW, mH, componentZDepth, lColor);
		} else {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_LEFT, mX, mY, lTileSize, mH, componentZDepth, lColor);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, mX + lTileSize, mY, mW - lTileSize * 2, mH, componentZDepth, lColor);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_RIGHT, mX + mW - lTileSize, mY, lTileSize, mH, componentZDepth, lColor);
		}

		final var lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final var lTextWidth = textFont.getStringWidth(lButtonText);

		textFont.drawText(lButtonText, mX + mW / 2f - lTextWidth / 2f, mY + mH / 2f - textFont.fontHeight() / 2f, componentZDepth, ColorConstants.WHITE, 1.f);
	}
}

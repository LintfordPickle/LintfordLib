package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;

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
		return mUiWidgetListenerUid;
	}

	public void buttonListenerID(final int pNewLabel) {
		mUiWidgetListenerUid = pNewLabel;
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

		if (!mIsClicked && intersectsAA(core.HUD().getMouseCameraSpace())) {
			mIsHoveredOver = true;

			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				mIsClicked = true;
				final var MINIMUM_CLICK_TIMER = 200;

				if (mUiWidgetListenerCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
					mClickTimer = 0;
					mUiWidgetListenerCallback.widgetOnClick(core.input(), mUiWidgetListenerUid);

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

		final var lColorMod = !mIsEnabled ? .4f : mIsHoveredOver ? .9f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		spriteBatch.begin(core.HUD());
		final var lTileSize = 32;
		if (mW < lTileSize) {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, (int) mX, mY, (int) mW, mH, componentZDepth, lColor);
		} else {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_LEFT, (int) mX, mY, (int) lTileSize, mH, componentZDepth, lColor);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, (int) mX + lTileSize, mY, (int) mW - lTileSize * 2, mH, componentZDepth, lColor);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_RIGHT, (int) mX + (int) mW - lTileSize, mY, lTileSize, mH, componentZDepth, lColor);
		}
		spriteBatch.end();

		final var lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final var lTextWidth = textFont.getStringWidth(lButtonText);

		textFont.begin(core.HUD());
		textFont.drawText(lButtonText, mX + mW / 2f - lTextWidth / 2f, (int) (mY + mH / 2f - textFont.fontHeight() / 2f), componentZDepth, ColorConstants.WHITE, 1.f);
		textFont.end();
	}
}

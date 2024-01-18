package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiButtonToggle extends UIWidget {

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
	private boolean mIsToggledOn;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isToggledOn() {
		return mIsToggledOn;
	}

	public void isToggledOn(boolean isToggledOn) {
		mIsToggledOn = isToggledOn;
	}

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(String pNewLabel) {
		mButtonLabel = pNewLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiButtonToggle(UiWindow pParentWindow) {
		this(pParentWindow, NO_LABEL_TEXT);
	}

	public UiButtonToggle(UiWindow pParentWindow, String label) {
		super(pParentWindow);

		mButtonLabel = label;
		mW = 200;
		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (isEnabled() == false)
			return false;

		if (!mIsClicked && intersectsAA(core.HUD().getMouseCameraSpace())) {
			mIsHoveredOver = true;

			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				mIsClicked = true;
				final var MINIMUM_CLICK_TIMER = 200;

				mIsToggledOn = !mIsToggledOn;

				if (mUiWidgetListenerCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
					mUiWidgetListenerCallback.widgetOnClick(core.input(), mUiWidgetListenerUid);
					mClickTimer = 0;

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
		final var lColorMod = mIsToggledOn ? 0.65f : mIsHoveredOver ? .9f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		final var lTileSize = 32;

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_LEFT, mX, mY, lTileSize, mH, componentZDepth, lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, mX + lTileSize, mY, mW - lTileSize * 2, mH, componentZDepth, lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_RIGHT, mX + mW - lTileSize, mY, lTileSize, mH, componentZDepth, lColor);
		spriteBatch.end();

		final var lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final var lTextWidth = textFont.getStringWidth(lButtonText);

		textFont.begin(core.HUD());
		textFont.drawText(lButtonText, mX + mW / 2f - lTextWidth / 2f, mY + mH / 2f - textFont.fontHeight() / 2f, componentZDepth, ColorConstants.WHITE, 1.f);
		textFont.end();
	}
}

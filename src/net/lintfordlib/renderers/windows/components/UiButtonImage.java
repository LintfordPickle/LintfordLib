package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.SpriteFrame;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.rendering.SharedResources;

public class UiButtonImage extends UIWidget {

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

	private boolean mSuccessLoad;
	private SpriteFrame mSpriteFrame;
	private String spriteName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/// Sets the name of the sprite (from SpriteSheet.Core) to display within this button
	public void spriteName(String spriteName) {
		this.spriteName = spriteName;
		mSuccessLoad = false;
	}

	public String spriteName() {
		return spriteName;
	}

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

	public UiButtonImage() {
		this(NO_LABEL_TEXT);
	}

	public UiButtonImage(String labelText) {
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
	}

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		if (!mIsVisible)
			return;

		final var lColorMod = !mIsEnabled ? .4f : mIsHoveredOver ? .9f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.setColor(lColor);

		final float lTileSize = 32.f;
		if (mW < lTileSize) {
			lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, mX, mY, mW, mH, componentZDepth);
		} else {
			lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_LEFT, mX, mY, lTileSize, mH, componentZDepth);
			lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_MID, mX + lTileSize, mY, mW - lTileSize * 2, mH, componentZDepth);
			lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X1_RIGHT, mX + mW - lTileSize, mY, lTileSize, mH, componentZDepth);
		}

		if (spriteName != null && !mSuccessLoad) {
			mSpriteFrame = coreSpritesheet.getSpriteFrame(spriteName);
			mSuccessLoad = true;
		}

		if (mSpriteFrame != null) {
			lSpriteBatch.draw(coreSpritesheet, mSpriteFrame, mX, mY, mW, mH, componentZDepth);
		}

		lSpriteBatch.end();

		final var lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final var lTextWidth = textFont.getStringWidth(lButtonText);

		textFont.begin(core.HUD());
		textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		textFont.drawText(lButtonText, mX + mW / 2f - lTextWidth / 2f, (int) (mY + mH / 2f - textFont.fontHeight() / 2f), componentZDepth - .01f, 1.f);
		textFont.end();
	}
}

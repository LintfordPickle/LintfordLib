package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UIToggleButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8326753044931375276L;

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	EntryInteractions mCallback;
	private int mClickID;
	private String mButtonLabel;
	private boolean mHoveredOver;
	private boolean mIsToggledOn;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isToggledOn() {
		return mIsToggledOn;
	}

	public void isToggledOn(final boolean pIsToggledOn) {
		mIsToggledOn = pIsToggledOn;
	}

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(final String pNewLabel) {
		mButtonLabel = pNewLabel;
	}

	public int buttonListenerID() {
		return mClickID;
	}

	public void buttonListenerID(final int pNewLabel) {
		mClickID = pNewLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIToggleButton(final UiWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIToggleButton(final UiWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = NO_LABEL_TEXT;
		w = 200;
		h = 25;

		entityColor.r = 0.3f;
		entityColor.g = 0.34f;
		entityColor.b = 0.65f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			mHoveredOver = true;

			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				// Callback to the listener and pass our ID
				if (mCallback != null) {
					mCallback.menuEntryOnClick(pCore.input(), mClickID);
				}

				return true;

			}

		} else {
			mHoveredOver = false;

		}

		return false;
	}

	@Override
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		final float lColorMod = mIsToggledOn ? mHoveredOver ? .9f : 1.f : .3f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_LEFT, x, y, 32, h, pComponentZDepth, lColor);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MID, x + 32, y, w - 64, h, pComponentZDepth, lColor);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_RIGHT, x + w - 32, y, 32, h, pComponentZDepth, lColor);

		if (mButtonLabel != null && mButtonLabel.length() > 0) {
			final float lTextWidth = pTextFont.getStringWidth(mButtonLabel);
			final float lTextHeight = pTextFont.getStringHeight(mButtonLabel);

			pTextFont.drawText(mButtonLabel, x + w / 2.f - lTextWidth / 2.f, y + h / 2f - lTextHeight / 2.f, pComponentZDepth, ColorConstants.WHITE, 1.0f);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions pCallbackObject) {
		mCallback = pCallbackObject;
	}

	public void removeClickListener(final EntryInteractions pCallbackObject) {
		mCallback = null;
	}

}

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
	private int mEntryUid;
	private String mButtonLabel;
	private boolean mHoveredOver;
	private boolean mIsToggledOn;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isToggledOn() {
		return mIsToggledOn;
	}

	public void isToggledOn(final boolean isToggledOn) {
		mIsToggledOn = isToggledOn;
	}

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(final String newLabel) {
		mButtonLabel = newLabel;
	}

	public int buttonListenerID() {
		return mEntryUid;
	}

	public void buttonListenerID(final int newEntryUid) {
		mEntryUid = newEntryUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIToggleButton(final UiWindow parentWindow) {
		this(parentWindow, 0);
	}

	public UIToggleButton(final UiWindow parentWindow, final int entryUid) {
		super(parentWindow);

		mEntryUid = entryUid;

		mButtonLabel = NO_LABEL_TEXT;
		mW = 200;
		mH = 25;

		entityColor.r = 0.3f;
		entityColor.g = 0.34f;
		entityColor.b = 0.65f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mHoveredOver = true;

			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				// Callback to the listener and pass our ID
				if (mCallback != null) {
					mCallback.menuEntryOnClick(core.input(), mEntryUid);
				}

				return true;
			}

		} else {
			mHoveredOver = false;
		}

		return false;
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		final float lColorMod = mIsToggledOn ? mHoveredOver ? .9f : 1.f : .3f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_LEFT, mX, mY, 32, mH, componentZDepth, lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MID, mX + 32, mY, mW - 64, mH, componentZDepth, lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_RIGHT, mX + mW - 32, mY, 32, mH, componentZDepth, lColor);

		if (mButtonLabel != null && mButtonLabel.length() > 0) {
			final float lTextWidth = textFont.getStringWidth(mButtonLabel);
			final float lTextHeight = textFont.getStringHeight(mButtonLabel);

			textFont.drawText(mButtonLabel, mX + mW / 2.f - lTextWidth / 2.f, mY + mH / 2f - lTextHeight / 2.f, componentZDepth, ColorConstants.WHITE, 1.0f);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions callbackObject) {
		mCallback = callbackObject;
	}

	public void removeClickListener(final EntryInteractions callbackObject) {
		mCallback = null;
	}
}

package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UIIconButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5431518038383191422L;

	private static final int CLICK_TIMER = 250;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient EntryInteractions mCallback;
	private transient int mClickID;
	private transient String mButtonLabel;
	private transient boolean mHoveredOver;
	private transient boolean mDrawButtonText;
	private transient float mClickTimer;
	private transient boolean mButtonSolidColorBackground;

	private transient SpriteSheetDefinition mButtonTexture;
	private transient int mSourceFrameIndex;

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
		return mClickID;
	}

	public void buttonListenerID(final int pNewLabel) {
		mClickID = pNewLabel;
	}

	public void setButtonSolidBackgroundColor(Color pColor) {
		mButtonSolidColorBackground = pColor != null;
		entityColor.setFromColor(pColor);
	}

	public boolean setButtonSolidBackgroundColor() {
		return mButtonSolidColorBackground;
	}

	public void drawButtonText(boolean pNewValue) {
		mDrawButtonText = pNewValue;
	}

	public boolean drawButtonText() {
		return mDrawButtonText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIIconButton(final UiWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIIconButton(final UiWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		w = 200;
		h = 25;

		mButtonSolidColorBackground = true;
		mDrawButtonText = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mIsVisible)
			return false;

		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (mClickTimer > CLICK_TIMER) {
				if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
					// Callback to the listener and pass our ID
					if (mCallback != null) {
						mCallback.menuEntryOnClick(pCore.input(), mClickID);

					}

					mClickTimer = 0;

					return true;

				}
			}

		} else {
			mHoveredOver = false;

		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		if (!mIsVisible)
			return;

		super.update(pCore);

		mClickTimer += pCore.appTime().elapsedTimeMilli();

	}

	@Override
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		if (!mIsVisible)
			return;

		if (mButtonSolidColorBackground) {
			drawSolidColorBackground(pCore, pSpriteBatch, pCoreSpritesheet);
		} else if (mButtonTexture != null) {
			drawTextureBackground(pCore, pSpriteBatch);
		}

		// text
		if (mDrawButtonText && mButtonLabel != null && mButtonLabel.length() > 0) {
			final float lTextWidth = pTextFont.getStringWidth(mButtonLabel);

			pTextFont.drawText(mButtonLabel, x + w / 2f - lTextWidth / 2f, y + h / 2f - pTextFont.fontHeight() / 2f, pComponentZDepth, ColorConstants.WHITE, 1f);
		}
	}

	private void drawSolidColorBackground(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet) {
		final float lColorMod = mHoveredOver ? mHoveredOver ? .9f : 1.f : .3f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, -1.0f, lColor);
	}

	private void drawTextureBackground(LintfordCore pCore, SpriteBatch pSpriteBatch) {
		final var lColorMod = mHoveredOver ? .5f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		pSpriteBatch.draw(mButtonTexture, mSourceFrameIndex, x, y, w, h, -1.0f, lColor);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions pCallbackObject, int pClickID) {
		mCallback = pCallbackObject;
		mClickID = pClickID;

	}

	public void removeClickListener(final EntryInteractions pCallbackObject) {
		mCallback = null;
	}

	public void setTextureSource(final SpriteSheetDefinition pSpritesheetDefinition, final int pSpriteFrameIndex) {
		mButtonSolidColorBackground = false;

		mButtonTexture = pSpritesheetDefinition;
		mSourceFrameIndex = pSpriteFrameIndex;
	}
}

package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UiIconToggleButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5431518038383191422L;

	private static final int CLICK_TIMER = 250;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient EntryInteractions mCallback;
	private transient int mEntryUid;
	private transient String mButtonLabel;
	private transient boolean mHoveredOver;
	private transient boolean mDrawButtonText;
	private transient float mClickTimer;
	private transient boolean mIsToggledOn;
	private transient boolean mButtonSolidColorBackground;

	private transient SpriteSheetDefinition mButtonTexture;
	private transient int mSourceFrameIndex;

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

	public void buttonLabel(final String newLabel) {
		mButtonLabel = newLabel;
	}

	public int buttonUid() {
		return mEntryUid;
	}

	public void buttionUid(final int entryUid) {
		mEntryUid = entryUid;
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

	public UiIconToggleButton(final UiWindow parentWindow) {
		this(parentWindow, 0);
	}

	public UiIconToggleButton(final UiWindow parentWindow, final int entryUid) {
		super(parentWindow);

		mEntryUid = entryUid;

		mW = 200;
		mH = 25;

		mButtonSolidColorBackground = true;
		mDrawButtonText = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!mIsVisible)
			return false;

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (mClickTimer > CLICK_TIMER) {
				if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
					// Callback to the listener and pass our ID
					if (mCallback != null) {
						mCallback.menuEntryOnClick(core.input(), mEntryUid);
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
	public void update(LintfordCore core) {
		if (!mIsVisible)
			return;

		super.update(core);

		mClickTimer += core.appTime().elapsedTimeMilli();
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		if (!mIsVisible)
			return;

		if (mButtonSolidColorBackground) {
			drawSolidColorBackground(core, spriteBatch, coreSpritesheetDefinition);
		} else if (mButtonTexture != null) {
			drawTextureBackground(core, spriteBatch);
		}

		if (mDrawButtonText && mButtonLabel != null && mButtonLabel.length() > 0) {
			final float lTextWidth = textFont.getStringWidth(mButtonLabel);
			textFont.drawText(mButtonLabel, mX + mW / 2f - lTextWidth / 2f, mY + mH / 2f - textFont.fontHeight() / 2f, componentZDepth, ColorConstants.WHITE, 1f);
		}
	}

	private void drawSolidColorBackground(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet) {
		final float lColorMod = mHoveredOver ? mHoveredOver ? .9f : 1.f : .3f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, -1.0f, lColor);
	}

	private void drawTextureBackground(LintfordCore pCore, SpriteBatch pSpriteBatch) {
		final var lColorMod = mHoveredOver ? .5f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		pSpriteBatch.draw(mButtonTexture, mSourceFrameIndex, mX, mY, mW, mH, -1.0f, lColor);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions callbackObject, int entryUid) {
		mCallback = callbackObject;
		mEntryUid = entryUid;
	}

	public void removeClickListener(final EntryInteractions callbackObject) {
		mCallback = null;
	}

	public void setTextureSource(final SpriteSheetDefinition spritesheetDefinition, final int spriteFrameIndex) {
		mButtonSolidColorBackground = false;

		mButtonTexture = spritesheetDefinition;
		mSourceFrameIndex = spriteFrameIndex;
	}
}

package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuToggleEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 51472065385268475L;
	private static final float SPACE_BETWEEN_TEXT = 15;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsChecked;
	private final String mSeparator = " : ";

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void label(String pNewLabel) {
		mText = pNewLabel;
	}

	public String label() {
		return mText;
	}

	public void entryText(String pNewValue) {
		mText = pNewValue;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean pNewValue) {
		mIsChecked = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuToggleEntry(ScreenManager pScreenManager, BaseLayout pParentlayout) {
		super(pScreenManager, pParentlayout, "");

		mHighlightOnHover = false;
		mDrawBackground = false;

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mCoreSpritesheet = null;

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mEnabled) {

					// TODO: Play menu click sound

					mParentLayout.parentScreen.setFocusOn(pCore, this, true);

					mIsChecked = !mIsChecked;

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);

					}

				}

			} else {
				hasFocus(true);

			}

			return true;

		} else {
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		final double lDeltaTime = pCore.appTime().elapsedTimeMilli() / 1000.;

		// Check if tool tips are enabled.
		if (mToolTipEnabled) {
			mToolTipTimer += lDeltaTime;
		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		final var lParentScreen = mParentLayout.parentScreen;
		final var lFont = lParentScreen.font();
		if (lFont == null)
			return;

		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lLabelWidth = lFont.getStringWidth(mText, lUiTextScale);
		final float lTextHeight = lFont.fontHeight() * lUiTextScale;
		final float lSeparatorHalfWidth = lFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		final var lSpriteBatch = lParentScreen.spriteBatch();

		final float lTileSize = 32;

		final var lScreenOffset = pScreen.screenPositionOffset();
		final var lParentScreenAlpha = pScreen.screenColor.a;

		entryColor.setFromColor(lParentScreen.screenColor);
		textColor.a = lParentScreenAlpha;

		mZ = pParentZDepth;

		if (mHoveredOver) {
			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - w / 2, lScreenOffset.y + centerY() - h / 2, 32, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (w / 2) + 32, lScreenOffset.y + centerY() - h / 2, w - 64, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (w / 2) - 32, lScreenOffset.y + centerY() - h / 2, 32, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();

		}

		lSpriteBatch.begin(pCore.HUD());

		// Render the check box (either ticked or empty)
		if (mIsChecked)
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_TICK, lScreenOffset.x + x + w / 2 + 16, lScreenOffset.y + y + h / 2 - lTileSize / 2, lTileSize, lTileSize, mZ, entryColor);
		else
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_CROSS, lScreenOffset.x + centerX() + lTileSize / 2, lScreenOffset.y + y + h / 2 - lTileSize / 2, lTileSize, lTileSize, mZ, entryColor);

		lSpriteBatch.end();

		lFont.begin(pCore.HUD());
		lFont.drawText(mText, lScreenOffset.x + x + w / 2 - lLabelWidth - SPACE_BETWEEN_TEXT - lSeparatorHalfWidth, lScreenOffset.y + y + h / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
		lFont.drawText(mSeparator, lScreenOffset.x + x + w / 2 - lSeparatorHalfWidth, lScreenOffset.y + y + h / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);

		if (mIsChecked) {
			lFont.drawText("Enabled", lScreenOffset.x + x + w / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + y + h / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
		} else {
			lFont.drawText("Disabled", lScreenOffset.x + x + w / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + y + h / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
		}

		lFont.end();

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lSpriteBatch, mInfoIconDstRectangle, lParentScreenAlpha);
		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lSpriteBatch, mWarnIconDstRectangle, lParentScreenAlpha);
		}

		drawDebugCollidableBounds(pCore, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;

		} else {
			mFocusLocked = false;

		}
	}
}

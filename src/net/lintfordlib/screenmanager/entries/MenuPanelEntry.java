package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.TextureBatch9Patch;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuPanelEntry extends MenuEntry {

	private static final long serialVersionUID = -8125859270010821953L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String text() {
		return mText;
	}

	public void text(String panelText) {
		mText = panelText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuPanelEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, null);

		mTopPadding = 5.f;
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		mZ = parentZDepth;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			final var lTileSize = 32.f;

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorRGBA(1.f, 1.f, 1.f, lParentScreenAlpha);
			TextureBatch9Patch.drawBackground(lSpriteBatch, mCoreSpritesheet, lTileSize, lScreenOffset.x + mX, lScreenOffset.y + mY, mW, mH, false, .01f);
			lSpriteBatch.end();
		}

		if (mText != null && mText.length() > 0) {
			final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lMenuFont = mParentScreen.font();

			if (lMenuFont != null) {
				lMenuFont.begin(core.HUD());
				final float lStringWidth = lMenuFont.getStringWidth(mText, lUiTextScale);
				final var lTextColor = ColorConstants.getTempColorCopy(mHasFocus ? ColorConstants.MenuEntryHighlightColor : ColorConstants.TextHeadingColor);
				lTextColor.a = lParentScreenAlpha;

				lMenuFont.setTextColor(lTextColor);
				lMenuFont.drawText(mText, lScreenOffset.x + centerX() - lStringWidth * 0.5f, lScreenOffset.y + mY + paddingTop(), mZ, lUiTextScale);
				lMenuFont.end();
			}
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mIsActive = !mIsActive;

		if (mIsActive)
			mParentScreen.onMenuEntryActivated(this);
		else
			mParentScreen.onMenuEntryDeactivated(this);

	}

	@Override
	public void resetCoolDownTimer() {
		mInputTimer = 50;
	}
}

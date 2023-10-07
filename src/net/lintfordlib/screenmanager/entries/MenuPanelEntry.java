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

	private String mText;

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
		// super.draw(core, screen, parentZDepth);

		mZ = parentZDepth;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			final int ts = 32;

			final var lColor = ColorConstants.getWhiteWithAlpha(lParentScreenAlpha);

			final int x = (int) (lScreenOffset.x + mX);
			final int y = (int) (lScreenOffset.y + mY);
			final int w = (int) mW;
			final int h = (int) mH;

			lSpriteBatch.begin(core.HUD());
			TextureBatch9Patch.drawBackground(core, lSpriteBatch, mCoreSpritesheet, ts, x, y, w, h, lColor, false, -0.01f);
			lSpriteBatch.end();
		}

		if (mText != null && mText.length() > 0) {
			final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lMenuFont = mParentScreen.font();

			if (lMenuFont != null) {
				lMenuFont.begin(core.HUD());
				final float lStringWidth = lMenuFont.getStringWidth(mText, lUiTextScale);
				final var lTextColor = ColorConstants.getColor(mHasFocus ? ColorConstants.FLAME : ColorConstants.TextHeadingColor);
				lTextColor.a = lParentScreenAlpha;
				lMenuFont.drawText(mText, lScreenOffset.x + centerX() - lStringWidth * 0.5f, lScreenOffset.y + mY + paddingTop(), mZ, lTextColor, lUiTextScale);
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
		mClickTimer = 50;
	}
}

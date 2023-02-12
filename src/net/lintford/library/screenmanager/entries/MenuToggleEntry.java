package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

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

	public void label(String label) {
		mText = label;
	}	

	public String label() {
		return mText;
	}

	public void entryText(String text) {
		mText = text;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean isChecked) {
		mIsChecked = isChecked;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuToggleEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mHighlightOnHover = false;
		mDrawBackground = false;
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void unloadResources() {
		super.unloadResources();

		mCoreSpritesheet = null;

	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		if (!mEnableUpdateDraw)
			return;

		super.update(core, screen);

		final double lDeltaTime = core.appTime().elapsedTimeMilli() / 1000.;

		if (mToolTipEnabled)
			mToolTipTimer += lDeltaTime;

	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!mEnableUpdateDraw)
			return;

		final var lTextBoldFont = mParentScreen.fontBold();
		final float lUiTextScale = mParentScreen.uiTextScale();

		final float lLabelWidth = lTextBoldFont.getStringWidth(mText, lUiTextScale);
		final float lTextHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final float lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		final var lSpriteBatch = mParentScreen.spriteBatch();

		final float lTileSize = 32;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		entryColor.setFromColor(mParentScreen.screenColor);
		textColor.a = lParentScreenAlpha;

		mZ = parentZDepth;

		if (mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}

		lSpriteBatch.begin(core.HUD());

		// Render the check box (either ticked or empty)
		if (mIsChecked)
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_TICK, lScreenOffset.x + mX + mW / 2 + 16, lScreenOffset.y + mY + mH / 2 - lTileSize / 2, lTileSize, lTileSize, mZ, entryColor);
		else
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_CROSS, lScreenOffset.x + centerX() + lTileSize / 2, lScreenOffset.y + mY + mH / 2 - lTileSize / 2, lTileSize, lTileSize, mZ, entryColor);

		lSpriteBatch.end();

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mText, lScreenOffset.x + mX + mW / 2 - lLabelWidth - SPACE_BETWEEN_TEXT - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);

		if (mIsChecked)
			lTextBoldFont.drawText("Enabled", lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
		else
			lTextBoldFont.drawText("Disabled", lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);

		lTextBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, lParentScreenAlpha);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, lParentScreenAlpha);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mIsChecked = !mIsChecked;
	}
}

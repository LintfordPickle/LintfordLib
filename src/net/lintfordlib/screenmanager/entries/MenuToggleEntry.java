package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

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
	private boolean mShowCheckedText;
	private final String mSeparator = " : ";
	private String mEnabledText;
	private String mDisabledText;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean showCheckedText() {
		return mShowCheckedText;
	}

	public void showCheckedText(boolean showCheckedText) {
		mShowCheckedText = showCheckedText;
	}

	public void setCheckedText(String disabledText, String enabledText) {
		mDisabledText = disabledText;
		mEnabledText = enabledText;
	}

	public void label(String label) {
		mText = label;
	}

	public String label() {
		return mText;
	}

	@Override
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

		contextHintState.buttonAHint = "toggle";
		contextHintState.keyReturnHint = "toggle";

		mDisabledText = "Disabled";
		mEnabledText = "Enabled";
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void unloadResources() {
		super.unloadResources();

		mCoreSpritesheet = null;

	}

	public boolean onHandleMouseInput(LintfordCore core) {
		if (mReadOnly)
			return false;

		if (mParentScreen == null)
			return false;

		if (!core.input().mouse().isMouseMenuSelectionEnabled()) {
			mIsMouseOver = false;
			return false;
		}

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = false;
			return false;
		}

		mIsMouseOver = true;

		if (!mHasFocus && mCanHaveFocus)
			mParentScreen.setFocusOnEntry(this);

		if (mToolTipEnabled)
			mToolTipTimer += core.appTime().elapsedTimeMilli();

		if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
			onClick(core.input());

			return true;
		}

		return false;
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
		final var lUiTextScale = mParentScreen.uiTextScale();

		final var lLabelWidth = lTextBoldFont.getStringWidth(mText, lUiTextScale);
		final var lTextHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final var lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;

		final var lSpriteBatch = mParentScreen.spriteBatch();

		final var lTileSize = 32.f;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		entryColor.setFromColor(mParentScreen.screenColor);
		textColor.a = lParentScreenAlpha;

		mZ = parentZDepth;

		if (mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2 + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + mW / 2 - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}

		lSpriteBatch.begin(core.HUD());

		// Render the check box (either ticked or empty)
		if (mIsChecked)
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_TICK, lScreenOffset.x + mX + mW / 2 + 16, lScreenOffset.y + mY, lTileSize, lTileSize, mZ, entryColor);
		else
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_EMPTY, lScreenOffset.x + centerX() + lTileSize / 2, lScreenOffset.y + mY + mH / 2 - lTileSize / 2, lTileSize, lTileSize, mZ, entryColor);

		lSpriteBatch.end();

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mText, lScreenOffset.x + mX + mW / 2 - lLabelWidth - SPACE_BETWEEN_TEXT - lSeparatorHalfWidth, lScreenOffset.y + mY + 32 / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);

		if (mShowCheckedText) {
			if (mIsChecked)
				lTextBoldFont.drawText(mEnabledText, lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
			else
				lTextBoldFont.drawText(mDisabledText, lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, textColor, lUiTextScale, -1);
		}

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
		mIsChecked = !mIsChecked;

		super.onClick(inputManager);
	}
}

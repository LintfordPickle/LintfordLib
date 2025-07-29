package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
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

	private static final String SEPARATOR_STRING = " : ";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsChecked;
	private boolean mShowCheckedText;
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
		this(screenManager, parentScreen, "");
	}

	public MenuToggleEntry(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, label);

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

	@Override
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
		final var lSeparatorHalfWidth = lTextBoldFont.getStringWidth(SEPARATOR_STRING, lUiTextScale) * 0.5f;

		final var spriteBatch = mParentScreen.spriteBatch();

		final var lTileSize = Math.min(32, mH);

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		entryColor.setFromColor(mParentScreen.screenColor);
		textColor.a = lParentScreenAlpha;

		mZ = parentZDepth;

		if (mHasFocus)
			renderHighlight(core, screen, true, spriteBatch);

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(entryColor);
		// Render the check box (either ticked or empty)
		if (mIsChecked)
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_TICK, lScreenOffset.x + centerX() + 8, lScreenOffset.y + mY, lTileSize, lTileSize, mZ);
		else
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_EMPTY, lScreenOffset.x + centerX() + 8, lScreenOffset.y + mY + mH / 2 - lTileSize / 2, lTileSize, lTileSize, mZ);

		spriteBatch.end();

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.setTextColor(textColor);
		lTextBoldFont.drawText(mText, lScreenOffset.x + mX + mW / 2 - lLabelWidth - SPACE_BETWEEN_TEXT - lSeparatorHalfWidth, lScreenOffset.y + mY + 32.f / 2.f - lTextHeight * 0.5f, mZ, lUiTextScale, -1);
		lTextBoldFont.drawText(SEPARATOR_STRING, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, lUiTextScale, -1);

		if (mShowCheckedText) {
			if (mIsChecked)
				lTextBoldFont.drawText(mEnabledText, lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, lUiTextScale, -1);
			else
				lTextBoldFont.drawText(mDisabledText, lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + lTileSize * 2, lScreenOffset.y + mY + mH / 2 - lTextHeight * 0.5f, mZ, lUiTextScale, -1);
		}

		lTextBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, lParentScreenAlpha);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, lParentScreenAlpha);

		drawDebugCollidableBounds(core, spriteBatch);
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

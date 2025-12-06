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
		if (mReadOnly || !enabled())
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

		final var textFont = mParentScreen.font();
		final var uiTextScale = mParentScreen.uiTextScale();

		final var labelWidth = textFont.getStringWidth(mText, uiTextScale);
		final var textHeight = textFont.fontHeight() * uiTextScale;
		final var separatorHalfWidth = textFont.getStringWidth(SEPARATOR_STRING, uiTextScale) * 0.5f;

		final var spriteBatch = mParentScreen.spriteBatch();

		final var tileSize = Math.min(32, mH);

		final var screenOffset = screen.screenPositionOffset();
		final var parentScreenAlpha = screen.screenColor.a;

		entryColor.setFromColor(mParentScreen.screenColor);
		textColor.a = parentScreenAlpha;

		mZ = parentZDepth;

		if (mHasFocus)
			renderHighlight(core, screen, true, spriteBatch);

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(entryColor);

		if (mIsChecked)
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_TICK, screenOffset.x + centerX() + 8, screenOffset.y + mY, tileSize, tileSize, mZ);
		else
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_EMPTY, screenOffset.x + centerX() + 8, screenOffset.y + mY + mH / 2 - tileSize / 2, tileSize, tileSize, mZ);

		spriteBatch.end();

		textFont.begin(core.HUD());
		textFont.setTextColor(textColor);
		textFont.drawText(mText, screenOffset.x + mX + mW / 2 - labelWidth - separatorHalfWidth, screenOffset.y + mY + mH / 2.f - textHeight * 0.5f, mZ, uiTextScale, -1);
		textFont.drawText(SEPARATOR_STRING, screenOffset.x + mX + mW / 2 - separatorHalfWidth, screenOffset.y + mY + mH / 2 - textHeight * 0.5f, mZ, uiTextScale, -1);

		if (mShowCheckedText) {
			if (mIsChecked)
				textFont.drawText(mEnabledText, screenOffset.x + mX + mW / 2 + separatorHalfWidth + tileSize * 2, screenOffset.y + mY + mH / 2 - textHeight * 0.5f, mZ, uiTextScale, -1);
			else
				textFont.drawText(mDisabledText, screenOffset.x + mX + mW / 2 + separatorHalfWidth + tileSize * 2, screenOffset.y + mY + mH / 2 - textHeight * 0.5f, mZ, uiTextScale, -1);
		}

		textFont.end();

		drawGamepadIcon(core, spriteBatch, screenOffset.x + mX + mW - 16, screenOffset.y + mY + mH - 16, parentScreenAlpha);

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, parentScreenAlpha);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, parentScreenAlpha);

		drawDebugCollidableBounds(core, spriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		mIsChecked = !mIsChecked;

		if (mClickListener != null)
			mClickListener.onMenuEntryChanged(this);

		super.onClick(inputManager);
	}
}

package net.lintford.library.screenmanager.entries;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.EventAction;
import net.lintford.library.core.input.IKeyInputCallback;
import net.lintford.library.core.input.InputHelper;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuKeyBindEntry extends MenuEntry implements IKeyInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -6246272207476797676L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsDirty;
	private String mBoundKeyText;
	private float mPadding = 15f;
	private boolean mShow;
	private final EventAction mEventAction;
	private boolean mBindingKey;
	private float mCaretFlashTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public EventAction eventAction() {
		return mEventAction;
	}

	/** Padding is applied when the label is either aligned left or right (not when centered). */
	public float padding() {
		return mPadding;
	}

	/** Padding is applied when the label is either aligned left or right (not when centered). */
	public void padding(float newValue) {
		mPadding = newValue;
	}

	public boolean show() {
		return mShow;
	}

	public void show(boolean newValue) {
		mShow = newValue;
	}

	public void label(String newLabel) {
		mText = newLabel;
	}

	public String label() {
		return mText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuKeyBindEntry(ScreenManager screenManager, BaseLayout parentLayout, EventAction eventAction) {
		super(screenManager, parentLayout, "");

		mEventAction = eventAction;
		mDrawBackground = false;
		mText = "Add your message";
		mShow = true;

		mCanHaveFocus = false;
		mCanHoverOver = true;

		mIsDirty = true;
		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unloadResources() {
		super.unloadResources();

		mCoreSpritesheet = null;

	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {
			if (!core.input().keyboard().isSomeComponentCapturingInputKeys()) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Changing key bind for " + mEventAction.eventActionUid());
				core.input().keyboard().StartKeyInputCapture(this);
				mBindingKey = true;
			}
		}

		return super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen, boolean isSelected) {
		super.update(core, screen, isSelected);

		if (mIsDirty) {
			mBoundKeyText = InputHelper.getGlfwPrintableKeyFromKeyCode(mEventAction.getBoundKeyCode()).toUpperCase();
			mIsDirty = false;
		}
	}

	@Override
	public void draw(LintfordCore core, Screen screen, boolean isSelected, float parentZDepth) {
		if (!enabled())
			return;

		if (mEventAction == null)
			return;

		final var lParentScreen = mParentLayout.parentScreen;
		final var lTextBoldFont = lParentScreen.fontBold();

		entryColor.r = mHoveredOver ? (ColorConstants.SecondaryColor.r) : .1f;
		entryColor.g = mHoveredOver ? (ColorConstants.SecondaryColor.g) : .1f;
		entryColor.b = mHoveredOver ? (ColorConstants.SecondaryColor.b) : .1f;
		entryColor.a = mHoveredOver ? lParentScreen.screenColor.a : 0.26f;

		textColor.a = lParentScreen.screenColor.a;

		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lLabelWidth = lTextBoldFont.getStringWidth(mText, lUiTextScale);
		final float lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;

		final var lSpriteBatch = lParentScreen.spriteBatch();

		if (mDrawBackground) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, parentZDepth + .15f, entryColor);
			lSpriteBatch.end();

		} else if (mHoveredOver) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, centerX() - mW / 2, centerY() - mH / 2, 32, mH, parentZDepth + .15f, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, centerX() - (mW / 2) + 32, centerY() - mH / 2, mW - 64, mH, parentZDepth + .15f, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, centerX() + (mW / 2) - 32, centerY() - mH / 2, 32, mH, parentZDepth + .15f, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}

		float lX = mX + mW / 2;

		mCaretFlashTimer += core.appTime().elapsedTimeMilli() * 0.001f;

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mText, lX - lLabelWidth - 20.f, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, textColor, lUiTextScale);
		lTextBoldFont.drawText(":", lX - 5.f, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, textColor, lUiTextScale);

		if (mBindingKey) {
			final String lBoundKeyText = "|";
			final float lColorMod = .5f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, parentZDepth + .15f, lColor);
			lSpriteBatch.end();

			if (mCaretFlashTimer % 1.f > .5f) {
				lTextBoldFont.drawText(lBoundKeyText, lX + 20.f, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, textColor, lUiTextScale);
			}

		} else if (mBoundKeyText != null && mBoundKeyText.length() > 0) {
			lTextBoldFont.drawText(mBoundKeyText, lX + 20.f, mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, textColor, lUiTextScale);
		}

		lTextBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, lParentScreen.screenColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, lParentScreen.screenColor.a);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, mZ, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void keyInput(int key, int scanCode, int action, int mods) {
		if (mHasFocus) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "key bind invoke " + mEventAction.eventActionUid() + " called to " + GLFW.glfwGetKeyName(GLFW.glfwGetKeyScancode(key), scanCode));
			mEventAction.boundKeyCode(key);
			mBindingKey = false;
			mHasFocus = false;
			mIsDirty = true;
		}
	}

}

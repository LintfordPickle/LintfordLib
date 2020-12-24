package net.lintford.library.screenmanager.entries;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.ColorConstants;
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

	private final EventAction eventAction;
	private boolean mBindingKey;
	private float mCaretFlashTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public EventAction eventAction() {
		return eventAction;
	}

	/** Padding is applied when the label is either aligned left or right (not when centered). */
	public float padding() {
		return mPadding;
	}

	/** Padding is applied when the label is either aligned left or right (not when centered). */
	public void padding(float pNewValue) {
		mPadding = pNewValue;
	}

	@Override
	public boolean hasFocus() {
		return super.hasFocus();
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		if (pNewValue) {

		}

		super.hasFocus(pNewValue);
	}

	public boolean show() {
		return mShow;
	}

	public void show(boolean pNewValue) {
		mShow = pNewValue;
	}

	public void label(String pNewLabel) {
		mText = pNewLabel;
	}

	public String label() {
		return mText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuKeyBindEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, EventAction pEventAction) {
		super(pScreenManager, pParentLayout, "");

		eventAction = pEventAction;
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
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUITexture = pResourceManager.textureManager().textureCore();

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mUITexture = null;

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {
			if (!pCore.input().keyboard().isSomeComponentCapturingInputKeys()) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Changing key bind for " + eventAction.eventActionUid);
				pCore.input().keyboard().StartKeyInputCapture(this);
				mBindingKey = true;

			}

		}

		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		if (mIsDirty) {
			mBoundKeyText = InputHelper.getGlfwPrintableKeyFromKeyCode(eventAction.getBoundKeyCode()).toUpperCase();
			mIsDirty = false;

		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!enabled())
			return;

		if (eventAction == null)
			return;

		final var lParentScreen = mParentLayout.parentScreen;
		final var lFont = lParentScreen.font();

		entryColor.r = mHoveredOver ? (ColorConstants.SecondaryColor.r) : .1f;
		entryColor.g = mHoveredOver ? (ColorConstants.SecondaryColor.g) : .1f;
		entryColor.b = mHoveredOver ? (ColorConstants.SecondaryColor.b) : .1f;
		entryColor.a = mHoveredOver ? lParentScreen.screenColor.a : 0.26f;

		textColor.a = lParentScreen.screenColor.a;

		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mText, lUiTextScale);
		final float lFontHeight = lFont.bitmap().fontHeight() * lUiTextScale;

		final var lTextureBatch = lParentScreen.textureBatch();

		if (mDrawBackground) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, pParentZDepth + .15f, entryColor);
			lTextureBatch.end();

		} else if (mHoveredOver) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, pParentZDepth + .15f, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, pParentZDepth + .15f, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, pParentZDepth + .15f, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.end();

		}

		float lX = x + w / 2;

		mCaretFlashTimer += pCore.appTime().elapsedTimeMilli() * 0.001f;

		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);

		lFont.draw(mText, lX - lLabelWidth - 20.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, textColor, lUiTextScale);
		lFont.draw(":", lX - 5.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, textColor, lUiTextScale);

		if (mBindingKey) {
			final String lBoundKeyText = "|";
			final float lColorMod = .5f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, pParentZDepth + .15f, lColor);
			lTextureBatch.end();

			if (mCaretFlashTimer % 1.f > .5f) {
				lFont.draw(lBoundKeyText, lX + 20.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, textColor, lUiTextScale);
			}

		} else if (mBoundKeyText != null && mBoundKeyText.length() > 0) {
			lFont.draw(mBoundKeyText, lX + 20.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, textColor, lUiTextScale);

		}

		lFont.end();

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lTextureBatch, mInfoIconDstRectangle, lParentScreen.screenColor.a);

		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lTextureBatch, mWarnIconDstRectangle, lParentScreen.screenColor.a);

		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, ColorConstants.Debug_Transparent_Magenta);
			lTextureBatch.end();

		}

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void keyInput(int pKey, int pScanCode, int pAction, int pMods) {
		if (mHasFocus) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "key bind invoke " + eventAction.eventActionUid + " called to " + GLFW.glfwGetKeyName(GLFW.glfwGetKeyScancode(pKey), pScanCode));
			eventAction.boundKeyCode = pKey;
			mBindingKey = false;
			mHasFocus = false;
			mIsDirty = true;

		}
	}

}

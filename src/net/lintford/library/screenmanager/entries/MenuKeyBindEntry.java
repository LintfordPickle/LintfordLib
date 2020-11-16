package net.lintford.library.screenmanager.entries;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.input.EventAction;
import net.lintford.library.core.input.IKeyInputCallback;
import net.lintford.library.core.input.InputHelper;
import net.lintford.library.screenmanager.MenuEntry;
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

	private float mPadding = 15f;
	private boolean mShow;
	private float mR, mG, mB;

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

	public void labelColor(float pR, float pG, float pB) {
		mR = pR;
		mG = pG;
		mB = pB;
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
		mR = mG = mB = 0.94f;

		// mBottomPadding = 10.f;

		mCanHaveFocus = false;
		mCanHoverOver = false;

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
	public void updateStructure() {
		super.updateStructure();

		// TODO: This -50 is because of the scrollbar - this is why I needed to keep the padding :(
		w = Math.min(mParentLayout.w() - 50f, mMaxWidth);

		final var lParentScreen = mParentLayout.parentScreen;
		final var lFont = lParentScreen.font();
		if (lFont == null)
			return;

		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lFontHeight = lFont.bitmap().fontHeight() * lUiTextScale;
		h = lFontHeight * lUiTextScale;

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mEnabled)
			return false;

		if (mHasFocus) {
			if (!pCore.input().keyboard().isSomeComponentCapturingInputKeys()) {
				System.out.println("changing key bind for " + eventAction.eventActionUid);
				pCore.input().keyboard().StartKeyInputCapture(this);
				mBindingKey = true;

			}

		}

		return super.handleInput(pCore);
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!enabled())
			return;

		if (eventAction == null)
			return;

		final var lParentScreen = mParentLayout.parentScreen;
		final var lFont = lParentScreen.font();

		final float lAlpha = 1f;
		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mText, lUiTextScale);
		final float lFontHeight = lFont.bitmap().fontHeight() * lUiTextScale;

		final var lTextureBatch = lParentScreen.textureBatch();

		if (mDrawBackground) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, pParentZDepth + .15f, 0.1f, 0.1f, 0.1f, lAlpha);
			lTextureBatch.end();

		} else if (mHoveredOver) {
			final float lHoveredColorHighlightR = 204.f / 255.f;
			final float lHoveredColorHighlightG = 115.f / 255.f;
			final float lHoveredColorHighlightB = 102.f / 255.f;

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, pParentZDepth + .15f, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, pParentZDepth + .15f, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, pParentZDepth + .15f, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.end();

		}

		float lX = x + w / 2; // Center label

		mCaretFlashTimer += pCore.appTime().elapsedTimeMilli() * 0.001f;

		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);

		lFont.draw(mText, lX - lLabelWidth - 20.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, mR, mG, mB, lParentScreen.a(), lUiTextScale);
		lFont.draw(":", lX - 5.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, mR, mG, mB, lParentScreen.a(), lUiTextScale);

		if (mBindingKey) {
			final String lBoundKeyText = "|";

			final float lBindingColorHighlightR = 204.f / 255.f;
			final float lBindingColorHighlightG = 115.f / 255.f;
			final float lBindingColorHighlightB = 102.f / 255.f;

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, pParentZDepth + .15f, lBindingColorHighlightR, lBindingColorHighlightG, lBindingColorHighlightB, lAlpha);
			lTextureBatch.end();

			if (mCaretFlashTimer % 1.f > .5f) {

				lFont.draw(lBoundKeyText, lX + 20.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, mR, mG, mB, lParentScreen.a(), lUiTextScale);
			}

		} else {
			final String lBoundKeyText = InputHelper.getGlfwPrintableKeyFromKeyCode(eventAction.getBoundKeyCode());
			lFont.draw(lBoundKeyText, lX + 20.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, mR, mG, mB, lParentScreen.a(), lUiTextScale);

		}

		lFont.end();

		if (mShowInfoIcon) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 192, 160, 32, 32, mInfoIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.end();

		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, lAlpha);
			lTextureBatch.end();

		}

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void keyInput(int pKey, int pScanCode, int pAction, int pMods) {
		if (mHasFocus) {
			System.out.println("key bind invoke " + eventAction.eventActionUid + " called to " + GLFW.glfwGetKeyName(GLFW.glfwGetKeyScancode(pKey), pScanCode));
			eventAction.boundKeyCode = pKey;
			mBindingKey = false;
			mHasFocus = false;
		}
	}

}

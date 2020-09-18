package net.lintford.library.screenmanager.entries;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuLabelValueEntry extends MenuEntry {

	private static final long serialVersionUID = -6246272207476797676L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Texture mUITexture;

	private float mPadding = 15f;
	private boolean mShow;
	private float mR, mG, mB;
	private boolean mDrawTextShadow;
	private boolean mTrimText;
	private String mValueText;
	private boolean mDrawBackground;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void valueText(String pValueText) {
		mValueText = pValueText;
	}

	public String valueText() {
		return mValueText;
	}

	public boolean trimText() {
		return mTrimText;
	}

	public void trimText(boolean pNewValue) {
		mTrimText = pNewValue;
	}

	public boolean enableTextShadow() {
		return mDrawTextShadow;
	}

	public void enableTextShadow(boolean pNewValue) {
		mDrawTextShadow = pNewValue;
	}

	public boolean enableBackground() {
		return mDrawBackground;
	}

	public void enableBackground(boolean pNewValue) {
		mDrawBackground = pNewValue;
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

	public MenuLabelValueEntry(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout, "");

		mDrawBackground = false;
		mText = "Add your message";
		mShow = true;
		mR = mG = mB = 0.94f;

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
		w = Math.min(mParentLayout.w() - 50f, MENUENTRY_MAX_WIDTH);

		final MenuScreen lParentScreen = mParentLayout.parentScreen();
		final FontUnit lFont = lParentScreen.font();
		if (lFont == null)
			return;

		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final float lFontHeight = lFont.bitmap().fontHeight() * luiTextScale;
		h = lFontHeight * luiTextScale;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!enabled())
			return;

		final float lAlpha = 1f;
		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final MenuScreen lParentScreen = mParentLayout.parentScreen();
		final FontUnit lFont = lParentScreen.font();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mText, luiTextScale);
		final float lFontHeight = lFont.bitmap().fontHeight() * luiTextScale;

		final TextureBatchPCT lTextureBatch = lParentScreen.rendererManager().uiTextureBatch();

		if (mDrawBackground) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, pParentZDepth + .15f, 0.1f, 0.1f, 0.1f, lAlpha);
			lTextureBatch.end();

		}

		float lX = x + w / 2 - lLabelWidth / 2; // Center label
		switch (mHorizontalAlignment) {
		case LEFT:
			lX = x;
			break;
		case RIGHT:
			lX = x - mPadding - lLabelWidth;
			break;
		default:
			lX = x + w / 2 - lLabelWidth / 2; // Center label
			break;
		}

		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);
		lFont.trimText(mTrimText);
		lFont.draw(mText, lX, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, mR, mG, mB, lParentScreen.a(), luiTextScale);
		lFont.draw(mValueText, lX + 100.f, y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, mR, mG, mB, lParentScreen.a(), luiTextScale);
		lFont.end();

		if (mShowInfoIcon) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 192, 160, 32, 32, mInfoIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.end();

		}

		if (mShowWarnIcon) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 224, 160, 32, 32, mWarnIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.end();

		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, lAlpha);
			lTextureBatch.end();

		}

	}

}

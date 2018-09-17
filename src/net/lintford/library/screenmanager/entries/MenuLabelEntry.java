package net.lintford.library.screenmanager.entries;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuLabelEntry extends MenuEntry {

	private static final long serialVersionUID = -6246272207476797676L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ALIGNMENT mAlignment = ALIGNMENT.center;
	private float mPadding = 15f;
	private boolean mShow;
	private float mR, mG, mB;
	private boolean mDrawBackground;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public void alignment(ALIGNMENT center) {
		mAlignment = center;
	}

	public ALIGNMENT alignment() {
		return mAlignment;
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

	public MenuLabelEntry(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout, "");

		mDrawBackground = false;
		mText = "Add your message";
		mShow = true;
		mR = 1.0f;
		mG = mB = 0.1f;

		mCanHaveFocus = false;
		mCanHoverOver = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		w = MENUENTRY_DEF_BUTTON_WIDTH;
		h = MENUENTRY_DEF_BUTTON_HEIGHT;

	}

	@Override
	public void updateStructureDimensions() {
		// TODO: This -50 is because of the scrollbar - this is why I needed to keep the padding :(
		w = Math.min(mParentLayout.w - 50f, MENUENTRY_MAX_WIDTH);

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!enabled())
			return;

		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final MenuScreen lParentScreen = mParentLayout.parentScreen();
		final FontUnit lFont = lParentScreen.font();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mText, luiTextScale);
		final float lFontHeight = lFont.bitmap().fontHeight() * luiTextScale;

		if (mDrawBackground) {
			mTextureBatch.begin(pCore.HUD());
			final float lAlpha = 0.4f;
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, mZ, 0.1f, 0.1f, 0.1f, lAlpha);
			mTextureBatch.end();
		}

		float lX = x + w / 2 - lLabelWidth / 2; // Center label
		switch (mAlignment) {
		case left:
			lX = x;
			break;
		case right:
			lX = x - mPadding - lLabelWidth;
			break;
		default:
			lX = x + w / 2 - lLabelWidth / 2; // Center label
			break;
		}

		lFont.begin(pCore.HUD());
		lFont.draw(mText, lX, y + h / 2f - lFontHeight / 2f, pParentZDepth + .1f, mR, mG, mB, lParentScreen.a(), luiTextScale);
		lFont.end();

		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			mTextureBatch.begin(pCore.HUD());
			final float lAlpha = 0.3f;
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, lAlpha);
			mTextureBatch.end();

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}

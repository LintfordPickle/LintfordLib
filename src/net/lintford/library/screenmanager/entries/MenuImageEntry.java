package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuImageEntry extends MenuEntry {

	private static final long serialVersionUID = 4053035578493366108L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ALIGNMENT mAlignment = ALIGNMENT.center;
	private boolean mShow;
	private float mR, mG, mB;

	private Texture mTexture;
	private boolean mShowLabel;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean enableLabel() {
		return mShowLabel;
	}

	public void enableLabel(boolean pEnableLabel) {
		mShowLabel = pEnableLabel;
	}

	public void alignment(ALIGNMENT left) {
		mAlignment = left;
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

	public MenuImageEntry(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout, "");

		mText = "Add your message";
		mShow = true;
		mR = 1.0f;
		mG = mB = 0.1f;

		mCanHaveFocus = false;
		mCanHoverOver = false;

		mShowLabel = true;

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
		super.updateStructureDimensions();

		h = mTexture != null ? mTexture.getTextureHeight() + 20f : MENUENTRY_DEF_BUTTON_HEIGHT;

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {

		MenuScreen lParentScreen = mParentLayout.parentScreen();
		FontUnit lFont = lParentScreen.font();

		final float lTextWidth = lFont.bitmap().getStringWidth(mText);
		final float lTextHeight = lFont.bitmap().getStringHeight(mText);

		mAlignment = ALIGNMENT.center;
		float lX = x + w / 2 - lTextWidth / 2;
		switch (mAlignment) {
		case left:
			lX = x + 5;
			break;
		case right:
			lX = x - 5 - lTextWidth;
			break;
		default:
			lX = x + w / 2 - lTextWidth / 2;
			break;
		}

		float lLabelHeightOffset = 0;

		final float lA = lParentScreen.a();

		if (mShowLabel) {

			final float FONT_SCALE = 1f;

			lFont.begin(pCore.HUD());
			lFont.draw(mText, lX, y, pParentZDepth + .1f, mR, mG, mB, lA, FONT_SCALE);
			lFont.end();

			lLabelHeightOffset += lTextHeight + 10;

		}

		final TextureBatch lTextureBatch = mParentLayout.parentScreen().rendererManager().uiTextureBatch();

		lTextureBatch.begin(pCore.HUD());

		h = 240;

		switch (mAlignment) {
		case left:
			lX = x + 5;
			break;
		case right:
			lX = x - 5 - w;
			break;
		default:
			lX = x + w / 2 - 320 / 2;
			break;
		}

		if (mTexture != null) {
			final int width = mTexture.getTextureWidth();
			final int height = mTexture.getTextureHeight();

			// TODO: Something needs fixing here
			lX = -width / 2;
			lTextureBatch.draw(mTexture, 0, 0, width, height, lX + 5, y + lLabelHeightOffset, width, height, pParentZDepth + .1f, 1f, 1f, 1f, 1f);

		}

		lTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTexture(Texture pTexture) {
		mTexture = pTexture;

	}

}

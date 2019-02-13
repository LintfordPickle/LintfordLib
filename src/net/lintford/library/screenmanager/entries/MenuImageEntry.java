package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.BaseLayout.LAYOUT_ALIGNMENT;

public class MenuImageEntry extends MenuEntry {

	private static final long serialVersionUID = 4053035578493366108L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LAYOUT_ALIGNMENT mAlignment = LAYOUT_ALIGNMENT.center;
	private boolean mShow;
	private float mR, mG, mB;

	private float mForceHeight;

	private Texture mTexture;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public float height() {
		if (forceHeight() < 0)
			return super.height();

		return forceHeight();
	}

	public void forceHeight(float pNewValue) {
		mForceHeight = pNewValue;
	}

	public float forceHeight() {
		return mForceHeight;
	}

	public void alignment(LAYOUT_ALIGNMENT left) {
		mAlignment = left;
	}

	public LAYOUT_ALIGNMENT alignment() {
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
	public void updateStructure() {
		super.updateStructure();

		h = mTexture != null ? mTexture.getTextureHeight() + 20f : MENUENTRY_DEF_BUTTON_HEIGHT;

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		if (mTexture != null) {
			final int lTextureHeight = mTexture.getTextureHeight();
			h = Math.min(mParentLayout.h - 20, lTextureHeight);

		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {

		final MenuScreen lParentScreen = mParentLayout.parentScreen();
		final TextureBatch lTextureBatch = lParentScreen.rendererManager().uiTextureBatch();

		lTextureBatch.begin(pCore.HUD());

		if (mTexture != null) {
			final int lTextureWidth = mTexture.getTextureWidth();
			final int lTextureHeight = mTexture.getTextureHeight();

			float lAspectRatio = (float) lTextureHeight / (float) lTextureWidth;
			float lModWidth = h / lAspectRatio;

			lTextureBatch.draw(mTexture, 0, 0, lTextureWidth, lTextureHeight, centerX() - lModWidth / 2f, y, lModWidth, h, pParentZDepth + .1f, 1f, 1f, 1f, 1f);

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

package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.BitmapFont;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuLabelEntry extends MenuEntry {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ALIGNMENT mAlignment = ALIGNMENT.center;
	private boolean mShow;
	private float mR, mG, mB;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public float getHeight() {
		if (mParentScreen.fontHeader() != null) {
			return mParentScreen.fontHeader().bitmap().fontHeight();
		}

		return super.getHeight();
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

	public MenuLabelEntry(ScreenManager pScreenManager, MenuScreen pParentScreen) {
		super(pScreenManager, pParentScreen, "");

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

		w = getWidth();
		h = MENUENTRY_HEIGHT;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		// We don't want to draw the background menu entry for a label
		// super.draw(pScreen, display, pIsSelected);

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		final float lLabelWidth = lFontBitmap.getStringWidth(mText);

		float lX = x + w / 2 - lLabelWidth / 2;
		switch (mAlignment) {
		case left:
			lX = x + 5;
			break;
		case right:
			lX = x - 5 - lLabelWidth;
			break;
		default:
			lX = x + w / 2 - lLabelWidth / 2;
			break;
		}

		final float FONT_SCALE = 1f;
		mParentScreen.font().begin(pCore.HUD());
		mParentScreen.font().draw(mText, lX, y, pParentZDepth + .1f, mR, mG, mB, mParentScreen.a(), FONT_SCALE);
		mParentScreen.font().end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public float getWidth() {
		return 800; // getTextWidth(mText);
	}

}

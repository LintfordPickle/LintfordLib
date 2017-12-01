package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.BitmapFont;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

/** This {@link MenuEntry} class lets you add a field to a {@link MenuScreen} to display information to the user. {@link MenuInfoEntry} instances are not selectable nor are they clickable. */
public class MenuInfoEntry extends MenuEntry {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ALIGNMENT mAlignment = ALIGNMENT.center;
	private boolean mShow;
	private float mR, mG, mB;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void alignment(ALIGNMENT pNewValue) {
		mAlignment = pNewValue;
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

	public MenuInfoEntry(ScreenManager pScreenManager, MenuScreen pParentScreen) {
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

		width = getWidth();
		height = MENUENTRY_HEIGHT;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		// We don't want to draw the background menuentry for a label
		// super.draw(pScreen, display, pIsSelected);

		BitmapFont lFontBitmap = mParentScreen.font().bitmap();

		final float lLabelWidth = lFontBitmap.getStringWidth(mText);

		// TODO(John): Implement the pixel font here
		// FontSpriteBatch lSpriteBatch = BitmapFontManager.bitmapFontManager().getBitmapFont("Main").mFontSpriteBatch;

		float lX = x + width / 2 - lLabelWidth / 2;
		switch (mAlignment) {
		case left:
			lX = x;
			break;
		case right:
			lX = width - lLabelWidth;
			break;
		default:
			lX = x + width / 2 - lLabelWidth / 2;
			break;
		}

		// TODO(John): Need to render the text for the MenuLabelEntry
		// (mText, lX, y, 0f, mR * mParentScreen.r(), mG * mParentScreen.g(), mB * mParentScreen.b(), mParentScreen.a())
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public float getWidth() {
		return width; // getTextWidth(mText);
	}

}

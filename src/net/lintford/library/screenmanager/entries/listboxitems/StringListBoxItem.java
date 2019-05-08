package net.lintford.library.screenmanager.entries.listboxitems;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.entries.ListBox;
import net.lintford.library.screenmanager.entries.ListBoxItem;

public class StringListBoxItem extends ListBoxItem {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3360202382609680982L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mR;
	private float mG;
	private float mB;
	private float mA;

	private String mTextValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void textValue(String pNewValue) {
		mTextValue = pNewValue;
	}

	public String textValue() {
		return mTextValue;
	}

	public void setTextColor(float pR, float pG, float pB, float pA) {
		mR = pR;
		mG = pG;
		mB = pB;
		mA = pA;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public StringListBoxItem(ScreenManager pScreenManager, ListBox pParentListBox, int pIndex, String pValue, int pEntityGroupID) {
		super(pScreenManager, pParentListBox, pIndex, pEntityGroupID);

		mTextValue = pValue;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, TextureBatch pSpriteBatch, boolean pIsSelected, float pParentZDepth) {
		if (mTextValue != null && mTextValue.length() > 0) {

			float lScale = 1.0f;// mScreenManager.UIHUDController().uiTextScaleFactor();

			final FontUnit lFont = mParentListBox.parentLayout().parentScreen().font();
			final float lFontHeight = lFont.bitmap().getStringHeight(mTextValue, lScale);

			h = 10;
			mR = mG = mB = mA = 1;

			// Draw profile information
			lFont.begin(pCore.HUD());
			lFont.draw(mTextValue, x, y /* - lFontHeight / 2 */, pParentZDepth + .1f, mR, mG, mB, mA, lScale, -1);
			lFont.end();

		}

		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), this);

		}

	}

}
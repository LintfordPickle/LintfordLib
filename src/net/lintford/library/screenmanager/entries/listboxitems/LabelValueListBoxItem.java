package net.lintford.library.screenmanager.entries.listboxitems;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.entries.ListBox;
import net.lintford.library.screenmanager.entries.ListBoxItem;

public class LabelValueListBoxItem extends ListBoxItem {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3360202382609680982L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelValue;
	private String mTextValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void labelValue(String pNewValue) {
		mLabelValue = pNewValue;
	}

	public String labelValue() {
		return mLabelValue;
	}

	public void textValue(String pNewValue) {
		mTextValue = pNewValue;
	}

	public String textValue() {
		return mTextValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LabelValueListBoxItem(ScreenManager pScreenManager, ListBox pParentListBox, int pIndex, String pLabel, String pValue, int pEntityGroupID) {
		super(pScreenManager, pParentListBox, pIndex, pEntityGroupID);

		mLabelValue = pLabel;
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
	public void draw(LintfordCore pCore, Screen pScreen, TextureBatchPCT pSpriteBatch, boolean pIsSelected, float pParentZDepth) {
		if (mLabelValue != null && mLabelValue.length() > 0) {

			if (mTextValue == null)
				mTextValue = "";

			final float lScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lFont = mParentListBox.parentLayout().parentScreen.font();

			h = 10;

			// Draw profile information
			lFont.begin(pCore.HUD());
			lFont.drawText(mLabelValue, x, y, pParentZDepth + .1f, textColor, lScale, -1);
			lFont.drawText(mTextValue, x + w / 2, y, pParentZDepth + .1f, textColor, lScale, -1);
			lFont.end();

		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), this);

		}

	}

}
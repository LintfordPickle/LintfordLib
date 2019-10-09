package net.lintford.library.screenmanager.toast;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class ToastMessage {

	// --------------------------------------
	// Variables
	// --------------------------------------

	float liveLeft;
	String messageText;
	String messageTitle;
	float x, y; // current
	float xx, yy; // final

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ToastMessage() {
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore pCore, FontUnit pFontUnit, TextureBatch pTextureBatch) {
		pFontUnit.draw(messageText, x, y, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);

	}

	public void init(String pTitle, String pMessage, float pTimeInMS) {
		liveLeft = pTimeInMS;
		messageText = pMessage;
		messageTitle = pTitle;

	}

	public void reset() {
		liveLeft = 0;
		messageText = "";
		messageTitle = "";
		x = xx = y = yy = 0;
	}

}

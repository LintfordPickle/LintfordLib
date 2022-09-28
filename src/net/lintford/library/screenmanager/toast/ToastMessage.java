package net.lintford.library.screenmanager.toast;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;

public class ToastMessage {

	// --------------------------------------
	// Variables
	// --------------------------------------

	float liveLeft;
	String messageText;
	String messageTitle;
	float x, y;
	float xx, yy;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ToastMessage() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core, FontUnit fontUnit, TextureBatchPCT textureBatch) {
		fontUnit.drawText(messageText, x, y, -0.1f, ColorConstants.WHITE, 1f, -1);
	}

	public void init(String title, String message, float timeInMs) {
		liveLeft = timeInMs;
		messageText = message;
		messageTitle = title;
	}

	public void reset() {
		liveLeft = 0;
		messageText = "";
		messageTitle = "";
		x = xx = y = yy = 0;
	}
}
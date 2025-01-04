package net.lintfordlib.screenmanager.toast;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.batching.TextureBatchPCT;
import net.lintfordlib.core.graphics.fonts.FontUnit;

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
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core, FontUnit fontUnit, TextureBatchPCT textureBatch) {
		fontUnit.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		fontUnit.drawText(messageText, x, y, -0.1f, 1f, -1);
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
package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.renderers.windows.UiWindow;

public class UiLabelledInt extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelText;
	private int mValueInt;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void labelText(String pLabelText) {
		mLabelText = pLabelText;
	}

	public String labelText() {
		return mLabelText;
	}

	public void value(int pValueInt) {
		mValueInt = pValueInt;
	}

	public int value() {
		return mValueInt;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiLabelledInt(UiWindow pParentWindow) {
		this(pParentWindow, "Label not set");

	}

	public UiLabelledInt(UiWindow pParentWindow, String pLabelText) {
		super(pParentWindow);

		mLabelText = pLabelText;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		final float lMouseX = pCore.HUD().getMouseWorldSpaceX();
		final float lMouseY = pCore.HUD().getMouseWorldSpaceY();

		boolean lIsMouseHovering = intersectsAA(lMouseX, lMouseY);

		if (lIsMouseHovering) {
			// Renders the background of the input text widget
			pTextureBatch.draw(pUITexture, 0, 288, 32, 32, x, y, 32, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
			if (w > 32) {
				pTextureBatch.draw(pUITexture, 64, 288, 32, 32, x + 32, y, w - 64, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
				pTextureBatch.draw(pUITexture, 128, 288, 32, 32, x + w - 32, y, 32, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
			}

		}

		final float lTextHeight = pTextFont.bitmap().fontHeight();
		pTextFont.draw(mLabelText, x + lHorizontalPadding, y + h / 2 - lTextHeight / 2, pComponentZDepth, ColorConstants.TextEntryColor, 1f, -1);

		final float lValueWidth = pTextFont.bitmap().getStringWidth(Integer.toString(mValueInt));
		pTextFont.draw(Integer.toString(mValueInt), x + w - lValueWidth - lHorizontalPadding, y + h / 2 - lTextHeight / 2, pComponentZDepth, ColorConstants.TextEntryColor, 1f, -1);

	}

}

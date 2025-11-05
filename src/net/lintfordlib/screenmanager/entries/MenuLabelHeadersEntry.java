package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;

public class MenuLabelHeadersEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8375915206665610220L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<String> mHeaders = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void addHeader(String headerValue) {
		mHeaders.add(headerValue);
	}

	public String getHeader(int index) {
		if (index < 0 || index >= mHeaders.size())
			return null;

		return mHeaders.get(index);
	}

	public void removeHeader(int index) {
		if (index < 0 || index >= mHeaders.size())
			return;

		mHeaders.remove(index);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuLabelHeadersEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		this(screenManager, parentScreen, "");
	}

	public MenuLabelHeadersEntry(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, label);

		mDrawBackground = false;

		mCanHaveFocus = false;
		entryColor.setFromColor(ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, .5f));

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!enabled())
			return;

		final var xoffset = screen.screenPositionOffset().x;
		final var numColumns = mHeaders.size() > 0 ? mHeaders.size() : 1;
		final var columnWidth = width() / numColumns;

		final var textBoldFont = mParentScreen.fontBold();
		final var screenOffset = screen.screenPositionOffset();
		final var uiTextScale = mParentScreen.uiTextScale();
		final var fontHeight = textBoldFont.fontHeight() * uiTextScale;
		final var spriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			final float lMidLength = mW - 64;

			spriteBatch.setColor(entryColor);

			spriteBatch.begin(core.HUD());
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_LEFT, screenOffset.x + mX, screenOffset.y + mY, 32, 32, parentZDepth + .15f);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_MID, screenOffset.x + mX + 32, screenOffset.y + mY, lMidLength, 32, parentZDepth + .15f);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_RIGHT, screenOffset.x + mX + lMidLength + 32, screenOffset.y + mY, 32, 32, parentZDepth + .15f);
			spriteBatch.end();

		} else if (mHasFocus && mEnabled)
			renderHighlight(core, screen, true, spriteBatch);

		textBoldFont.begin(core.HUD());
		textBoldFont.setTextColor(textColor);
		for (int i = 0; i < numColumns; i++) {
			final var headerText = mHeaders.get(i);
			final var padding = i == 0 ? 10.f : 0f;

			final var columnX = xoffset + x() + padding + columnWidth * i;

			textBoldFont.drawText(headerText, columnX, screenOffset.y + mY + mH / 2f - fontHeight / 2f, parentZDepth + .15f, uiTextScale);

		}
		textBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, mParentScreen.screenColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, mParentScreen.screenColor.a);

		drawDebugCollidableBounds(core, spriteBatch);
	}
}

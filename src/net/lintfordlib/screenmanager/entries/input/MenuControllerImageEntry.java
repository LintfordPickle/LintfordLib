package net.lintfordlib.screenmanager.entries.input;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuControllerImageEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -6991418500871568629L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuControllerImageEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen);

		mCanHaveFocus = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var textureBatch = core.sharedResources().uiSpriteBatch();
		final var parentScreenAlpha = mParentScreen.screenColor.a;

		textureBatch.setColorWhite();
		textureBatch.setColorA(parentScreenAlpha);
		textureBatch.begin(core.HUD());

		final var controllerFrame = mCoreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_CONTROLLERBASE);
		final var controllerWidth = controllerFrame.width();
		final var controllerHeight = controllerFrame.height();

		final var offsetX = screen.screenPositionOffset().x;
		final var offsetY = screen.screenPositionOffset().y + 60;

		textureBatch.draw(mCoreSpritesheet, controllerFrame, offsetX + -controllerWidth * .5f, offsetY + -controllerHeight * .5f, controllerWidth, controllerHeight, 1f);
		textureBatch.end();

	}

}

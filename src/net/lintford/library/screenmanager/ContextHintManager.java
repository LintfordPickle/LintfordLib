package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.maths.Vector2i;

public class ContextHintManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private final int cIconSize = 16;
	private final int cSpacing = 5;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final ContextHintState mScreenManagerHintState = new ContextHintState();
	private IContextHintProvider mIContextHintProvider;

	private final Vector2i mPositionMarker = new Vector2i();
	private SpriteBatch mSpriteBatch;
	private FontUnit mHintFont;
	private SpriteSheetDefinition mSpritesheetDefinition;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ContextHintState screenManagerHintState() {
		return mScreenManagerHintState;
	}

	public void contextHintProvider(IContextHintProvider hintProvider) {
		mIContextHintProvider = hintProvider;
	}

	public IContextHintProvider contextHintProvider() {
		return mIContextHintProvider;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ContextHintManager() {
		mSpriteBatch = new SpriteBatch();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadGlContent(ResourceManager resourceManager) {
		mSpriteBatch.loadResources(resourceManager);
		mSpritesheetDefinition = resourceManager.spriteSheetManager().coreSpritesheet();
		mHintFont = resourceManager.fontManager().getFontUnit(ScreenManager.FONT_MENU_ENTRY_NAME);
	}

	public void unloadResources() {
		mSpriteBatch.unloadResources();
		mHintFont.unloadResources();

		mSpritesheetDefinition = null;
	}

	public void update(LintfordCore core) {
		if (mIContextHintProvider == null)
			return;
	}

	public void draw(LintfordCore core) {
		final var lContextHints = mIContextHintProvider != null ? mIContextHintProvider.contextHints() : mScreenManagerHintState;
		final var lHudBoundingBox = core.HUD().boundingRectangle();

		mPositionMarker.x = (int) lHudBoundingBox.right() - cIconSize - cSpacing;
		mPositionMarker.y = (int) lHudBoundingBox.bottom() - cIconSize - cSpacing;

		mHintFont.begin(core.HUD());
		mSpriteBatch.begin(core.HUD());

		if (mScreenManagerHintState.buttonDpadR)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_DARK, mScreenManagerHintState.buttonDpadRHint);
		else if (lContextHints.buttonDpadR)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_DARK, lContextHints.buttonDpadRHint);

		if (mScreenManagerHintState.buttonDpadL)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_LEFT_DARK, mScreenManagerHintState.buttonDpadLHint);
		else if (lContextHints.buttonDpadL)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_LEFT_DARK, lContextHints.buttonDpadLHint);

		if (mScreenManagerHintState.buttonDpadD)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_DOWN_DARK, mScreenManagerHintState.buttonDpadDHint);
		else if (lContextHints.buttonDpadD)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_DOWN_DARK, lContextHints.buttonDpadDHint);

		if (mScreenManagerHintState.buttonDpadU)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_UP_DARK, mScreenManagerHintState.buttonDpadUHint);
		else if (lContextHints.buttonDpadU)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_UP_DARK, lContextHints.buttonDpadUHint);

		if (mScreenManagerHintState.buttonY)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_Y_DARK_COLOR, mScreenManagerHintState.buttonYHint);
		else if (lContextHints.buttonY)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_Y_DARK_COLOR, lContextHints.buttonYHint);

		if (mScreenManagerHintState.buttonX)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_X_DARK_COLOR, mScreenManagerHintState.buttonXHint);
		else if (lContextHints.buttonX)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_X_DARK_COLOR, lContextHints.buttonXHint);

		if (mScreenManagerHintState.buttonB)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_B_DARK_COLOR, mScreenManagerHintState.buttonBHint);
		else if (lContextHints.buttonB)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_B_DARK_COLOR, lContextHints.buttonBHint);

		if (mScreenManagerHintState.buttonA)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_A_DARK_COLOR_, mScreenManagerHintState.buttonAHint);
		else if (lContextHints.buttonA)
			drawHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_A_DARK_COLOR_, lContextHints.buttonAHint);

		mSpriteBatch.end();
		mHintFont.end();
	}

	private void drawHint(LintfordCore core, SpriteBatch spriteBatch, FontUnit font, int spriteFrameIndex, String hintText) {

		if (hintText != null) {
			mPositionMarker.x -= font.getStringWidth(hintText);
			font.drawText(hintText, mPositionMarker.x, mPositionMarker.y, -0.001f, 1.f);
			mPositionMarker.x -= cSpacing;
		}

		mPositionMarker.x -= cIconSize;
		spriteBatch.draw(mSpritesheetDefinition, mSpritesheetDefinition.getSpriteFrame(spriteFrameIndex), mPositionMarker.x, mPositionMarker.y, cIconSize, cIconSize, -0.01f, ColorConstants.WHITE);
		mPositionMarker.x -= cSpacing;
	}

}

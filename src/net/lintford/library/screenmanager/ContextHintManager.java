package net.lintford.library.screenmanager;

import org.lwjgl.opengl.GL11;

import net.lintford.library.GameVersion;
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

	private final int cIconSize = 32;
	private final int cSpacing = 5;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final ContextHintState mScreenManagerHintState = new ContextHintState();
	private IContextHintProvider mIContextHintProvider;

	private boolean mDrawContextBackground;
	private final Vector2i mPositionMarker = new Vector2i();
	private SpriteBatch mSpriteBatch;
	private FontUnit mHintFont;
	private SpriteSheetDefinition mSpritesheetDefinition;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean drawContextBackground() {
		return mDrawContextBackground;
	}

	public void drawContextBackground(boolean drawContextBackground) {
		mDrawContextBackground = drawContextBackground;
	}

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

	}

	public void draw(LintfordCore core) {
		if (mDrawContextBackground == false)
			return;

		final var lContextHints = mIContextHintProvider != null ? mIContextHintProvider.contextHints() : mScreenManagerHintState;
		final var lHudBoundingBox = core.HUD().boundingRectangle();
		final var lVersionText = GameVersion.GAME_VERSION;

		final var lVersionTextHeight = 64;

		mPositionMarker.x = (int) lHudBoundingBox.right() - cIconSize - cSpacing;
		mPositionMarker.y = (int) lHudBoundingBox.bottom() - cIconSize - cSpacing;

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		mHintFont.begin(core.HUD());
		mSpriteBatch.begin(core.HUD());

		mSpriteBatch.draw(mSpritesheetDefinition, CoreTextureNames.TEXTURE_FOOTER_TRANSITION, lHudBoundingBox.right() - 512, lHudBoundingBox.bottom() - lVersionTextHeight - 2, 64, lVersionTextHeight + 2, -.1f, ColorConstants.GREY_DARK);
		mSpriteBatch.draw(mSpritesheetDefinition, CoreTextureNames.TEXTURE_FOOTER_64X64, lHudBoundingBox.right() - (512 - 64), lHudBoundingBox.bottom() - lVersionTextHeight - 2, lHudBoundingBox.width(), lVersionTextHeight + 2, -.1f, ColorConstants.GREY_DARK);

		if (mScreenManagerHintState.buttonDpadR)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_DARK, mScreenManagerHintState.buttonDpadRHint);
		else if (lContextHints.buttonDpadR)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_DARK, lContextHints.buttonDpadRHint);

		if (mScreenManagerHintState.buttonDpadL)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_LEFT_DARK, mScreenManagerHintState.buttonDpadLHint);
		else if (lContextHints.buttonDpadL)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_LEFT_DARK, lContextHints.buttonDpadLHint);

		if (mScreenManagerHintState.buttonDpadD)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_DOWN_DARK, mScreenManagerHintState.buttonDpadDHint);
		else if (lContextHints.buttonDpadD)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_DOWN_DARK, lContextHints.buttonDpadDHint);

		if (mScreenManagerHintState.buttonDpadU)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_UP_DARK, mScreenManagerHintState.buttonDpadUHint);
		else if (lContextHints.buttonDpadU)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_UP_DARK, lContextHints.buttonDpadUHint);

		if (mScreenManagerHintState.buttonY)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_Y_DARK_COLOR, mScreenManagerHintState.buttonYHint);
		else if (lContextHints.buttonY)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_Y_DARK_COLOR, lContextHints.buttonYHint);

		if (mScreenManagerHintState.buttonX)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_X_DARK_COLOR, mScreenManagerHintState.buttonXHint);
		else if (lContextHints.buttonX)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_X_DARK_COLOR, lContextHints.buttonXHint);

		if (mScreenManagerHintState.buttonB)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_B_DARK_COLOR, mScreenManagerHintState.buttonBHint);
		else if (lContextHints.buttonB)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_B_DARK_COLOR, lContextHints.buttonBHint);

		if (mScreenManagerHintState.buttonA)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_A_DARK_COLOR, mScreenManagerHintState.buttonAHint);
		else if (lContextHints.buttonA)
			drawGamePadHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_GAMEPAD_A_DARK_COLOR, lContextHints.buttonAHint);

		if (mScreenManagerHintState.keyReturn)
			drawKeyboardHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_KEY_MEDIUM, "Return");

		if (mScreenManagerHintState.keyEsc)
			drawKeyboardHint(core, mSpriteBatch, mHintFont, CoreTextureNames.TEXTURE_KEY_SMALL, "Esc");

		mSpriteBatch.end();

		mHintFont.drawText(lVersionText, lHudBoundingBox.left() + 5.f, lHudBoundingBox.bottom() - mHintFont.fontHeight(), -0.01f, 1.f);

		mHintFont.end();
	}

	private void drawGamePadHint(LintfordCore core, SpriteBatch spriteBatch, FontUnit font, int spriteFrameIndex, String hintText) {

		if (hintText != null) {
			mPositionMarker.x -= font.getStringWidth(hintText);
			font.drawText(hintText, mPositionMarker.x, mPositionMarker.y + cIconSize * .5f - font.fontHeight() * .5f, -0.01f, 1.f);
			mPositionMarker.x -= cSpacing;
		}

		mPositionMarker.x -= cIconSize;
		spriteBatch.draw(mSpritesheetDefinition, mSpritesheetDefinition.getSpriteFrame(spriteFrameIndex), mPositionMarker.x, mPositionMarker.y, cIconSize, cIconSize, -0.1f, ColorConstants.WHITE);
		mPositionMarker.x -= cSpacing;
	}

	private void drawKeyboardHint(LintfordCore core, SpriteBatch spriteBatch, FontUnit font, int spriteFrameIndex, String hintText) {

		if (hintText != null) {
			mPositionMarker.x -= font.getStringWidth(hintText);
			font.drawText(hintText, mPositionMarker.x, mPositionMarker.y + cIconSize * .5f - font.fontHeight() * .5f, -0.001f, 1.f);
			mPositionMarker.x -= cSpacing;

			mPositionMarker.x -= cIconSize;
			spriteBatch.draw(mSpritesheetDefinition, mSpritesheetDefinition.getSpriteFrame(spriteFrameIndex), mPositionMarker.x, mPositionMarker.y, cIconSize, cIconSize, -0.01f, ColorConstants.WHITE);
			mPositionMarker.x -= cSpacing;
		}
	}

}

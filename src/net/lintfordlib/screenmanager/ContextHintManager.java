package net.lintfordlib.screenmanager;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.GameVersion;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.maths.Vector2i;

public class ContextHintManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int ICON_SIZE = 32;
	private static final int SPACING = 5;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final ContextHintState mScreenManagerHintState = new ContextHintState();
	private IContextHintProvider mIContextHintProvider;

	private String mFooterPreText;
	private boolean mDrawContextBackground;
	private final Vector2i mPositionMarker = new Vector2i();
	private boolean mDrawFooterBar;

	private boolean mGamePadHintsEnabled;
	private boolean mKeyboardHintsEnabled;

	private boolean mContextHintsEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean enabled() {
		return mContextHintsEnabled;
	}

	public void enabled(boolean enabled) {
		mContextHintsEnabled = enabled;
	}

	public void setGamePadHints() {
		mGamePadHintsEnabled = true;
		mKeyboardHintsEnabled = false;
	}

	public void setKeyboardHints() {
		mGamePadHintsEnabled = false;
		mKeyboardHintsEnabled = true;
	}

	/** The pre-text appears in the footbar (if its enabled) before the version string. */
	public void setFootPreText(String preText) {
		mFooterPreText = preText;
	}

	public boolean drawFooterBar() {
		return mDrawFooterBar;
	}

	public void drawVersionBar(boolean drawVersionBar) {
		mDrawFooterBar = drawVersionBar;
	}

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
		// default to showing keyboard hints
		mKeyboardHintsEnabled = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core) {
		if (!mContextHintsEnabled || !mDrawContextBackground)
			return;

		final var lHudBoundingBox = core.HUD().boundingRectangle();
		final var lVersionTextHeight = 64;

		mPositionMarker.x = (int) lHudBoundingBox.right() - ICON_SIZE - SPACING;
		mPositionMarker.y = (int) lHudBoundingBox.bottom() - ICON_SIZE - SPACING;

		if (mDrawFooterBar)
			drawFooterText(core);

		final var lSpriteBatch = core.sharedResources().uiSpriteBatch();
		final var lFontUnit = core.sharedResources().uiTextFont();

		final var lCoreSpritesheetDefinition = core.resources().spriteSheetManager().coreSpritesheet();

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		lFontUnit.begin(core.HUD());
		lSpriteBatch.begin(core.HUD());

		lSpriteBatch.setColor(ColorConstants.GREY_DARK());
		if (mDrawFooterBar)
			lSpriteBatch.draw(lCoreSpritesheetDefinition, CoreTextureNames.TEXTURE_FOOTER_TRANSITION, lHudBoundingBox.right() - 512, lHudBoundingBox.bottom() - lVersionTextHeight - 2, 64, lVersionTextHeight + 2.f, .1f);
		else
			lSpriteBatch.draw(lCoreSpritesheetDefinition, CoreTextureNames.TEXTURE_FOOTER_END_64X64, lHudBoundingBox.right() - 512, lHudBoundingBox.bottom() - lVersionTextHeight - 2, 64, lVersionTextHeight + 2.f, .1f);

		lSpriteBatch.draw(lCoreSpritesheetDefinition, CoreTextureNames.TEXTURE_FOOTER_64X64, lHudBoundingBox.right() - (512 - 64), lHudBoundingBox.bottom() - lVersionTextHeight - 2, lHudBoundingBox.width(), lVersionTextHeight + 2.f, .1f);

		if (mGamePadHintsEnabled)
			drawGamePadHints(core);

		if (mKeyboardHintsEnabled)
			drawKeyboardHints(core);

		lSpriteBatch.end();
		lFontUnit.end();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void drawKeyboardHints(LintfordCore core) {
		final var lSpriteBatch = core.sharedResources().uiSpriteBatch();
		final var lFontUnit = core.sharedResources().uiTextFont();

		final var lContextHints = mIContextHintProvider != null ? mIContextHintProvider.contextHints() : mScreenManagerHintState;

		if (mScreenManagerHintState.keyReturn)
			drawKeyboardHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_KEY_MEDIUM, "Return", "Select");
		else if (lContextHints.keyReturn)
			drawKeyboardHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_KEY_MEDIUM, "Return", lContextHints.keyReturnHint);

		if (mScreenManagerHintState.keyEsc)
			drawKeyboardHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_KEY_SMALL, "Esc", "Back");
		else if (lContextHints.keyEsc)
			drawKeyboardHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_KEY_MEDIUM, "Esc", lContextHints.keyEscHint);
	}

	private void drawGamePadHints(LintfordCore core) {
		final var lSpriteBatch = core.sharedResources().uiSpriteBatch();
		final var lFontUnit = core.sharedResources().uiTextFont();

		final var lContextHints = mIContextHintProvider != null ? mIContextHintProvider.contextHints() : mScreenManagerHintState;

		if (mScreenManagerHintState.buttonDpadR)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_DARK, mScreenManagerHintState.buttonDpadRHint);
		else if (lContextHints.buttonDpadR)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_RIGHT_DARK, lContextHints.buttonDpadRHint);

		if (mScreenManagerHintState.buttonDpadL)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_LEFT_DARK, mScreenManagerHintState.buttonDpadLHint);
		else if (lContextHints.buttonDpadL)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_LEFT_DARK, lContextHints.buttonDpadLHint);

		if (mScreenManagerHintState.buttonDpadD)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DOWN_DARK, mScreenManagerHintState.buttonDpadDHint);
		else if (lContextHints.buttonDpadD)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DOWN_DARK, lContextHints.buttonDpadDHint);

		if (mScreenManagerHintState.buttonDpadU)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_UP_DARK, mScreenManagerHintState.buttonDpadUHint);
		else if (lContextHints.buttonDpadU)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_UP_DARK, lContextHints.buttonDpadUHint);

		if (mScreenManagerHintState.buttonY)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_Y_DARK_COLOR, mScreenManagerHintState.buttonYHint);
		else if (lContextHints.buttonY)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_Y_DARK_COLOR, lContextHints.buttonYHint);

		if (mScreenManagerHintState.buttonX)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_X_DARK_COLOR, mScreenManagerHintState.buttonXHint);
		else if (lContextHints.buttonX)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_X_DARK_COLOR, lContextHints.buttonXHint);

		if (mScreenManagerHintState.buttonB)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_B_DARK_COLOR, mScreenManagerHintState.buttonBHint);
		else if (lContextHints.buttonB)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_B_DARK_COLOR, lContextHints.buttonBHint);

		if (mScreenManagerHintState.buttonA)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_A_DARK_COLOR, mScreenManagerHintState.buttonAHint);
		else if (lContextHints.buttonA)
			drawGamePadHint(core, lSpriteBatch, lFontUnit, CoreTextureNames.TEXTURE_GAMEPAD_A_DARK_COLOR, lContextHints.buttonAHint);
	}

	private void drawFooterText(LintfordCore core) {
		final var lSpriteBatch = core.sharedResources().uiSpriteBatch();
		final var lFontUnit = core.sharedResources().uiTextFont();

		final var lHudBounds = core.HUD().boundingRectangle();
		final var lVersionText = GameVersion.GAME_VERSION;

		final var lVersionTextHeight = 32;

		final var lCoreSpritesheetDefinition = core.resources().spriteSheetManager().coreSpritesheet();

		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.setColor(ColorConstants.GREY_DARK());
		lSpriteBatch.draw(lCoreSpritesheetDefinition, CoreTextureNames.TEXTURE_FOOTER_32X32, lHudBounds.left(), lHudBounds.bottom() - lVersionTextHeight, lHudBounds.width(), lVersionTextHeight + 2.f, -.02f);
		lSpriteBatch.end();

		String text;
		if (mFooterPreText != null)
			text = mFooterPreText + " - " + lVersionText;
		else
			text = lVersionText;

		lFontUnit.begin(core.HUD());
		lFontUnit.drawText(text, lHudBounds.left() + 5.f, lHudBounds.bottom() - lFontUnit.fontHeight(), -0.02f, 1.f);
		lFontUnit.end();
	}

	private void drawGamePadHint(LintfordCore core, SpriteBatch spriteBatch, FontUnit font, int spriteFrameIndex, String hintText) {

		if (hintText != null) {
			mPositionMarker.x -= font.getStringWidth(hintText);
			font.drawText(hintText, mPositionMarker.x, mPositionMarker.y + ICON_SIZE * .5f - font.fontHeight() * .5f, .01f, 1.f);
			mPositionMarker.x -= SPACING;
		}

		final var lCoreSpritesheetDefinition = core.resources().spriteSheetManager().coreSpritesheet();

		mPositionMarker.x -= ICON_SIZE;

		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
		spriteBatch.draw(lCoreSpritesheetDefinition, lCoreSpritesheetDefinition.getSpriteFrame(spriteFrameIndex), mPositionMarker.x, mPositionMarker.y, ICON_SIZE, ICON_SIZE, .1f);

		mPositionMarker.x -= SPACING;
	}

	private void drawKeyboardHint(LintfordCore core, SpriteBatch spriteBatch, FontUnit font, int spriteFrameIndex, String keyText, String hintText) {

		final var lKeyHintWidth = font.getStringWidth(hintText);
		final var lKeyTextWidth = font.getStringWidth(keyText);

		if (hintText != null) {
			font.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			font.drawText(hintText, mPositionMarker.x - lKeyHintWidth, mPositionMarker.y + ICON_SIZE * .5f - font.fontHeight() * .5f, .001f, 1.f);

			final var lCoreSpritesheetDefinition = core.resources().spriteSheetManager().coreSpritesheet();

			mPositionMarker.x -= 10.f;
			mPositionMarker.x -= lKeyHintWidth;
			spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
			spriteBatch.draw(lCoreSpritesheetDefinition, lCoreSpritesheetDefinition.getSpriteFrame(CoreTextureNames.TEXTURE_KEY_END_RIGHT), mPositionMarker.x, mPositionMarker.y, 7, ICON_SIZE, .01f);
			spriteBatch.draw(lCoreSpritesheetDefinition, lCoreSpritesheetDefinition.getSpriteFrame(CoreTextureNames.TEXTURE_KEY_MID), mPositionMarker.x - lKeyTextWidth, mPositionMarker.y, lKeyTextWidth, ICON_SIZE, .01f);
			mPositionMarker.x -= lKeyTextWidth;
			font.setTextColor(ColorConstants.GREY_DARK());
			font.drawText(keyText, mPositionMarker.x, mPositionMarker.y + ICON_SIZE * .5f - font.fontHeight() * .5f, .001f, 1.f);
			mPositionMarker.x -= 7.f;

			spriteBatch.draw(lCoreSpritesheetDefinition, lCoreSpritesheetDefinition.getSpriteFrame(CoreTextureNames.TEXTURE_KEY_END_LEFT), mPositionMarker.x, mPositionMarker.y, 7, ICON_SIZE, .01f);
			mPositionMarker.x -= SPACING;
		}
	}

}

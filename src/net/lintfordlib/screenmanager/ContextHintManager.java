package net.lintfordlib.screenmanager;

import net.lintfordlib.GameVersion;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.maths.Vector2i;

public class ContextHintManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int ICON_SIZE = 16;
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
	private boolean mContextHintsEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean contextHintsEnabled() {
		return mContextHintsEnabled;
	}

	public void contextHintsEnabled(boolean enabled) {
		mContextHintsEnabled = enabled;
	}

	public void gamepadHintsEnabled(boolean newValue) {
		mGamePadHintsEnabled = newValue;
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
		mGamePadHintsEnabled = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core) {
		if (!mContextHintsEnabled || !mDrawContextBackground)
			return;

		final var lHudBoundingBox = core.HUD().boundingRectangle();

		mPositionMarker.x = (int) lHudBoundingBox.right() - ICON_SIZE - SPACING;
		mPositionMarker.y = (int) lHudBoundingBox.bottom() - ICON_SIZE - SPACING;

		if (mDrawFooterBar)
			drawFooterText(core);

		if (mGamePadHintsEnabled && core.input().gamepads().isGamepadAvailable())
			drawGamePadHints(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void drawGamePadHints(LintfordCore core) {
		final var spriteBatch = core.sharedResources().uiSpriteBatch();
		final var fontUnit = core.sharedResources().uiTextFont();

		final var contextHints = mIContextHintProvider != null ? mIContextHintProvider.contextHints() : mScreenManagerHintState;

		fontUnit.begin(core.HUD());
		fontUnit.setTextColorWhite();

		spriteBatch.begin(core.HUD());
		spriteBatch.setColorWhite();

		if (mScreenManagerHintState.buttonDpadR)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_RIGHT, mScreenManagerHintState.buttonDpadRHint);
		else if (contextHints.buttonDpadR)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_RIGHT, contextHints.buttonDpadRHint);

		if (mScreenManagerHintState.buttonDpadL)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_LEFT, mScreenManagerHintState.buttonDpadLHint);
		else if (contextHints.buttonDpadL)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_LEFT, contextHints.buttonDpadLHint);

		if (mScreenManagerHintState.buttonDpadD)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_DOWN, mScreenManagerHintState.buttonDpadDHint);
		else if (contextHints.buttonDpadD)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_DOWN, contextHints.buttonDpadDHint);

		if (mScreenManagerHintState.buttonDpadU)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_UP, mScreenManagerHintState.buttonDpadUHint);
		else if (contextHints.buttonDpadU)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_DPAD_UP, contextHints.buttonDpadUHint);

		if (mScreenManagerHintState.buttonY)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_BLUE, mScreenManagerHintState.buttonYHint);
		else if (contextHints.buttonY)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_BLUE, contextHints.buttonYHint);

		if (mScreenManagerHintState.buttonX)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_YELLOW, mScreenManagerHintState.buttonXHint);
		else if (contextHints.buttonX)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_YELLOW, contextHints.buttonXHint);

		if (mScreenManagerHintState.buttonB)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_RED, mScreenManagerHintState.buttonBHint);
		else if (contextHints.buttonB)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_RED, contextHints.buttonBHint);

		if (mScreenManagerHintState.buttonA)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_GREEN, mScreenManagerHintState.buttonAHint);
		else if (contextHints.buttonA)
			drawGamePadHint(core, spriteBatch, fontUnit, CoreTextureNames.TEXTURE_GAMEPAD_GREEN, contextHints.buttonAHint);

		spriteBatch.end();
		fontUnit.end();
	}

	private void drawFooterText(LintfordCore core) {
		final var lFontUnit = core.sharedResources().uiTextFont();

		final var lHudBounds = core.HUD().boundingRectangle();
		final var lVersionText = GameVersion.GAME_VERSION;

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

		float xPos = mPositionMarker.x;
		float yPos = mPositionMarker.y;

		final var lCoreSpritesheetDefinition = core.resources().spriteSheetManager().coreSpritesheet();

		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
		spriteBatch.draw(lCoreSpritesheetDefinition, lCoreSpritesheetDefinition.getSpriteFrame(spriteFrameIndex), xPos, yPos, ICON_SIZE, ICON_SIZE, .1f);

		xPos -= SPACING;

		if (hintText != null) {
			xPos -= font.getStringWidth(hintText);
			font.drawShadowedText(hintText, xPos, yPos + ICON_SIZE * .5f - font.fontHeight() * .5f, .01f, 1, 1, 1.f);
		}

		mPositionMarker.y -= 20;

	}
}

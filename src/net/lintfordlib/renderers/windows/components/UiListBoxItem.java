package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class UiListBoxItem extends Rectangle implements Comparable<UiListBoxItem>, IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -3976341798893720687L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int itemUid;
	public int listOrderIndex;

	public int mMouseInputTimer;
	public String definitionName;
	public String displayName;
	public Object data;

	public final Color backgroundColor = new Color();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiListBoxItem(int itemUid) {
		this.itemUid = itemUid;
	}

	public UiListBoxItem(int itemUid, String displayName) {
		this.itemUid = itemUid;
		this.displayName = displayName;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		return false; // nothing specific handled in this
	}

	public void update(LintfordCore core) {
		if (mMouseInputTimer > 0.f)
			mMouseInputTimer -= core.gameTime().elapsedTimeMilli();
	}

	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreDef, FontUnit fontUnit, float zDepth) {
		final var lTextPosX = mX + 5.f;
		final var lTextPosY = mY + mH * .5f - fontUnit.fontHeight() * .5f;

		if (backgroundColor.a > 0.f) {
			spriteBatch.draw(coreDef, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, zDepth, backgroundColor);
		}

		final var lDisplayText = displayName != null ? displayName : String.valueOf(itemUid);
		fontUnit.drawText(lDisplayText, lTextPosX, lTextPosY, zDepth, 1.f);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setAsset(String definitionName, String displayName) {
		this.definitionName = definitionName;

		this.displayName = displayName;
	}

	@Override
	public int compareTo(UiListBoxItem o) {
		return listOrderIndex - o.listOrderIndex;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseInputTimer <= 0.f;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mMouseInputTimer = IInputProcessor.INPUT_COOLDOWN_TIME;
	}

	@Override
	public boolean allowKeyboardInput() {
		return false;
	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}
}

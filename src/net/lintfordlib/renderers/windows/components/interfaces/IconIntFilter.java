package net.lintfordlib.renderers.windows.components.interfaces;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.renderers.windows.components.UiIconFilter;

public class IconIntFilter implements IInputProcessor {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiIconFilter mUIIconFilter;
	private int mFilterValue;
	private transient SpriteSheetDefinition mIconSpritesheetDefinition;
	private transient int mIconSpriteFrameIndex;
	private Rectangle mUIDstRectangle;
	private boolean mEnabled;
	private String mFilterName;
	private boolean mHoveredOver;
	private float mMouseTimer;
	public final Color color = new Color();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void resetHovered() {
		mHoveredOver = false;
	}

	public String filterName() {
		return mFilterName;
	}

	public Rectangle uiDstRectangle() {
		return mUIDstRectangle;
	}

	public int filterValue() {
		return mFilterValue;
	}

	public void filterEnabled(boolean newValue) {
		mEnabled = newValue;
	}

	public boolean filterEnabled() {
		return mEnabled;
	}

	public void isFilterEnabled(boolean newValue) {
		mEnabled = newValue;
	}

	public void setDstRectangle(float x, float y, float width, float height) {
		mUIDstRectangle.set(x, y, width, height);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public IconIntFilter(UiIconFilter parent, SpriteSheetDefinition spritesheet, int spriteFrameIndex, String name, int filterValue) {
		mEnabled = false;

		mUIIconFilter = parent;
		mIconSpritesheetDefinition = spritesheet;
		mIconSpriteFrameIndex = spriteFrameIndex;
		mUIDstRectangle = new Rectangle();
		mFilterName = name;
		mFilterValue = filterValue;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		if (mUIDstRectangle.intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			mHoveredOver = true;
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				mUIIconFilter.onFilterClick(this);
				return true;
			}
		}

		return false;
	}

	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		final var lColorMod = mEnabled ? 1.f : .8f;
		final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.WHITE, lColorMod);

		spriteBatch.begin(core.HUD());

		if (mEnabled) {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_BLUE, mUIDstRectangle.x() - 2, mUIDstRectangle.y() - 2, mUIDstRectangle.width() + 4, mUIDstRectangle.height() + 6, -0.5f, lColor);
		} else {
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_GREEN, mUIDstRectangle.x() - 2, mUIDstRectangle.y() - 2, mUIDstRectangle.width() + 4, mUIDstRectangle.height() + 6, -0.5f, lColor);
		}

		if (mHoveredOver) {
			final float lTextHalfW = textFont.getStringWidth(mFilterName) / 2;
			final float lTextHeight = textFont.fontHeight();

			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_RED, mUIDstRectangle.x() + 16 - lTextHalfW, mUIDstRectangle.y() - 19, lTextHalfW * 2 + 4, lTextHeight, componentZDepth, ColorConstants.WHITE);
		}

		spriteBatch.draw(mIconSpritesheetDefinition, mIconSpriteFrameIndex, mUIDstRectangle, componentZDepth, lColor);
		spriteBatch.end();

		if (mHoveredOver) {
			final float lTextHalfW = textFont.getStringWidth(mFilterName) / 2;
			textFont.begin(core.HUD());
			textFont.drawText(mFilterName, mUIDstRectangle.x() + 16 - lTextHalfW, mUIDstRectangle.y() - 19, componentZDepth, ColorConstants.WHITE, 1f);
			textFont.end();
		}
	}

	// --------------------------------------
	// Inherited methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;
	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowKeyboardInput() {
		return false;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}
}

package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.windows.UiWindow;

public abstract class UIWidget extends Rectangle implements IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8734195273392955490L;

	protected static final float lHorizontalPadding = 5.0f;
	protected static final float lVerticalPadding = 5.0f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Color entityColor = new Color(1.f, 1.f, 1.f, 1.f);
	protected UiWindow mParentWindow;

	protected boolean mIsEnabled;
	protected boolean mIsVisible;
	protected boolean mIsHoveredOver;

	protected float mMouseTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isHoveredOver() {
		return mIsHoveredOver;
	}

	public void isHoveredOver(boolean newValue) {
		mIsHoveredOver = newValue;
	}

	public boolean isEnabled() {
		return mIsEnabled;
	}

	public void isEnabled(boolean newValue) {
		mIsEnabled = newValue;
	}

	public boolean isVisible() {
		return mIsVisible;
	}

	public void isVisible(boolean newValue) {
		mIsVisible = newValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIWidget(final UiWindow parentWindow) {
		this(parentWindow, 0, 0, 0, 0);
	}

	public UIWidget(final UiWindow parentWindow, final Rectangle bounds) {
		this(parentWindow, bounds.x(), bounds.y(), bounds.width(), bounds.height());
	}

	public UIWidget(final UiWindow parentWindow, float x, float y, float w, float h) {
		super(x, y, w, h);

		mIsVisible = true;
		mIsEnabled = true;

		mParentWindow = parentWindow;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public void loadResources(final ResourceManager resourceManager) {

	}

	public void unloadResources() {

	}

	public boolean handleInput(LintfordCore core) {
		return false;
	}

	public void update(LintfordCore core) {
		if (mMouseTimer >= 0) {
			mMouseTimer -= core.appTime().elapsedTimeMilli();
		}
	}

	public abstract void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth);

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;
	}
}

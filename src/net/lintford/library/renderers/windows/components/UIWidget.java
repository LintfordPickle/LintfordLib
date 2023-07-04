package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.input.mouse.IInputProcessor;
import net.lintford.library.renderers.windows.UiWindow;

public abstract class UIWidget extends Rectangle implements IInputProcessor {

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

	protected IUiWidgetInteractions mCallback;
	protected int mUiWidgetUid;

	protected boolean mIsEnabled;
	protected boolean mIsVisible;
	protected boolean mIsHoveredOver;
	protected boolean mIsDoubleHeight;

	protected float mMouseTimer;

	protected float mLayoutWeight;

	// The margin is applied to the outside of this component
	protected int mMarginTop;
	protected int mMarginBottom;
	protected int mMarginLeft;
	protected int mMarginRight;

	// The margin is applied to the inside of this component
	protected float mPaddingTop;
	protected float mPaddingBottom;
	protected float mPaddingLeft;
	protected float mPaddingRight;

	private float mDesiredHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void desiredHeight(float desiredHeight) {
		mDesiredHeight = desiredHeight;
	}

	public float desiredHeight() {
		return mDesiredHeight;
	}

	public boolean isDoubleHeight() {
		return mIsDoubleHeight;
	}

	public void isDoubleHeight(boolean layoutWeight) {
		mIsDoubleHeight = layoutWeight;
	}

	public float layoutWeight() {
		return mLayoutWeight;
	}

	public void layoutWeight(float layoutWeight) {
		mLayoutWeight = layoutWeight;
	}

	public float paddingLeft() {
		return mPaddingLeft;
	}

	public float paddingRight() {
		return mPaddingRight;
	}

	public float paddingTop() {
		return mPaddingTop;
	}

	public float paddingBottom() {
		return mPaddingBottom;
	}

	public void paddingLeft(float pNewValue) {
		mPaddingLeft = pNewValue;
	}

	public void paddingRight(float pNewValue) {
		mPaddingRight = pNewValue;
	}

	public void paddingTop(float pNewValue) {
		mPaddingTop = pNewValue;
	}

	public void paddingBottom(float pNewValue) {
		mPaddingBottom = pNewValue;
	}

	public int marginRight() {
		return mMarginRight;
	}

	public void marginRight(int newValue) {
		mMarginRight = newValue;
	}

	public int marginLeft() {
		return mMarginLeft;
	}

	public void marginLeft(int newValue) {
		mMarginLeft = newValue;
	}

	public int marginBottom() {
		return mMarginBottom;
	}

	public void marginBottom(int newValue) {
		mMarginBottom = newValue;
	}

	public int marginTop() {
		return mMarginTop;
	}

	public void marginTop(int newValue) {
		mMarginTop = newValue;
	}

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

	@Override
	public boolean allowGamepadInput() {
		return true;
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean allowMouseInput() {
		return true;
	}

	public void setClickListener(IUiWidgetInteractions callback, int clickUid) {
		mCallback = callback;
		mUiWidgetUid = clickUid;
	}

	public void removeClickListener() {
		mCallback = null;
	}
}
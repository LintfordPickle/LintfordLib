package net.lintford.library.screenmanager.layouts;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.mouse.IInputProcessor;
import net.lintford.library.screenmanager.MenuScreen;

/**
 * The list layout lays out all the menu entries linearly down the layout.
 */
public class FloatingLayout extends BaseLayout implements IInputProcessor {

	// --------------------------------------
	// COnstants
	// --------------------------------------

	private static final long serialVersionUID = -7568188688210642680L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mClickTimer;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FloatingLayout(MenuScreen parentScreen) {
		super(parentScreen);

		mMinWidth = 0.f;
		mMaxWidth = 900.f;

		// inevitably, there is some portion of the background graphic which
		// shouldn't have content rendered over it. that's this
		mCropPaddingBottom = 0.f;
		mCropPaddingTop = 0.f;
	}

	public FloatingLayout(MenuScreen parentScreen, float x, float y) {
		this(parentScreen);

		mX = x;
		mY = y;
	}

	public FloatingLayout(MenuScreen parentScreen, float x, float y, float width, float height) {
		this(parentScreen, x, y);

		mW = width;
		mH = height;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mClickTimer >= 0) {
			mClickTimer -= core.appTime().elapsedTimeMilli();
		}
	}

	// --------------------------------------
	// IProcessMouseInput-Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mClickTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mClickTimer = 200;

	}

	@Override
	public boolean allowGamepadInput() {
		return parentScreen.allowGamepadInput();
	}

	@Override
	public boolean allowKeyboardInput() {
		return parentScreen.allowKeyboardInput();
	}

	@Override
	public boolean allowMouseInput() {
		return parentScreen.allowMouseInput();
	}
}

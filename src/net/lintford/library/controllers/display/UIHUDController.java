package net.lintford.library.controllers.display;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;

public class UIHUDController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "UIHUDController";

	public static final float UI_TEXT_SCALE_FACTOR_MIN = 0.8f;
	public static final float UI_TEXT_SCALE_FACTOR_MAX = 1.2f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsInitialised;

	private boolean mBigUIEnabled;
	private Rectangle mHUDRectangle;

	private float mUITransparencyFactorUser;
	private float mUITransparencyFactorActual;
	private float mUIScaleFactorUser;
	private float mUIScaleFactorActual;
	private float mUITextScaleFactorUser;
	private float mUITextScaleFactorActual;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float uiScaleFactor() {
		return mUIScaleFactorActual;
	}

	public void uiScaleFactor(float pNewValue) {
		mUIScaleFactorUser = pNewValue;
	}

	public float uiTransparencyFactor() {
		return mUITransparencyFactorActual;
	}

	public void uiTransparencyFactor(float pNewValue) {
		mUITransparencyFactorUser = pNewValue;
	}

	public float uiTextScaleFactor() {
		return mUITextScaleFactorActual;
	}

	public void uiTextScaleFactor(float pNewValue) {
		mUITextScaleFactorUser = pNewValue;
	}

	public boolean useBigUI() {
		return mBigUIEnabled;
	}

	@Override
	public boolean isInitialised() {
		return mIsInitialised;
	}

	public Rectangle HUDRectangle() {
		return mHUDRectangle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIHUDController(ControllerManager pControllerManager, final int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mUITextScaleFactorUser = 1.0f;
		mUIScaleFactorUser = 1.0f;
		mUITransparencyFactorUser = 1.0f;

		mHUDRectangle = new Rectangle();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	/** Get the needed references and create the player UI windows (renderers). */
	public void initialise(LintfordCore pCore) {
		updateUIScale(pCore);

		mIsInitialised = true;

	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		updateUIScale(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateUIScale(LintfordCore pCore) {

		final float lWindowWidth = pCore.config().display().windowSize().x;
		final float lWindowHeight = pCore.config().display().windowSize().y;

		// Buffer = 64
		// Left_Panel + Buffer + Hotbar + Buffer + Right_Panel
		final float lMinBigHUDWidth = 1280;
		final float lMinBigHUDHeight = 768;

		float lHUDRatio = 0.70f;

		if (lWindowWidth < lMinBigHUDWidth || lWindowHeight < lMinBigHUDHeight) {
			mBigUIEnabled = false;
			lHUDRatio = 0.80f;

			mUITextScaleFactorActual = mUITextScaleFactorUser * 0.8f;
			mUIScaleFactorActual = mUIScaleFactorUser * 0.7f;
			mUITransparencyFactorActual = mUITransparencyFactorUser * 0.7f;

		} else {
			mBigUIEnabled = true;

			mUITextScaleFactorActual = mUITextScaleFactorUser * 1.0f;
			mUIScaleFactorActual = mUIScaleFactorUser * 1.0f;
			mUITransparencyFactorActual = mUITransparencyFactorUser * 1.0f;

		}

		// Reconstructe the

		final float lHUDWidth = lWindowWidth * lHUDRatio;
		final float lHUDHeight = lWindowHeight * lHUDRatio;

		final float lWindowHalfWidth = lHUDWidth / 2f;
		final float lWindowHalfHeight = lHUDHeight / 2f;

		mHUDRectangle.set(-lWindowHalfWidth, -lWindowHalfHeight, lHUDWidth, lHUDHeight);

	}

}
package net.lintford.library.controllers.display;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.options.DisplayManager;

public class UIHUDController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "UIHUDController";

	private static final int MIN_HUD_WIDTH = 1024;
	private static final int MIN_HUD_HEIGHT = 768;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayManager mDisplayManager;
	private Rectangle mHUDRectangle;
	private boolean mIsInitialised;
	private boolean mBigUIEnabled;
	private float mUITransparencyFactorActual;
	private float mUIScaleFactorActual;
	private float mUITextScaleFactorActual;
	private float mUIBottomPanelHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float bottomPanelHeight() {
		return mUIBottomPanelHeight;
	}
	
	public float uiTransparencyFactor() {
		return mUITransparencyFactorActual;
	}

	public float uiScaleFactor() {
		return mUIScaleFactorActual;
	}

	// TODO: Replace the following function with:
	/*
	   final boolean lIsBigUI = mUIHUDGameController.useBigUI();
	   final float lTextScale = mUIHUDGameController.uiTextScaleFactor() * (lIsBigUI ? GraphicsSettings.BIG_UI_SCALE_FACTOR : GraphicsSettings.SMALL_UI_SCALE_FACTOR);
	 */
	public float uiTextScaleFactor() {
		return mUITextScaleFactorActual;
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

	public UIHUDController(DisplayManager pDisplayManager, ControllerManager pControllerManager, final int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mDisplayManager = pDisplayManager;

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

		final float lWindowWidth = pCore.config().display().windowWidth();
		final float lWindowHeight = pCore.config().display().windowHeight();

		// Buffer = 64
		// Left_Panel + Buffer + Hotbar + Buffer + Right_Panel
		final float lMinBigHUDWidth = 1280;
		final float lMinBigHUDHeight = 768;

		float lHUDRatio = 0.70f;

		if (lWindowWidth < lMinBigHUDWidth || lWindowHeight < lMinBigHUDHeight) {
			mBigUIEnabled = false;
			lHUDRatio = 0.80f;
			mUIBottomPanelHeight = 50;

			mUITextScaleFactorActual = mDisplayManager.graphicsSettings().UITextScale() * 1f;
			mUITransparencyFactorActual = mDisplayManager.graphicsSettings().UITransparencyScale() * 1f;

		} else {
			mBigUIEnabled = true;
			mUIBottomPanelHeight = 0;

			mUITextScaleFactorActual = mDisplayManager.graphicsSettings().UITextScale() * 1f;
			mUITransparencyFactorActual = mDisplayManager.graphicsSettings().UITransparencyScale() * 1f;

		}

		// Reconstructe the

		final float lHUDWidth = Math.min(lWindowWidth * lHUDRatio, MIN_HUD_WIDTH);// * lHUDRatio;
		final float lHUDHeight = Math.min(lWindowHeight * lHUDRatio, MIN_HUD_HEIGHT);// * lHUDRatio;

		final float lWindowHalfWidth = lHUDWidth / 2f;
		final float lWindowHalfHeight = lHUDHeight / 2f;

		mHUDRectangle.set(-lWindowHalfWidth, -lWindowHalfHeight, lHUDWidth, lHUDHeight - mUIBottomPanelHeight);

	}

}
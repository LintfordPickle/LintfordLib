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

	private Rectangle mGameHUDRectangle;
	private Rectangle mMenuTitleRectangle;
	private Rectangle mMenuMainRectangle;
	private Rectangle mMenuFooterRectangle;

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
	 * final boolean lIsBigUI = mUIHUDGameController.useBigUI(); final float lTextScale = mUIHUDGameController.uiTextScaleFactor() * (lIsBigUI ? GraphicsSettings.BIG_UI_SCALE_FACTOR : GraphicsSettings.SMALL_UI_SCALE_FACTOR);
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

	public Rectangle gameHUDRectangle() {
		return mGameHUDRectangle;
	}

	public Rectangle menuTitleRectangle() {
		return mMenuTitleRectangle;
	}

	public Rectangle menuMainRectangle() {
		return mMenuMainRectangle;
	}

	public Rectangle menuFooterRectangle() {
		return mMenuFooterRectangle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIHUDController(DisplayManager pDisplayManager, ControllerManager pControllerManager, final int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mDisplayManager = pDisplayManager;

		mGameHUDRectangle = new Rectangle();
		mMenuTitleRectangle = new Rectangle();
		mMenuMainRectangle = new Rectangle();
		mMenuFooterRectangle = new Rectangle();

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

		// ** GAME HUD BOUNDS ** //

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

		// ** MENU HUD BOUNDS ** //

		final float lHUDWidth = Math.min(lWindowWidth * lHUDRatio, MIN_HUD_WIDTH);// * lHUDRatio;
		final float lHUDHeight = Math.min(lWindowHeight * lHUDRatio, MIN_HUD_HEIGHT);// * lHUDRatio;

		final float lWindowHalfWidth = lHUDWidth / 2f;
		final float lWindowHalfHeight = lHUDHeight / 2f;

		mGameHUDRectangle.set(-lWindowHalfWidth, -lWindowHalfHeight, lHUDWidth, lHUDHeight - mUIBottomPanelHeight);

		final float lBorder = 20f;
		final float lInnerBorder = 1f;
		final float lModWidth = lWindowWidth - lBorder * 2f;
		final float lModHeight = lWindowHeight - lBorder * 2f;

		float lRemainingHeight = lModHeight;
		final float MINIMUM_TITLE_HEGIHT = mBigUIEnabled ? 180 : 100;
		final float MINIMUM_FOOTER_HEGIHT = mBigUIEnabled ? 150 : 90;
		final float lTitleHeight = (float)Math.max(MINIMUM_TITLE_HEGIHT, lModHeight * .15f - lInnerBorder);
		final float lFooterHeight = (float)Math.max(MINIMUM_FOOTER_HEGIHT, lModHeight * .10f - lInnerBorder);
		lRemainingHeight -= lTitleHeight;
		lRemainingHeight -= lFooterHeight;
		
		final float lMainHeight = lRemainingHeight;

		mMenuTitleRectangle.set( -lModWidth / 2, -lModHeight / 2f,                 lModWidth, lTitleHeight);
		mMenuMainRectangle.set(  -lModWidth / 2, -lModHeight / 2f + lTitleHeight + lInnerBorder*2f,  lModWidth, lMainHeight);
		mMenuFooterRectangle.set(-lModWidth / 2,  lModHeight / 2f - lFooterHeight + lInnerBorder, lModWidth, lFooterHeight);

	}

}
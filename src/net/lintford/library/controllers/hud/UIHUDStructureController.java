package net.lintford.library.controllers.hud;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.options.DisplayManager;

public class UIHUDStructureController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "UIHUDController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayManager mDisplayManager;

	private Rectangle mGameHUDRectangle;
	private Rectangle mGameFooterRectangle; // hotbar etc.
	private Rectangle mGameHeaderRectangle;

	private Rectangle mMenuTitleRectangle;
	private Rectangle mMenuMainRectangle;
	private Rectangle mMenuFooterRectangle;

	private boolean mIsInitialised;
	private boolean mBigUIEnabled;

	private float mUITransparencyFactorActual;
	private float mUIScaleFactorActual;
	private float mUITextScaleFactorActual;

	private float mWindowPaddingH;
	private float mWindowPaddingV;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float windowPaddingH() {
		return mWindowPaddingH;
	}

	public float windowPaddingV() {
		return mWindowPaddingV;
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

	public Rectangle gameHeaderRectangle() {
		return mGameHeaderRectangle;
	}

	public Rectangle gameHUDRectangle() {
		return mGameHUDRectangle;
	}

	public Rectangle gameFooterRectangle() {
		return mGameFooterRectangle;
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

	public UIHUDStructureController(DisplayManager pDisplayManager, ControllerManager pControllerManager, final int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mDisplayManager = pDisplayManager;

		mGameHeaderRectangle = new Rectangle();
		mGameHUDRectangle = new Rectangle();
		mGameFooterRectangle = new Rectangle();

		mMenuTitleRectangle = new Rectangle();
		mMenuMainRectangle = new Rectangle();
		mMenuFooterRectangle = new Rectangle();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	/** Get the needed references and create the player UI windows (renderers). */
	public void initialise(LintfordCore pCore) {
		updateHUDAreas(pCore);

		mIsInitialised = true;

	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		updateHUDAreas(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateHUDAreas(LintfordCore pCore) {

		// ** DIMENSIONS AND SCALES ** //

		final float lWindowWidth = pCore.config().display().windowWidth();
		final float lWindowHeight = pCore.config().display().windowHeight();

		float lWindowPadding = 15f;

		final float lMinBigHUDWidth = 1280;
		final float lMinBigHUDHeight = 768;

		float lHUDRatio = 1f;

		if (lWindowWidth < lMinBigHUDWidth || lWindowHeight < lMinBigHUDHeight) {
			mBigUIEnabled = false;
			lHUDRatio = 1.0f;

			mUIScaleFactorActual = mDisplayManager.graphicsSettings().UIScale() * lHUDRatio;
			mUITextScaleFactorActual = mDisplayManager.graphicsSettings().UITextScale() * lHUDRatio;
			mUITransparencyFactorActual = mDisplayManager.graphicsSettings().UITransparencyScale() * lHUDRatio;

		} else {
			mBigUIEnabled = true;

			mUIScaleFactorActual = mDisplayManager.graphicsSettings().UIScale() * 1f;
			mUITextScaleFactorActual = mDisplayManager.graphicsSettings().UITextScale() * 1f;
			mUITransparencyFactorActual = mDisplayManager.graphicsSettings().UITransparencyScale() * 1f;

		}

		// ** GAME HUD BOUNDS ** //

		final float lGameFooterHeight = mBigUIEnabled ? 64f : 64f;
		final float lGameHeaderHeight = mBigUIEnabled ? 64f : 64f;

		float lHUDWidth = lWindowWidth - lWindowPadding * 2f;
		float lHUDHeight = lWindowHeight - lGameFooterHeight * 2f;

		if (mBigUIEnabled) {
			lHUDWidth = Math.min(lHUDWidth, 1280);
			lHUDHeight = Math.min(lHUDHeight, 1024);
		}

		mGameFooterRectangle.set(-lWindowWidth / 2f, -lWindowHeight / 2, lWindowWidth, lGameFooterHeight);
		mGameHUDRectangle.set(-lHUDWidth / 2f, -lHUDHeight / 2f, lHUDWidth, lHUDHeight);
		mGameHeaderRectangle.set(-lWindowWidth / 2f, lWindowHeight / 2 - lGameHeaderHeight, lWindowWidth, lGameHeaderHeight);

		// ** MENU HUD BOUNDS ** //

		final float lBorder = 20f;
		final float lInnerBorder = 1f;
		final float lModWidth = lWindowWidth - lBorder * 2f;
		final float lModHeight = lWindowHeight - lBorder * 2f;

		float lRemainingHeight = lModHeight;
		final float MINIMUM_TITLE_HEGIHT = mBigUIEnabled ? 180 : 100;
		final float MINIMUM_FOOTER_HEGIHT = mBigUIEnabled ? 150 : 90;
		final float lTitleHeight = (float) Math.max(MINIMUM_TITLE_HEGIHT, lModHeight * .15f - lInnerBorder);
		final float lFooterHeight = (float) Math.max(MINIMUM_FOOTER_HEGIHT, lModHeight * .10f - lInnerBorder);
		lRemainingHeight -= lTitleHeight;
		lRemainingHeight -= lFooterHeight;

		final float lMainHeight = lRemainingHeight;

		mMenuTitleRectangle.set(-lModWidth / 2, -lModHeight / 2f, lModWidth, lTitleHeight);
		mMenuMainRectangle.set(-lModWidth / 2, -lModHeight / 2f + lTitleHeight + lInnerBorder * 2f, lModWidth, lMainHeight);
		mMenuFooterRectangle.set(-lModWidth / 2, lModHeight / 2f - lFooterHeight + lInnerBorder, lModWidth, lFooterHeight);

		// ** WINDOW ** //

		if (mBigUIEnabled) {
			mWindowPaddingH = 40;
			mWindowPaddingV = 30;

		} else {
			mWindowPaddingH = 10;
			mWindowPaddingV = 10;

		}

	}

}
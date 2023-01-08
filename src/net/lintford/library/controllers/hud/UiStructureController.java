package net.lintford.library.controllers.hud;

import net.lintford.library.ConstantsDisplay;
import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.options.DisplayManager;

public class UiStructureController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Ui Structure Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayManager mDisplayManager;
	private Rectangle mGameHUDRectangle;
	private Rectangle mGameHeaderRectangle;
	private Rectangle mGameFooterRectangle;
	private Rectangle mMenuTitleRectangle;
	private Rectangle mMenuMainRectangle;
	private Rectangle mMenuFooterRectangle;

	private float mGameCanvasWScaleFactor;
	private float mGameCanvasHScaleFactor;
	private float mUiCanvasWScaleFactor;
	private float mUiCanvasHScaleFactor;

	private float mUITransparencyFactorActual;
	private float mUIScaleFactorActual;
	private float mUITextScaleFactorActual;

	private float mWindowPaddingH;
	private float mWindowPaddingV;
	private float mMinimumTitleHeight = 30.f;
	private float mFooterHeight = 30.f;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Assuming all UiWindow / UiWidgets are prepared for 1920, this is the scale factor for different canvas sizes
	 */
	public float gameCanvasWScaleFactor() {
		return mGameCanvasWScaleFactor;
	}

	public float gameCanvasHScaleFactor() {
		return mGameCanvasHScaleFactor;
	}

	public float uiCanvasWScaleFactor() {
		return mUiCanvasWScaleFactor;
	}

	public float uiCanvasHScaleFactor() {
		return mUiCanvasHScaleFactor;
	}

	public float minimumTitleHeight() {
		return mMinimumTitleHeight;
	}

	public float footerHeight() {
		return mFooterHeight;
	}

	/** Represents the padding space at the edge of the screen (horizontal). */
	public float windowPaddingH() {
		return mWindowPaddingH;
	}

	/** Represents the padding space at the edge of the screen (vertical). */
	public float windowPaddingV() {
		return mWindowPaddingV;
	}

	public float uiTransparencyFactor() {
		return mUITransparencyFactorActual;
	}

	public float uiScaleFactor() {
		return mUIScaleFactorActual;
	}

	public float uiTextScaleFactor() {
		return mUITextScaleFactorActual;
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

	public UiStructureController(DisplayManager displayManager, ControllerManager controllerManager, final int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mDisplayManager = displayManager;

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

	public void initialize(LintfordCore core) {
		super.initialize(core);
		updateHUDAreas(core);
		updateWindowUiComponentStructures(core);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lDisplayConfig = core.config().display();

		if (lDisplayConfig.stretchGameScreen()) {
			mGameCanvasWScaleFactor = (float) lDisplayConfig.gameResolutionWidth() / (float) ConstantsDisplay.REFERENCE_GAME_RESOLUTION_W;
			mGameCanvasHScaleFactor = (float) lDisplayConfig.gameResolutionHeight() / (float) ConstantsDisplay.REFERENCE_GAME_RESOLUTION_H;

			mUiCanvasWScaleFactor = (float) lDisplayConfig.uiResolutionWidth() / (float) ConstantsDisplay.REFERENCE_UI_RESOLUTION_W;
			mUiCanvasHScaleFactor = (float) lDisplayConfig.uiResolutionHeight() / (float) ConstantsDisplay.REFERENCE_UI_RESOLUTION_H;
		} else {
			mGameCanvasWScaleFactor = 1.f;
			mGameCanvasHScaleFactor = 1.f;

			mUiCanvasWScaleFactor = 1.f;
			mUiCanvasHScaleFactor = 1.f;
		}

		updateHUDAreas(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateHUDAreas(LintfordCore core) {
		updateGameUiStructure(core);
		updateMenuUiStructure(core);
	}

	private void updateWindowUiComponentStructures(LintfordCore core) {
		mUIScaleFactorActual = mDisplayManager.graphicsSettings().UiUserScale();
		mUITextScaleFactorActual = mDisplayManager.graphicsSettings().UiUserTextScale();
		mUITransparencyFactorActual = mDisplayManager.graphicsSettings().UiUserTransparencyScale();
	}

	private void updateMenuUiStructure(LintfordCore core) {
		updateWindowUiComponentStructures(core); // FIXME: remove from update when finished

		final float lWindowWidth = core.config().display().gameResolutionWidth();
		final float lWindowHeight = core.config().display().gameResolutionHeight();

		final float lVerticalInnerPadding = 1.f * mGameCanvasHScaleFactor;
		final float lModWidth = lWindowWidth - mWindowPaddingH * 2.f;
		final float lModHeight = lWindowHeight - mWindowPaddingV * 2.f;

		float lRemainingHeight = lModHeight;
		final float lMinimumTitleHeight = mMinimumTitleHeight;
		final float lMinimumFooterHeight = mFooterHeight;
		final float lTitleHeight = (float) Math.max(lMinimumTitleHeight, lModHeight * .15f - lVerticalInnerPadding);
		final float lFooterHeight = (float) Math.max(lMinimumFooterHeight, lModHeight * .10f - lVerticalInnerPadding);
		lRemainingHeight -= lTitleHeight;
		lRemainingHeight -= lFooterHeight;

		final float lMainHeight = lRemainingHeight;

		mMenuTitleRectangle.set(-lModWidth / 2, -lModHeight / 2, lModWidth, lTitleHeight);
		mMenuMainRectangle.set(-lModWidth / 2, -lModHeight / 2f + lTitleHeight + lVerticalInnerPadding * 2f, lModWidth, lMainHeight);
		mMenuFooterRectangle.set(-lModWidth / 2, lModHeight / 2f - lFooterHeight + lVerticalInnerPadding * 4f, lModWidth, lFooterHeight);
	}

	private void updateGameUiStructure(LintfordCore core) {
		final float lWindowWidth = core.config().display().windowWidth();
		final float lWindowHeight = core.config().display().windowHeight();

		final float lGameHeaderHeight = 40.f * mGameCanvasHScaleFactor;
		final float lGameFooterHeight = 70.f * mGameCanvasHScaleFactor;

		final float lMaxGameHudWidth = 1280.f;
		final float lMinGameHudWidth = 800.f;
		final float lGameHudHorizontalPadding = 50.f;
		final float lGameHudVerticalPadding = 10.f;

		final var lHUDWidth = Math.min(lMaxGameHudWidth, Math.max(lMinGameHudWidth, lWindowWidth)) - lGameHudHorizontalPadding;
		final var lHUDHeight = lWindowHeight - lGameFooterHeight - lGameHeaderHeight - lGameHudVerticalPadding;

		mGameHeaderRectangle.set(-lWindowWidth / 2f, -lWindowHeight / 2, lWindowWidth, lGameHeaderHeight);
		mGameHUDRectangle.set(-lHUDWidth / 2f, mGameHeaderRectangle.y() + mGameHeaderRectangle.height() + lGameHudVerticalPadding * .5f, lHUDWidth, lHUDHeight);
		mGameFooterRectangle.set(-lWindowWidth / 2f, lWindowHeight / 2 - lGameFooterHeight, lWindowWidth, lGameFooterHeight);
	}
}
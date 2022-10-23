package net.lintford.library.controllers.hud;

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
	private float mWindowAutoScaleFactorX;
	private float mWindowAutoScaleFactorY;
	private float mUITransparencyFactorActual;
	private float mUIScaleFactorActual;
	private float mUITextScaleFactorActual;
	private float mWindowPaddingH;
	private float mWindowPaddingV;
	private float mMinimumTitleHeight = 30.f;
	private float mMinimumFooterHeight = 30.f;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float minimumTitleHeight() {
		return mMinimumTitleHeight;
	}

	public void minimumTitleHeight(float minimumTitleHeight) {
		mMinimumTitleHeight = minimumTitleHeight;
	}

	public float minimumFooterHeight() {
		return mMinimumFooterHeight;
	}

	public void minimumFooterHeight(float minimumFooterHeight) {
		mMinimumFooterHeight = minimumFooterHeight;
	}

	/** The windowAutoScaleFactorX is the factor between the current window width and the base window width. */
	public float windowAutoScaleFactorX() {
		return mWindowAutoScaleFactorX;
	}

	/** The windowAutoScaleFactorY is the factor between the current window height and the base window height. */
	public float windowAutoScaleFactorY() {
		return mWindowAutoScaleFactorY;
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
	public void unload() {

	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lDisplayConfig = core.config().display();

		final float lBaseWindowWidth = lDisplayConfig.baseGameResolutionWidth();
		final float lBaseWindowHeight = lDisplayConfig.baseGameResolutionHeight();

		if (lDisplayConfig.stretchGameScreen()) {
			mWindowAutoScaleFactorX = 1.f;
			mWindowAutoScaleFactorY = 1.f;
		} else {
			mWindowAutoScaleFactorX = core.config().display().windowWidth() / lBaseWindowWidth;
			mWindowAutoScaleFactorY = core.config().display().windowHeight() / lBaseWindowHeight;
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
		mUIScaleFactorActual = mDisplayManager.graphicsSettings().UIScale();
		mUITextScaleFactorActual = mDisplayManager.graphicsSettings().UITextScale();
		mUITransparencyFactorActual = mDisplayManager.graphicsSettings().UITransparencyScale();
	}

	private void updateMenuUiStructure(LintfordCore core) {
		updateWindowUiComponentStructures(core); // FIXME: remove from update when finished

		// TODO: You are here
		// The problem is, the UiStructureController is assigning a boundary for the hud to render controls into (e.g. for the pause menu)
		// but it assumes it always has the area of the window - it actually has the game canvas, which when stretched, is just a subset

		final float lWindowWidth = core.config().display().baseGameResolutionWidth();
		final float lWindowHeight = core.config().display().baseGameResolutionHeight();

		final float lVerticalInnerPadding = 1.f * mWindowAutoScaleFactorY;
		final float lModWidth = lWindowWidth - mWindowPaddingH * 2.f;
		final float lModHeight = lWindowHeight - mWindowPaddingV * 2.f;

		float lRemainingHeight = lModHeight;
		final float lMinimumTitleHeight = mMinimumTitleHeight;
		final float lMinimumFooterHeight = mMinimumFooterHeight;
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

		final float lGameHeaderHeight = 40f * windowAutoScaleFactorY();
		final float lGameFooterHeight = 70f * windowAutoScaleFactorY();

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
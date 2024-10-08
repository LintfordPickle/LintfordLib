package net.lintfordlib.controllers.hud;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.options.DisplayManager;

public class HudLayoutController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Layout Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayManager mDisplayManager;

	private Rectangle mGameHUDRectangle;
	private Rectangle mGameHeaderRectangle;
	private Rectangle mGameFooterRectangle;

	public final Rectangle mMenuTitleRectangle;
	public final Rectangle mMenuMainRectangle;
	public final Rectangle mMenuLeftColumnRectangle = new Rectangle();
	public final Rectangle mMenuFooterRectangle;

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

	public HudLayoutController(DisplayManager displayManager, ControllerManager controllerManager, final int entityGroupUid) {
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
		updateWindowUiComponentStructures(core);

		final var lHud = core.HUD();
		final var lHudBB = lHud.boundingRectangle();
		final float lWindowWidth = lHudBB.width();
		final float lWindowHeight = lHudBB.height();

		final float lVerticalInnerPadding = 1.f;
		final float lModWidth = lWindowWidth - mWindowPaddingH * 2.f;
		final float lModHeight = lWindowHeight - mWindowPaddingV * 2.f;

		float lRemainingHeight = lModHeight;
		final float lMinimumTitleHeight = mMinimumTitleHeight;
		final float lMinimumFooterHeight = mFooterHeight;
		final float lTitleHeight = Math.max(lMinimumTitleHeight, lModHeight * .15f - lVerticalInnerPadding);
		final float lFooterHeight = Math.max(lMinimumFooterHeight, lModHeight * .10f - lVerticalInnerPadding);
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

		final float lGameHeaderHeight = 40.f;
		final float lGameFooterHeight = 70.f;

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
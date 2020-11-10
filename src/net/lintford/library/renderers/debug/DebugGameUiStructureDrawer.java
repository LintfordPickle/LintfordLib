package net.lintford.library.renderers.debug;

import net.lintford.library.controllers.hud.UiStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugGameUiStructureDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG Game Ui Outlines";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiStructureController mUiStructureController;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return true;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugGameUiStructureDrawer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mIsActive = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mUiStructureController = (UiStructureController) pCore.controllerManager().getControllerByNameRequired(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isActive())
			return;

		if (mUiStructureController == null) {
			mUiStructureController = (UiStructureController) pCore.controllerManager().getControllerByNameRequired(UiStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		} else {
			// Game HUD
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUiStructureController.gameHeaderRectangle(), 0f, 1f, 0f);
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUiStructureController.gameHUDRectangle(), 1f, 0f, 1f);
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUiStructureController.gameFooterRectangle(), 0f, 1f, 0f);

		}

	}

}

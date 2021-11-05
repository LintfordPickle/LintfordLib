package net.lintford.library.renderers.debug;

import net.lintford.library.controllers.hud.UiStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugMenuUiStructureDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG Menu Ui Outlines";

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

	public DebugMenuUiStructureDrawer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mIsActive = true;

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
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUiStructureController.menuTitleRectangle(), 0f, 1f, 0f);
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUiStructureController.menuMainRectangle(), 1f, 0f, 1f);
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUiStructureController.menuFooterRectangle(), 0f, 1f, 0f);

		}

	}
}
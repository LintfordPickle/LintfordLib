package net.lintford.library.renderers.debug;

import net.lintford.library.controllers.hud.UIHUDStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugHUDDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG HUD Outlines";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UIHUDStructureController mUIHUDController;

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

	public DebugHUDDrawer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mIsActive = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mUIHUDController = (UIHUDStructureController) pCore.controllerManager().getControllerByNameRequired(UIHUDStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isActive())
			return;

		if (mUIHUDController == null) {
			mUIHUDController = (UIHUDStructureController) pCore.controllerManager().getControllerByNameRequired(UIHUDStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		} else {
			// Game HUD
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.gameHeaderRectangle(), 0f, 1f, 0f);
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.gameHUDRectangle(), 1f, 0f, 1f);
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), mUIHUDController.gameFooterRectangle(), 0f, 1f, 0f);

		}

	}

}

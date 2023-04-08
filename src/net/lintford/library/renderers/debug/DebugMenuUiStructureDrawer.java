package net.lintford.library.renderers.debug;

import net.lintford.library.controllers.hud.HudLayoutController;
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

	private HudLayoutController mUiStructureController;

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

	public DebugMenuUiStructureDrawer(RendererManager rendererManager, int entityGroupUid) {
		super(rendererManager, RENDERER_NAME, entityGroupUid);

		mIsActive = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mUiStructureController = (HudLayoutController) core.controllerManager().getControllerByNameRequired(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	@Override
	public void draw(LintfordCore core) {
		if (!isActive())
			return;

		if (mUiStructureController == null) {
			mUiStructureController = (HudLayoutController) core.controllerManager().getControllerByNameRequired(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		} else {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mUiStructureController.menuTitleRectangle(), 0f, 1f, 0f);
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mUiStructureController.menuMainRectangle(), 1f, 0f, 1f);
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), mUiStructureController.menuFooterRectangle(), 0f, 1f, 0f);
		}
	}
}
package net.lintfordlib.renderers.debug;

import net.lintfordlib.controllers.hud.HudLayoutController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.SimpleRendererManager;

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

	public DebugMenuUiStructureDrawer(SimpleRendererManager rendererManager, int entityGroupUid) {
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
	public void draw(LintfordCore core, RenderPass renderPass) {
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
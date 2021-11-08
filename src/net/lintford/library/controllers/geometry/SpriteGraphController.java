package net.lintford.library.controllers.geometry;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphManager;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphInstance;

public class SpriteGraphController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Sprite Graph Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourceController mResourceController;
	private SpriteGraphManager mSpriteGraphManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpriteGraphManager spriteGraphManager() {
		return mSpriteGraphManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphController(ControllerManager pControllerManager, SpriteGraphManager pSpriteGraphManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mSpriteGraphManager = pSpriteGraphManager;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);
		mResourceController = (ResourceController) pCore.controllerManager().getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	@Override
	public void unload() {

	}

	public void update(LintfordCore pCore) {
		final var lSpriteGraphList = mSpriteGraphManager.instances();
		final var lSpriteGraphCount = lSpriteGraphList.size();

		for (int i = 0; i < lSpriteGraphCount; i++) {
			final var lSpriteGraph = lSpriteGraphList.get(i);
			if (lSpriteGraph == null)
				continue;

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteGraphInstance getSpriteGraphInstance(String lSpriteGraphDefinitionName, int lEntityGroupUid) {
		final var lResourceManager = mResourceController.resourceManager();
		final var lSpriteGraphDefinition = lResourceManager.spriteGraphRepository().getSpriteGraphDefinition(lSpriteGraphDefinitionName, mEntityGroupID);

		if (lSpriteGraphDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Could not resolve mob SpriteGraphDefinition from name '%s'", lSpriteGraphDefinitionName));
			return null;

		}

		return spriteGraphManager().getInstanceOfGraph(lSpriteGraphDefinition, entityGroupID());
	}
}
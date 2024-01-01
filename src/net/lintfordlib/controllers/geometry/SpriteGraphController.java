package net.lintfordlib.controllers.geometry;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.core.ResourceController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.spritegraph.SpriteGraphManager;
import net.lintfordlib.core.geometry.spritegraph.instances.SpriteGraphInstance;

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

	public SpriteGraphController(ControllerManager controllerManager, SpriteGraphManager spriteGraphManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mSpriteGraphManager = spriteGraphManager;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		mResourceController = (ResourceController) core.controllerManager().getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	public void update(LintfordCore core) {
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

	public SpriteGraphInstance getSpriteGraphInstance(String spriteGraphDefinitionName, int entityGroupUid) {
		final var lResourceManager = mResourceController.resourceManager();
		final var lSpriteGraphDefinition = lResourceManager.spriteGraphRepository().getSpriteGraphDefinition(spriteGraphDefinitionName, mEntityGroupUid);

		if (lSpriteGraphDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Could not resolve mob SpriteGraphDefinition from name '%s'", spriteGraphDefinitionName));
			return null;
		}

		return spriteGraphManager().getInstanceOfGraph(lSpriteGraphDefinition, entityGroupUid());
	}
}
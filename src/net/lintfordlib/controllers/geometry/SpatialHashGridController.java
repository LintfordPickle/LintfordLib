package net.lintfordlib.controllers.geometry;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.geometry.partitioning.GridEntity;
import net.lintfordlib.core.geometry.partitioning.SpatialHashGrid;

public class SpatialHashGridController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Spatial Hash Grid Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private SpatialHashGrid<GridEntity> mHashGrid;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpatialHashGrid<GridEntity> hashGrid() {
		return mHashGrid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpatialHashGridController(ControllerManager controllerManager, SpatialHashGrid<GridEntity> hashGrid, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mHashGrid = hashGrid;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}

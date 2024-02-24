package net.lintfordlib.controllers.editor;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.geometry.partitioning.GridEntity;
import net.lintfordlib.core.geometry.partitioning.SpatialHashGrid;

public class EditorHashGridController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor HashGrid Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private SpatialHashGrid<GridEntity> mHashGrid;
	private final List<IGridControllerCallback> mHashContainerCallbacks = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpatialHashGrid<GridEntity> hashGrid() {
		return mHashGrid;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public EditorHashGridController(ControllerManager controllerManager, SpatialHashGrid<GridEntity> hashGrid, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mHashGrid = hashGrid;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addGridListener(IGridControllerCallback gridListener) {
		if (mHashContainerCallbacks.contains(gridListener) == false) {
			mHashContainerCallbacks.add(gridListener);
		}
	}

	public void removeGridListener(IGridControllerCallback gridListener) {
		mHashContainerCallbacks.remove(gridListener);
	}

	public void resizeGrid(int width, int height, int tilesWide, int tilesHigh) {
		if (mHashGrid.boundaryWidth() == width && mHashGrid.boundaryHeight() == height && mHashGrid.numTilesWide() == tilesWide && mHashGrid.numTilesHigh() == tilesHigh)
			return;

		final int lNumListeners = mHashContainerCallbacks.size();
		for (int i = 0; i < lNumListeners; i++) {
			mHashContainerCallbacks.get(i).gridDeleted(mHashGrid);
		}

		// TODO: There is a chance that the hash grid won't even change right?
		mHashGrid.createNewHashGrid(width, height, tilesWide, tilesHigh);

		for (int i = 0; i < lNumListeners; i++) {
			mHashContainerCallbacks.get(i).gridCreated(mHashGrid);
		}
	}
}

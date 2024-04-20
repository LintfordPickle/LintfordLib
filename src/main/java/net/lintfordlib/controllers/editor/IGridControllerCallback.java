package net.lintfordlib.controllers.editor;

import net.lintfordlib.core.geometry.partitioning.GridEntity;
import net.lintfordlib.core.geometry.partitioning.SpatialHashGrid;

public interface IGridControllerCallback {

	void gridCreated(SpatialHashGrid<GridEntity> grid);

	void gridDeleted(SpatialHashGrid<GridEntity> grid);

}

package net.lintfordlib.core.graphics.decalbin;

import java.util.List;

import net.lintfordlib.core.binpacking.IBinPackedItem;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Vector2f;

public interface IDecalBinPackedItem extends IBinPackedItem {

	Rectangle world();

	List<Vector2f> localVertices();

	List<Vector2f> uvs();

	float worldCenterX();

	float worldCenterY();

}

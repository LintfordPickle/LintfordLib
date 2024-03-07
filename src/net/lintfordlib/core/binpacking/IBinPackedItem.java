package net.lintfordlib.core.binpacking;

import java.util.List;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Vector2f;

public interface IBinPackedItem {

	Rectangle world();

	List<Vector2f> localVertices();
	
	List<Vector2f> uvs();

	float worldCenterX();

	float worldCenterY();

	float binPositionX();

	float binPositionY();

	float binWidth();

	float binHeight();

	void assignToBin(float x, float y, float width, float height);
}

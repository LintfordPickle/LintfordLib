package net.lintfordlib.core.binpacking;

import java.util.List;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Vector4f;

public interface IBinPackedItem {

	Rectangle world();

	List<Vector4f> worldVerticesUv();

	float worldCenterX();

	float worldCenterY();

	float binPositionX();

	float binPositionY();

	float binWidth();

	float binHeight();

	void assignToBin(float x, float y, float width, float height);
}

package net.lintfordlib.core.binpacking;

import net.lintfordlib.core.geometry.Rectangle;

public interface IBinPackedItem {

	Rectangle world();

	float binPositionX();

	float binPositionY();

	float binWidth();

	float binHeight();

	void assignToBin(float x, float y, float width, float height);
}

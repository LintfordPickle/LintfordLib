package net.lintford.library.core.collisions;

import net.lintford.library.core.maths.Vector2f;

public class SAT {

	public static final CollisionManifold tempResult = new CollisionManifold();

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static CollisionManifold intersectsCircles(Vector2f centerA, float radiusA, Vector2f centerB, float radiusB) {
		final float dist = Vector2f.distance(centerA.x, centerA.y, centerB.x, centerB.y);
		final float radii = radiusA + radiusB;

		if (dist >= radii) {
			tempResult.intersection = false;
			return tempResult;
		}

		tempResult.normal.x = centerB.x - centerA.x;
		tempResult.normal.y = centerB.y - centerA.y;
		tempResult.normal.nor();
		tempResult.depth = radii - dist;

		tempResult.intersection = true;

		return tempResult;
	}

	public static boolean overlaps(Vector2f point0, Vector2f point1) {
		return !(point0.x > point1.y || point1.x > point0.y);
	}
}

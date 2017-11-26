package net.lintford.library.core.maths;

// https://github.com/kg/PlatformerStarterKit/blob/master/RectangleExtensions.cs
public class RectangleExtensions {

	public float x;
	public float y;

	/**
	 * 
	 * Calculates the signed depth of intersection between two rectangles.
	 * 
	 * The amount of overlap between two intersecting rectangles. These depth values can be negative depending on which sides the rectangles intersect. This allows callers to determine the correct direction to push objects in order to resolve
	 * collisions.
	 *
	 * If the rectangles are not intersecting, Vector2.Zero is returned.
	 */
	public int performIntersectionDepth(float ax, float ay, float aw, float ah, float bx, float by, float bw, float bh) {
		// Calculate half sizes.
		float halfWidthA = aw / 2.0f;
		float halfHeightA = ah / 2.0f;
		float halfWidthB = bw / 2.0f;
		float halfHeightB = bh / 2.0f;

		// Calculate centers.
		// TODO: this is Java, where structure don't exist!
		Vector2f centerA = new Vector2f(ax + halfWidthA, ay + halfHeightA);
		Vector2f centerB = new Vector2f(bx + halfWidthB, by + halfHeightB);

		// Calculate current and minimum-non-intersecting distances between centers.
		float distanceX = centerA.x - centerB.x;
		float distanceY = centerA.y - centerB.y;
		float minDistanceX = halfWidthA + halfWidthB;
		float minDistanceY = halfHeightA + halfHeightB;

		// If we are not intersecting at all, return (0, 0).
		if (Math.abs(distanceX) >= minDistanceX || Math.abs(distanceY) >= minDistanceY)
			return -2;

		// Calculate and return intersection depths.
		float depthX = distanceX > 0 ? minDistanceX - distanceX : -minDistanceX - distanceX;
		float depthY = distanceY > 0 ? minDistanceY - distanceY : -minDistanceY - distanceY;
		x = depthX;
		y = depthY;

		return 0;
	}
}

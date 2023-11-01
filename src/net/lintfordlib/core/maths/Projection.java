package net.lintfordlib.core.maths;

import java.util.List;

public class Projection {

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	public static class SatCollisionProjectionResult {
		public float min;
		public float max;

		public void set(float min, float max) {
			this.min = min;
			this.max = max;
		}
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static void projectVertices(List<Vector2f> vertices, float axisX, float axisY, SatCollisionProjectionResult result) {
		var min = Float.MAX_VALUE;
		var max = -Float.MAX_VALUE;

		final var lNumVerts = vertices.size();
		for (int i = 0; i < lNumVerts; i++) {
			final var v = vertices.get(i);
			final var proj = Vector2f.dot(v.x, v.y, axisX, axisY);

			if (proj < min)
				min = proj;

			if (proj > max)
				max = proj;
		}

		result.set(min, max);
	}

	public static void projectCircle(float centerX, float centerY, float radius, float axisX, float axisY, SatCollisionProjectionResult result) {
		final var axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

		final var directionX = axisX / axisLength;
		final var directionY = axisY / axisLength;

		final var directionAndDirX = directionX * radius;
		final var directionAndDirY = directionY * radius;

		final var point1X = centerX + directionAndDirX;
		final var point1Y = centerY + directionAndDirY;

		final var point2X = centerX - directionAndDirX;
		final var point2Y = centerY - directionAndDirY;

		result.min = Vector2f.dot(point1X, point1Y, axisX, axisY);
		result.max = Vector2f.dot(point2X, point2Y, axisX, axisY);

		if (result.min > result.max) {
			final float t = result.min;
			result.min = result.max;
			result.max = t;
		}
	}

	public static void projectLine(float sx, float sy, float ex, float ey, float axisX, float axisY, SatCollisionProjectionResult toFill) {
		toFill.min = Vector2f.dot(sx, sy, axisX, axisY);
		toFill.max = Vector2f.dot(ex, ey, axisX, axisY);

		if (toFill.min > toFill.max) {
			final float t = toFill.min;
			toFill.min = toFill.max;
			toFill.max = t;
		}
	}

	public static void projectLineWidth(float sx, float sy, float ex, float ey, float radius, float axisX, float axisY, SatCollisionProjectionResult toFill) {
		toFill.min = Float.MAX_VALUE;
		toFill.max = Float.MIN_VALUE;

		var min = Float.MAX_VALUE;
		var max = -Float.MAX_VALUE;
		float proj = 0;

		// TODO: Check if we can pre-compute these (obviously without the axis')

		proj = Vector2f.dot(sx - radius * axisX, sy - radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		proj = Vector2f.dot(sx + radius * axisX, sy + radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		proj = Vector2f.dot(ex - radius * axisX, ey - radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		proj = Vector2f.dot(ex + radius * axisX, ey + radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		toFill.set(min, max);
	}

}

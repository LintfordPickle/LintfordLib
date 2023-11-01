package net.lintfordlib.core.physics.collisions;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public class CollisionExtensions {

	/***
	 * Checks if the given point lies within a {@link RigidBody}s bounding radius. This assumes the RigidBody has a radius defined!
	 * 
	 * @param body The body against which the check should be made.
	 * @param x    The x component of the point to check.
	 * @param y    The x component of the point to check.
	 * @return Returns true if the point lies within the radius of the body's bounding radius, otherwise false.
	 */
	public static boolean pointIntersectsBodyRadius(RigidBody body, float x, float y) {
		final float xx = body.transform.p.x - x;
		final float yy = body.transform.p.y - y;
		return (xx * xx + yy * yy) < (body.shape().radius() * body.shape().radius());
	}

	/***
	 * Checks if the given point lies within the bounds of the box body.
	 * 
	 * @param body The box body against which to check.
	 * @param x    The x component of the point to check.
	 * @param y    The y component of the point to check.
	 * @return Returns true if a collision is detected, otherwise false.
	 */
	public static final boolean pointIntersectsBoxBody(RigidBody body, float x, float y) {

		assert (body.getWorldVertices().size() == 4) : "pointIntersectsBoxPolygon requires 4 vertices";

		final var lWorldVertices = body.getWorldVertices();
		final var a = lWorldVertices.get(0);
		final var b = lWorldVertices.get(1);
		final var c = lWorldVertices.get(2);
		final var d = lWorldVertices.get(3);

		{ // tri a
			float fAB = (y - a.y) * (b.x - a.x) - (x - a.x) * (b.y - a.y);
			float fBC = (y - b.y) * (c.x - b.x) - (x - b.x) * (c.y - b.y);
			float fCA = (y - c.y) * (a.x - c.x) - (x - c.x) * (a.y - c.y);

			if (fAB * fBC > 0 && fBC * fCA > 0) {
				return true;
			}
		}

		{ // tri b
			float fAC = (y - a.y) * (c.x - a.x) - (x - a.x) * (c.y - a.y);
			float fCD = (y - c.y) * (d.x - c.x) - (x - c.x) * (d.y - c.y);
			float fDA = (y - d.y) * (a.x - d.x) - (x - d.x) * (a.y - d.y);

			if (fAC * fCD > 0 && fCD * fDA > 0) {

				return true;
			}
		}

		return false;
	}

	public static final boolean pointIntersectsLineWidthBody(RigidBody lineWidthBody, float x, float y) {

		assert (lineWidthBody.getWorldVertices().size() == 2) : "pointIntersectsLineWidthBody requires 2 vertices";

		final var lWorldVertices = lineWidthBody.getWorldVertices();
		final var start = lWorldVertices.get(0);
		final var end = lWorldVertices.get(1);

		final var lLineX1 = end.x - start.x;
		final var lLineY1 = end.y - start.y;

		final var lLineX2 = x - start.x;
		final var lLineY2 = y - start.y;

		final var lEdgeLength = lLineX1 * lLineX1 + lLineY1 * lLineY1;

		final var v = lLineX1 * lLineX2 + lLineY1 * lLineY2;
		final var t = MathHelper.clamp(v, 0.f, lEdgeLength) / lEdgeLength;

		final var lClosestPointX = start.x + t * lLineX1;
		final var lClosestPointY = start.y + t * lLineY1;

		final var distance = (float) Math.sqrt((x - lClosestPointX) * (x - lClosestPointX) + (y - lClosestPointY) * (y - lClosestPointY));

		final var lPointRadius = 1.f * ConstantsPhysics.PixelsToUnits();
		final var lLineRadius = lineWidthBody.shape().height() * .5f;
		if (distance <= (lLineRadius + lPointRadius))
			return true;

		return false;
	}

	/***
	 * Checks if the given points lies within the bounds of the RidigBody. This function calls outs depending on the type of the attached {@link ShapeType}
	 */
	public static boolean pointIntersectsBody(RigidBody body, float x, float y) {
		switch (body.shape().shapeType()) {
		case Polygon: {
			return intersectsPointPolygon(body, x, y);
		}

		case LineWidth: {
			return pointIntersectsLineWidthBody(body, x, y);
		}

		default:
		case Circle: {
			return pointIntersectsCircleBody(body, x, y);
		}

		}
	}

	/***
	 * Checks if the given point lies within the bounds of a concave polygon.
	 * 
	 * @param x               The x component of the input point to test.
	 * @param y               The y component of the input point to test.
	 * @param polygonVertices The Polygon vertices. The polygon must be concave and have a CCW winding order.
	 * @return true if the point lies within the polygon, otherwise false
	 */
	public static final boolean intersectsPointPolygon(RigidBody concavePolygonBody, float x, float y) {
		final var polygonVertices = concavePolygonBody.getWorldVertices();

		int windingNumber = 0;
		final int n = polygonVertices.size();
		for (int i = 0; i < n; i++) {
			final var cur_i = i;
			final var next_i = i + 1 == n ? 0 : i + 1;

			if (polygonVertices.get(cur_i).y <= y) { // start y <= P.y
				if (polygonVertices.get(next_i).y > y) { // an upward crossing
					final var l = isLeft(polygonVertices.get(cur_i), polygonVertices.get(next_i), x, y);
					if (l > 0) // the point left of edge
						++windingNumber; // have a valid up intersect
					else if (l == 0)
						return true;
				}
			} else {
				if (polygonVertices.get(next_i).y <= y) { // a downward crossing
					final var l = isLeft(polygonVertices.get(cur_i), polygonVertices.get(next_i), x, y);
					if (l < 0) // the point right of edge
						--windingNumber; // have a valid down intersect
					else if (l == 0)
						return true;
				}
			}
		}
		return windingNumber != 0;
	}

	// utility function used within intersectsPointPolygon for checking the winding order
	private static final float isLeft(Vector2f p0, Vector2f p1, float x, float y) {
		return ((p1.x - p0.x) * (y - p0.y) - (x - p0.x) * (p1.y - p0.y));
	}

	/***
	 * Checks if the given point intersects a CircleShape.
	 */
	public static final boolean pointIntersectsCircleBody(RigidBody circleBody, float pointX, float pointY) {
		final var circleX = circleBody.transform.p.x;
		final var circleY = circleBody.transform.p.y;
		final var circleRadius = circleBody.shape().radius();

		final float lDist = (float) (circleX - pointX) * (circleX - pointX) + (circleY - pointY) * (circleY - pointY);
		if (lDist < circleRadius * circleRadius) {
			return true;
		}

		return false;
	}
}
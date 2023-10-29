package net.lintfordlib.core.physics.collisions;

import java.util.List;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public class CollisionExtensions {

	/** returns true if the point lies within the radius of the body's bounding radius */
	public static boolean intersectsBodyRadius(RigidBody body, float x, float y) {
		final float xx = body.transform.p.x - x;
		final float yy = body.transform.p.y - y;
		return (xx * xx + yy * yy) < (body.shape().radius() * body.shape().radius());
	}

	// TODO: Split this method into separate methods for each shape
	// TODO: A lot of methods in the SATIntersections class are actualy just algorithms that aren't SAT related, but belong in here

	/** returns true if the point lies within the area of the rigid body. Note the implementation is different depending on the type of {@link ShapeType} */
	public static boolean intersectsBody(RigidBody body, float x, float y) {
		switch (body.shape().shapeType()) {
		case Polygon: {
			return intersectsPointPolygon(x, y, body.getWorldVertices());
		}

//		case Box: {
//			final var lWorldVertices = body.getWorldVertices();
//			final var a = lWorldVertices.get(0);
//			final var b = lWorldVertices.get(1);
//			final var c = lWorldVertices.get(2);
//			final var d = lWorldVertices.get(3);
//
//			{ // tri a
//				float fAB = (y - a.y) * (b.x - a.x) - (x - a.x) * (b.y - a.y);
//				float fBC = (y - b.y) * (c.x - b.x) - (x - b.x) * (c.y - b.y);
//				float fCA = (y - c.y) * (a.x - c.x) - (x - c.x) * (a.y - c.y);
//
//				if (fAB * fBC > 0 && fBC * fCA > 0) {
//					return true;
//				}
//			}
//
//			{ // tri b
//				float fAC = (y - a.y) * (c.x - a.x) - (x - a.x) * (c.y - a.y);
//				float fCD = (y - c.y) * (d.x - c.x) - (x - c.x) * (d.y - c.y);
//				float fDA = (y - d.y) * (a.x - d.x) - (x - d.x) * (a.y - d.y);
//
//				if (fAC * fCD > 0 && fCD * fDA > 0) {
//
//					return true;
//				}
//			}
//
//			return false;
//		}

		case LineWidth: {
			final var lWorldVertices = body.getWorldVertices();
			final var start = lWorldVertices.get(0);
			final var end = lWorldVertices.get(1);

			float lLineX1 = end.x - start.x;
			float lLineY1 = end.y - start.y;

			float lLineX2 = x - start.x;
			float lLineY2 = y - start.y;

			float lEdgeLength = lLineX1 * lLineX1 + lLineY1 * lLineY1;

			float v = lLineX1 * lLineX2 + lLineY1 * lLineY2;
			float t = MathHelper.clamp(v, 0.f, lEdgeLength) / lEdgeLength;

			float lClosestPointX = start.x + t * lLineX1;
			float lClosestPointY = start.y + t * lLineY1;

			float distance = (float) Math.sqrt((x - lClosestPointX) * (x - lClosestPointX) + (y - lClosestPointY) * (y - lClosestPointY));

			final float lPointRadius = 1.f * ConstantsPhysics.PixelsToUnits();
			if (distance <= (body.shape().radius() * lPointRadius))
				return true;

			return false;
		}

		default:
		case Circle: {
			return intersectsCirclePoint(body.transform.p.x, body.transform.p.y, body.shape().radius(), x, y);
		}

		}
	}

	// ----

	/***
	 * @param x               The x component of the input point to test.
	 * @param y               The y component of the input point to test.
	 * @param polygonVertices The Polygon vertices. The polygon must be concave and have a CCW winding order.
	 * @return true if the point lies within the polygon, otherwise false
	 */
	public static final boolean intersectsPointPolygon(float x, float y, List<Vector2f> polygonVertices) {
		int windingNumber = 0;
		int n = polygonVertices.size();
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

	private static final float isLeft(Vector2f p0, Vector2f p1, float x, float y) {
		return ((p1.x - p0.x) * (y - p0.y) - (x - p0.x) * (p1.y - p0.y));
	}

	// ---

	/** Checks if a point is within a given circle */
	public static final boolean intersectsCirclePoint(float circleX, float circleY, float circleRadius, float pointX, float pointY) {
		final float lDist = (float) (circleX - pointX) * (circleX - pointX) + (circleY - pointY) * (circleY - pointY);
		if (lDist < circleRadius * circleRadius) {
			return true;
		}

		return false;
	}

	public static boolean doCirclesOverlap(float x1, float y1, float r1, float x2, float y2, float r2) {
		return Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) < (r1 + r2) * (r1 + r2);
	}

	public static boolean intersectsLineLine(Vector2f a, Vector2f b, Vector2f p, Vector2f q, Vector2f outVector) {
		final float l0x = b.x - a.x;
		final float l0y = b.y - a.y;
		final float l1x = q.x - p.x;
		final float l1y = q.y - p.y;

		float s = (-l0y * (a.x - p.x) + l0x * (a.y - p.y)) / (-l1x * l0y + l0x * l1y);

		float t = (l1x * (a.y - p.y) - l1y * (a.x - p.x)) / (-l1x * l0y + l0x * l1y);

		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
			outVector.x = a.x + (t * l0x);
			outVector.y = a.y + (t * l0y);
			return true;
		}

		return false; // No collision
	}
}
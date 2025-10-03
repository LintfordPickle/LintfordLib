package net.lintfordlib.core.maths;

import java.util.List;

public class CollisionExtensions {

	private CollisionExtensions() {
	}

	/***
	 * Checks if the given point lies within the bounds of the box body.
	 * 
	 * @param vertices The 4 vertices which form the quadrilateral polygon.
	 * @param x        The Y component of the point to check.
	 * @param y        The X component of the point to check.
	 * @return Returns true if the point is within one of the triangles which form the polygon.
	 */
	public static final boolean pointIntersectsQuadrilateralPolygon(List<Vector2f> vertices, float x, float y) {

		assert (vertices.size() == 4) : "PointIntersectsQuadrilateralPolygon requires 4 vertices";

		final var a = vertices.get(0);
		final var b = vertices.get(1);
		final var c = vertices.get(2);
		final var d = vertices.get(3);

		// tri a
		float fAB = (y - a.y) * (b.x - a.x) - (x - a.x) * (b.y - a.y);
		float fBC = (y - b.y) * (c.x - b.x) - (x - b.x) * (c.y - b.y);
		float fCA = (y - c.y) * (a.x - c.x) - (x - c.x) * (a.y - c.y);

		if (fAB * fBC > 0 && fBC * fCA > 0)
			return true;

		// tri b
		float fAC = (y - a.y) * (c.x - a.x) - (x - a.x) * (c.y - a.y);
		float fCD = (y - c.y) * (d.x - c.x) - (x - c.x) * (d.y - c.y);
		float fDA = (y - d.y) * (a.x - d.x) - (x - d.x) * (a.y - d.y);

		if (fAC * fCD > 0 && fCD * fDA > 0)
			return true;

		return false;
	}

	/**
	 * Checks for collision between a point and a lineWidth (that is, a line which has a width). Note that this collision detection causes the line to appear larger at each end, by exactly the radius of the line.
	 * 
	 * @param vertices   The 2 vertices which form the start and end of the line.
	 * @param lineRadius The half width of the line.
	 * @param x          The X component of the point to test.
	 * @param y          The Y component of the point to test.
	 * @param pr         The radius of the point to check.
	 * 
	 * @return True if a collision is detected, otherwise false.
	 */
	public static final boolean pointIntersectsLineWidth(List<Vector2f> vertices, float lineRadius, float x, float y, float pr) {
		if (vertices == null || vertices.size() <= 2)
			return false;

		final var start = vertices.get(0);
		final var end = vertices.get(1);

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
		return distance <= (lineRadius + pr);
	}

	/**
	 * Checks for collision between a point and a lineWidth (that is, a line which has a width). Note that this collision detection causes the line to appear larger at each end, by exactly the radius of the line.
	 * 
	 * @param lsx        The X coordinate of the start of the line to check against.
	 * @param lsy        The Y coordinate of the start of the line to check against.
	 * @param lex        The X coordinate of the end of the line to check against.
	 * @param ley        The Y coordinate of the end of the line to check against.
	 * @param lineRadius The radius of the line.
	 * @param cwx        The X component of the circle to test.
	 * @param cwy        The Y component of the circle to test.
	 * @param cr         The radius of the circle to check.
	 * 
	 * @return True if a collision is detected, otherwise false.
	 */
	public static boolean intersectsLineCircle(float lsx, float lsy, float lex, float ley, float lineRadius, float cwx, float cwy, float cr) {
		final var lLineX1 = lex - lsx;
		final var lLineY1 = ley - lsy;

		final var lLineX2 = cwx - lsx;
		final var lLineY2 = cwy - lsy;

		final var lEdgeLength = lLineX1 * lLineX1 + lLineY1 * lLineY1;

		final var v = lLineX1 * lLineX2 + lLineY1 * lLineY2;
		final var t = MathHelper.clamp(v, 0.f, lEdgeLength) / lEdgeLength;

		final var lClosestPointX = lsx + t * lLineX1;
		final var lClosestPointY = lsy + t * lLineY1;

		final var distance = (float) Math.sqrt((cwx - lClosestPointX) * (cwx - lClosestPointX) + (cwy - lClosestPointY) * (cwy - lClosestPointY));
		return distance <= (lineRadius + cr);
	}

	/***
	 * Checks if the given point lies within the bounds of a concave polygon.
	 * 
	 * @param vertices The Polygon vertices. The polygon must be concave and have a CCW winding order.
	 * @param x        The X component of the input point to test.
	 * @param y        The Y component of the input point to test.
	 * 
	 * @return true if the point lies within the polygon, otherwise false
	 */
	public static final boolean intersectsPointPolygon(List<Vector2f> vertices, float x, float y) {
		int windingNumber = 0;
		final int n = vertices.size();
		for (int i = 0; i < n; i++) {
			final var cur_i = i;
			final var next_i = i + 1 == n ? 0 : i + 1;

			if (vertices.get(cur_i).y <= y) { // start y <= P.y
				if (vertices.get(next_i).y > y) { // an upward crossing
					final var l = isLeft(vertices.get(cur_i), vertices.get(next_i), x, y);
					if (l > 0) // the point left of edge
						++windingNumber; // have a valid up intersect
					else if (l == 0)
						return true;
				}
			} else {
				if (vertices.get(next_i).y <= y) { // a downward crossing
					final var l = isLeft(vertices.get(cur_i), vertices.get(next_i), x, y);
					if (l < 0) // the point right of edge
						--windingNumber; // have a valid down intersect
					else if (l == 0)
						return true;
				}
			}
		}
		return windingNumber != 0;
	}

	// utility function used within intersectsPointPolygon for checking the winding order.
	private static final float isLeft(Vector2f p0, Vector2f p1, float x, float y) {
		return ((p1.x - p0.x) * (y - p0.y) - (x - p0.x) * (p1.y - p0.y));
	}

	/***
	 * Checks if the given point intersects a circle.
	 * 
	 * @param circleCenterX The X component of the center of the circle.
	 * @param circleCenterY The Y component of the center of the circle.
	 * @param circleRadius  The radius of the circle.
	 * @param pointX        The X component of the point to test.
	 * @param pointY        The Y component of the point to test.
	 * 
	 * @return True if a collision is detected, otherwise false.
	 */
	public static final boolean pointIntersectsCircle(float circleCenterX, float circleCenterY, float circleRadius, float pointX, float pointY) {
		final var xx = circleCenterX - pointX;
		final var yy = circleCenterY - pointY;

		return (xx * xx) + (yy * yy) < circleRadius * circleRadius;
	}

	/***
	 * Checks for collisions between two circles.
	 * 
	 * @param circle1CenterX The X component of the first circle's center position.
	 * @param circle1CenterY The Y component of the first circle's center position.
	 * @param circle1Radius  The radius of the first circle
	 * @param circle2CenterX The X component of the second circle's center position.
	 * @param circle2CenterY The Y component of the second circle's center position.
	 * @param circle2Radius  The radius of the first circle
	 * 
	 * @return True if a collision occurs, otherwise false.
	 */
	public static final boolean intersectsCircleCircle(float circle1CenterX, float circle1CenterY, float circle1Radius, float circle2CenterX, float circle2CenterY, float circle2Radius) {
		return Math.abs((circle1CenterX - circle2CenterX) * (circle1CenterX - circle2CenterX) + (circle1CenterY - circle2CenterY) * (circle1CenterY - circle2CenterY)) < (circle1Radius + circle2Radius) * (circle1Radius + circle2Radius);
	}

	/***
	 * Checks for collisions between two line segments as defined by [a,b] and [p,q]. The resulting position of a collision is stored in {@link outVector}. If the lines do not collide, then {@link outVector} is not modified.
	 * 
	 * @param a         The start point of the first line segment.
	 * @param b         The end point of the first line segment.
	 * @param p         The start point of the second line segment.
	 * @param q         The end point of the second line segment.
	 * @param outVector The position of a collsion, if a collision occurs. Otherwise unmodified.
	 * @return true if a collision occured, otherwise false.
	 */
	public static final boolean intersectsLineLine(Vector2f a, Vector2f b, Vector2f p, Vector2f q, Vector2f outVector) {
		final var l0x = b.x - a.x;
		final var l0y = b.y - a.y;
		final var l1x = q.x - p.x;
		final var l1y = q.y - p.y;

		final var s = (-l0y * (a.x - p.x) + l0x * (a.y - p.y)) / (-l1x * l0y + l0x * l1y);
		final var t = (l1x * (a.y - p.y) - l1y * (a.x - p.x)) / (-l1x * l0y + l0x * l1y);

		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
			outVector.x = a.x + (t * l0x);
			outVector.y = a.y + (t * l0y);
			return true;
		}

		return false;
	}

	public static boolean pointIntersectsAA(float x, float y, float w, float h, float pointX, float pointY) {
		return ((((pointX < x + w) && (x < pointX)) && (pointY < y + h)) && (y < pointY));
	}

	public static boolean overlap(float x1, float w1, float x2, float w2) {
		final var min1 = x1 - (w1 * .5f);
		final var max1 = x1 + (w1 * .5f);
		final var min2 = x2 - (w2 * .5f);
		final var max2 = x2 + (w2 * .5f);
		return !((max1 < min2) || (min1 > max2));
	}

}
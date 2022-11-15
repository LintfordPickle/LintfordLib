package net.lintford.library.core.collisions;

import net.lintford.library.core.entity.CircleEntity;
import net.lintford.library.core.entity.RectangleEntity;
import net.lintford.library.core.maths.Vector2f;

public class CollisionExtensions {

	public static boolean intersects(CircleEntity c, float pointX, float pointY) {
		final float xx = c.x - pointX;
		final float yy = c.y - pointY;
		return (xx * xx + yy * yy) < (c.radius * c.radius);
	}

	/**
	 * Checks to see if the given point P lies within the rectangle defined by AB, BC
	 */
	public static boolean insersects(Vector2f a, Vector2f b, Vector2f c, Vector2f p) {
		final var ABx = b.x - a.x;
		final var ABy = b.y - a.y;

		final var BCx = c.x - b.x;
		final var BCy = c.y - b.y;

		final var magAB = Vector2f.dot(ABx, ABy, ABx, ABy); // square of magnitude of vector AB
		final var magBC = Vector2f.dot(BCx, BCy, BCx, BCy); // square of magnitude of vector BC

		float projX = Vector2f.dot(ABx, ABy, p.x - a.x, p.y - a.y);
		float projY = Vector2f.dot(BCx, BCy, p.x - b.x, p.y - b.y);

		return projX >= 0.f && projX <= magAB && projY >= 0.f && projY <= magBC;
	}

	/**
	 * Checks for an intersection between the given circle entity and rectangle entity.
	 */
	public static boolean intersects(CircleEntity circle, RectangleEntity rectangle) {
		final float circleDistanceX = Math.abs(circle.x - rectangle.x + rectangle.w() / 2);
		final float circleDistanceY = Math.abs(circle.y - rectangle.y + rectangle.h() / 2);

		final float lRectWidthHalf = rectangle.w() / 2;
		final float lRectHeightHalf = rectangle.h() / 2;

		if (circleDistanceX > (lRectWidthHalf + circle.radius))
			return false;

		if (circleDistanceY > (lRectHeightHalf + circle.radius))
			return false;

		if (circleDistanceX <= lRectWidthHalf)
			return true;

		if (circleDistanceY <= lRectHeightHalf)
			return true;

		final float cornerDistance_sq = (circleDistanceX - lRectWidthHalf) * (circleDistanceX - lRectWidthHalf) + (circleDistanceY - lRectHeightHalf) * (circleDistanceY - lRectHeightHalf);

		return (cornerDistance_sq <= circle.radius * circle.radius);
	}

	/** Checks if a point is within a given circle */
	public static boolean intersectsCirclePoint(CircleEntity circle, Vector2f point) {
		return intersectsCirclePoint(circle.x, circle.y, circle.radius, point.x, point.y);
	}

	/** Checks if a point is within a given circle */
	public static boolean intersectsCirclePoint(float circleX, float circleY, float circleRadius, float pointX, float pointY) {
		final float lDist = (float) (circleX - pointX) * (circleX - pointX) + (circleY - pointY) * (circleY - pointY);
		if (lDist < circleRadius * circleRadius) {
			return true;
		}

		return false;
	}

	/** A quick approximate distance check which doesn't rely on a square root */
	public static boolean fastIntersects(float point1X, float point1Y, float point2X, float point2Y, float objectSize) {
		if (Math.abs(point1X - point1Y) < objectSize && Math.abs(point1X - point1Y) < objectSize) {
			return true;
		}

		return false;
	}

	public static boolean doCirclesOverlap(float x1, float y1, float r1, float x2, float y2, float r2) {
		return Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) < (r1 + r2) * (r1 + r2);
	}

	public static boolean intersection(Vector2f a, Vector2f b, Vector2f p, Vector2f q, Vector2f outVector) {
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

	public static boolean checkHasArrived(float entityAPositionX, float entityAPositionY, float entityBPositionX, float entityBPositionY, float minDistance) {
		return Vector2f.distance(entityAPositionX, entityAPositionY, entityBPositionX, entityBPositionY) < minDistance;
	}
}
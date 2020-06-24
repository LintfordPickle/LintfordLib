package net.lintford.library.core.collisions;

import net.lintford.library.core.entity.CircleEntity;
import net.lintford.library.core.entity.RectangleEntity;
import net.lintford.library.core.maths.Vector2f;

public class CollisionExtensions {

	/** Checks for an intersection between the given circle entity and rectangle entity. */
	public static boolean intersects(CircleEntity pCircle, RectangleEntity pRect) {
		final float circleDistanceX = Math.abs(pCircle.worldPositionX - pRect.worldPositionX + pRect.w() / 2);
		final float circleDistanceY = Math.abs(pCircle.worldPositionY - pRect.worldPositionY + pRect.h() / 2);

		final float lRECT_WIDTH_H = pRect.w() / 2;
		final float lRECT_HEIGHT_H = pRect.h() / 2;

		if (circleDistanceX > (lRECT_WIDTH_H + pCircle.rad())) {
			return false;
		}
		if (circleDistanceY > (lRECT_HEIGHT_H + pCircle.rad())) {
			return false;
		}

		if (circleDistanceX <= lRECT_WIDTH_H) {
			return true;
		}
		if (circleDistanceY <= lRECT_HEIGHT_H) {
			return true;
		}

		final float cornerDistance_sq = (circleDistanceX - lRECT_WIDTH_H) * (circleDistanceX - lRECT_WIDTH_H) + (circleDistanceY - lRECT_HEIGHT_H) * (circleDistanceY - lRECT_HEIGHT_H);

		return (cornerDistance_sq <= pCircle.rad() * pCircle.rad());
	}

	/** Checks if a point is within a given circle */
	public static boolean intersectsCirclePoint(CircleEntity pCircle, Vector2f pPoint) {
		return intersectsCirclePoint(pCircle.worldPositionX, pCircle.worldPositionY, pCircle.radius, pPoint.x, pPoint.y);

	}

	/** Checks if a point is within a given circle */
	public static boolean intersectsCirclePoint(float pCircleX, float pCircleY, float pCircleRadius, float pPointX, float pPointY) {
		float lDist = (float) Math.sqrt((pCircleX - pPointX) * (pCircleX - pPointX) + (pCircleY - pPointY) * (pCircleY - pPointY));
		if (lDist < pCircleRadius) {
			return true;

		}

		return false;

	}

	/** A quick approximate distance check which doesn't rely on a square root */
	public static boolean fastIntersects(float pPoint1X, float pPoint1Y, float pPoint2X, float pPoint2Y, float pObjectSize) {
		if (Math.abs(pPoint1X - pPoint1Y) < pObjectSize && Math.abs(pPoint1X - pPoint1Y) < pObjectSize) {
			return true;

		}

		return false;

	}

	public static boolean intersection(Vector2f a, Vector2f b, Vector2f p, Vector2f q, Vector2f pOutVector) {
		float A1 = b.y - a.y;
		float B1 = a.x - b.x;
		float C1 = A1 * a.x + B1 * a.y;

		float A2 = q.y - p.y;
		float B2 = p.x - q.x;
		float C2 = A2 * p.x + B2 * p.y;

		float det = A1 * B2 - A2 * B1;
		float x = (B2 * C1 - B1 * C2) / det;
		float y = (A1 * C2 - A2 * C1) / det;

		pOutVector.x = x;
		pOutVector.y = y;

		if (x != x || y != y) {
			return false;
		}

		return true;

	}

}
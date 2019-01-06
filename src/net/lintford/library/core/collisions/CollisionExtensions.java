package net.lintford.library.core.collisions;

import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.data.entities.CircleEntity;
import net.lintford.library.data.entities.RectangleEntity;

public class CollisionExtensions {

	/** Checks for an intersection between the given circle entity and rectangle entity. */
	public static boolean intersects(CircleEntity pCircle, RectangleEntity pRect) {
		final float circleDistanceX = Math.abs(pCircle.x - pRect.x + pRect.w() / 2);
		final float circleDistanceY = Math.abs(pCircle.y - pRect.y + pRect.h() / 2);

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
		return intersectsCirclePoint(pCircle.x, pCircle.y, pCircle.radius, pPoint.x, pPoint.y);

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

//	public static boolean intersects(RectangleEntity pOther) {
//		// Poly
//		if (pOther instanceof PolyEntity) {
//			// TODO: Rectangle <-> Poly collision
//		}
//
//		// Rect
//		else if (pOther instanceof RectangleEntity) {
//			RectangleEntity otherRect = (RectangleEntity) pOther;
//			if (Math.abs(x - pOther.x) > width / 2 + otherRect.width / 2)
//				return false;
//			if (Math.abs(y - pOther.y) > height / 2 + otherRect.height / 2)
//				return false;
//			return true;
//		}
//
//		// Circle
//		else if (pOther instanceof CircleEntity) {
//			CircleEntity c = (CircleEntity) pOther;
//			float circleDistX = Math.abs(c.x - this.x);
//			float circleDistY = Math.abs(c.y - this.y);
//
//			if (circleDistX > (this.width / 2 + c.radius)) {
//				return false;
//			}
//			if (circleDistY > (this.height / 2 + c.radius)) {
//				return false;
//			}
//
//			if (circleDistX <= (this.width / 2)) {
//				return true;
//			}
//			if (circleDistY <= (this.height / 2)) {
//				return true;
//			}
//
//			float dist_sq = (circleDistX - this.width / 2) * (circleDistX - this.width / 2) + (circleDistY - this.height / 2) * (circleDistX - this.width / 2) * (circleDistX - this.width / 2)
//					+ (circleDistY - this.height / 2);
//
//			return (dist_sq <= (c.radius * c.radius));
//
//		}
//
//		// no collision
//		return false;
//	}

}
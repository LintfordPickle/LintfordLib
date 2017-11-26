package net.lintford.library.core.collisions;

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

	
	
}

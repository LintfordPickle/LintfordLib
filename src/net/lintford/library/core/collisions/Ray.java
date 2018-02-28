package net.lintford.library.core.collisions;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Vector2f;

public class Ray {

	// --------------------------------------
	// Constants
	// --------------------------------------
	public static final float EPSILON = 0.0001f;
	public static final int NO_INTERSECTION = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Vector2f mPosition;
	protected Vector2f mDirection;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Vector2f position() {
		return mPosition;
	}

	public void position(Vector2f pPosition) {
		mPosition = pPosition;
	}

	public void position(float pX, float pY) {
		mPosition.x = pX;
		mPosition.y = pY;
	}

	public Vector2f direction() {
		return mDirection;
	}

	public void direction(Vector2f pDirection) {
		mDirection = pDirection;
	}

	public void direction(float pX, float pY) {
		mDirection.x = pX;
		mDirection.y = pY;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Ray() {
		mPosition = new Vector2f();
		mDirection = new Vector2f();
	}

	public Ray(Vector2f pPosition, Vector2f pDirection) {
		this();
		mPosition.x = pPosition.x;
		mPosition.y = pPosition.y;

		mDirection.x = pDirection.x;
		mDirection.y = pDirection.y;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static float intersects(Rectangle pRect, Ray pRay) {

		float lMinValue = NO_INTERSECTION;
		float lMaxValue = NO_INTERSECTION;

		if (Math.abs(pRay.direction().x) < EPSILON) {
			if (pRay.position().x < pRect.left() || pRay.position().x > pRect.right()) {
				return NO_INTERSECTION;
			}
		}

		else {
			lMinValue = (pRect.left() - pRay.position().x) / pRay.direction().x;
			lMaxValue = (pRect.right() - pRay.position().x) / pRay.direction().x;
			if (lMinValue > lMaxValue) {
				float tempX = lMinValue;
				lMinValue = lMaxValue;
				lMaxValue = tempX;
			}

		}

		if (Math.abs(pRay.direction().y) < EPSILON) {
			if (pRay.position().y < pRect.top() || pRay.position().y > pRect.bottom()) {
				return NO_INTERSECTION;
			}
		} else {
			float lMinValueY = (pRect.top() - pRay.position().y) / pRay.direction().y;
			float lMaxValueY = (pRect.bottom() - pRay.position().y) / pRay.direction().y;
			if (lMinValueY > lMaxValueY) {
				float tempY = lMinValueY;
				lMinValueY = lMaxValueY;
				lMaxValueY = tempY;
			}

			if ((lMinValue != NO_INTERSECTION && lMinValue > lMaxValueY) || (lMaxValue != NO_INTERSECTION && lMinValueY > lMaxValue)) {
				return NO_INTERSECTION;
			}

			if (lMinValue == NO_INTERSECTION || lMinValueY > lMinValue)
				lMinValue = lMinValueY;
			if (lMaxValue == NO_INTERSECTION || lMaxValueY < lMaxValue)
				lMaxValue = lMaxValueY;
		}

		// having a positive tMin and a negative tMax means the ray is inside the box
		// we expect the intesection distance to be 0 in that case
		if ((lMinValue != NO_INTERSECTION && lMinValue < 0) && lMaxValue > 0)
			return NO_INTERSECTION;

		// a negative tMin means that the intersection point is behind the ray's origin
		// we discard these as not hitting the AABB
		if (lMinValue < 0)
			return NO_INTERSECTION;

		return lMinValue;

	}

}

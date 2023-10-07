package net.lintfordlib.core.maths;

import net.lintfordlib.core.geometry.Rectangle;

public class Ray {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_INTERSECTION = -1;
	public static final float EPSILON = 0.05f;

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

	public void position(Vector2f position) {
		mPosition = position;
	}

	public Vector2f direction() {
		return mDirection;
	}

	public void direction(Vector2f direction) {
		mDirection = direction;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Ray() {
		mPosition = new Vector2f();
		mDirection = new Vector2f();
	}

	public Ray(Vector2f position, Vector2f direction) {
		this();
		mPosition.x = position.x;
		mPosition.y = position.y;

		mDirection.x = direction.x;
		mDirection.y = direction.y;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static float intersects(Rectangle rectangle, Ray ray) {
		float lMinValue = NO_INTERSECTION;
		float lMaxValue = NO_INTERSECTION;

		if (Math.abs(ray.direction().x) < EPSILON) {
			if (ray.position().x < rectangle.left() || ray.position().x > rectangle.right()) {
				return NO_INTERSECTION;
			}
		}

		else {
			lMinValue = (rectangle.left() - ray.position().x) / ray.direction().x;
			lMaxValue = (rectangle.right() - ray.position().x) / ray.direction().x;
			if (lMinValue > lMaxValue) {
				float tempX = lMinValue;
				lMinValue = lMaxValue;
				lMaxValue = tempX;
			}
		}

		if (Math.abs(ray.direction().y) < EPSILON) {
			if (ray.position().y < rectangle.top() || ray.position().y > rectangle.bottom()) {
				return NO_INTERSECTION;
			}
		} else {
			float lMinValueY = (rectangle.top() - ray.position().y) / ray.direction().y;
			float lMaxValueY = (rectangle.bottom() - ray.position().y) / ray.direction().y;
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

package net.lintford.library.core.collisions;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Vector2f;

public class Ray2D extends Ray {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Vector2f mEndPoint;
	private float mRayLength;
	private boolean mHit;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float legth() {
		return mRayLength;
	}

	public void length(float length) {
		mRayLength = length;
	}

	public boolean hit() {
		return mHit;
	}

	public float angle() {
		return (float) Math.atan2(mEndPoint.x, -mEndPoint.y);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Ray2D(Vector2f position, Vector2f end) {
		super();

		mPosition.set(position);
		mEndPoint = end.cpy();
		Vector2f tempVector = new Vector2f();
		tempVector.x = mPosition.x - end.x;
		tempVector.y = mPosition.y - end.y;
		mDirection.set(tempVector.nor());

		mRayLength = 1.0f;
		mHit = false;
	}

	public Ray2D(Vector2f position, Vector2f direction, float length) {
		super();

		mPosition.set(position);
		mDirection.set(direction);
		mEndPoint = new Vector2f();
		mEndPoint.x = mPosition.x + (direction.x * length);
		mEndPoint.x = mPosition.y + (direction.y * length);

		mRayLength = length;
		mHit = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float intersects(Rectangle rectangle) {
		float lResult = Ray.intersects(rectangle, this);
		mHit = lResult != NO_INTERSECTION && lResult <= mRayLength;

		return lResult;
	}
}

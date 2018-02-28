package net.lintford.library.core.maths;

import net.lintford.library.core.geometry.Rectangle;

public class Ray2D extends Ray {

	// ===========================================================
	// Variables
	// ===========================================================

	private Vector2f mEndPoint;
	private float mRayLength;
	private boolean mHit;

	// ===========================================================
	// Properties
	// ===========================================================

	public float legth() {
		return mRayLength;
	}

	public void length(float pNewLength) {
		mRayLength = pNewLength;
	}

	public boolean hit() {
		return mHit;
	}

	// public float angle() {
	// return (float) Math.atan2(mEndPosition.x, -mEndPosition.y);
	// }

	// ===========================================================
	// Constructor
	// ===========================================================

	public Ray2D(Vector2f pPosition, Vector2f pEnd) {
		super();

		mPosition = pPosition.cpy();
		mEndPoint = pEnd.cpy();
		Vector2f tempVector = new Vector2f();
		tempVector.x = mPosition.x - pEnd.x;
		tempVector.y = mPosition.y - pEnd.y;
		mDirection = tempVector.nor();

		mRayLength = 1.0f;
		mHit = false;
	}

	public Ray2D(Vector2f pPosition, Vector2f pDirection, float pLength) {
		super();

		mPosition = pPosition.cpy();
		mDirection = pDirection.cpy();
		mEndPoint = new Vector2f();
		mEndPoint.x = mPosition.x + (pDirection.x * pLength);
		mEndPoint.x = mPosition.y + (pDirection.y * pLength);

		mRayLength = pLength;
		mHit = false;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public float intersects(Rectangle pRect) {
		float lResult = Ray.intersects(pRect, this);
		mHit = lResult != NO_INTERSECTION && lResult <= mRayLength;

		return lResult;
	}

}

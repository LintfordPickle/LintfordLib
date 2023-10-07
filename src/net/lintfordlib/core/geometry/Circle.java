package net.lintfordlib.core.geometry;

import java.io.Serializable;

public class Circle implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mCenterX;
	private float mCenterY;
	private float mRadius;

	private float mRotationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float centerX() {
		return mCenterX;
	}

	public float centerY() {
		return mCenterY;
	}

	public float radius() {
		return mRadius;
	}

	public void radius(float radius) {
		mRadius = radius;
	}

	public float rotation() {
		return mRotationInRadians;
	}

	public void rotateRel(float relativeRotationAmount) {
		mRotationInRadians += relativeRotationAmount;
	}

	public void rotateAbs(float absolutionRotationAmount) {
		mRotationInRadians = absolutionRotationAmount;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Circle() {
		this(0, 0, 10);

	}

	public Circle(float centerX, float centerY, float radius) {
		mCenterX = centerX;
		mCenterY = centerY;
		mRadius = radius;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean intersectsAA(float pointX, float pointY) {
		final float xx = mCenterX - pointX;
		final float yy = mCenterY - pointY;
		return (xx * xx + yy * yy) < (mRadius * mRadius);
	}

	/**
	 * Centers the center of the circle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setPosition(float centerX, float centerY) {
		set(centerX, centerY, mRadius);
	}

	public void setRadius(float radius) {
		set(mCenterX, mCenterY, radius);
	}

	public void set(float centerX, float centerY, float radius) {
		mCenterX = centerX;
		mCenterY = centerY;
		mRadius = radius;
	}

	public void set(Circle otherCicle) {
		mCenterX = otherCicle.mCenterX;
		mCenterY = otherCicle.mCenterY;

		mRadius = otherCicle.mRadius;
	}

	public void expand(float expandByAmount) {
		mRadius += expandByAmount;
	}
}

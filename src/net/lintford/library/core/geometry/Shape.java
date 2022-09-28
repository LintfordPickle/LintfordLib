package net.lintford.library.core.geometry;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.lintford.library.core.maths.Vector2f;

public abstract class Shape implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7275174603814416071L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "pivotX")
	protected float mPivotX;
	@SerializedName(value = "pivotY")
	protected float mPivotY;
	@SerializedName(value = "rotation")
	protected float mRotation;
	@SerializedName(value = "radius")
	protected float mRadius;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public abstract List<Vector2f> getVertices();

	public float radius() {
		return mRadius;
	}

	public void radius(float newRadius) {
		mRadius = newRadius;
	}

	public float rotation() {
		return mRotation;
	}

	public void rotation(float newRotation) {
		mRotation = newRotation;
	}

	public void setPivotPoint(float pX, float pY) {
		mPivotX = pX;
		mPivotY = pY;
	}

	public float pivotX() {
		return mPivotX;
	}

	public void pivotX(float pNewPivotX) {
		mPivotX = pNewPivotX;
	}

	public float pivotY() {
		return mPivotY;
	}

	public void pivotY(float pNewPivotY) {
		mPivotY = pNewPivotY;
	}

	public abstract float centerX();

	public abstract float centerY();

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Shape() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract Vector2f project(Vector2f axis, Vector2f toFill);

	public abstract Vector2f[] getAxes();

	public abstract void rotateRel(float rotateAmt);

	public abstract void rotateAbs(float rotateAmt);

}
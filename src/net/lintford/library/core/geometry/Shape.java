package net.lintford.library.core.geometry;

import java.io.Serializable;
import java.util.List;

import net.lintford.library.core.maths.Vector2f;

public abstract class Shape implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7275174603814416071L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float py;
	public float px;
	public float rot;
	public float rad;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public abstract List<Vector2f> getVertices();

	/**
	 * Returns the maximum radius of the shape
	 */
	public float radius() {
		return rad;
	}

	public float rotation() {
		return rot;
	}

	public float pivotX() {
		return px;
	}

	public void pivotX(float pNewValue) {
		px = pNewValue;
	}

	public float pivotY() {
		return py;
	}

	public void pivotY(float pNewValue) {
		py = pNewValue;
	}

	public void setPivotPoint(float pX, float pY) {
		px = pX;
		py = pY;
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

	public abstract Vector2f project(Vector2f pAxis, Vector2f pToFill);

	public abstract Vector2f[] getAxes();

	public abstract void rotateRel(float pRotateAmt);

	public abstract void rotateAbs(float pRotateAmt);

}
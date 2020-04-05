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

	public float pivotY;
	public float pivotX;
	public float rotation;
	public float radius;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public abstract List<Vector2f> getVertices();

	public void setPivotPoint(float pX, float pY) {
		pivotX = pX;
		pivotY = pY;
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
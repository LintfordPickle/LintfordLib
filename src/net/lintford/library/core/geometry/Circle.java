package net.lintford.library.core.geometry;

import net.lintford.library.core.maths.Vector2f;

// SAT Ref: http://www.dyn4j.org/2010/01/sat/
public class Circle extends Shape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	public static final int NUM_VERTICES = 1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Vector2f[] mVertices;;

	private float mRadius;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public float radius() {
		return mRadius;
	}

	public Vector2f[] getVertices() {
		return mVertices;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Circle() {
		this(0, 0, 10);

	}

	public Circle(float pCenterX, float pCenterY, float pRadius) {
		centerX = pCenterX;
		centerY = pCenterY;
		mRadius = pRadius;

		mVertices = new Vector2f[NUM_VERTICES];
		mVertices[0] = new Vector2f(centerX, centerY);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Vector2f[] getAxes() {
		// Circle has infinate axis
		return null;

	}

	public Vector2f getNearestVertex(Shape pOtherShape, Vector2f pToFill) {
		float min = Float.MAX_VALUE;

		final int NUM_VERTICES = pOtherShape.getVertices().length;
		for (int i = 0; i < NUM_VERTICES; i++) {
			float distTo = Vector2f.distance(centerX, centerY, pOtherShape.getVertices()[i].x, pOtherShape.getVertices()[i].y);

			if (distTo < min) {
				pToFill.x = pOtherShape.getVertices()[i].x - centerX;
				pToFill.y = pOtherShape.getVertices()[i].y - centerY;

				min = distTo;
			}

		}

		pToFill.nor();

		return pToFill;

	}

	@Override
	public Vector2f project(Vector2f pAxis, Vector2f pToFill) {

		float c = Vector2f.dot(centerX, centerY, pAxis.x, pAxis.y);

		pToFill.x = c - mRadius;
		pToFill.y = c + mRadius;

		return pToFill;
	}

	/**
	 * Returns true if this circle's radius is zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return (this.mRadius == 0);
	}

	/**
	 * Centers the center of the circle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setPosition(float pX, float pY) {
		set(pX, pY, mRadius);

	}

	public void setRadius(float pRadius) {
		set(centerX, centerY, pRadius);
	}

	public void set(float pX, float pY, float pRadius) {
		centerX = pX;
		centerY = pY;
		mRadius = pRadius;

	}

	public void set(Circle pRect) {
		centerX = pRect.centerX;
		centerY = pRect.centerY;
		mRadius = pRect.mRadius;

	}

	public void expand(float pAmt) {
		mRadius += pAmt;

	}

	@Override
	public void rotateRel(float pRotAmt) {
		rot += pRotAmt;

	}

	@Override
	public void rotateAbs(float pRotAmt) {
		rot = pRotAmt;

	}

}

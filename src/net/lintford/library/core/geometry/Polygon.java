package net.lintford.library.core.geometry;

import net.lintford.library.core.maths.Vector2f;

// SAT Ref: http://www.dyn4j.org/2010/01/sat/
public class Polygon extends Shape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	public static final int MAX_NUM_VERTICES = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Vector2f[] mVertices;
	private int mVertexCounter = 0;
	public float x;
	public float y;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Vertices are defined locally around the center point (x,y)
	 */
	@Override
	public Vector2f[] getVertices() {
		return mVertices;
	}

	public float centerX() {
		return x;
	}

	public float centerY() {
		return y;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Polygon() {
		this(0, 0);

	}

	public Polygon(float pCenterX, float pCenterY) {
		x = pCenterX;
		y = pCenterY;

		mVertices = new Vector2f[MAX_NUM_VERTICES];

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * This axis-aligned Rectangle contains that rectangle. n.b. If you have applied a rotation to this Rectangle, then it is no longer axis-aligned
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given rectangle. False otherwise.
	 */
	public boolean intersects(Polygon pOtherPoly) {
		return false;
	}

	public boolean intersects(Rectangle pOtherRect) {
		return false;
	}

	/**
	 * This axis-aligned Rectangle contains that point. n.b. If you have applied a rotation to this Rectangle, then it is no longer axis-aligned
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersects(Vector2f pOtherPoint) {
		return intersects(pOtherPoint.x, pOtherPoint.y);
	}

	/**
	 * This Rectangle contains that point.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersects(float pX, float pY) {
		return false;
	}

	@Override
	public Vector2f[] getAxes() {
		final int AXES_LENGTH = 2;
		Vector2f[] axes = new Vector2f[MAX_NUM_VERTICES]; // Rectangle only has two axis to be tested against

		// NOTE: Pay attention to winding order here (of the axes won't be correct)?

		return axes;
	}

	@Override
	public Vector2f project(Vector2f pAxis, Vector2f pToFill) {
		if(pAxis == null) return pToFill;
		
		float min = Vector2f.dot(mVertices[0].x, mVertices[0].y, pAxis.x, pAxis.y);
		float max = min;
		for (int i = 1; i < mVertices.length; i++) {
			if(mVertices[i] == null) continue;
			
			float p = Vector2f.dot(mVertices[i].x, mVertices[i].y, pAxis.x, pAxis.y);
			if (p < min) {
				min = p;

			} else if (p > max) {
				max = p;

			}

		}

		if (pToFill == null)
			pToFill = new Vector2f();

		pToFill.x = min;
		pToFill.y = max;

		return pToFill;

	}

	public boolean overlaps(Vector2f p1, Vector2f p2) {
		return !(p1.x > p2.y || p2.x > p1.y);

	}

	/**
	 * Returns true if this polygon has no vertices assigned.
	 * 
	 * @Returs True if this polygon has no vertices.
	 */
	public boolean isEmpty() {
		return mVertexCounter == 0;
	}

	public void setCenterPosition(float pNewCenterX, float pNewCenterY) {
		x = pNewCenterX;
		y = pNewCenterY;

	}

	@Override
	public void rotateRel(float pRotAmt) {
		rot += pRotAmt;

	}

	@Override
	public void rotateAbs(float pRotAmt) {
		rot = pRotAmt;

	}

	public void addVertex(Vector2f pNewVertex) {
		if (mVertexCounter >= MAX_NUM_VERTICES)
			return; // Cannot add more vertices

		if (pNewVertex == null)
			return;

		mVertices[mVertexCounter++] = pNewVertex;

	}

}

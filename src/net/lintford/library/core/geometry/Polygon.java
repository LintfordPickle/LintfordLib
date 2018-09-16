package net.lintford.library.core.geometry;

import net.lintford.library.core.maths.Vector2f;

// SAT Ref: http://www.dyn4j.org/2010/01/sat/
public class Polygon extends Shape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Vector2f[] mVertices;
	protected Vector2f[] mAxes;
	protected boolean mDirty;
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
		if (mDirty || mAxes == null) {
			if (mAxes == null || mAxes.length != mVertices.length)
				mAxes = new Vector2f[mVertices.length];

			// FIXME: Garbage created
			for (int i = 0; i < mVertices.length; i++) {
				int nextIndex = i < mVertices.length - 1 ? i + 1 : 0;

				if (mVertices[i] == null || mVertices[nextIndex] == null)
					continue;

				if (mAxes[i] == null)
					mAxes[i] = new Vector2f();

				// This could cause problems later
				mAxes[i].x = (mVertices[i].y - mVertices[nextIndex].y);
				mAxes[i].y = -(mVertices[i].x - mVertices[nextIndex].x);
				mAxes[i].nor();

			}

			mDirty = false;

		}

		return mAxes;

	}

	@Override
	public Vector2f project(Vector2f pAxis, Vector2f pToFill) {
		if (pAxis == null)
			return pToFill;

		float min = Vector2f.dot(mVertices[0].x, mVertices[0].y, pAxis.x, pAxis.y);
		float max = min;
		for (int i = 1; i < mVertices.length; i++) {
			if (mVertices[i] == null)
				continue;

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
		return mVertices == null || mVertices.length == 0;
	}

	public void setCenterPosition(float pNewCenterX, float pNewCenterY) {
		x = pNewCenterX;
		y = pNewCenterY;

	}

	@Override
	public void rotateRel(float pRotAmt) {
		mDirty = true;
		rot += pRotAmt;

	}

	@Override
	public void rotateAbs(float pRotAmt) {
		mDirty = true;
		rot = pRotAmt;

	}

	public void addVertices(Vector2f... pNewVertices) {
		if (pNewVertices == null)
			return;

		mVertices = pNewVertices;
		mDirty = true;

	}

}

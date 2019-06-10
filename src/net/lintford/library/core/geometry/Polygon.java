package net.lintford.library.core.geometry;

import java.util.ArrayList;
import java.util.List;

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

	protected List<Vector2f> mVertices;
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
		// TODO: Garbage
		Vector2f[] ll = new Vector2f[mVertices.size()];
		mVertices.toArray(ll);

		return ll;
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
		mVertices = new ArrayList<>();

		x = pCenterX;
		y = pCenterY;

	}

	public Polygon(Polygon pOther) {
		mVertices = new ArrayList<>();

		Vector2f[] lOtherVerts = pOther.getVertices();
		final int lOtherVertCount = lOtherVerts.length;

		for (int i = 0; i < lOtherVertCount; i++) {
			mVertices.add(new Vector2f(lOtherVerts[i]));
		}

		Vector2f[] lOtherAxes = pOther.getAxes();
		final int lOtherAxesCount = lOtherAxes.length;
		mAxes = new Vector2f[lOtherAxesCount];
		for (int i = 0; i < lOtherAxesCount; i++) {
			mAxes[i] = new Vector2f(lOtherAxes[i]);
		}

		mDirty = pOther.mDirty;
		x = pOther.x;
		y = pOther.y;

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
			if (mAxes == null || mAxes.length != mVertices.size())
				mAxes = new Vector2f[mVertices.size()];

			// FIXME: Garbage created
			for (int i = 0; i < mVertices.size(); i++) {
				int nextIndex = i < mVertices.size() - 1 ? i + 1 : 0;

				if (mVertices.get(i) == null || mVertices.get(nextIndex) == null)
					continue;

				if (mAxes[i] == null)
					mAxes[i] = new Vector2f();

				// This could cause problems later
				mAxes[i].x = (mVertices.get(i).y - mVertices.get(nextIndex).y);
				mAxes[i].y = -(mVertices.get(i).x - mVertices.get(nextIndex).x);
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

		float min = Vector2f.dot(mVertices.get(0).x, mVertices.get(0).y, pAxis.x, pAxis.y);
		float max = min;
		for (int i = 1; i < mVertices.size(); i++) {
			if (mVertices.get(i) == null)
				continue;

			float p = Vector2f.dot(mVertices.get(i).x, mVertices.get(i).y, pAxis.x, pAxis.y);
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
		return mVertices == null || mVertices.size() == 0;
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

		mVertices.clear();

		final int lLength = pNewVertices.length;
		for (int i = 0; i < lLength; i++) {
			mVertices.add(pNewVertices[i]);
		}

		mDirty = true;

	}

	public void addVertex(Vector2f pNewVertex) {
		mVertices.add(pNewVertex);

	}

	// Sutherland-Hodgmann Al
	public static Shape getIntersection(Polygon pClipper, Polygon pSubject, Polygon pResult) {
		pResult = new Polygon(pSubject);

		int len = pClipper.getVertices().length;
		for (int i = 0; i < len; i++) {

			int len2 = pResult.getVertices().length;
			Polygon lInputPolygon = pResult;
			pResult = new Polygon();

			Vector2f A = pClipper.getVertices()[(i + len - 1) % len];
			Vector2f B = pClipper.getVertices()[i];

			for (int j = 0; j < len2; j++) {

				Vector2f P = lInputPolygon.getVertices()[(j + len2 - 1) % len2];
				Vector2f Q = lInputPolygon.getVertices()[j];

				if (isInside(A, B, Q)) {
					if (!isInside(A, B, P))
						pResult.addVertex(intersection(A, B, P, Q));
					pResult.addVertex(Q);
				} else if (isInside(A, B, P))
					pResult.addVertex(intersection(A, B, P, Q));
			}
		}

		return pResult;
	}

	private static boolean isInside(Vector2f a, Vector2f b, Vector2f c) {
		return (a.x - c.x) * (b.y - c.y) > (a.y - c.y) * (b.x - c.x);

	}

	private static Vector2f intersection(Vector2f a, Vector2f b, Vector2f p, Vector2f q) {
		float A1 = b.y - a.y;
		float B1 = a.x - b.x;
		float C1 = A1 * a.x + B1 * a.y;

		float A2 = q.y - p.y;
		float B2 = p.x - q.x;
		float C2 = A2 * p.x + B2 * p.y;

		float det = A1 * B2 - A2 * B1;
		float x = (B2 * C1 - B1 * C2) / det;
		float y = (A1 * C2 - A2 * C1) / det;

		return new Vector2f(x, y);

	}

}

package net.lintford.library.core.geometry;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.maths.Vector2f;

// SAT Ref: http://www.dyn4j.org/2010/01/sat/
public class Rectangle extends Shape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	public static final int NUM_VERTICES = 4;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<Vector2f> mVertices;
	protected boolean mIsAABB; // Blocks rotations
	public float x;
	public float y;
	public float w;
	public float h;
	protected float sx;
	protected float sy;
	protected boolean mFlipH;
	protected boolean mFlipV;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAABB() {
		return mIsAABB;
	}

	public void isAABB(boolean pNewValue) {
		mIsAABB = pNewValue;
	}

	public float left() {
		return x;
	}

	public float right() {
		return x + w;
	}

	public float top() {
		return y;
	}

	public float bottom() {
		return y + h;
	}

	public float width() {
		return w;
	}

	public float height() {
		return h;
	}

	public float scaleX() {
		return sx;
	}

	public void scaleX(float pNewValue) {
		sx = pNewValue;
	}

	public float scaleY() {
		return sy;
	}

	public void scaleY(float pNewValue) {
		sy = pNewValue;

	}

	public void setScale(float pX, float pY) {
		sx = pX;
		sy = pY;

	}

	public List<Vector2f> getVertices() {
		// FIXME: Add in a isDirty check here
		updateVertices();

		return mVertices;
	}

	public boolean flipH() {
		return mFlipH;
	}

	public void flipH(boolean pNewValue) {
		mFlipH = pNewValue;
	}

	public boolean flipV() {
		return mFlipV;
	}

	public void flipV(boolean pNewValue) {
		mFlipV = pNewValue;
	}

	public float centerX() {
		return x + w / 2;
	}

	public float centerY() {
		return y + h / 2;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Rectangle() {
		this(0, 0, 0, 0);

	}

	public Rectangle(Rectangle pRectangle) {
		this(pRectangle.x, pRectangle.y, pRectangle.width(), pRectangle.height());

	}

	public Rectangle(float pX, float pY, float pWidth, float pHeight) {
		x = pX;
		y = pY;
		w = pWidth;
		h = pHeight;

		mVertices = new ArrayList<>(NUM_VERTICES);
		mVertices.add(new Vector2f(x, y));
		mVertices.add(new Vector2f(x + w, y));
		mVertices.add(new Vector2f(x, y + h));
		mVertices.add(new Vector2f(x + w, y + h));

		sx = 1f;
		sy = 1f;

		mIsAABB = true;

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
	public boolean intersectsAA(Rectangle pOtherRect) {
		return ((((pOtherRect.left() < right()) && (left() < pOtherRect.right())) && (pOtherRect.top() < bottom())) && (top() < pOtherRect.bottom()));
	}

	/**
	 * This axis-aligned Rectangle contains that point. n.b. If you have applied a rotation to this Rectangle, then it is no longer axis-aligned
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersectsAA(Vector2f pPoint) {
		return ((((left() <= pPoint.x) && (pPoint.x < right())) && (top() <= pPoint.y)) && (pPoint.y < bottom()));
	}

	/**
	 * This Rectangle contains that point.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersectsAA(float pX, float pY) {
		return pX >= left() && pX <= right() && pY >= top() && pY <= bottom();
	}

	@Override
	public Vector2f[] getAxes() {
		updateVertices();

		final int AXES_LENGTH = 2;
		Vector2f[] axes = new Vector2f[AXES_LENGTH]; // Rectangle only has two axis to be tested against

		// FIXME: Garbage created
		// The order of the vertices used here depends on the winding-order
		axes[0] = new Vector2f((mVertices.get(0).y - mVertices.get(1).y), -(mVertices.get(0).x - mVertices.get(1).x)).nor();
		axes[1] = new Vector2f((mVertices.get(0).y - mVertices.get(2).y), -(mVertices.get(0).x - mVertices.get(2).x)).nor();

		return axes;
	}

	@Override
	public Vector2f project(Vector2f pAxis, Vector2f pToFill) {
		if (pAxis == null)
			return pToFill;
		float min = Vector2f.dot(mVertices.get(0).x, mVertices.get(0).y, pAxis.x, pAxis.y);
		float max = min;
		final int lVertCount = mVertices.size();
		for (int i = 1; i < lVertCount; i++) {
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
	 * Returns true if this rectangle's dimensions and position are zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return (this.w == 0 && this.h == 0);
	}

	/**
	 * Centers the center of the rectangle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setPosition(float pX, float pY) {
		setCenter(pX, pY, w, h);

	}

	public void setCenterPosition(float pNewCenterX, float pNewCenterY) {
		x = pNewCenterX - w / 2;
		y = pNewCenterY - h / 2;

	}

	public void setWidth(float pWidth) {
		w = pWidth;

	}

	public void setHeight(float pHeight) {
		h = pHeight;

	}

	public void setDimensions(float pWidth, float pHeight) {
		w = pWidth;
		h = pHeight;

	}

	public void set(Rectangle pRect) {
		x = pRect.x;
		y = pRect.y;
		w = pRect.w;
		h = pRect.h;

	}

	public void set(float pX, float pY, float pWidth, float pHeight) {
		x = pX;
		y = pY;
		w = pWidth;
		h = pHeight;

	}

	public void setCenter(float pCenterX, float pCenterY, float pWidth, float pHeight) {
		x = pCenterX - pWidth / 2;
		y = pCenterY - pHeight / 2;
		w = pWidth;
		h = pHeight;

	}

	public void expand(float pAmt) {
		x -= pAmt * 0.5f;
		y -= pAmt * 0.5f;
		w += pAmt * 2;
		h += pAmt * 2;
	}

	@Override
	public void rotateRel(float pRotAmt) {
		rot += pRotAmt;

	}

	@Override
	public void rotateAbs(float pRotAmt) {
		rot = pRotAmt;

	}

	protected void updateVertices() {

		final float lWidth = mFlipH ? -w : w;
		final float lHeight = mFlipV ? -h : h;

		final float lPX = mFlipH ? -px : px;
		final float lPY = mFlipV ? -py : py;

		// Get local space vertex positions
		mVertices.get(0).set(-lWidth / 2, -lHeight / 2);
		mVertices.get(1).set(lWidth / 2, -lHeight / 2);
		mVertices.get(2).set(-lWidth / 2, lHeight / 2);
		mVertices.get(3).set(lWidth / 2, lHeight / 2);

		// rot = 0;
		float sin = (float) (Math.sin(rot));
		float cos = (float) (Math.cos(rot));

		// iterate over the vertices, rotating them by the given amt around the origin point of the rectangle.
		for (int i = 0; i < NUM_VERTICES; i++) {
			// Scale the vertices out from local center (before applying world translation)
			float dx = -lPX + mVertices.get(i).x * sx;
			float dy = -lPY + mVertices.get(i).y * sy;

			mVertices.get(i).set(centerX() + (dx * cos - (dy * 1f) * sin) * sx, centerY() + (dx * sin + (dy * 1f) * cos) * sy);

		}

		mIsAABB = rot == 0;

	}

}

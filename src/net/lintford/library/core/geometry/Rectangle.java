package net.lintford.library.core.geometry;

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

	protected Vector2f[] mVertices;
	protected boolean mIsDirty;
	protected boolean mIsAABB; // Blocks rotations
	protected float mWidth;
	protected float mHeight;
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
		return centerX - mWidth / 2f;
	}

	public float right() {
		return centerX + mWidth / 2f;
	}

	public float top() {
		return centerY - mHeight / 2f;
	}

	public float bottom() {
		return centerY + mHeight / 2f;
	}

	public float width() {
		return mWidth;
	}

	public float height() {
		return mHeight;
	}

	public float scaleX() {
		return sx;
	}

	public void scaleX(float pNewValue) {
		if (sx != pNewValue) {
			setDirty();

		}

		sx = pNewValue;
	}

	public float scaleY() {
		return sy;
	}

	public void scaleY(float pNewValue) {
		if (sy != pNewValue) {
			setDirty();

		}

		sy = pNewValue;

	}

	public void setScale(float pX, float pY) {
		setDirty();

		sx = pX;
		sy = pY;

	}

	public Vector2f[] getVertices() {
		if (mIsDirty) {
			updateVertices();
		}

		return mVertices;
	}

	public void setDirty() {
		mIsDirty = true;
	}

	public boolean flipH() {
		return mFlipH;
	}

	public void flipH(boolean pNewValue) {
		if (pNewValue != mFlipH)
			mIsDirty = true;
		mFlipH = pNewValue;
	}

	public boolean flipV() {
		return mFlipV;
	}

	public void flipV(boolean pNewValue) {
		if (pNewValue != mFlipV)
			mIsDirty = true;
		mFlipV = pNewValue;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Rectangle() {
		this(0, 0, 0, 0);

	}

	public Rectangle(float pCenterX, float pCenterY, float pWidth, float pHeight) {
		centerX = pCenterX;
		centerY = pCenterY;
		mWidth = pWidth;
		mHeight = pHeight;

		mVertices = new Vector2f[NUM_VERTICES];
		mVertices[0] = new Vector2f(centerX - mWidth / 2, centerY - mHeight / 2);
		mVertices[1] = new Vector2f(centerX + mWidth / 2, centerY - mHeight / 2);
		mVertices[2] = new Vector2f(centerX - mWidth / 2, centerY + mHeight / 2);
		mVertices[3] = new Vector2f(centerX + mWidth / 2, centerY + mHeight / 2);

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
		if (mIsDirty)
			updateVertices();

		final int AXES_LENGTH = 2;
		Vector2f[] axes = new Vector2f[AXES_LENGTH]; // Rectangle only has two axis to be tested against

		// FIXME: Garbage created
		// The order of the vertices used here depends on the winding-order
		axes[0] = new Vector2f((mVertices[0].y - mVertices[1].y), -(mVertices[0].x - mVertices[1].x)).nor();
		axes[1] = new Vector2f((mVertices[0].y - mVertices[2].y), -(mVertices[0].x - mVertices[2].x)).nor();

		return axes;
	}

	@Override
	public Vector2f project(Vector2f pAxis, Vector2f pToFill) {
		float min = Vector2f.dot(mVertices[0].x, mVertices[0].y, pAxis.x, pAxis.y);
		float max = min;
		for (int i = 1; i < mVertices.length; i++) {
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
	 * Returns true if this rectangle's dimensions and position are zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return (this.mWidth == 0 && this.mHeight == 0);
	}

	/**
	 * Centers the center of the rectangle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setPosition(float pX, float pY) {
		set(pX, pY, mWidth, mHeight);

	}

	public void setWidth(float pWidth) {
		set(centerX, centerY, pWidth, mHeight);
	}

	public void setHeight(float pHeight) {
		set(centerX, centerY, mWidth, pHeight);

	}

	public void setDimensions(float pWidth, float pHeight) {
		set(centerX, centerY, pWidth, pHeight);

	}

	public void set(Rectangle pRect) {
		set(pRect.centerX, pRect.centerY, pRect.mWidth, pRect.mHeight);

	}

	public void set(float pCenterX, float pCenterY, float pWidth, float pHeight) {
		centerX = pCenterX;
		centerY = pCenterY;
		mWidth = pWidth;
		mHeight = pHeight;

		mIsDirty = true;
	}

	public void expand(float pAmt) {
		mWidth += pAmt * 2;
		mHeight += pAmt * 2;
		mIsDirty = true;
	}

	@Override
	public void rotateRel(float pRotAmt) {
		rot += pRotAmt;
		mIsDirty = true;

	}

	@Override
	public void rotateAbs(float pRotAmt) {
		rot = pRotAmt;
		mIsDirty = true;

	}

	protected void updateVertices() {

		final float lWidth = mFlipH ? -mWidth : mWidth;
		final float lHeight = mFlipV ? -mHeight : mHeight;

		final float lPX = mFlipH ? -px : px;
		final float lPY = mFlipV ? -py : py;

		// Get local space vertex positions
		mVertices[0].x = -lWidth / 2;
		mVertices[0].y = -lHeight / 2;

		mVertices[1].x = lWidth / 2;
		mVertices[1].y = -lHeight / 2;

		mVertices[2].x = -lWidth / 2;
		mVertices[2].y = lHeight / 2;

		mVertices[3].x = lWidth / 2;
		mVertices[3].y = lHeight / 2;

		float sin = (float) (Math.sin(rot));
		float cos = (float) (Math.cos(rot));

		// iterate over the vertices, rotating them by the given amt around the origin point of the rectangle.
		for (int i = 0; i < NUM_VERTICES; i++) {
			// Scale the vertices out from local center (before applying world translation)
			float dx = -lPX + mVertices[i].x * sx;
			float dy = -lPY + mVertices[i].y * sy;

			mVertices[i].x = centerX + (dx * cos - (dy * 1f) * sin) * sx;
			mVertices[i].y = centerY + (dx * sin + (dy * 1f) * cos) * sy;

		}

		mIsAABB = rot == 0;

		mIsDirty = false;
	}

}

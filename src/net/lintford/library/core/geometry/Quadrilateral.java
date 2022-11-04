package net.lintford.library.core.geometry;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.lintford.library.core.maths.Vector2f;

public class Quadrilateral extends Shape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	public static final int NUM_VERTICES = 4;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected boolean mAreVerticesDirty;
	protected List<Vector2f> mVertices;

	@SerializedName(value = "x")
	protected float mX;
	@SerializedName(value = "y")
	protected float mY;
	@SerializedName(value = "w")
	protected float mW;
	@SerializedName(value = "h")
	protected float mH;
	@SerializedName(value = "scaleX")
	protected float mScaleX;
	@SerializedName(value = "scaleY")
	protected float mScaleY;

	protected boolean mFlipHorizontal;
	protected boolean mFlipVertical;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float x() {
		return mX;
	}

	public void x(float pX) {
		mAreVerticesDirty = mAreVerticesDirty || mX != pX;
		mX = pX;
	}

	public float y() {
		return mY;
	}

	public void y(float pY) {
		mAreVerticesDirty = mAreVerticesDirty || mY != pY;
		mY = pY;
	}

	public float left() {
		return mX;
	}

	public float right() {
		return mX + mW;
	}

	public float top() {
		return mY;
	}

	public float bottom() {
		return mY + mH;
	}

	public float width() {
		return mW;
	}

	public void width(float width) {
		mAreVerticesDirty = mAreVerticesDirty || width != mW;
		mW = width;
	}

	public float height() {
		return mH;
	}

	public void height(float height) {
		mAreVerticesDirty = mAreVerticesDirty || height != mH;
		mH = height;
	}

	public boolean flipHorizontal() {
		return mFlipHorizontal;
	}

	public void flipHorizontal(boolean flipHorizontal) {
		mAreVerticesDirty = mAreVerticesDirty || flipHorizontal != mFlipHorizontal;
		mFlipHorizontal = flipHorizontal;
	}

	public boolean flipVertical() {
		return mFlipVertical;
	}

	public void flipVertical(boolean flipVertical) {
		mAreVerticesDirty = mAreVerticesDirty || flipVertical != mFlipVertical;
		mFlipVertical = flipVertical;
	}

	public float scaleX() {
		return mScaleX;
	}

	public void scaleX(float scaleX) {
		mAreVerticesDirty = mAreVerticesDirty || scaleX != mScaleX;
		mScaleX = scaleX;
	}

	public float scaleY() {
		return mScaleY;
	}

	public void scaleY(float scaleY) {
		mAreVerticesDirty = mAreVerticesDirty || scaleY != mScaleY;
		mScaleY = scaleY;
	}

	public void setScale(float scaleX, float scaleY) {
		mAreVerticesDirty = mAreVerticesDirty || scaleX != mScaleX || scaleY != mScaleY;
		mScaleX = scaleX;
		mScaleY = scaleY;
	}

	public List<Vector2f> getVertices() {
		if (mAreVerticesDirty) {
			updateVertices();
		}

		return mVertices;
	}

	public float centerX() {
		return mX + mW / 2;
	}

	public float centerY() {
		return mY + mH / 2;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Quadrilateral() {
		this(0, 0, 0, 0);
	}

	public Quadrilateral(Quadrilateral rectangle) {
		this(rectangle.mX, rectangle.mY, rectangle.width(), rectangle.height());
	}

	public Quadrilateral(float x, float y, float width, float height) {
		mX = x;
		mY = y;
		mW = width;
		mH = height;

		mVertices = new ArrayList<>(NUM_VERTICES);
		mVertices.add(new Vector2f(mX, mY));
		mVertices.add(new Vector2f(mX + mW, mY));
		mVertices.add(new Vector2f(mX, mY + mH));
		mVertices.add(new Vector2f(mX + mW, mY + mH));

		mScaleX = 1f;
		mScaleY = 1f;

		mAreVerticesDirty = true;
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
	public boolean intersectsAA(Quadrilateral otherRectangle) {
		return ((((otherRectangle.left() < right()) && (left() < otherRectangle.right())) && (otherRectangle.top() < bottom())) && (top() < otherRectangle.bottom()));
	}

	/**
	 * This axis-aligned Rectangle contains that point. n.b. If you have applied a rotation to this Rectangle, then it is no longer axis-aligned
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersectsAA(Vector2f otherPoint) {
		return ((((left() <= otherPoint.x) && (otherPoint.x < right())) && (top() <= otherPoint.y)) && (otherPoint.y < bottom()));
	}

	/**
	 * This Rectangle contains that point.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersectsAA(float pointX, float pointY) {
		return pointX >= left() && pointX <= right() && pointY >= top() && pointY <= bottom();
	}

	@Override
	public Vector2f[] getAxes() {
		updateVertices();

		// FIXME: Garbage
		final int AXES_LENGTH = 2;
		Vector2f[] axes = new Vector2f[AXES_LENGTH]; // Rectangle only has two axis to be tested against

		// FIXME: Garbage
		// The order of the vertices used here depends on the winding-order
		axes[0] = new Vector2f((mVertices.get(0).y - mVertices.get(1).y), -(mVertices.get(0).x - mVertices.get(1).x)).nor();
		axes[1] = new Vector2f((mVertices.get(0).y - mVertices.get(2).y), -(mVertices.get(0).x - mVertices.get(2).x)).nor();

		return axes;
	}

	@Override
	public Vector2f project(Vector2f axis, Vector2f toFill) {
		if (axis == null)
			return toFill;

		float min = Vector2f.dot(mVertices.get(0).x, mVertices.get(0).y, axis.x, axis.y);
		float max = min;
		final int lVertCount = mVertices.size();
		for (int i = 1; i < lVertCount; i++) {
			float p = Vector2f.dot(mVertices.get(i).x, mVertices.get(i).y, axis.x, axis.y);
			if (p < min) {
				min = p;
			} else if (p > max) {
				max = p;
			}
		}

		if (toFill == null)
			toFill = new Vector2f();

		toFill.x = min;
		toFill.y = max;

		return toFill;
	}

	public boolean overlaps(Vector2f point1, Vector2f point2) {
		return !(point1.x > point2.y || point2.x > point1.y);
	}

	/**
	 * Returns true if this rectangle's dimensions and position are zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return (this.mW == 0 && this.mH == 0);
	}

	/**
	 * Centers the center of the rectangle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setPosition(float x, float y) {
		mAreVerticesDirty = mAreVerticesDirty || mX != x || mY != y;
		mX = x;
		mY = y;
	}

	public void setCenterPosition(float newCenterX, float newCenterY) {
		mAreVerticesDirty = mAreVerticesDirty || newCenterX != mX || newCenterY != mY;
		mX = newCenterX - mW / 2;
		mY = newCenterY - mH / 2;
	}

	public void setDimensions(float width, float height) {
		mW = width;
		mH = height;
	}

	public void set(Quadrilateral rectangle) {
		mAreVerticesDirty = mAreVerticesDirty || rectangle.mX != mX || rectangle.mY != mY || rectangle.mW != mW || rectangle.mH != mH;
		mX = rectangle.mX;
		mY = rectangle.mY;
		mW = rectangle.mW;
		mH = rectangle.mH;
	}

	public void set(float x, float y, float width, float height) {
		mAreVerticesDirty = mAreVerticesDirty || x != mX || y != mY || width != mW || height != mH;
		mX = x;
		mY = y;
		mW = width;
		mH = height;
	}

	public void setCenter(float centerX, float centerY, float width, float height) {
		mAreVerticesDirty = mAreVerticesDirty || (centerX - width / 2) != mX || (centerY - height / 2) != mY || width != mW || height != mH;
		mX = centerX - width / 2;
		mY = centerY - height / 2;
		mW = width;
		mH = height;
	}

	public void expand(float expandByAmount) {
		mAreVerticesDirty = true;
		mX -= expandByAmount * 0.5f;
		mY -= expandByAmount * 0.5f;
		mW += expandByAmount * 2;
		mH += expandByAmount * 2;
	}

	@Override
	public void rotateRel(float relativeRotation) {
		mAreVerticesDirty = true;
		mRotation += relativeRotation;
	}

	@Override
	public void rotateAbs(float absoluteRotationAmount) {
		mAreVerticesDirty = true;
		mRotation = absoluteRotationAmount;
	}

	protected void updateVertices() {
		if (!mAreVerticesDirty)
			return;

		final float lWidth = mFlipHorizontal ? -mW : mW;
		final float lHeight = mFlipVertical ? -mH : mH;

		final float lPX = mFlipHorizontal ? -mPivotX : mPivotX;
		final float lPY = mFlipVertical ? -mPivotY : mPivotY;

		mVertices.get(0).set(-lWidth / 2, -lHeight / 2);
		mVertices.get(1).set(lWidth / 2, -lHeight / 2);
		mVertices.get(2).set(-lWidth / 2, lHeight / 2);
		mVertices.get(3).set(lWidth / 2, lHeight / 2);

		final var sin = (float) (Math.sin(mRotation));
		final var cos = (float) (Math.cos(mRotation));

		for (int i = 0; i < NUM_VERTICES; i++) {
			float dx = -lPX + mVertices.get(i).x * 1.f;
			float dy = -lPY + mVertices.get(i).y * 1.f;

			mVertices.get(i).set(centerX() + (dx * cos - (dy * 1f) * sin) * mScaleX, centerY() + (dx * sin + (dy * 1f) * cos) * mScaleY);
		}

		mAreVerticesDirty = false;
	}

	/** Expands the bounds of this rectangle to include the new point */
	public void updateAABBToEnclosePoint(float pointX, float pointY) {
		if (mX > pointX) {
			final float lDiffX = mX - pointX;
			mX = pointX;
			mW += lDiffX;
		}

		if (right() < pointX)
			mW = pointX - mX;

		if (mY > pointY) {
			final float lDiffY = mY - pointY;
			mY = pointY;
			mH += lDiffY;
		}

		if (bottom() < pointY)
			mH = pointY - mY;

	}
}

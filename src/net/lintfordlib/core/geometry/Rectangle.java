package net.lintfordlib.core.geometry;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.maths.Vector2f;

public class Rectangle implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1795671904806528834L;

	public static final int NUM_VERTICES = 4;

	// --------------------------------------
	// Variables
	// --------------------------------------

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

	@SerializedName(value = "pivotX")
	protected float mPivotX;
	@SerializedName(value = "pivotY")
	protected float mPivotY;

	protected boolean mFlipHorizontal;
	protected boolean mFlipVertical;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float x() {
		return mX;
	}

	public void x(float pX) {
		mX = pX;
	}

	public float y() {
		return mY;
	}

	public void y(float pY) {
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
		mW = width;
	}

	public float height() {
		return mH;
	}

	public void height(float height) {
		mH = height;
	}

	public boolean flipHorizontal() {
		return mFlipHorizontal;
	}

	public void flipHorizontal(boolean flipHorizontal) {
		mFlipHorizontal = flipHorizontal;
	}

	public boolean flipVertical() {
		return mFlipVertical;
	}

	public void flipVertical(boolean flipVertical) {
		mFlipVertical = flipVertical;
	}

	public float scaleX() {
		return mScaleX;
	}

	public void scaleX(float scaleX) {
		mScaleX = scaleX;
	}

	public float scaleY() {
		return mScaleY;
	}

	public void scaleY(float scaleY) {
		mScaleY = scaleY;
	}

	public void setScale(float scaleX, float scaleY) {
		mScaleX = scaleX;
		mScaleY = scaleY;
	}

	public float centerX() {
		return mX + mW / 2;
	}

	public float centerY() {
		return mY + mH / 2;
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

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Rectangle() {
		this(0, 0, 0, 0);
	}

	public Rectangle(Rectangle rectangle) {
		this(rectangle.mX, rectangle.mY, rectangle.width(), rectangle.height());
	}

	public Rectangle(float x, float y, float width, float height) {
		mX = x;
		mY = y;
		mW = width;
		mH = height;

		mScaleX = 1f;
		mScaleY = 1f;
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
	public boolean intersectsAA(Rectangle otherRectangle) {
		return ((((otherRectangle.left() < right()) && (left() < otherRectangle.right())) && (otherRectangle.top() < bottom())) && (top() < otherRectangle.bottom()));
	}

	public boolean intersectsAA(float x, float y, float w, float h) {
		return ((((x < right()) && (left() < x + w)) && (y < bottom())) && (top() < y + h));
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
		mX = x;
		mY = y;
	}

	public void setCenterPosition(float newCenterX, float newCenterY) {
		mX = newCenterX - mW / 2;
		mY = newCenterY - mH / 2;
	}

	public void setDimensions(float width, float height) {
		mW = width;
		mH = height;
	}

	public void set(Rectangle rectangle) {
		mX = rectangle.mX;
		mY = rectangle.mY;
		mW = rectangle.mW;
		mH = rectangle.mH;
	}

	public void set(float x, float y, float width, float height) {
		mX = x;
		mY = y;
		mW = width;
		mH = height;
	}

	public void absMoveCenter(float absX, float absY) {
		mX = absX - mW * .5f;
		mY = absY - mH * .5f;
	}

	public void absMove(float absX, float absY) {
		mX = absX;
		mY = absY;
	}

	public void relMove(float dX, float dY) {
		mX += dX;
		mY += dY;
	}

	public void relResize(float dW, float dH) {
		mW += dW;
		mH += dH;
	}

	public void setCenter(float centerX, float centerY, float width, float height) {
		mX = centerX - width * .5f;
		mY = centerY - height * .5f;
		mW = width;
		mH = height;
	}

	public void expand(float expandByAmount) {
		mX -= expandByAmount * 0.5f;
		mY -= expandByAmount * 0.5f;
		mW += expandByAmount * 2;
		mH += expandByAmount * 2;
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

	public void updateAABBToEncloseRectangle(Rectangle other) {
		if (mX > other.mX) {
			final float lDiffX = mX - other.x();
			mX = other.x();
			mW += lDiffX;
		}

		if (mY > other.mY) {
			final float lDiffY = mY - other.y();
			mY = other.y();
			mH += lDiffY;
		}

		if (right() < other.right())
			mW = other.right() - mX;

		if (bottom() < other.bottom())
			mH = other.bottom() - mY;

	}
}

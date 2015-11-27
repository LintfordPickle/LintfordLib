package net.ld.library.core.maths;

public class Rectangle {

	// ===========================================================
	// Variables
	// ===========================================================

	public float mX;
	public float mY;
	public float mWidth;
	public float mHeight;
	private Vector2f mCenter;

	// ===========================================================
	// Properties
	// ===========================================================

	public float left() {
		return mX;
	}

	public float right() {
		return mX + mWidth;
	}

	public float x() {
		return mX;
	}

	public float y() {
		return mY;
	}

	public float top() {
		return mY;
	}

	public float bottom() {
		return mY + mHeight;
	}

	public float width() {
		return mWidth;
	}

	public float height() {
		return mHeight;
	}

	public Vector2f center() {
		return mCenter;
	}

	// ===========================================================
	// Constructors
	// ===========================================================

	public Rectangle() {
		this(0, 0, 0, 0);
	}

	public Rectangle(float pX, float pY, float pWidth, float pHeight) {
		mX = pX;
		mY = pY;
		mWidth = pWidth;
		mHeight = pHeight;
		mCenter = new Vector2f(0, 0);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * This rectangle intersects that rectangle.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance intersects the given rectangle. False otherwise.
	 */
	public boolean intersects(Rectangle pValue) {
		return ((((pValue.mX < (this.mX + this.mWidth)) && (this.mX < (pValue.mX + pValue.mWidth))) && (pValue.mY < (this.mY + this.mHeight))) && (this.mY < (pValue.mY + pValue.mHeight)));
	}

	/*
	 * This rectangle intersects that rectangle.
	 * 
	 * @param otherRect
	 * 
	 * @Returns True if this rectangle instance intersects the given rectangle. False otherwise.
	 */
	public boolean intersects(Vector2f pValue) {
		return ((((pValue.x < (this.mX + this.mWidth)) && (this.mX < (pValue.x))) && (pValue.y < (this.mY + this.mHeight))) && (this.mY < (pValue.y)));
	}
	
	/* This rectangle intersects that rectangle.
	 * 
	 * @param otherRect
	 * 
	 * @Returns True if this rectangle instance intersects the given rectangle. False otherwise.
	 */
	public boolean intersects(float x, float y) {
		return ((((x < (this.mX + this.mWidth)) && (this.mX < (x))) && (y < (this.mY + this.mHeight))) && (this.mY < (y)));
	}

	/**
	 * Returns true if this rectangle's dimensions and position are zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return ((((this.mWidth == 0) && (this.mHeight == 0)) && (this.mX == 0)) && (this.mY == 0));
	}

	/**
	 * @Returns The center X coordinate of this rectangle.
	 */
	public float centerX() {
		return this.mX + (this.mWidth / 2);
	}

	/**
	 * @Returns The center Y coordinate of this rectangle.
	 */
	public float centerY() {
		return this.mY + (this.mHeight / 2);
	}

	/**
	 * Centers the rectangle around the given coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public void setCenterPosition(float pX, float pY) {
		mX = pX - (mWidth * 0.5f);
		mY = pY - (mHeight * 0.5f);
	}

	public void setPosition(float pX, float pY) {
		mX = pX;
		mY = pY;
	}

	public void setWidth(float pWidth) {
		mWidth = pWidth;
	}

	public void setHeight(float pHeight) {
		mHeight = pHeight;
	}

	public void setbounds(float x, float y, float w, float h) {
		this.mX = x;
		this.mY = y;
		this.mWidth = w;
		this.mHeight = h;
		updateCenter();
	}

	private void updateCenter() {
		mCenter.x = this.mX + (this.mWidth / 2);
		mCenter.y = this.mY + (this.mHeight / 2);
	}

	public void setSize(float pWidth, float pHeight) {
		mWidth = pWidth;
		mHeight = pHeight;
	}

	public static Vector2f getIntersectionDepth(Rectangle pRectA, Rectangle pRectB) {

		float halfWidthA = pRectA.width() / 2.0f;
		float halfHeightA = pRectA.height() / 2.0f;
		float halfWidthB = pRectB.width() / 2.0f;
		float halfHeightB = pRectB.height() / 2.0f;

		Vector2f centerA = new Vector2f(pRectA.x() + halfWidthA, pRectA.y() + halfHeightA);
		Vector2f centerB = new Vector2f(pRectB.x() + halfWidthB, pRectB.y() + halfHeightB);

		float distanceX = centerA.x - centerB.x;
		float distanceY = centerA.y - centerB.y;
		float minDistanceX = halfWidthA + halfWidthB;
		float minDistanceY = halfHeightA + halfHeightB;

		if (Math.abs(distanceX) >= minDistanceX || Math.abs(distanceY) >= minDistanceY)
			return new Vector2f();

		// Calculate and return intersection depths.
		float depthX = distanceX > 0 ? minDistanceX - distanceX : -minDistanceX - distanceX;
		float depthY = distanceY > 0 ? minDistanceY - distanceY : -minDistanceY - distanceY;
		return new Vector2f(depthX, depthY);

	}
}

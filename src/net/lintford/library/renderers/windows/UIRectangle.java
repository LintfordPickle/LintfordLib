package net.lintford.library.renderers.windows;

import net.lintford.library.core.maths.Vector2f;

public class UIRectangle {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float x;
	public float y;
	public float width;
	public float height;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float left() {
		return x;
	}

	public float right() {
		return x + width;
	}

	public float top() {
		return y;
	}

	public float bottom() {
		return y + height;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public UIRectangle() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
	}

	public UIRectangle(UIRectangle pCopy) {
		x = pCopy.x;
		y = pCopy.y;
		width = pCopy.width;
		height = pCopy.height;
	}

	public UIRectangle(float pX, float pY, float pWidth, float pHeight) {
		x = pX;
		y = pY;
		width = pWidth;
		height = pHeight;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * This Rectangle contains that rectangle.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given rectangle. False otherwise.
	 */
	public boolean intersects(UIRectangle pOtherRect) {
		return ((((pOtherRect.x < (this.x + this.width)) && (this.x < (pOtherRect.x + pOtherRect.width))) && (pOtherRect.y < (this.y + this.height))) && (this.y < (pOtherRect.y + pOtherRect.height)));
	}

	/**
	 * This Rectangle contains that point.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersects(Vector2f pPoint) {
		return ((((this.x <= pPoint.x) && (pPoint.x < (this.x + this.width))) && (this.y <= pPoint.y)) && (pPoint.y < (this.y + this.height)));
	}

	/**
	 * This Rectangle contains that point.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersects(float pX, float pY) {
		return pX >= x && pX <= right() && pY >= y && pY <= bottom();
	}

	public boolean intersects(float pX, float pY, float pW, float pH) {
		return ((((pX < (this.x + this.width)) && (this.x < (pX + pW))) && (pY < (this.y + this.height))) && (this.y < (pY + pH)));
	}

	/**
	 * Returns true if this rectangle's dimensions and position are zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return ((((this.width == 0) && (this.height == 0)) && (this.x == 0)) && (this.y == 0));
	}

	/** @Returns The center X coordinate of this rectangle. */
	public float centerX() {
		return this.x + (this.width / 2);
	}

	/** @Returns The center Y coordinate of this rectangle. */
	public float centerY() {
		return this.y + (this.height / 2);
	}

	/**
	 * Centers the rectangle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setCenterPosition(float pX, float pY) {
		x = pX - (width * 0.5f);
		y = pY - (height * 0.5f);
	}

	public void setPosition(float pX, float pY) {
		x = pX;
		y = pY;
	}

	public void setWidth(float pWidth) {
		width = pWidth;
	}

	public void setHeight(float pHeight) {
		height = pHeight;
	}

	public void set(float pX, float pY, float pWidth, float pHeight) {
		x = pX;
		y = pY;
		width = pWidth;
		height = pHeight;

	}

	public void set(UIRectangle pRect) {
		x = pRect.x;
		y = pRect.y;
		width = pRect.width;
		height = pRect.height;

	}

	public void set(UIRectangle pRect, float pRatio) {
		final float NEW_WIDTH = pRect.width * pRatio;
		final float NEW_HEIGHT = pRect.height * pRatio;

		x = pRect.x - NEW_WIDTH / 2f;
		y = pRect.y - NEW_HEIGHT / 2f;
		width = NEW_WIDTH;
		height = NEW_HEIGHT;

	}

	public void set(UIRectangle pRect, float pWRatio, float pHRatio) {
		final float NEW_WIDTH = pRect.width * pWRatio;
		final float NEW_HEIGHT = pRect.height * pHRatio;

		x = pRect.x - NEW_WIDTH / 2f;
		y = pRect.y - NEW_HEIGHT / 2f;
		width = NEW_WIDTH;
		height = NEW_HEIGHT;

	}

	/** Expands the bounds of this rectangle by the given amount. */
	public void expand(float pAmt) {
		x += pAmt * 0.5f;
		width -= pAmt * 2;
		y += pAmt * 0.5f;
		height -= pAmt * 2;
	}

}

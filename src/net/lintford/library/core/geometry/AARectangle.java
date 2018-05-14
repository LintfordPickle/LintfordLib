package net.lintford.library.core.geometry;

import java.io.Serializable;

import net.lintford.library.core.maths.Vector2f;

/**
 * Represents an axis-aligned rectangle and collision methods.
 */
public class AARectangle implements Serializable {

	private static final long serialVersionUID = -2882875960939792798L;
	
	// --------------------------------------
	// Variables
	// --------------------------------------

	public float x;
	public float y;
	public float w;
	public float h;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public AARectangle() {
		x = 0;
		y = 0;
		w = 0;
		h = 0;
	}

	public AARectangle(AARectangle pCopy) {
		x = pCopy.x;
		y = pCopy.y;
		w = pCopy.w;
		h = pCopy.h;
	}

	public AARectangle(float pX, float pY, float pWidth, float pHeight) {
		x = pX;
		y = pY;
		w = pWidth;
		h = pHeight;
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
	public boolean intersects(AARectangle pOtherRect) {
		return ((((pOtherRect.x < (this.x + this.w)) && (this.x < (pOtherRect.x + pOtherRect.w))) && (pOtherRect.y < (this.y + this.h))) && (this.y < (pOtherRect.y + pOtherRect.h)));
	}

	/**
	 * This Rectangle contains that point.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersects(Vector2f pPoint) {
		return ((((this.x <= pPoint.x) && (pPoint.x < (this.x + this.w))) && (this.y <= pPoint.y)) && (pPoint.y < (this.y + this.h)));
	}

	/**
	 * This Rectangle contains that point.
	 * 
	 * @param otherRect
	 * @Returns True if this rectangle instance entirely contains the given point. False otherwise.
	 */
	public boolean intersects(float pX, float pY) {
		boolean lResult = pX >= left() && pX <= right() && pY >= top() && pY <= bottom();
		return lResult;
	}

	public boolean intersects(float pX, float pY, float pW, float pH) {
		return ((((pX < (this.x + this.w)) && (this.x < (pX + pW))) && (pY < (this.y + this.h))) && (this.y < (pY + pH)));
	}

	/**
	 * Returns true if this rectangle's dimensions and position are zero.
	 * 
	 * @Returs True if everything is zero.
	 */
	public boolean isEmpty() {
		return ((((this.w == 0) && (this.h == 0)) && (this.x == 0)) && (this.y == 0));
	}

	/** @Returns The center X coordinate of this rectangle. */
	public float centerX() {
		return this.x + (this.w / 2);
	}

	/** @Returns The center Y coordinate of this rectangle. */
	public float centerY() {
		return this.y + (this.h / 2);
	}

	/**
	 * Centers the rectangle around the given coordinates.
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setCenterPosition(float pX, float pY) {
		x = pX - (w * 0.5f);
		y = pY - (h * 0.5f);
	}

	public void setPosition(float pX, float pY) {
		x = pX;
		y = pY;
	}

	public void setWidth(float pWidth) {
		w = pWidth;
	}

	public void setHeight(float pHeight) {
		h = pHeight;
	}

	public void set(float pX, float pY, float pWidth, float pHeight) {
		x = pX;
		y = pY;
		w = pWidth;
		h = pHeight;

	}

	public void set(AARectangle pRect) {
		x = pRect.x;
		y = pRect.y;
		w = pRect.w;
		h = pRect.h;

	}

	public void set(AARectangle pRect, float pRatio) {
		final float NEW_WIDTH = pRect.w * pRatio;
		final float NEW_HEIGHT = pRect.h * pRatio;

		x = pRect.x - NEW_WIDTH / 2f;
		y = pRect.y - NEW_HEIGHT / 2f;
		w = NEW_WIDTH;
		h = NEW_HEIGHT;

	}

	public void set(AARectangle pRect, float pWRatio, float pHRatio) {
		final float NEW_WIDTH = pRect.w * pWRatio;
		final float NEW_HEIGHT = pRect.h * pHRatio;

		x = pRect.x - NEW_WIDTH / 2f;
		y = pRect.y - NEW_HEIGHT / 2f;
		w = NEW_WIDTH;
		h = NEW_HEIGHT;

	}

	/** Expands the bounds of this rectangle by the given amount. */
	public void expand(float pAmt) {
		x -= pAmt * 0.5f;
		w += pAmt * 2;
		y -= pAmt * 0.5f;
		h += pAmt * 2;
	}

}

package net.lintford.library.data.entities;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;

public class RectangleEntity extends WorldEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static final long serialVersionUID = -4164547250141298109L;

	protected Rectangle mBounds;

	public float rotation;
	public float width;
	public float height;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float w() {
		return width;
	}

	public float h() {
		return height;
	}

	public float left() {
		return x - width / 2;
	}

	public float top() {
		return y - height / 2;
	}

	public Rectangle bounds() {
		return mBounds;
	}

	@Override
	public float maxLength() {
		return Math.max(width, height);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RectangleEntity() {
		this(0, 0, 0, 0);
	}

	public RectangleEntity(float pWidth, float pHeight) {
		this(0, 0, pWidth, pHeight);
	}

	public RectangleEntity(float pX, float pY, float pWidth, float pHeight) {
		x = pX;
		y = pY;
		width = pWidth;
		height = pHeight;

		mBounds = new Rectangle();
		mBounds = new Rectangle(x, y, width, height);
		mBounds.expand(SKIN_WIDTH * -2f);

		initialize();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		super.update(pCore);

		mBounds.rotateAbs(rotation); // + (float) Math.toRadians(90));
		mBounds.setCenter(x, y, width, height);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;

	}

	@Override
	public boolean intersects(WorldEntity pOther) {
		// Poly
		if (pOther instanceof PolyEntity) {
			// TODO: Rectangle <-> Poly collision
		}

		// Rect
		else if (pOther instanceof RectangleEntity) {
			RectangleEntity otherRect = (RectangleEntity) pOther;
			if (Math.abs(x - pOther.x) > width / 2 + otherRect.width / 2)
				return false;
			if (Math.abs(y - pOther.y) > height / 2 + otherRect.height / 2)
				return false;
			return true;
		}

		// Circle
		else if (pOther instanceof CircleEntity) {
			CircleEntity c = (CircleEntity) pOther;
			float circleDistX = Math.abs(c.x - this.x);
			float circleDistY = Math.abs(c.y - this.y);

			if (circleDistX > (this.width / 2 + c.radius)) {
				return false;
			}
			if (circleDistY > (this.height / 2 + c.radius)) {
				return false;
			}

			if (circleDistX <= (this.width / 2)) {
				return true;
			}
			if (circleDistY <= (this.height / 2)) {
				return true;
			}

			float dist_sq = (circleDistX - this.width / 2) * (circleDistX - this.width / 2) + (circleDistY - this.height / 2) * (circleDistX - this.width / 2) * (circleDistX - this.width / 2)
					+ (circleDistY - this.height / 2);

			return (dist_sq <= (c.radius * c.radius));

		}

		// no collision
		return false;
	}

}
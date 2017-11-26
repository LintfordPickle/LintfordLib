package net.lintford.library.data.entities;

import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.core.time.GameTime;

public class RectangleEntity extends WorldEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static final long serialVersionUID = -4164547250141298109L;

	protected Rectangle mBounds;

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

	public void initialize() {

	}

	public void update(final GameTime pGameTime) {
		super.update(pGameTime);

		mBounds.set(x - width / 2, y - height / 2, width, height);

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
			// TODO: Rectangle <-> Circle collision
		}

		// no collision
		return false;
	}

}
package net.lintford.library.core.entity;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;

public abstract class RectangleEntity extends Entity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static final long serialVersionUID = -4164547250141298109L;

	protected Rectangle mBounds;

	protected float mWidth;
	protected float mHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float w() {
		return mWidth;
	}

	public float h() {
		return mHeight;
	}

	public float left() {
		return x - mWidth / 2;
	}

	public float top() {
		return y - mHeight / 2;
	}

	public Rectangle bounds() {
		return mBounds;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RectangleEntity(int entityUid) {
		this(entityUid, 0, 0, 0, 0);
	}

	public RectangleEntity(int entityUid, float width, float height) {
		this(entityUid, 0, 0, width, height);
	}

	public RectangleEntity(int entityUid, float xPosition, float yPosition, float width, float height) {
		super(entityUid);

		x = xPosition;
		y = yPosition;
		mWidth = width;
		mHeight = height;

		mBounds = new Rectangle();
		mBounds = new Rectangle(x, y, mWidth, mHeight);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		mBounds.rotationInRadians(rotationRadians);
		mBounds.setCenter(x, y, mWidth, mHeight);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float width, float height) {
		mWidth = width;
		mHeight = height;
	}
}
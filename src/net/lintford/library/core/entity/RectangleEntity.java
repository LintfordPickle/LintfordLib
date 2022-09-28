package net.lintford.library.core.entity;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;

public abstract class RectangleEntity extends WorldEntity {

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
		return mWorldPositionX - mWidth / 2;
	}

	public float top() {
		return mWorldPositionY - mHeight / 2;
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

	public RectangleEntity(float width, float height) {
		this(0, 0, width, height);
	}

	public RectangleEntity(float x, float y, float width, float height) {
		super();

		mWorldPositionX = x;
		mWorldPositionY = y;
		mWidth = width;
		mHeight = height;

		mBounds = new Rectangle();
		mBounds = new Rectangle(mWorldPositionX, mWorldPositionY, mWidth, mHeight);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		mBounds.rotateAbs(mRotationInRadians);
		mBounds.setCenter(mWorldPositionX, mWorldPositionY, mWidth, mHeight);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float width, float height) {
		mWidth = width;
		mHeight = height;
	}
}
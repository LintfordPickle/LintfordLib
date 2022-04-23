package net.lintford.library.core.entity;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;

public abstract class RectangleEntity extends WorldEntity {

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
		return worldPositionX - width / 2;
	}

	public float top() {
		return worldPositionY - height / 2;
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
		super();

		worldPositionX = pX;
		worldPositionY = pY;
		width = pWidth;
		height = pHeight;

		mBounds = new Rectangle();
		mBounds = new Rectangle(worldPositionX, worldPositionY, width, height);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		mBounds.rotateAbs(rotation); // + (float) Math.toRadians(90));
		mBounds.setCenter(worldPositionX, worldPositionY, width, height);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;
	}

}
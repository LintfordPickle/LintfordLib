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
		return mWorldPositionX - width / 2;
	}

	public float top() {
		return mWorldPositionY - height / 2;
	}

	public Rectangle bounds() {
		return mBounds;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RectangleEntity(final int pPoolUid) {
		this(pPoolUid, 0, 0, 0, 0);
	}

	public RectangleEntity(final int pPoolUid, float pWidth, float pHeight) {
		this(pPoolUid, 0, 0, pWidth, pHeight);
	}

	public RectangleEntity(final int pPoolUid, float pX, float pY, float pWidth, float pHeight) {
		super(pPoolUid);

		mWorldPositionX = pX;
		mWorldPositionY = pY;
		width = pWidth;
		height = pHeight;

		mBounds = new Rectangle();
		mBounds = new Rectangle(mWorldPositionX, mWorldPositionY, width, height);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		mBounds.rotateAbs(rotation); // + (float) Math.toRadians(90));
		mBounds.setCenter(mWorldPositionX, mWorldPositionY, width, height);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;

	}

}
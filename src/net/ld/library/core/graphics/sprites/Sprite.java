package net.ld.library.core.graphics.sprites;

import net.ld.library.core.time.GameTime;

public class Sprite implements ISprite {

	// =============================================
	// Variables
	// =============================================

	public final float mX;
	public final float mY;
	private final int mWidth;
	private final int mHeight;

	// =============================================
	// Properties
	// =============================================

	// =============================================
	// Constructor
	// =============================================

	public Sprite(final float pX, final float pY, final int pWidth, final int pHeight) {
		mX = pX;
		mY = pY;
		mWidth = pWidth;
		mHeight = pHeight;
	}

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void update(GameTime pGameTime) {

	}

	// =============================================
	// Methods
	// =============================================

	@Override
	public float getX() {

		return mX;
	}

	@Override
	public float getY() {

		return mY;
	}

	@Override
	public int getWidth() {
		return mWidth;
	}

	@Override
	public int getHeight() {
		return mHeight;
	}

	public Sprite getSprite() {
		return this;
	}

	@Override
	public ISprite copy() {
		return new Sprite(mX, mY, mWidth, mHeight);
	}

}

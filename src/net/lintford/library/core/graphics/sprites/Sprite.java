package net.lintford.library.core.graphics.sprites;

/** Defines a single sprite animation frame */
public class Sprite implements ISprite {

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** The x coordinate within the spritesheet of this frame */
	public int x;

	/** The y coordinate within the spritesheet of this frame */
	public int y;

	/** The width of the frame */
	public int w;

	/** The height of the frame */
	public int h;

	/** The x coordinate within the spritesheet of the anchor point */
	public int ax;

	/** The y coordinate within the spritesheet of the anchor point */
	public int ay;

	/** pivot point x */
	public int px;

	/** pivot point y */
	public int py;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Sprite(final int pX, final int pY, final int pWidth, final int pHeight) {
		x = pX;
		y = pY;
		w = pWidth;
		h = pHeight;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------
	
	
	
	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------
	
	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public int getW() {
		return w;
	}

	@Override
	public int getH() {
		return h;
	}

	@Override
	public float getAX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAY() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

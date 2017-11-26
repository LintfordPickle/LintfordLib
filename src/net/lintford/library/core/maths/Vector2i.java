package net.lintford.library.core.maths;

/** Encapsulates a 2D vector with integer components. */
public final class Vector2i {

	/** the x-component of this vector **/
	public int x;

	/** the y-component of this vector **/
	public int y;

	public Vector2i() {
		x = y = 0;
	}

	public Vector2i(int pV) {
		x = y = pV;
	}

	public Vector2i(int pX, int pY) {
		x = pX;
		y = pY;
	}

}

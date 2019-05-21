package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;

public class SpriteAnchor implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5318734212696515674L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public float x, y, r;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public SpriteAnchor() {

	}

	public SpriteAnchor(String pName, float pX, float pY) {
		name = pName;
		x = pX;
		y = pY;

	}

}

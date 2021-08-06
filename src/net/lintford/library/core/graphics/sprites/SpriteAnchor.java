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

	/** Uniquely identifies this anchor. */
	public String anchorName;
	public int x, y; 
	public float r;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public SpriteAnchor() {

	}

	public SpriteAnchor(String pAnchorName, int pX, int pY) {
		anchorName = pAnchorName;
		x = pX;
		y = pY;

	}

}

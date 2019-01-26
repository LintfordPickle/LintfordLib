package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;

import net.lintford.library.core.geometry.Rectangle;

/** Defines a single sprite animation frame */
public class SpriteFrame extends Rectangle implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3808825740133201931L;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float getDefaultRotation() {
		return rot;
	}

	public float getPivotPointX() {
		return px;
	}

	public float getPivotPointY() {
		return py;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteFrame() {

	}

	public SpriteFrame(float pSrcX, float pSrcY, float pSrcW, float pSrcH) {
		x = pSrcX;
		y = pSrcY;
		w = pSrcW;
		h = pSrcH;
	}

}

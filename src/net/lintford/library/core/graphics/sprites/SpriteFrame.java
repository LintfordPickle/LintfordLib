package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;
import java.util.List;

import net.lintford.library.core.geometry.Rectangle;

/** Defines a single sprite animation frame */
public class SpriteFrame extends Rectangle implements Serializable {

	public static final SpriteAnchor ZERO_ANCHOR = new SpriteAnchor("ZERO", 0, 0);

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3808825740133201931L;

	private SpriteAnchor[] anchorPoints;

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

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteAnchor getAnchor(String pName) {
		if (anchorPoints == null || anchorPoints.length == 0)
			return ZERO_ANCHOR;

		final int lAnchorCount = anchorPoints.length;
		for (int i = 0; i < lAnchorCount; i++) {
			if (anchorPoints[i] == null)
				continue;

			if (anchorPoints[i].name.equals(pName))
				return anchorPoints[i];
		}
		return ZERO_ANCHOR;

	}
}

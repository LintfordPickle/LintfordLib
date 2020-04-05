package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;

import net.lintford.library.core.geometry.Rectangle;

/** Defines a single sprite animation frame */
public class SpriteFrame extends Rectangle implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3808825740133201931L;

	private SpriteAnchor[] anchorPoints;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float getDefaultRotation() {
		return rotation;
	}

	public float getPivotPointX() {
		return pivotX;
	}

	public float getPivotPointY() {
		return pivotY;
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

	public int anchorCount() {
		if (anchorPoints == null)
			return 0;

		return anchorPoints.length;
	}

	public SpriteAnchor getAnchorByIndex(int pArrayIndex) {
		if (anchorPoints == null || anchorPoints.length == 0 || pArrayIndex >= anchorPoints.length) {
			return null;

		}

		return anchorPoints[pArrayIndex];

	}

	public SpriteAnchor getAnchorByName(String pName) {
		if (anchorPoints == null || anchorPoints.length == 0)
			return null;

		final int lAnchorCount = anchorPoints.length;
		for (int i = 0; i < lAnchorCount; i++) {
			if (anchorPoints[i] == null)
				continue;

			if (anchorPoints[i].anchorName.equals(pName))
				return anchorPoints[i];
		}

		return null;

	}
}

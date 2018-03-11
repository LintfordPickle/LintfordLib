package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;

import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphAnchorDef;

/** Defines a single sprite animation frame */
// TODO: Check out the serialized version of this class - it contains a lot of potentially superflous information.
public class SpriteFrame extends AARectangle implements Serializable {

	private static final long serialVersionUID = 3808825740133201931L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Defines a default rotation for this sprite. */
	public float rot;

	/** The pivot point X component. */
	public float px;

	/** The pivot point Y component. */
	public float py;

	/** A list of named anchor points. */
	public SpriteGraphAnchorDef[] anchors;

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
	// Inherited-Methods
	// --------------------------------------

	public SpriteGraphAnchorDef getAnchorPoint(String pName) {
		if (anchors == null || anchors.length == 0)
			return null;

		final int SIZE = anchors.length;
		for (int i = 0; i < SIZE; i++) {
			if (anchors[i].name.equals(pName))
				return anchors[i];

		}

		return null;

	}

}

package net.lintford.library.core.graphics.sprites;

import java.io.Serializable;

import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphAnchorDef;

/** Defines a single sprite animation frame */
// TODO: Check out the serialized version of this class - it contains a lot of potentially superflous information.
public class Sprite extends Rectangle implements Serializable, ISprite {

	private static final long serialVersionUID = 3808825740133201931L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** The x coordinate within the spritesheet of this frame */
	public float x;

	/** The y coordinate within the spritesheet of this frame */
	public float y;

	/** The width of the frame */
	public float w;

	/** The height of the frame */
	public float h;
	
	public SpriteGraphAnchorDef[] anchors;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Sprite() {

	}

	public Sprite(float pSrcX, float pSrcY, float pSrcW, float pSrcH) {
		x = pSrcX;
		y = pSrcY;
		w = pSrcW;
		h = pSrcH;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public float getSrcX() {
		return x;
	}

	@Override
	public float getSrcY() {
		return y;
	}

	@Override
	public float getSrcWidth() {
		return w;
	}

	@Override
	public float getSrcHeight() {
		return h;
	}

	@Override
	public float getRotation() {
		return rot;
	}
	
	@Override
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

	@Override
	public float getPivotPointX() {
		return pivotX();
	}

	@Override
	public float getPivotPointY() {
		return pivotY();
	}

}

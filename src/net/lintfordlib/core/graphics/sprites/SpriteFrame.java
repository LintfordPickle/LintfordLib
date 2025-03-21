package net.lintfordlib.core.graphics.sprites;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.geometry.Rectangle;

/** Defines a single sprite animation frame */
public class SpriteFrame extends Rectangle implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3808825740133201931L;

	@SerializedName(value = "name")
	private String mName;

	@SerializedName(value = "anchorToPointName")
	private String mAnchorToPointName;

	@SerializedName(value = "anchorPoints")
	private SpriteAnchor[] mAnchorPoints;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mName;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteFrame() {
		mScaleX = 1.0f;
		mScaleY = 1.0f;
	}

	public SpriteFrame(float sourceX, float sourceY, float sourceW, float sourceH) {
		this();

		mX = sourceX;
		mY = sourceY;
		mW = sourceW;
		mH = sourceH;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public int anchorCount() {
		if (mAnchorPoints == null)
			return 0;

		return mAnchorPoints.length;
	}

	public SpriteAnchor getAnchorByIndex(int arrayIndex) {
		if (mAnchorPoints == null || mAnchorPoints.length == 0 || arrayIndex >= mAnchorPoints.length) {
			return null;
		}

		return mAnchorPoints[arrayIndex];
	}

	public String getAnchorToPointName() {
		return mAnchorToPointName;
	}

	public SpriteAnchor getAnchorByName(String anchorName) {
		if (mAnchorPoints == null || mAnchorPoints.length == 0)
			return null;

		final int lAnchorCount = mAnchorPoints.length;
		for (int i = 0; i < lAnchorCount; i++) {
			if (mAnchorPoints[i] == null)
				continue;

			if (mAnchorPoints[i].anchorName().equals(anchorName))
				return mAnchorPoints[i];
		}

		return null;
	}
}

package net.lintfordlib.core.graphics.sprites;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SpriteAnchor implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5318734212696515674L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "anchorName")
	private String mAnchorName;

	@SerializedName(value = "x")
	private int mLocalX;

	@SerializedName(value = "y")
	private int mLocalY;

	@SerializedName(value = "r")
	private float mRotation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String anchorName() {
		return mAnchorName;
	}

	public int localX() {
		return mLocalX;
	}

	public int localY() {
		return mLocalY;
	}

	public float rotation() {
		return mRotation;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public SpriteAnchor() {

	}

	public SpriteAnchor(String anchorName, int localX, int localY) {
		mAnchorName = anchorName;
		mLocalX = localX;
		mLocalY = localY;
	}
}

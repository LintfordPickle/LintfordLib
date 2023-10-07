package net.lintfordlib.core.geometry.spritegraph.definitions;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SpriteGraphNodeDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "name")
	private String mName;

	@SerializedName(value = "anchorNodeName")
	private String mAnchorNodeName;

	@SerializedName(value = "x")
	private float mX;

	@SerializedName(value = "y")
	private float mY;

	@SerializedName(value = "r")
	private float mRotation;

	@SerializedName(value = "zDepth")
	private int mZDepth;

	@SerializedName(value = "controlsGraphAnimationListener")
	private boolean mControlsGraphAnimationListener;

	/** Only ISpriteGraphAttachments of the same category can be attached to this node */
	@SerializedName(value = "attachmentCategory")
	private int mAttachmentCategory;

	/** A list of child parts which are anchored on this {@link SpriteGraphNodeDefinition}. */
	@SerializedName(value = "childParts")
	private List<SpriteGraphNodeDefinition> mChildParts;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mName;
	}

	public void name(String name) {
		mName = name;
	}

	public String anchorNodeName() {
		return mAnchorNodeName;
	}

	public void anchorNodeName(String anchorNodeName) {
		mAnchorNodeName = anchorNodeName;
	}

	public float x() {
		return mX;
	}

	public void x(float x) {
		mX = x;
	}

	public float y() {
		return mY;
	}

	public void y(float y) {
		mY = y;
	}

	public int zDepth() {
		return mZDepth;
	}

	public void zDepth(int zDepth) {
		mZDepth = zDepth;
	}

	public float rotation() {
		return mRotation;
	}

	public void rotation(float rotation) {
		mRotation = rotation;
	}

	public boolean controlsGraphAnimationListener() {
		return mControlsGraphAnimationListener;
	}

	public void controlsGraphAnimationListener(boolean controlsGraphAnimationListener) {
		mControlsGraphAnimationListener = controlsGraphAnimationListener;
	}

	public int attachmentCategory() {
		return mAttachmentCategory;
	}

	public void controlsGraphAnimationListener(int attachmentCategory) {
		mAttachmentCategory = attachmentCategory;
	}

	public List<SpriteGraphNodeDefinition> childParts() {
		return mChildParts;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphNodeDefinition() {
		mChildParts = new ArrayList<>();
		mControlsGraphAnimationListener = false;
	}

	public SpriteGraphNodeDefinition(String name) {
		this();
		mName = name;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addChild(SpriteGraphNodeDefinition spriteGraphNodeDefinition) {
		if (!mChildParts.contains(spriteGraphNodeDefinition)) {
			mChildParts.add(spriteGraphNodeDefinition);
		}
	}
}

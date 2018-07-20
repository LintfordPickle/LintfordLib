package net.lintford.library.core.fractal;

import java.io.Serializable;

import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;
import net.lintford.library.core.graphics.ResourceManager;

public class LSystemDefinition implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2692655159108332212L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String SpriteSheetName;
	public String rootNodeSpriteName;
	public String branchNodeSpriteName;
	public String leafNodeSpriteName;
	
	public int leafNodeDepth = 4; 

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LSystemDefinition() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		// TODO: This is where we resolve the whole sprite sizes schnittscnitz

	}

	public void unloadGLContent() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float getSpriteHeight(int pNodeDepth) {
		return 32f;

	}

	public float getSpriteInlayOffset(int pNodeDepth, float pAngle) {
		return getSpriteHeight(pNodeDepth) * 0.5f - (getSpriteHeight(pNodeDepth) * 0.25f * fresnelTerm(pAngle));
	}

	public float getAnchorPointX(int pNodeDepth) {
		return 0;
	}

	public float getAnchorPointY(int pNodeDepth) {
		return -getSpriteHeight(pNodeDepth) + 4;
	}

	public float getPivotX(int pNodeDepth) {
		return 0;
	}

	public float getPivotY(int pNodeDepth) {
		return 16;
	}

	public float getMinAngle(int pNodeDepth) {
		return 20;
	}

	public float getMaxAngle(int pNodeDepth) {
		return 45;
	}

	public float fresnelTerm(float pAngle) {
		return 1f;
	}

	public void onRootNodeCreated(SpriteGraphNodeInst pNode) {

	}

	public void onBranchNodeCreated(SpriteGraphNodeInst pNode) {

	}

	public void onLeafNodeCreated(SpriteGraphNodeInst pNode) {

	}

}
package net.lintford.library.core.lindenmeyer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInst;

public class LSystemDefinition implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2692655159108332212L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String axiom;
	public List<LRuleSet> rules;

	public String SpriteSheetName;
	public String rootNodeSpriteName;
	public String branchNodeSpriteName;
	public String leafNodeSpriteName;

	public int maxDepth;
	public int maxDepthPerIteration;
	public int leafNodeDepth;

	public float minAngle;
	public float maxAngle;
	
	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LSystemDefinition() {
		rules = new ArrayList<>();
		leafNodeDepth = 4;

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
		return 0;
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

	// --------------------------------------
	// Callbacks
	// --------------------------------------

	public void onGraphCreation(SpriteGraphInst pInst) {

	}

	public void onRootNodeCreation(SpriteGraphNodeInst pInst) {

	}
	
	public void onNodeCreation(SpriteGraphNodeInst pInst) {

	}

	public void onLeafNodeCreation(SpriteGraphNodeInst pInst) {

	}
	
}
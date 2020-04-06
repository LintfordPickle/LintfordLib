package net.lintford.library.core.geometry.spritegraph.instance;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.PooledBaseData;
import net.lintford.library.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintford.library.core.geometry.spritegraph.definition.SpriteGraphNodeDefinition;
import net.lintford.library.core.graphics.sprites.SpriteAnchor;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

// ToDo: Attachable Box2d bodies: Some nodes need to interact with the world via the sprite graph nodes
// ToDo: Attachable SpriteInstance: Each node instance should have its own sprite animation for the current spritesheetdefinition
public class SpriteGraphNodeInstance extends PooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 622930312736268776L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public SpriteGraphInstance mParentGraphInst;

	/** Each SpriteGraphNodeInstance has a specific sheetsheet assigned to it. It is from which this sheet that all the anchor information is taken. */
	public transient SpriteSheetDefinition spriteSheetDefinition;
	public transient SpriteInstance spriteInstance;
	public String spriteSheetName;

	/** The ID of the {@link SpriteGraphAnchorDef} on the parent. */
	public int parentAnchorID;

	/** The name of the {@link SpriteGraphAnchorDef} on the parent. */
	public String anchorNodeName;

	/** A list of child parts which are anchored on this {@link SpriteGraphNodeInstance}. */
	public List<SpriteGraphNodeInstance> childNodes;

	public int nodeDepth;
	public int zDepth;

	public int entityGroupID;

	public boolean angToPointEnabled;
	public float angToPoint;
	public float staticRotationOffset;

	private float mPositionX;
	private float mPositionY;
	private float mPivotX;
	private float mPivotY;
	private float mRotation;

	public Object attachedItemInstance;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isAssigned() {
		return mParentGraphInst != null;
	}

	public float pivotX() {
		return mPivotX;

	}

	public void pivotX(float pNewValue) {
		mPivotX = pNewValue;

	}

	public float pivotY() {
		return mPivotY;

	}

	public void pivotY(float pNewValue) {
		mPivotY = pNewValue;

	}

	public float positionX() {
		return mPositionX;

	}

	public void positionX(float pNewValue) {
		mPositionX = pNewValue;

	}

	public float positionY() {
		return mPositionY;

	}

	public void positionY(float pNewValue) {
		mPositionY = pNewValue;

	}

	public float rotation() {
		return mRotation;

	}

	public void rotation(float pNewValue) {
		mRotation = pNewValue;

	}

	public SpriteGraphNodeInstance getNodeNyNodeName(String pNodeName) {
		if (name == null) {
			return null;

		}

		if (name.equals(pNodeName)) {
			return this;

		}

		final int lNumChildNodes = childNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			final var lSpriteGraphNodeInstance = childNodes.get(i).getNodeNyNodeName(pNodeName);
			if (lSpriteGraphNodeInstance != null) {
				return lSpriteGraphNodeInstance;

			}

		}

		return null;

	}

	public SpriteGraphNodeInstance getNodeNyNodeSpriteFrameName(String pSpriteFrameNodeName) {
		if (anchorNodeName != null && anchorNodeName.equals(pSpriteFrameNodeName)) {
			return this;

		}

		final int lNumChildNodes = childNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			final var lSpriteGraphNodeInstance = childNodes.get(i).getNodeNyNodeSpriteFrameName(pSpriteFrameNodeName);
			if (lSpriteGraphNodeInstance != null) {
				return lSpriteGraphNodeInstance;

			}

		}

		return null;

	}

	public SpriteAnchor getAnchorPointByName(String pAnchorName) {
		if (spriteInstance == null)
			return null;

		// The anchor points are all stored in the individual SpriteFrames.

		final var lCurrentSpriteFrame = spriteInstance.currentSpriteFrame();
		if (lCurrentSpriteFrame == null)
			return null;

		return lCurrentSpriteFrame.getAnchorByName(pAnchorName);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphNodeInstance(final int pPoolUid) {
		super(pPoolUid);

		childNodes = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void init(List<SpriteGraphNodeInstance> pFlat, ISpriteGraphPool pSpriteGraphPool, SpriteGraphInstance pSpriteGraphInst, SpriteGraphNodeDefinition pGraphNodeDef, int pEntityGroupUid, int pNodeDepth) {
		mParentGraphInst = pSpriteGraphInst;
		name = pGraphNodeDef.name;
		anchorNodeName = pGraphNodeDef.anchorNodeName;
		nodeDepth = pNodeDepth;
		entityGroupID = pEntityGroupUid;
		zDepth = pGraphNodeDef.zDepth;

		final int lChildNodeCount = pGraphNodeDef.childParts.size();
		for (int i = 0; i < lChildNodeCount; i++) {
			final var lSpriteGraphNodeDefinition = pGraphNodeDef.childParts.get(i);
			final var lNewNode = pSpriteGraphPool.getSpriteGraphNodeInstance();

			lNewNode.init(pFlat, pSpriteGraphPool, pSpriteGraphInst, lSpriteGraphNodeDefinition, pEntityGroupUid, nodeDepth + 1);

			pFlat.add(lNewNode);

			childNodes.add(lNewNode);

		}

	}

	public void reset(ISpriteGraphPool pSpriteGraphPool) {

	}

	public void update(LintfordCore pCore, SpriteGraphInstance pParentGraph, SpriteGraphNodeInstance pParentGraphNode) {
		// Updating the sprite attached to us
		if (spriteInstance != null) {

			spriteInstance.flipHorizontal = mParentGraphInst.mFlipHorizontal;
			spriteInstance.flipVertical = mParentGraphInst.mFlipVertical;

			spriteInstance.update(pCore);

			final var lSpriteWidth = spriteInstance.width();
			final var lSpriteHeight = spriteInstance.height();

			final var lSpritePositionX = mPositionX - lSpriteWidth / 2f;
			final var lSpritePositionY = mPositionY - lSpriteHeight / 2f;

			final float lRotRadians = (float) Math.toRadians(mRotation);
			final float lRotationAdapted = spriteInstance.flipHorizontal ? -lRotRadians : lRotRadians;
			spriteInstance.rotateAbs(lRotationAdapted);
			spriteInstance.set(lSpritePositionX, lSpritePositionY, lSpriteWidth, lSpriteHeight);

		}

		final int lChildrenCount = childNodes.size();
		for (int i = 0; i < lChildrenCount; i++) {
			final var lChildSpriteGraphNode = childNodes.get(i);
			updateChildNodeTransform(pCore, lChildSpriteGraphNode);

			childNodes.get(i).update(pCore, pParentGraph, this);

		}

	}

	private void updateChildNodeTransform(LintfordCore pCore, SpriteGraphNodeInstance pChildGraphNodeInstance) {
		float lAnchorPositionX = 0.0f;
		float lAnchorPositionY = 0.0f;
		float lAnchorRotation = 0.0f;

		if (spriteInstance != null) {
			final var lCurrentSpriteFrame = spriteInstance.currentSpriteFrame();
			if (lCurrentSpriteFrame != null) {
				final var lAnchorPoint = lCurrentSpriteFrame.getAnchorByName(pChildGraphNodeInstance.anchorNodeName);

				if (lAnchorPoint != null) {
					lAnchorPositionX = mParentGraphInst.mFlipHorizontal ? -lAnchorPoint.x : lAnchorPoint.x;
					lAnchorPositionY = lAnchorPoint.y;
					lAnchorRotation = lAnchorPoint.r;
				}

			}

		}

		final var lChildPositionX = mPositionX - mPivotX + lAnchorPositionX;
		final var lChildPositionY = mPositionY - mPivotY + lAnchorPositionY;

		pChildGraphNodeInstance.positionX(lChildPositionX);
		pChildGraphNodeInstance.positionY(lChildPositionY);
		pChildGraphNodeInstance.rotation(lAnchorRotation);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void attachRenderableObjectToSpriteGraphNode(Object pObjectToAttac, SpriteSheetDefinition pSpriteSheetDefinition, SpriteInstance pSpriteInstance) {
		if (pObjectToAttac == null) {
			detachRenderableObjectFromSpriteGraphNode();
			return;

		}

		if (attachedItemInstance != pObjectToAttac) {
			spriteSheetDefinition = pSpriteSheetDefinition;
			spriteInstance = pSpriteInstance;

			attachedItemInstance = pObjectToAttac;

		}

	}

	public void detachRenderableObjectFromSpriteGraphNode() {
		if (spriteInstance != null) {
			spriteInstance.kill();
			spriteInstance = null;
		}

		spriteSheetDefinition = null;
		attachedItemInstance = null;
	}

	public void addChild(SpriteGraphNodeInstance pPart) {
		if (!childNodes.contains(pPart)) {
			childNodes.add(pPart);

		}

	}

	public void reset() {
		// reset child nodes
		final int lNumChildNodes = childNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			childNodes.get(i).reset();

		}

		name = null;
		anchorNodeName = null;

	}

	public void resetSprite() {
		if (spriteInstance != null) {
			spriteInstance.kill();
			spriteInstance = null;
		}

		spriteSheetName = "";
		spriteSheetDefinition = null;

	}

	public void setAngToPoint(float pNewValue) {
		angToPoint = pNewValue;

	}

	public void assignNewSpriteSheetDefinition(SpriteSheetDefinition pSpriteSheetDefintion) {
		if (pSpriteSheetDefintion == null) {
			unassignNewSpriteSheetDefinition();

			return;

		}

		spriteSheetDefinition = pSpriteSheetDefintion;
	}

	public void unassignNewSpriteSheetDefinition() {
		if (spriteInstance != null) {
			if (spriteSheetDefinition != null) {
				spriteSheetDefinition.releaseInistance(spriteInstance);

			}

		}

		spriteSheetDefinition = null;
		spriteInstance = null;

	}

	public void assignNewSprite(String pSpriteName) {
		if (spriteSheetDefinition == null) {
			return;

		}

		final var lSpriteDefinition = spriteSheetDefinition.getSpriteDefinition(pSpriteName);
		if (lSpriteDefinition == null) {
			return;

		}

		spriteSheetName = spriteSheetDefinition.spriteSheetName;
		spriteInstance = spriteSheetDefinition.getSpriteInstance(lSpriteDefinition);

		// TODO: Add sprite listeners, so we can act when an animation ends

	}

	public void unassignSprite() {
		if (spriteSheetDefinition == null) {
			return;

		}

		spriteSheetDefinition.releaseInistance(spriteInstance);
		spriteInstance = null;

	}

}
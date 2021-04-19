package net.lintford.library.core.geometry.spritegraph.instance;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.instances.PooledBaseData;
import net.lintford.library.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintford.library.core.geometry.spritegraph.attachment.ISpriteGraphNodeAttachment;
import net.lintford.library.core.geometry.spritegraph.definition.SpriteGraphNodeDefinition;
import net.lintford.library.core.graphics.sprites.SpriteAnchor;
import net.lintford.library.core.graphics.sprites.SpriteInstance;

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

	/** Each SpriteGraphNodeInstance can have a specific sheetsheet assigned to it. It is from which this sheet that all the anchor information is taken. */
	public transient ISpriteGraphNodeAttachment attachedItemInstance;
	public transient SpriteInstance mSpriteInstance;

	/** The ID of the {@link SpriteGraphAnchorDef} on the parent. */
	public int parentAnchorID;

	/** The name of the {@link SpriteGraphAnchorDef} on the parent. */
	public String anchorNodeName;
	public String currentSpriteActionName;

	/** A list of child parts which are anchored on this {@link SpriteGraphNodeInstance}. */
	public List<SpriteGraphNodeInstance> childNodes;

	public int nodeDepth;
	public int zDepth;

	public int entityGroupID;
	public int attachmentCategory;

	public boolean angToPointEnabled;
	public float angToPoint;
	public float staticRotationOffset;

	private boolean mFlippedHorizontal;
	private boolean mFlippedVertical;
	private float mPositionX;
	private float mPositionY;
	private float mPivotX;
	private float mPivotY;
	private float mRotation;
	public boolean disableTreeUpdatesPosition;
	public boolean disableTreeUpdatesRotation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpriteInstance spriteInstance() {
		return mSpriteInstance;
	}

	public boolean flippedHorizontal() {
		return mFlippedHorizontal;

	}

	public void flippedHorizontal(boolean pNewValue) {
		mFlippedHorizontal = pNewValue;

	}

	public boolean flippedVertical() {
		return mFlippedVertical;

	}

	public void flippedVertical(boolean pNewValue) {
		mFlippedVertical = pNewValue;

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

	public SpriteGraphNodeInstance getNodeNyAttachmentCategory(int pNodeCategory) {
		if (name == null) {
			return null;

		}

		if (attachmentCategory == pNodeCategory) {
			return this;

		}

		final int lNumChildNodes = childNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			final var lSpriteGraphNodeInstance = childNodes.get(i).getNodeNyAttachmentCategory(pNodeCategory);
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
		if (mSpriteInstance == null)
			return null;

		// The anchor points are all stored in the individual SpriteFrames.

		final var lCurrentSpriteFrame = mSpriteInstance.currentSpriteFrame();
		if (lCurrentSpriteFrame == null)
			return null;

		return lCurrentSpriteFrame.getAnchorByName(pAnchorName);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphNodeInstance(final int pPoolUid) {
		super(pPoolUid);

		disableTreeUpdatesRotation = false;
		disableTreeUpdatesPosition = false;
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
		attachmentCategory = pGraphNodeDef.attachmentCategory;

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
		if (mSpriteInstance != null) {

			flippedHorizontal(mParentGraphInst.mFlipHorizontal);
			flippedVertical(mParentGraphInst.mFlipVertical);

			mSpriteInstance.flipHorizontal(flippedHorizontal());
			mSpriteInstance.flipVertical(flippedVertical());

			mSpriteInstance.update(pCore);

			final var lSpriteWidth = mSpriteInstance.width();
			final var lSpriteHeight = mSpriteInstance.height();

			mPivotX = mSpriteInstance.pivotX;
			mPivotY = mSpriteInstance.pivotY;

			final float lRotationRadians = mRotation;

			final float lSpriteHalfWidth = lSpriteWidth / 2f;
			final float lSpriteHalfHeight = lSpriteHeight / 2f;

			final float lRotationAdapted = mSpriteInstance.flipHorizontal() ? -lRotationRadians : lRotationRadians;
			mSpriteInstance.rotateAbs(lRotationAdapted);
			mSpriteInstance.set(mPositionX - lSpriteHalfWidth, mPositionY - lSpriteHalfHeight, lSpriteWidth, lSpriteHeight);

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

		if (mSpriteInstance != null) {
			final var lCurrentSpriteFrame = mSpriteInstance.currentSpriteFrame();
			if (lCurrentSpriteFrame != null) {
				final var lAnchorPoint = lCurrentSpriteFrame.getAnchorByName(pChildGraphNodeInstance.anchorNodeName);

				if (lAnchorPoint != null) {
					lAnchorPositionX = mParentGraphInst.mFlipHorizontal ? -lAnchorPoint.x : lAnchorPoint.x;
					lAnchorPositionY = lAnchorPoint.y;
					lAnchorRotation = lAnchorPoint.r;
				}

			}

		}

		float lAngleInRadians = flippedHorizontal() ? -mRotation : mRotation;

		float lNX = rotatePointX(0, 0, lAngleInRadians, lAnchorPositionX, lAnchorPositionY);
		float lNY = rotatePointY(0, 0, lAngleInRadians, lAnchorPositionX, lAnchorPositionY);

		if (!pChildGraphNodeInstance.disableTreeUpdatesPosition) {
			pChildGraphNodeInstance.positionX(mPositionX + lNX);
			pChildGraphNodeInstance.positionY(mPositionY + lNY);

		}

		if (!pChildGraphNodeInstance.disableTreeUpdatesRotation)
			pChildGraphNodeInstance.rotation((float) Math.toRadians(lAnchorRotation));

	}

	float rotatePointX(float cx, float cy, float angle, float pX, float pY) {
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);

		return cos * (pX - cx) - sin * (pY - cy) + cx;
	}

	float rotatePointY(float cx, float cy, float angle, float pX, float pY) {
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);

		return sin * (pX - cx) + cos * (pY - cy) + cy;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void attachItemToSpriteGraphNode(ISpriteGraphNodeAttachment pObjectToAttac) {
		if (attachedItemInstance != null) {
			detachItemFromSpriteGraphNode();

		}

		attachedItemInstance = pObjectToAttac;

	}

	public void detachItemFromSpriteGraphNode() {
		if (mSpriteInstance != null) {
			mSpriteInstance.kill();
			mSpriteInstance = null;
		}

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
		if (mSpriteInstance != null) {
			mSpriteInstance.kill();
			mSpriteInstance = null;
		}

		mSpriteInstance = null;

	}

	public void setAngToPoint(float pNewValue) {
		angToPoint = pNewValue;

	}

	// --------------------------------------
	// Animation Listeners
	// --------------------------------------

}
package net.lintford.library.core.geometry.spritegraph.instances;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.instances.IndexedPooledBaseData;
import net.lintford.library.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintford.library.core.geometry.spritegraph.definitions.SpriteGraphNodeDefinition;
import net.lintford.library.core.graphics.sprites.SpriteAnchor;
import net.lintford.library.core.graphics.sprites.SpriteInstance;

// ToDo: Attachable Box2d bodies: Some nodes need to interact with the world via the sprite graph nodes
// ToDo: Attachable SpriteInstance: Each node instance should have its own sprite animation for the current spritesheetdefinition
public class SpriteGraphNodeInstance extends IndexedPooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 622930312736268776L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public SpriteGraphInstance mParentGraphInst;

	private transient SpriteGraphAttachmentInstance mSpritegraphAttachmentInstance;
	public transient SpriteInstance mSpriteInstance;

	/** The ID of the {@link SpriteGraphAnchorDef} on the parent. */
	public int parentAnchorID;

	/** The name of the {@link SpriteGraphAnchorDef} on the parent. */
	public String anchorNodeName;
	public String nextAnimationName;
	public String currentNodeSpriteActionName;
	public boolean controlsGraphAnimationListener;

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
	private int mPivotX;
	private int mPivotY;
	private float mRotationInRadians;
	public boolean disableTreeUpdatesPosition;
	public boolean disableTreeUpdatesRotation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpriteGraphAttachmentInstance spritegraphAttachmentInstance() {
		return mSpritegraphAttachmentInstance;
	}

	public void nextAnimationName(String pNextAnimationName) {
		nextAnimationName = pNextAnimationName;
	}

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

	public int pivotX() {
		return mPivotX;

	}

	public void pivotX(int pNewValue) {
		mPivotX = pNewValue;

	}

	public int pivotY() {
		return mPivotY;

	}

	public void pivotY(int pNewValue) {
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
		return mRotationInRadians;
	}

	public void rotationInRadians(float pNewValue) {
		mRotationInRadians = pNewValue;
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
		controlsGraphAnimationListener = pGraphNodeDef.controlsGraphAnimationListener;

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
		controlsGraphAnimationListener = false;
	}

	public void update(LintfordCore pCore, SpriteGraphInstance pParentGraph, SpriteGraphNodeInstance pParentGraphNode) {
		updateSpriteInstance(pCore, pParentGraph, pParentGraphNode);

		if (mSpriteInstance != null) {
			flippedHorizontal(mParentGraphInst.mFlipHorizontal);
			flippedVertical(mParentGraphInst.mFlipVertical);

			mSpriteInstance.flipHorizontal(flippedHorizontal());
			mSpriteInstance.flipVertical(flippedVertical());

			mSpriteInstance.update(pCore);

			final var lSpriteWidth = mSpriteInstance.width();
			final var lSpriteHeight = mSpriteInstance.height();

			mPivotX = mSpriteInstance.flipHorizontal() ? -(int) mSpriteInstance.pivotX - 1 : (int) mSpriteInstance.pivotX;
			mPivotY = (int) mSpriteInstance.pivotY;

			final float lRotationRadians = mRotationInRadians;

			final float lSpriteHalfWidth = (int) (lSpriteWidth / 2f);
			final float lSpriteHalfHeight = (int) (lSpriteHeight / 2f);

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

	private void updateSpriteInstance(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pParentSpriteGraphNode) {
		if (mSpritegraphAttachmentInstance == null || mSpritegraphAttachmentInstance.isInitialized() == false)
			return;

		final var lAttachment = mSpritegraphAttachmentInstance;

		if (lAttachment.spritesheetDefinition() == null) {
			if (lAttachment.resolvedSpritesheetDefinitionName) {
				return;
			}

			if (lAttachment.useDynamicNames) {
				loadNodeSpritesheetDefinitionFromAttachment(pCore, pSpriteGraph, lAttachment);
			} else {
				loadNodeSpritesheetDefinitionFromAttachment(pCore, lAttachment);
			}
			// this causes us to miss one frame of animation
			return;
		}

		// Update animations
		final var lSpritesheetDefinition = lAttachment.spritesheetDefinition();

		if (mSpriteInstance != null) {
			final var lSpriteDef = mSpriteInstance.spriteDefinition();
			if (lSpriteDef.minimumViableRuntime() > 0) {
				if (mSpriteInstance.getTimeAliveInMs() < lSpriteDef.minimumViableRuntime()) {
					return;
				}
			}
		}

		boolean reqUpdate = false;
		String tlTempNextAnimFrameName = null;
		if (currentNodeSpriteActionName == null) {
			if (nextAnimationName != null) {
				tlTempNextAnimFrameName = nextAnimationName;
			} else if (mSpritegraphAttachmentInstance.defaultSpriteName != null) {
				tlTempNextAnimFrameName = mSpritegraphAttachmentInstance.defaultSpriteName;
			}
			reqUpdate = true;
		} else if (nextAnimationName != null && nextAnimationName.equals(currentNodeSpriteActionName) == false) {
			tlTempNextAnimFrameName = nextAnimationName;
			reqUpdate = true;
		}

		//      Maybe still need a method of playing animations based on the parent spritegraph (as opposed to a frame-based solution above)
		//		if (reqUpdate == false && currentNodeSpriteActionName == null && tlTempNextAnimFrameName == null) {
		//			var lFoundSprintInstance = lSpritesheetDefinition.getSpriteInstance(pSpriteGraph.currentAnimation());
		//			if (lFoundSprintInstance != null) {
		//				mSpriteInstance = lFoundSprintInstance;
		//				currentNodeSpriteActionName = pSpriteGraph.currentAnimation();
		//			}
		//		}

		if (reqUpdate && tlTempNextAnimFrameName != null) {
			var lFoundSprintInstance = lSpritesheetDefinition.getSpriteInstance(tlTempNextAnimFrameName);
			if (lFoundSprintInstance != null) {
				mSpriteInstance = lFoundSprintInstance;
				currentNodeSpriteActionName = nextAnimationName;
			} else {
				lFoundSprintInstance = lSpritesheetDefinition.getSpriteInstance(lAttachment.defaultSpriteName);
				if (lFoundSprintInstance != null) {
					mSpriteInstance = lFoundSprintInstance;
					currentNodeSpriteActionName = lAttachment.defaultSpriteName;
				}
			}

			if (lFoundSprintInstance == null) {
				// Sometimes it is okay that no animation is found on a graph node
				nextAnimationName = null;
				currentNodeSpriteActionName = null;
			}

			if (controlsGraphAnimationListener) {
				mSpriteInstance.animatedSpriteListender(pSpriteGraph);
			}
		}
	}

	private void loadNodeSpritesheetDefinitionFromAttachment(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphAttachmentInstance pAttachment) {
		pAttachment.resolvedSpritesheetDefinitionName = true;

		var lSpriteSheetDefinition = pAttachment.spritesheetDefinition();
		if (lSpriteSheetDefinition == null) {
			final var lResolvedSpriteName = "SPRITESHEET_" + pSpriteGraph.mDynamicSpritesheetName + pAttachment.spritesheetDefinitionName;
			final var lResourceManager = pCore.resources();
			lSpriteSheetDefinition = lResourceManager.spriteSheetManager().getSpriteSheet(lResolvedSpriteName, entityGroupID);

			if (lSpriteSheetDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't resolve dynamic SpriteGraphNodeSpritesheetDefinition name '" + lResolvedSpriteName + "'");
				return;
			}

			pAttachment.spritesheetDefinition(lSpriteSheetDefinition);
		}
	}

	private void loadNodeSpritesheetDefinitionFromAttachment(LintfordCore pCore, SpriteGraphAttachmentInstance pAttachment) {
		pAttachment.resolvedSpritesheetDefinitionName = true;

		var lSpriteSheetDefinition = pAttachment.spritesheetDefinition();
		if (lSpriteSheetDefinition == null) {
			final var lResourceManager = pCore.resources();
			lSpriteSheetDefinition = lResourceManager.spriteSheetManager().getSpriteSheet(pAttachment.spritesheetDefinitionName, entityGroupID);

			if (lSpriteSheetDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't resolve static SpriteGraphNodeSpritesheetDefinition name '" + pAttachment.spritesheetDefinitionName + "'");
				return;
			}

			pAttachment.spritesheetDefinition(lSpriteSheetDefinition);
		}
	}

	private void updateChildNodeTransform(LintfordCore pCore, SpriteGraphNodeInstance pChildGraphNodeInstance) {
		int lAnchorPositionX = 0;
		int lAnchorPositionY = 0;
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

		if (!pChildGraphNodeInstance.disableTreeUpdatesPosition) {
			float lAngleInRadians = flippedHorizontal() ? -mRotationInRadians : mRotationInRadians;

			float lNX = rotatePointX(pivotX(), pivotY(), lAngleInRadians, lAnchorPositionX, lAnchorPositionY);
			float lNY = rotatePointY(pivotX(), pivotY(), lAngleInRadians, lAnchorPositionX, lAnchorPositionY);

			pChildGraphNodeInstance.positionX(mPositionX - mPivotX + lNX);
			pChildGraphNodeInstance.positionY(mPositionY - mPivotY + lNY);

		}

		if (!pChildGraphNodeInstance.disableTreeUpdatesRotation)
			pChildGraphNodeInstance.rotationInRadians((float) Math.toRadians(lAnchorRotation) + mRotationInRadians);

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

	public void attachItemToSpriteGraphNode(SpriteGraphAttachmentInstance pAttachmentInstance) {
		if (mSpritegraphAttachmentInstance != null && mSpritegraphAttachmentInstance.isInitialized()) {
			detachItemFromSpriteGraphNode();
		}

		currentNodeSpriteActionName = null;
		nextAnimationName = null;

		mSpritegraphAttachmentInstance = pAttachmentInstance;
	}

	public void detachItemFromSpriteGraphNode() {
		if (mSpriteInstance != null) {
			mSpriteInstance.kill();
			mSpriteInstance = null;
		}

		if (mSpritegraphAttachmentInstance != null) {
			mSpritegraphAttachmentInstance.unload();
			mSpritegraphAttachmentInstance = null;
		}
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
}
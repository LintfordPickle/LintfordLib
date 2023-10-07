package net.lintfordlib.core.geometry.spritegraph.instances;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.instances.OpenPooledBaseData;
import net.lintfordlib.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintfordlib.core.geometry.spritegraph.definitions.SpriteGraphNodeDefinition;
import net.lintfordlib.core.graphics.sprites.SpriteAnchor;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;

// ToDo: Attachable Box2d bodies: Some nodes need to interact with the world via the sprite graph nodes
// ToDo: Attachable SpriteInstance: Each node instance should have its own sprite animation for the current spritesheetdefinition
public class SpriteGraphNodeInstance extends OpenPooledBaseData {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mName;
	private SpriteGraphInstance mParentGraphInst;
	private transient SpriteGraphAttachmentInstance mSpritegraphAttachmentInstance;
	private transient SpriteInstance mSpriteInstance;
	private String mAnchorNodeName;
	private String mNextAnimationName;
	private String mCurrentNodeSpriteActionName;
	private boolean mControlsGraphAnimationListener;
	private List<SpriteGraphNodeInstance> mChildNodes;
	private int mNodeDepth;
	private int mZDepth;
	private int mEntityGroupUid;
	private int mAttachmentCategory;
	private boolean mAngToPointEnabled;
	private float mAngleToPoint;
	private boolean mFlippedHorizontal;
	private boolean mFlippedVertical;
	private float mPositionX;
	private float mPositionY;
	private int mPivotX;
	private int mPivotY;
	private float mRotationInRadians;
	private boolean mDisableTreeUpdatesPosition;
	private boolean mDisableTreeUpdatesRotation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setAngToPoint(float newValue) {
		mAngleToPoint = newValue;
	}

	public float setAngleToPoint() {
		return mAngleToPoint;
	}

	public void disableTreeUpdatesPositions(boolean pNewValue) {
		mDisableTreeUpdatesPosition = pNewValue;
	}

	public boolean disableTreeUpdatesPositions() {
		return mDisableTreeUpdatesPosition;
	}

	public void disableTreeUpdatesRotations(boolean pNewValue) {
		mDisableTreeUpdatesRotation = pNewValue;
	}

	public boolean disableTreeUpdatesRotations() {
		return mDisableTreeUpdatesRotation;
	}

	public int zDepth() {
		return mZDepth;
	}

	public void zDepth(int zDepth) {
		mZDepth = zDepth;
	}

	public boolean angToPointEnabled() {
		return mAngToPointEnabled;
	}

	public void angToPointEnabled(boolean newValue) {
		mAngToPointEnabled = newValue;
	}

	public SpriteGraphAttachmentInstance spritegraphAttachmentInstance() {
		return mSpritegraphAttachmentInstance;
	}

	public void nextAnimationName(String pNextAnimationName) {
		mNextAnimationName = pNextAnimationName;
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
		if (mName == null) {
			return null;

		}

		if (mName.equals(pNodeName)) {
			return this;

		}

		final int lNumChildNodes = mChildNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			final var lSpriteGraphNodeInstance = mChildNodes.get(i).getNodeNyNodeName(pNodeName);
			if (lSpriteGraphNodeInstance != null) {
				return lSpriteGraphNodeInstance;

			}

		}

		return null;

	}

	public SpriteGraphNodeInstance getNodeNyAttachmentCategory(int pNodeCategory) {
		if (mName == null) {
			return null;
		}

		if (mAttachmentCategory == pNodeCategory) {
			return this;
		}

		final int lNumChildNodes = mChildNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			final var lSpriteGraphNodeInstance = mChildNodes.get(i).getNodeNyAttachmentCategory(pNodeCategory);
			if (lSpriteGraphNodeInstance != null) {
				return lSpriteGraphNodeInstance;
			}
		}

		return null;
	}

	public SpriteGraphNodeInstance getNodeNyNodeSpriteFrameName(String pSpriteFrameNodeName) {
		if (mAnchorNodeName != null && mAnchorNodeName.equals(pSpriteFrameNodeName)) {
			return this;

		}

		final int lNumChildNodes = mChildNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			final var lSpriteGraphNodeInstance = mChildNodes.get(i).getNodeNyNodeSpriteFrameName(pSpriteFrameNodeName);
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

		mDisableTreeUpdatesRotation = false;
		mDisableTreeUpdatesPosition = false;
		mChildNodes = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void init(List<SpriteGraphNodeInstance> pFlat, ISpriteGraphPool pSpriteGraphPool, SpriteGraphInstance pSpriteGraphInst, SpriteGraphNodeDefinition pGraphNodeDef, int entityGroupUid, int pNodeDepth) {
		mParentGraphInst = pSpriteGraphInst;
		mName = pGraphNodeDef.name();
		mAnchorNodeName = pGraphNodeDef.anchorNodeName();
		mEntityGroupUid = entityGroupUid;
		mZDepth = pGraphNodeDef.zDepth();
		mAttachmentCategory = pGraphNodeDef.attachmentCategory();
		mControlsGraphAnimationListener = pGraphNodeDef.controlsGraphAnimationListener();
		mNodeDepth = pNodeDepth;

		final int lChildNodeCount = pGraphNodeDef.childParts().size();
		for (int i = 0; i < lChildNodeCount; i++) {
			final var lSpriteGraphNodeDefinition = pGraphNodeDef.childParts().get(i);
			final var lNewNode = pSpriteGraphPool.getSpriteGraphNodeInstance();

			lNewNode.init(pFlat, pSpriteGraphPool, pSpriteGraphInst, lSpriteGraphNodeDefinition, entityGroupUid, mNodeDepth + 1);

			pFlat.add(lNewNode);

			mChildNodes.add(lNewNode);
		}
	}

	public void reset(ISpriteGraphPool pSpriteGraphPool) {
		mControlsGraphAnimationListener = false;
	}

	public void update(LintfordCore core, SpriteGraphInstance parentGraph, SpriteGraphNodeInstance parentGraphNode) {
		updateSpriteInstance(core, parentGraph, parentGraphNode);

		if (mSpriteInstance != null) {
			flippedHorizontal(mParentGraphInst.flipHorizontal);
			flippedVertical(mParentGraphInst.flipVertical);

			mSpriteInstance.flipHorizontal(flippedHorizontal());
			mSpriteInstance.flipVertical(flippedVertical());

			mSpriteInstance.update(core);

			final var lSpriteWidth = mSpriteInstance.width();
			final var lSpriteHeight = mSpriteInstance.height();

			mPivotX = mSpriteInstance.flipHorizontal() ? -(int) mSpriteInstance.pivotX() - 1 : (int) mSpriteInstance.pivotX();
			mPivotY = (int) mSpriteInstance.pivotY();

			final float lRotationRadians = mRotationInRadians;

			final float lSpriteHalfWidth = (int) (lSpriteWidth / 2f);
			final float lSpriteHalfHeight = (int) (lSpriteHeight / 2f);

			final float lRotationAdapted = mSpriteInstance.flipHorizontal() ? -lRotationRadians : lRotationRadians;
			mSpriteInstance.rotationInRadians(lRotationAdapted);
			mSpriteInstance.set(mPositionX - lSpriteHalfWidth, mPositionY - lSpriteHalfHeight, lSpriteWidth, lSpriteHeight);
		}

		final int lChildrenCount = mChildNodes.size();
		for (int i = 0; i < lChildrenCount; i++) {
			final var lChildSpriteGraphNode = mChildNodes.get(i);
			updateChildNodeTransform(core, lChildSpriteGraphNode);

			mChildNodes.get(i).update(core, parentGraph, this);
		}
	}

	private void updateSpriteInstance(LintfordCore core, SpriteGraphInstance spriteGraph, SpriteGraphNodeInstance parentSpriteGraphNode) {
		if (mSpritegraphAttachmentInstance == null || mSpritegraphAttachmentInstance.isInitialized() == false)
			return;

		final var lAttachment = mSpritegraphAttachmentInstance;

		if (lAttachment.spritesheetDefinition() == null) {
			if (lAttachment.resolvedSpritesheetDefinitionName()) {
				return;
			}

			if (lAttachment.useDynamicNames()) {
				loadNodeSpritesheetDefinitionFromAttachment(core, spriteGraph, lAttachment);
			} else {
				loadNodeSpritesheetDefinitionFromAttachment(core, lAttachment);
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
		if (mCurrentNodeSpriteActionName == null) {
			if (mNextAnimationName != null) {
				tlTempNextAnimFrameName = mNextAnimationName;
			}

			else if (mSpritegraphAttachmentInstance.defaultAnimationName() != null) {
				tlTempNextAnimFrameName = mSpritegraphAttachmentInstance.defaultAnimationName();
			}

			else if (mSpritegraphAttachmentInstance.defaultSpriteName() != null) {
				tlTempNextAnimFrameName = mSpritegraphAttachmentInstance.defaultSpriteName();
			}
			reqUpdate = true;
		} else if (mNextAnimationName != null && mNextAnimationName.equals(mCurrentNodeSpriteActionName) == false) {
			tlTempNextAnimFrameName = mNextAnimationName;
			reqUpdate = true;
		}

		if (reqUpdate && tlTempNextAnimFrameName != null) {
			var lFoundSprintInstance = lSpritesheetDefinition.getSpriteInstance(tlTempNextAnimFrameName);
			if (lFoundSprintInstance != null) {
				mSpriteInstance = lFoundSprintInstance;
				mCurrentNodeSpriteActionName = mNextAnimationName;
			} else {
				lFoundSprintInstance = lSpritesheetDefinition.getSpriteInstance(lAttachment.defaultSpriteName());
				if (lFoundSprintInstance != null) {
					mSpriteInstance = lFoundSprintInstance;
					mCurrentNodeSpriteActionName = lAttachment.defaultSpriteName();
				}
			}

			if (lFoundSprintInstance == null) {
				// Sometimes it is okay that no animation is found on a graph node
				mNextAnimationName = null;
				mCurrentNodeSpriteActionName = null;
			}

			if (mControlsGraphAnimationListener) {
				mSpriteInstance.animatedSpriteListender(spriteGraph);
			}
		}
	}

	private void loadNodeSpritesheetDefinitionFromAttachment(LintfordCore core, SpriteGraphInstance spriteGraph, SpriteGraphAttachmentInstance attachment) {
		attachment.resolvedSpritesheetDefinitionName(true);

		var lSpriteSheetDefinition = attachment.spritesheetDefinition();
		if (lSpriteSheetDefinition == null) {
			final var lResolvedSpriteName = "SPRITESHEET_" + spriteGraph.dynamicSpritesheetName() + attachment.spritesheetDefinitionName();
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Resolving dynamic sprite name to : " + lResolvedSpriteName);

			final var lResourceManager = core.resources();
			lSpriteSheetDefinition = lResourceManager.spriteSheetManager().getSpriteSheet(lResolvedSpriteName, mEntityGroupUid);

			if (lSpriteSheetDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't resolve dynamic SpriteGraphNodeSpritesheetDefinition name '" + lResolvedSpriteName + "'");
				return;
			}

			attachment.spritesheetDefinition(lSpriteSheetDefinition);
		}
	}

	private void loadNodeSpritesheetDefinitionFromAttachment(LintfordCore core, SpriteGraphAttachmentInstance attachment) {
		attachment.resolvedSpritesheetDefinitionName(true);

		var lSpriteSheetDefinition = attachment.spritesheetDefinition();
		if (lSpriteSheetDefinition == null) {
			final var lResourceManager = core.resources();
			lSpriteSheetDefinition = lResourceManager.spriteSheetManager().getSpriteSheet(attachment.spritesheetDefinitionName(), mEntityGroupUid);

			if (lSpriteSheetDefinition == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't resolve static SpriteGraphNodeSpritesheetDefinition name '" + attachment.spritesheetDefinitionName() + "'");
				return;
			}

			attachment.spritesheetDefinition(lSpriteSheetDefinition);
		}
	}

	private void updateChildNodeTransform(LintfordCore core, SpriteGraphNodeInstance childGraphNodeInstance) {
		int lAnchorPositionX = 0;
		int lAnchorPositionY = 0;
		float lAnchorRotation = 0.0f;

		if (mSpriteInstance != null) {
			final var lCurrentSpriteFrame = mSpriteInstance.currentSpriteFrame();
			if (lCurrentSpriteFrame != null) {
				final var lAnchorPoint = lCurrentSpriteFrame.getAnchorByName(childGraphNodeInstance.mAnchorNodeName);

				if (lAnchorPoint != null) {
					lAnchorPositionX = mParentGraphInst.flipHorizontal ? -lAnchorPoint.localX() : lAnchorPoint.localX();
					lAnchorPositionY = lAnchorPoint.localY();
					lAnchorRotation = lAnchorPoint.rotation();
				}
			}
		}

		if (!childGraphNodeInstance.mDisableTreeUpdatesPosition) {
			float lAngleInRadians = flippedHorizontal() ? -mRotationInRadians : mRotationInRadians;

			float lNX = rotatePointX(pivotX(), pivotY(), lAngleInRadians, lAnchorPositionX, lAnchorPositionY);
			float lNY = rotatePointY(pivotX(), pivotY(), lAngleInRadians, lAnchorPositionX, lAnchorPositionY);

			childGraphNodeInstance.positionX(mPositionX - mPivotX + lNX);
			childGraphNodeInstance.positionY(mPositionY - mPivotY + lNY);
		}

		if (!childGraphNodeInstance.mDisableTreeUpdatesRotation)
			childGraphNodeInstance.rotationInRadians((float) Math.toRadians(lAnchorRotation) + mRotationInRadians);
	}

	private float rotatePointX(float cx, float cy, float angle, float x, float y) {
		final float cos = (float) Math.cos(angle);
		final float sin = (float) Math.sin(angle);

		return cos * (x - cx) - sin * (y - cy) + cx;
	}

	private float rotatePointY(float cx, float cy, float angle, float x, float y) {
		final float cos = (float) Math.cos(angle);
		final float sin = (float) Math.sin(angle);

		return sin * (x - cx) + cos * (y - cy) + cy;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void attachItemToSpriteGraphNode(SpriteGraphAttachmentInstance attachmentInstance) {
		if (mSpritegraphAttachmentInstance != null && mSpritegraphAttachmentInstance.isInitialized()) {
			detachItemFromSpriteGraphNode();
		}

		mCurrentNodeSpriteActionName = null;
		mNextAnimationName = null;

		mSpritegraphAttachmentInstance = attachmentInstance;
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

	public void addChild(SpriteGraphNodeInstance spriteGraphNodeInstance) {
		if (!mChildNodes.contains(spriteGraphNodeInstance)) {
			mChildNodes.add(spriteGraphNodeInstance);
		}
	}

	public void reset() {
		// reset child nodes
		final int lNumChildNodes = mChildNodes.size();
		for (int i = 0; i < lNumChildNodes; i++) {
			mChildNodes.get(i).reset();

		}

		mName = null;
		mAnchorNodeName = null;

	}

	public void resetSprite() {
		if (mSpriteInstance != null) {
			mSpriteInstance.kill();
			mSpriteInstance = null;
		}

		mSpriteInstance = null;

	}
}
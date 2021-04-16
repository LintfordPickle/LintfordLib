package net.lintford.library.core.geometry.spritegraph.instance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.instances.PooledBaseData;
import net.lintford.library.core.geometry.spritegraph.AnimatedSpriteGraphListener;
import net.lintford.library.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphManager;
import net.lintford.library.core.geometry.spritegraph.attachment.ISpriteGraphNodeAttachment;
import net.lintford.library.core.geometry.spritegraph.definition.SpriteGraphDefinition;
import net.lintford.library.core.graphics.sprites.AnimatedSpriteListener;
import net.lintford.library.core.graphics.sprites.SpriteInstance;

/**
 * Represents a geometric instance of a SpriteGraphDef in the world, complete with information about transforms and part types (if for example multiple types are available per part).
 */
public class SpriteGraphInstance extends PooledBaseData implements AnimatedSpriteListener {

	public static final Comparator<SpriteGraphNodeInstance> SpriteGraphNodeInstanceZComparator = new SpriteGraphNodeInstanceZComparator();

	private static class SpriteGraphNodeInstanceZComparator implements Comparator<SpriteGraphNodeInstance> {

		@Override
		public int compare(SpriteGraphNodeInstance o1, SpriteGraphNodeInstance o2) {
			return o1.zDepth - o2.zDepth;

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5557875926084955431L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public SpriteGraphNodeInstance rootNode;
	public String spriteGraphName;

	public String mCurrentAction;

	public boolean mFlipHorizontal;
	public boolean mFlipVertical;

	private boolean mOrdered;
	private transient List<SpriteGraphNodeInstance> mFlatNodes;

	private transient AnimatedSpriteGraphListener mAnimatedSpriteGraphListener;

	public float positionX;
	public float positionY;
	public float rotationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String currentAnimation() {
		return mCurrentAction;
	}

	public void currentAnimation(String pCurrentAnimation) {
		mCurrentAction = pCurrentAnimation;
	}

	public SpriteGraphNodeInstance getNodeByName(String pNodeName) {
		return rootNode.getNodeNyNodeName(pNodeName);
	}

	public SpriteGraphNodeInstance getNodeByAttachmentCategroy(int pAttachmentCategory) {
		return rootNode.getNodeNyAttachmentCategory(pAttachmentCategory);
	}

	public SpriteGraphNodeInstance getNodeBySpriteFrameName(String pSpriteFrameName) {
		return rootNode.getNodeNyNodeSpriteFrameName(pSpriteFrameName);
	}

	public List<SpriteGraphNodeInstance> flatList() {
		return mFlatNodes;
	}

	/** Returns a list of all nodes in this graph, ordered by the ascending Z depth. */
	public List<SpriteGraphNodeInstance> getZOrderedFlatList(Comparator<SpriteGraphNodeInstance> pComparator) {
		if (!mOrdered) {
			mFlatNodes.sort(pComparator);
			mOrdered = true;

		}
		return mFlatNodes;
	}

	public boolean isAssigned() {
		return !(spriteGraphName == null && spriteGraphName.length() == 0);

	}

	public boolean isAnimatedSpriteGraphListenerAssigned() {
		return mAnimatedSpriteGraphListener != null;
	}

	public void animatedSpriteGraphListener(AnimatedSpriteGraphListener pAnimatedSpriteGraphListener) {
		mAnimatedSpriteGraphListener = pAnimatedSpriteGraphListener;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphInstance(int pPoolUid) {
		super(pPoolUid);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void init(SpriteGraphDefinition pSpriteGraphDefinition, ISpriteGraphPool pSpriteGraphPool, int pEntityGroupUid) {
		spriteGraphName = pSpriteGraphDefinition.name;

		mFlatNodes = new ArrayList<>();

		final var lNewNodeInst = pSpriteGraphPool.getSpriteGraphNodeInstance();
		lNewNodeInst.init(mFlatNodes, pSpriteGraphPool, this, pSpriteGraphDefinition.rootNode, pEntityGroupUid, 0);

		rootNode = lNewNodeInst;
		mOrdered = false;

		mFlatNodes.add(rootNode);

	}

	public void resetAndReturn(ISpriteGraphPool pSpriteGraphManager) {
		if (mFlatNodes != null) {
			mFlatNodes.clear();

		}

		// TODO: Need to properly return the SpriteGraphInstance and all SpriteGraphNodeInstances to the manager.

	}

	public void update(LintfordCore pCore) {
		updateRootNodeTransform();

		rootNode.update(pCore, this, null);

	}

	private void updateRootNodeTransform() {
		rootNode.positionX(positionX);
		rootNode.positionY(positionY);

		// Rotation
		// rootNode.rotation(rotationInRadians);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void attachItemToNode(ISpriteGraphNodeAttachment pNodeAttachment) {
		if (pNodeAttachment == null)
			return;

		final var lAttachmentCategory = pNodeAttachment.attachmentCategory();
		final var lSpriteGraphNode = getNodeByAttachmentCategroy(lAttachmentCategory);
		if (lSpriteGraphNode != null) {
			lSpriteGraphNode.attachItemToSpriteGraphNode(pNodeAttachment);

		}

	}

	public void detachItemFromNode(String pSpriteGraphNodeName) {
		final var lSpriteGraphNodeInstance = getNodeByName(pSpriteGraphNodeName);
		if (lSpriteGraphNodeInstance != null) {
			lSpriteGraphNodeInstance.detachItemFromSpriteGraphNode();

		}

	}

	public boolean setNodeAngleToPoint(String pNodeName, float pAngle) {
		SpriteGraphNodeInstance lNode = rootNode.getNodeNyNodeName(pNodeName);

		if (lNode != null) {
			lNode.angToPointEnabled = true;
			lNode.setAngToPoint(pAngle);

			return true;
		}

		return false;

	}

	public boolean setNodeAngleToPointOff(String pNodeName) {
		SpriteGraphNodeInstance lNode = rootNode.getNodeNyNodeName(pNodeName);

		if (lNode != null) {
			lNode.angToPointEnabled = false;
			lNode.setAngToPoint(0);

			return true;
		}

		return false;

	}

	public void reset(SpriteGraphManager pSpriteGraphManager) {
		rootNode.reset();
		spriteGraphName = null;
		mOrdered = false;

	}

	@Override
	public void onStarted(SpriteInstance pSender) {
		if (mAnimatedSpriteGraphListener != null) {
			mAnimatedSpriteGraphListener.onSpriteAnimationStarted(this, pSender.spriteDefinition());

		}

	}

	@Override
	public void onLooped(SpriteInstance pSender) {
		if (mAnimatedSpriteGraphListener != null) {
			mAnimatedSpriteGraphListener.onSpriteAnimationLooped(this, pSender.spriteDefinition());

		}

	}

	@Override
	public void onStopped(SpriteInstance pSender) {
		if (mAnimatedSpriteGraphListener != null) {
			mAnimatedSpriteGraphListener.onSpriteAnimationStopped(this, pSender.spriteDefinition());

		}

	}

}

package net.lintford.library.core.geometry.spritegraph.instances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.instances.IndexedPooledBaseData;
import net.lintford.library.core.geometry.spritegraph.AnimatedSpriteGraphListener;
import net.lintford.library.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphManager;
import net.lintford.library.core.geometry.spritegraph.definitions.SpriteGraphDefinition;
import net.lintford.library.core.graphics.sprites.AnimatedSpriteListener;
import net.lintford.library.core.graphics.sprites.SpriteInstance;

/**
 * Represents a geometric instance of a SpriteGraphDef in the world, complete with information about transforms and part types (if for example multiple types are available per part).
 */
public class SpriteGraphInstance extends IndexedPooledBaseData implements AnimatedSpriteListener {

	public static final Comparator<SpriteGraphNodeInstance> SpriteGraphNodeInstanceZComparator = new SpriteGraphNodeInstanceZComparator();

	private static class SpriteGraphNodeInstanceZComparator implements Comparator<SpriteGraphNodeInstance> {

		@Override
		public int compare(SpriteGraphNodeInstance o1, SpriteGraphNodeInstance o2) {
			return o1.zDepth() - o2.zDepth();
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5557875926084955431L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient List<SpriteGraphNodeInstance> mFlatNodes;
	private transient AnimatedSpriteGraphListener mAnimatedSpriteGraphListener;
	private SpriteGraphNodeInstance mRootNode;
	private String mSpriteGraphName;
	private String mCurrentlyPlayingAction;
	private String mDynamicSpritesheetName;
	private boolean mFlipHorizontal;
	private boolean mFlipVertical;
	private boolean mOrdered;
	private float mPositionX;
	private float mPositionY;
	private float mRtationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float rotationInRadians() {
		return mRtationInRadians;
	}

	public void rotationInRadians(float rotationInRadians) {
		mRtationInRadians = rotationInRadians;
	}

	public String currentlyPlayingAction() {
		return mCurrentlyPlayingAction;
	}

	public String dynamicSpritesheetName() {
		return mDynamicSpritesheetName;
	}

	public void dynamicSpritesheetName(String newDynamicSpritesheetName) {
		mDynamicSpritesheetName = newDynamicSpritesheetName;
	}

	public boolean flipHorizontal() {
		return mFlipHorizontal;
	}

	public void flipHorizontal(boolean flipHorizontal) {
		mFlipHorizontal = flipHorizontal;
	}

	public boolean flipVerticle() {
		return mFlipVertical;
	}

	public void flipVerticle(boolean flipVertical) {
		mFlipVertical = flipVertical;
	}

	public float positionX() {
		return mPositionX;
	}

	public void positionX(float positionX) {
		mPositionX = positionX;
	}

	public float positionY() {
		return mPositionY;
	}

	public void positionY(float positionY) {
		mPositionY = positionY;
	}

	public SpriteGraphNodeInstance rootNode() {
		return mRootNode;
	}

	public String currentAnimation() {
		return mCurrentlyPlayingAction;
	}

	public void currentAnimation(String currentAnimation) {
		mCurrentlyPlayingAction = currentAnimation;
	}

	public SpriteGraphNodeInstance getNodeByName(String nodeName) {
		return mRootNode.getNodeNyNodeName(nodeName);
	}

	public SpriteGraphNodeInstance getNodeByAttachmentCategory(int attachmentCategory) {
		return mRootNode.getNodeNyAttachmentCategory(attachmentCategory);
	}

	public SpriteGraphNodeInstance getNodeBySpriteFrameName(String spriteFrameName) {
		return mRootNode.getNodeNyNodeSpriteFrameName(spriteFrameName);
	}

	public List<SpriteGraphNodeInstance> flatList() {
		return mFlatNodes;
	}

	/** Returns a list of all nodes in this graph, ordered by the ascending Z depth. */
	public List<SpriteGraphNodeInstance> getZOrderedFlatList(Comparator<SpriteGraphNodeInstance> comparator) {
		if (!mOrdered) {
			mFlatNodes.sort(comparator);
			mOrdered = true;
		}
		return mFlatNodes;
	}

	public boolean isAssigned() {
		return !(mSpriteGraphName == null && mSpriteGraphName.length() == 0);

	}

	public boolean isAnimatedSpriteGraphListenerAssigned() {
		return mAnimatedSpriteGraphListener != null;
	}

	public void animatedSpriteGraphListener(AnimatedSpriteGraphListener animatedSpriteGraphListener) {
		mAnimatedSpriteGraphListener = animatedSpriteGraphListener;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphInstance(int poolUid) {
		super(poolUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void init(SpriteGraphDefinition spriteGraphDefinition, ISpriteGraphPool spriteGraphPool, int entityGroupUid) {
		mSpriteGraphName = spriteGraphDefinition.name;

		mFlatNodes = new ArrayList<>();

		final var lNewNodeInst = spriteGraphPool.getSpriteGraphNodeInstance();
		lNewNodeInst.init(mFlatNodes, spriteGraphPool, this, spriteGraphDefinition.rootNode(), entityGroupUid, 0);

		mRootNode = lNewNodeInst;
		mOrdered = false;

		mFlatNodes.add(mRootNode);
	}

	public void resetAndReturn(ISpriteGraphPool spriteGraphManager) {
		if (mFlatNodes != null) {
			mFlatNodes.clear();
		}

		// TODO: Need to properly return the SpriteGraphInstance and all SpriteGraphNodeInstances to the manager.

	}

	public void update(LintfordCore core) {
		updateRootNodeTransform();

		mRootNode.update(core, this, null);
	}

	public void updateRootNodeTransform() {
		mRootNode.positionX(mPositionX);
		mRootNode.positionY(mPositionY);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void attachItemToNode(SpriteGraphAttachmentInstance spritegraphAttachmentInstance) {
		if (spritegraphAttachmentInstance == null) {
			return;
		}

		final var lAttachmentCategory = spritegraphAttachmentInstance.attachmentCategory();
		final var lSpriteGraphNode = getNodeByAttachmentCategory(lAttachmentCategory);

		if (lSpriteGraphNode != null) {
			lSpriteGraphNode.attachItemToSpriteGraphNode(spritegraphAttachmentInstance);
		}
	}

	public void detachItemFromNode(String spriteGraphNodeName) {
		final var lSpriteGraphNodeInstance = getNodeByName(spriteGraphNodeName);
		if (lSpriteGraphNodeInstance != null) {
			lSpriteGraphNodeInstance.detachItemFromSpriteGraphNode();
		}
	}

	public boolean setNodeAngleToPoint(String nodeName, float angle) {
		final var lNode = mRootNode.getNodeNyNodeName(nodeName);

		if (lNode != null) {
			lNode.angToPointEnabled(true);
			lNode.setAngToPoint(angle);

			return true;
		}

		return false;
	}

	public boolean setNodeAngleToPointOff(String nodeName) {
		final var lNode = mRootNode.getNodeNyNodeName(nodeName);

		if (lNode != null) {
			lNode.angToPointEnabled(false);
			lNode.setAngToPoint(0);

			return true;
		}

		return false;
	}

	public void reset(SpriteGraphManager spriteGraphManager) {
		mRootNode.reset();
		mSpriteGraphName = null;
		mOrdered = false;
	}

	public void detachAllAttachments() {
		mFlatNodes.forEach(nodeInstance -> {
			nodeInstance.detachItemFromSpriteGraphNode();
		});
	}

	// --------------------------------------
	// Animation Listeners
	// --------------------------------------

	@Override
	public void onStarted(SpriteInstance spriteInstance) {
		if (mAnimatedSpriteGraphListener != null) {
			mAnimatedSpriteGraphListener.onSpriteAnimationStarted(this, spriteInstance.spriteDefinition());
		}
	}

	@Override
	public void onLooped(SpriteInstance spriteInstance) {
		if (mAnimatedSpriteGraphListener != null) {
			mAnimatedSpriteGraphListener.onSpriteAnimationLooped(this, spriteInstance.spriteDefinition());
		}
	}

	@Override
	public void onStopped(SpriteInstance spriteInstance) {
		if (mAnimatedSpriteGraphListener != null) {
			mAnimatedSpriteGraphListener.onSpriteAnimationStopped(this, spriteInstance.spriteDefinition());
		}
	}
}

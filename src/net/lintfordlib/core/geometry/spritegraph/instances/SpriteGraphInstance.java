package net.lintfordlib.core.geometry.spritegraph.instances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.entities.instances.OpenPooledBaseData;
import net.lintfordlib.core.geometry.spritegraph.AnimatedSpriteGraphListener;
import net.lintfordlib.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintfordlib.core.geometry.spritegraph.SpriteGraphManager;
import net.lintfordlib.core.geometry.spritegraph.definitions.SpriteGraphDefinition;
import net.lintfordlib.core.graphics.sprites.AnimatedSpriteListener;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;

/**
 * Represents a geometric instance of a SpriteGraphDef in the world, complete
 * with information about transforms and part types (if for example multiple
 * types are available per part).
 */
public class SpriteGraphInstance extends OpenPooledBaseData implements AnimatedSpriteListener {

	public static final Comparator<SpriteGraphNodeInstance> SpriteGraphNodeInstanceZComparator = new SpriteGraphNodeInstanceZComparator();

	private static class SpriteGraphNodeInstanceZComparator implements Comparator<SpriteGraphNodeInstance> {

		@Override
		public int compare(SpriteGraphNodeInstance o1, SpriteGraphNodeInstance o2) {
			return o1.zDepth() - o2.zDepth();
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient List<SpriteGraphNodeInstance> mFlatNodes;
	private transient AnimatedSpriteGraphListener mAnimatedSpriteGraphListener;
	private SpriteGraphNodeInstance mRootNode;
	private String mSpriteGraphName;
	private String mCurrentlyPlayingAction;
	private String mDynamicSpritesheetName;
	private boolean mOrdered;
	public boolean flipHorizontal;
	public boolean flipVertical;

	public float x;
	public float y;
	public float rot;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String currentlyPlayingAction() {
		return mCurrentlyPlayingAction;
	}

	public String dynamicSpritesheetName() {
		return mDynamicSpritesheetName;
	}

	public void dynamicSpritesheetName(String newDynamicSpritesheetName) {
		mDynamicSpritesheetName = newDynamicSpritesheetName;
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

	/**
	 * Returns a list of all nodes in this graph, ordered by the ascending Z depth.
	 */
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

		// TODO: Need to properly return the SpriteGraphInstance and all
		// SpriteGraphNodeInstances to the manager.

	}

	public void update(LintfordCore core) {
		updateRootNodeTransform();

		mRootNode.update(core, this, null);
	}

	public void updateRootNodeTransform() {
		mRootNode.positionX(x);
		mRootNode.positionY(y);
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

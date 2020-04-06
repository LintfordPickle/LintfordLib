package net.lintford.library.core.geometry.spritegraph.instance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.PooledBaseData;
import net.lintford.library.core.geometry.spritegraph.ISpriteGraphPool;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphManager;
import net.lintford.library.core.geometry.spritegraph.definition.SpriteGraphDefinition;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

/**
 * Represents a geometric instance of a SpriteGraphDef in the world, complete with information about transforms and part types (if for example multiple types are available per part).
 */
public class SpriteGraphInstance extends PooledBaseData {

	public class SpriteGraphNodeInstanceZComparator implements Comparator<SpriteGraphNodeInstance> {

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

	private SpriteGraphNodeInstanceZComparator mSpriteGraphNodeInstanceZComparator = new SpriteGraphNodeInstanceZComparator();
	public SpriteGraphNodeInstance rootNode;
	public String spriteGraphName;
	public boolean mFlipHorizontal;
	public boolean mFlipVertical;

	private boolean mOrdered;
	public transient List<SpriteGraphNodeInstance> mFlatNodes;

	public float positionX;
	public float positionY;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpriteGraphNodeInstance getNodeByName(String pNodeName) {
		return rootNode.getNodeNyNodeName(pNodeName);
	}

	public SpriteGraphNodeInstance getNodeBySpriteFrameName(String pSpriteFrameName) {
		return rootNode.getNodeNyNodeSpriteFrameName(pSpriteFrameName);
	}

	/** Returns a list of all nodes in this graph, ordered by the ascending Z depth. */
	public List<SpriteGraphNodeInstance> getZOrderedFlatList() {
		if (!mOrdered) {
			mFlatNodes.sort(mSpriteGraphNodeInstanceZComparator);
			mOrdered = true;

		}
		return mFlatNodes;
	}

	@Override
	public boolean isAssigned() {
		return !(spriteGraphName == null && spriteGraphName.length() == 0);

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

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void attachedObjectToNode(String pSpriteGraphNodeName, Object pObjectToAttac, SpriteSheetDefinition pSpriteSheetDefinition, SpriteInstance pSpriteInstance) {
		final var lSpriteGraphWeaponNode = getNodeByName(pSpriteGraphNodeName);
		if (lSpriteGraphWeaponNode != null) {
			lSpriteGraphWeaponNode.attachRenderableObjectToSpriteGraphNode(pObjectToAttac, pSpriteSheetDefinition, pSpriteInstance);

		}

	}

	public void detachObjectFromNode(String pSpriteGraphNodeName) {
		final var lSpriteGraphWeaponNode = getNodeByName(pSpriteGraphNodeName);
		if (lSpriteGraphWeaponNode != null) {
			lSpriteGraphWeaponNode.detachRenderableObjectFromSpriteGraphNode();

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

}

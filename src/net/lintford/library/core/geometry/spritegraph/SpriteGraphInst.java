package net.lintford.library.core.geometry.spritegraph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.LintfordCore;

/**
 * Represents a geometric instance of a SpriteGraphDef in the world, complete with information about transforms and part types (if for example multiple types are available per part).
 */
public class SpriteGraphInst implements Serializable {

	private static final long serialVersionUID = -5557875926084955431L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public SpriteGraphNodeListener mCallbackSubscriber;
	public SpriteGraphManager mSpriteGraphManager;
	public SpriteGraphNodeInst rootNode;
	public String spriteGraphName;
	public String objectState;
	public boolean mFlipHorizontal;
	public boolean mFlipVertical;
	public float positionX;
	public float positionY;

	/**
	 * There are two kinds of anchors which can be used when constructing a SpriteGraph: the first are the anchors within each SpriteFrame, which allow a per-frame update of nodes based on Sprite animations. The second
	 * kind of anchors are placed within the nodes themselves. These are created when building a SpriteGraph from a Lindenmayer System.
	 */
	public boolean useSpriteAnchors;
	public boolean useSpriteFrameReferences;

	public Map<String, String> currentActions;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isFree() {
		return !(spriteGraphName != null && spriteGraphName.length() > 0);
	}

	public SpriteGraphNodeInst getNode(String pName) {
		return rootNode.getNode(pName);
	}

	public void addEventListener(SpriteGraphNodeListener pListener) {
		mCallbackSubscriber = pListener;

	}

	public void removeEventListener() {
		mCallbackSubscriber = null;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphInst() {
		currentActions = new HashMap<>();

	}

	public SpriteGraphInst(SpriteGraphDef pSpriteGraphDef) {
		this();

		spriteGraphName = pSpriteGraphDef.name;
		useSpriteAnchors = pSpriteGraphDef.updateAnimSpritePositions;
		useSpriteFrameReferences = pSpriteGraphDef.useSpriteFrameReferences;

		// Create the SpriteGraph tree using the definition
		rootNode = new SpriteGraphNodeInst(this, pSpriteGraphDef.rootNode);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	/**
	 * Updates the {@link SpriteGraphInst} and all its {@link SpriteGraphNodeInst} child parts, updating their positions and vertices.
	 */
	public void update(LintfordCore pCore) {
		rootNode.setPosition(positionX, positionY);
		rootNode.update(pCore, this, null);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean setNodeAction(String pNodeName, String pActionKeyName, String pActionTagName) {
		SpriteGraphNodeInst lNode = rootNode.getNode(pNodeName);

		if (lNode != null) {
			lNode.setActionState(pActionKeyName, pActionTagName);
			currentActions.put(pActionKeyName, pNodeName);

			return true;
		}

		return false;

	}

	public void stopAction(String pActionKeyName) {
		if (!currentActions.containsKey(pActionKeyName))
			return;

		SpriteGraphNodeInst lNode = getNode(currentActions.get(pActionKeyName));

		if (lNode != null) {
			lNode.stopAnimation();
			System.out.println("Stopped action '" + pActionKeyName + "'");

		}

		currentActions.remove(pActionKeyName);

	}

	public boolean isAction(String pName) {
		return currentActions.containsKey(pName);

	}

	public boolean setNodeAngleToPoint(String pNodeName, float pAngle) {
		SpriteGraphNodeInst lNode = rootNode.getNode(pNodeName);

		if (lNode != null) {
			lNode.angToPointEnabled = true;
			lNode.setAngToPoint(pAngle);

			return true;
		}

		return false;

	}

	public boolean setNodeAngleToPointOff(String pNodeName) {
		SpriteGraphNodeInst lNode = rootNode.getNode(pNodeName);

		if (lNode != null) {
			lNode.angToPointEnabled = false;
			lNode.setAngToPoint(0);

			return true;
		}

		return false;

	}

	public void reset() {
		// reset child nodes
		rootNode.reset();
		spriteGraphName = null;

	}

	public void nodeAnimationStarted(SpriteGraphNodeInst pNode, String pStateKeyName, String pStateName) {
		if (mCallbackSubscriber != null) {
			mCallbackSubscriber.onStateStarted(pNode, pStateName);

		}

	}

	public void nodeAnimationStopped(SpriteGraphNodeInst pNode, String pStateKeyName, String pStateName) {
		currentActions.remove(pStateKeyName);

		if (mCallbackSubscriber != null) {
			mCallbackSubscriber.onStateStopped(pNode, pStateName);

		}

	}

}

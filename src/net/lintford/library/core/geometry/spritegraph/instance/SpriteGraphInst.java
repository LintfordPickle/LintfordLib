package net.lintford.library.core.geometry.spritegraph.instance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphManager;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeListener;
import net.lintford.library.core.geometry.spritegraph.definition.GraphObjectDefinition;

/**
 * Represents a geometric instance of a SpriteGraphDef in the world, complete with information about transforms and part types (if for example multiple types are available per part).
 */
public class SpriteGraphInst implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------
	
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
	private boolean mIsLoaded;
	public float positionX;
	public float positionY;

	public Map<String, String> currentActions;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isFree() {
		return !(spriteGraphName != null && spriteGraphName.length() > 0);
	}

	public boolean isLoaded() {
		return mIsLoaded;
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

	public SpriteGraphInst(GraphObjectDefinition pSpriteGraphDef) {
		this();

		spriteGraphName = pSpriteGraphDef.name;
		rootNode = new SpriteGraphNodeInst(this, pSpriteGraphDef.rootNode);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		rootNode.loadContent(pResourceManager);
		mIsLoaded = true;

	}

	public void unloadGLContent() {
		rootNode.unloadContent();
		mIsLoaded = false;

	}

	/**
	 * Updates the {@link SpriteGraphInst} and all its {@link SpriteGraphNodeInst} child parts, updating their positions and vertices.
	 */
	public void update(LintfordCore pCore) {
		if (!mIsLoaded)
			return;

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
		unloadGLContent();

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

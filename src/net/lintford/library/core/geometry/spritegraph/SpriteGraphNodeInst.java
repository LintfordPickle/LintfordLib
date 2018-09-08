package net.lintford.library.core.geometry.spritegraph;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Anchor;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.sprites.AnimatedSpriteListener;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;
import net.lintford.library.core.maths.Vector2f;

public class SpriteGraphNodeInst extends Rectangle implements AnimatedSpriteListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 622930312736268776L;

	public static final String DEFAULT_ACTION_STATE = "_idle";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;

	/** List of possible types is loaded from the {@link SpriteSheetDef}. */
	public String type;

	/** The ID of the {@link SpriteGraphAnchorDef} on the parent. */
	public int parentAnchorID;

	/** If this sprite node represents some object (like a steelshirt being worn on the torso), then add the object reference link here. */
	public int mObjectReferenceID; // TODO: Should we have a reference to objects in the library ??

	/** A list of child parts which are anchored on this {@link SpriteGraphNodeInst}. */
	public List<SpriteGraphNodeInst> childNodes;

	// Initially set from spriteGraphNodeDef.defaultSpriteSheetName, can be changed in charcter setup
	public String spriteSheetNameRef;

	// Save an instance of the sprite here, because AnimatedSprites have frame state information specific to each
	// SpriteGraphNode.
	public transient SpriteInstance nodeSprite;
	public String currentStateName;

	/** Some nodes are just holders for placing itms in (e.g. a placeholder for a sword or gun in a character's shand). */
	public boolean isPlaceHolder;

	public float nodeDepth;

	public boolean angToPointEnabled;
	public float angToPoint;
	public float staticRotationOffset;

	public SpriteGraphInst mParentGraphInst;

	/** Used to identify the current state of this Node (e.g. ATTACK) */
	private String mActionKeyName;

	/** Used to resolve a specific animation (e.g. _shootrange) */
	public String mActionStateName;

	public Anchor nodeAnchor;

	public boolean useSpriteAnimationDimensions;
	public boolean useSpriteAnimationRotations;

	public float r = 1, g = 1, b = 1, a = 1;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float pivotX() {
		if (nodeSprite == null || nodeSprite.isFree())
			return 0;

		return nodeSprite.getFrame().getPivotPointX();

	}

	public float pivotY() {
		if (nodeSprite == null)
			return 0;

		return nodeSprite.getFrame().getPivotPointY();

	}

	public SpriteGraphNodeInst getNode(String pName) {
		// Check this node,
		if (name.equals(pName))
			return this;

		// Check children
		final int CHILD_SIZE = childNodes.size();
		for (int i = 0; i < CHILD_SIZE; i++) {
			SpriteGraphNodeInst lReturn = childNodes.get(i).getNode(pName);
			if (lReturn != null)
				return lReturn;
		}

		return null;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphNodeInst() {
		childNodes = new ArrayList<>();

	}

	public SpriteGraphNodeInst(SpriteGraphInst pSpriteGraphInst, SpriteGraphNodeDef pGraphNodeDef) {
		this(pSpriteGraphInst, pGraphNodeDef, 0);
	}

	public SpriteGraphNodeInst(SpriteGraphInst pSpriteGraphInst, SpriteGraphNodeDef pGraphNodeDef, float pNodeDepth) {
		this();
		mParentGraphInst = pSpriteGraphInst;

		name = pGraphNodeDef.name;
		type = pGraphNodeDef.type;
		nodeDepth = pNodeDepth;
		spriteSheetNameRef = pGraphNodeDef.defaultSpriteSheetName;

		// Setup the child nodes
		int COUNT_SIZE = pGraphNodeDef.childParts.size();
		for (int i = 0; i < COUNT_SIZE; i++) {
			SpriteGraphNodeDef lNodeDef = pGraphNodeDef.childParts.get(i);
			SpriteGraphNodeInst lNewNodeInst = new SpriteGraphNodeInst(pSpriteGraphInst, lNodeDef, nodeDepth + 1);

			childNodes.add(lNewNodeInst);

		}

	}

	public SpriteGraphNodeInst(SpriteGraphInst pSpriteGraphInst, String pSpriteSheetName, String pName, String pType, float pNodeDepth) {
		this();
		mParentGraphInst = pSpriteGraphInst;
		name = pName;
		type = pType;
		nodeDepth = pNodeDepth;
		spriteSheetNameRef = pSpriteSheetName;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore, SpriteGraphInst pParentGraph, SpriteGraphNodeInst pParentGraphNode) {

		if (pParentGraph == null)
			return;

		if (pParentGraph.animator() != null) {
			SpriteSheetDef lNodeSpriteSheet = pCore.resources().spriteSheetManager().getSpriteSheet(spriteSheetNameRef);

			pParentGraph.animator().animate(pCore, pParentGraph, pParentGraphNode, this, lNodeSpriteSheet);

		}

		final int COUNT = childNodes.size();
		for (int i = 0; i < COUNT; i++) {
			childNodes.get(i).update(pCore, pParentGraph, this);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addChild(SpriteGraphNodeInst pPart) {
		if (!childNodes.contains(pPart)) {
			childNodes.add(pPart);

		}

	}

	public void reset() {
		// reset child nodes
		final int CHILD_COUNT = childNodes.size();
		for (int i = 0; i < CHILD_COUNT; i++) {
			childNodes.get(i).reset();

		}

		mParentGraphInst = null;
		nodeSprite = null;

		name = null;
		type = null;

	}

	public Anchor getAnchorPoint(String pName) {
		if (nodeSprite != null) {
			return nodeSprite.getFrame().getAnchorPoint(pName);

		}

		if (nodeAnchor != null)
			return nodeAnchor;

		return Anchor.ZERO_ANCHOR;
	}

	/** Sets the state of the object until the next {@link AnimatedSpriteListener} transition. */
	public void setActionState(String pActionKeyName, String pActionTagName) {
		if (nodeSprite != null) {
			// Unregister the current callback
			mParentGraphInst.nodeAnimationStopped(this, mActionKeyName, mActionStateName);

		}

		mActionKeyName = pActionKeyName;
		mActionStateName = pActionTagName;

	}

	public void stopAnimation() {
		mParentGraphInst.nodeAnimationStopped(this, mActionKeyName, mActionStateName);
		mActionKeyName = null;
		mActionStateName = null;

	}

	public void setAngToPoint(float pNewValue) {
		angToPoint = pNewValue;

	}

	// --------------------------------------
	// Event-Listeners
	// --------------------------------------

	@Override
	public void onStarted(SpriteInstance pSender) {
		mParentGraphInst.nodeAnimationStarted(this, mActionKeyName, mActionStateName);

	}

	@Override
	public void onLooped(SpriteInstance pSender) {
		mParentGraphInst.nodeAnimationStarted(this, mActionKeyName, mActionStateName);
		mActionKeyName = null;
		mActionStateName = null;

	}

	@Override
	public void onStopped(SpriteInstance pSender) {
		pSender.animatedSpriteListender(null);
		stopAnimation();

	}

	public void setVertices(Vector2f[] pNewVertices) {
		if (pNewVertices != null && pNewVertices.length == 4)
			mVertices = pNewVertices;

	}

	/**
	 * Override because not all GraphNodes will be rectangular in shape.
	 */
	@Override
	protected void updateVertices() {
		final float lWidth = mFlipH ? -w : w;
		final float lHeight = mFlipV ? -h : h;

		final float lPX = mFlipH ? -px : px;
		final float lPY = mFlipV ? -py : py;

		// Get local space vertex positions
		mVertices[0].x = -lWidth / 2;
		mVertices[0].y = -lHeight / 2;

		mVertices[1].x = lWidth / 2;
		mVertices[1].y = -lHeight / 2;

		mVertices[2].x = -lWidth / 2;
		mVertices[2].y = lHeight / 2;

		mVertices[3].x = lWidth / 2;
		mVertices[3].y = lHeight / 2;

		float sin = (float) (Math.sin(rot));
		float cos = (float) (Math.cos(rot));

		// iterate over the vertices, rotating them by the given amt around the origin point of the GraphNode.
		for (int i = 0; i < NUM_VERTICES; i++) {
			// Scale the vertices out from local center (before applying world translation)
			float dx = -lPX + mVertices[i].x * sx;
			float dy = -lPY + mVertices[i].y * sy;

			mVertices[i].x = centerX() + (dx * cos - (dy * 1f) * sin) * sx;
			mVertices[i].y = centerY() + (dx * sin + (dy * 1f) * cos) * sy;

		}

		mIsAABB = rot == 0;

	}

}

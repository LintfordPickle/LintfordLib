package net.lintford.library.core.geometry.spritegraph;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.sprites.AnimatedSpriteListener;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;

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
	private String mActionStateName;

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
		name = pGraphNodeDef.name;
		type = pGraphNodeDef.type;
		nodeDepth = pNodeDepth;
		spriteSheetNameRef = pGraphNodeDef.defaultSpriteSheetName;
		mParentGraphInst = pSpriteGraphInst;

		int COUNT_SIZE = pGraphNodeDef.childParts.size();
		for (int i = 0; i < COUNT_SIZE; i++) {
			SpriteGraphNodeDef lNodeDef = pGraphNodeDef.childParts.get(i);
			SpriteGraphNodeInst lNewNodeInst = new SpriteGraphNodeInst(pSpriteGraphInst, lNodeDef, nodeDepth + 1);

			childNodes.add(lNewNodeInst);

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadContent(ResourceManager pResourceManager) {
		final int CHILDPARTS_SIZE = childNodes.size();
		for (int i = 0; i < CHILDPARTS_SIZE; i++) {
			SpriteGraphNodeInst lChildNode = childNodes.get(i);
			lChildNode.loadContent(pResourceManager);

		}

	}

	public void unloadContent() {
		nodeSprite = null;

		final int CHILDPARTS_SIZE = childNodes.size();
		for (int i = 0; i < CHILDPARTS_SIZE; i++) {
			SpriteGraphNodeInst lChildNode = childNodes.get(i);
			lChildNode.unloadContent();

		}
	}

	public void update(LintfordCore pCore, SpriteGraphInst pParentGraph, SpriteGraphNodeInst pParentGraphNode) {

		// Update our position, relative to the parent
		float lX = 0;
		float lY = 0;
		float lRot = 0;

		if (pParentGraphNode != null) {
			lRot = pParentGraphNode.rot;

			float sin = (float) (Math.sin(lRot));
			float cos = (float) (Math.cos(lRot));

			SpriteGraphAnchorDef lAnchorPoint = null;
			lAnchorPoint = pParentGraphNode.getAnchorPoint(name);
			if (lAnchorPoint != null) {
				// Anchor point needs to be rotated with the parent node ..
				float dx = -pParentGraphNode.pivotX() + (lAnchorPoint.x * (mFlipH ? -1f : 1f)) * sx;
				float dy = -pParentGraphNode.pivotY() + (lAnchorPoint.y * (mFlipV ? -1f : 1f)) * sy;

				lX += (dx * cos - (dy * 1f) * sin);
				lY += (dx * sin + (dy * 1f) * cos);
				lRot += (float) Math.toRadians(lAnchorPoint.rot);

			}

			lX += pParentGraphNode.centerX;
			lY += pParentGraphNode.centerY;

		} else {
			lX = centerX;
			lY = centerY;
		}

		// Update the spritegraphnode
		SpriteSheetDef lNodeSpriteSheet = pCore.resources().spriteSheetManager().getSpriteSheet(spriteSheetNameRef);
		if (lNodeSpriteSheet != null) {
			// e.g. Torso00_idle
			// TODO: ---> Overide with local node states (e.g. ARM_SWING, ARM_STAB)
			String lResolvedName = name + type;
			if (mActionStateName != null)
				lResolvedName += mActionStateName;
			else
				lResolvedName += pParentGraph.objectState;

			// Check to see if the animation state of this node has changed
			if (!lResolvedName.equals(currentStateName)) {
				nodeSprite = lNodeSpriteSheet.getSpriteInstance(lResolvedName);

				if (nodeSprite != null) {
					// When we change to a new animation, we need to
					nodeSprite.animatedSpriteListender(this);
					nodeSprite.playFromBeginning();

				} else {
					// If the sprite equals null, then try and fallback to some default (the first sprite in the SpriteMap).
					nodeSprite = lNodeSpriteSheet.getSpriteInstance(name + type + DEFAULT_ACTION_STATE);

				}

				// Only do this once
				currentStateName = lResolvedName;

			}

		}

		mFlipH = pParentGraph.mFlipHorizontal;

		if (nodeSprite != null) {
			nodeSprite.update(pCore);

			SpriteFrame lCurrentFrame = nodeSprite.getFrame();

			rot = (float) Math.toRadians(lCurrentFrame.getDefaultRotation());

			// Set the pivot point of this GraphNode to that of the current Sprite's.
			pivotX(lCurrentFrame.getPivotPointX());
			pivotY(lCurrentFrame.getPivotPointY());

			float signum = mFlipH ? -1f : 1f;
			if (angToPointEnabled) {
				rotateAbs(rot * signum);
				rotateRel(angToPoint);
				rotateRel(staticRotationOffset);
			} else {
				rotateAbs(rot * signum);
				rotateRel(staticRotationOffset);

			}

			set(lX, lY, lCurrentFrame.w, lCurrentFrame.h);

			if (pParentGraphNode != null)
				rotateAbs(lRot + rot);

		} else {
			setPosition(lX, lY);
			rotateAbs(lRot);

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

		name = null;
		type = null;

	}

	public SpriteGraphAnchorDef getAnchorPoint(String pName) {
		if (nodeSprite != null) {
			return nodeSprite.getFrame().getAnchorPoint(pName);

		}

		return SpriteGraphAnchorDef.ZERO_ANCHOR;
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

}

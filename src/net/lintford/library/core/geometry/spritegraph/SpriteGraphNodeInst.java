package net.lintford.library.core.geometry.spritegraph;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.sprites.AnimatedSprite;
import net.lintford.library.core.graphics.sprites.ISprite;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheet;

public class SpriteGraphNodeInst extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 622930312736268776L;

	public static final String DEFAULT_ACTION_STATE = "_idle";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;

	/** List of possible types is loaded from the {@link SpriteSheet}. */
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
	public transient ISprite nodeSprite;
	public String currentSpriteName;

	/** Some nodes are just holders for placing itms in (e.g. a placeholder for a sword or gun in a character's shand). */
	public boolean isPlaceHolder;

	public float nodeDepth;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float pivotX() {
		if (nodeSprite == null)
			return 0;
		return nodeSprite.getPivotPointX();
	}

	public float pivotY() {
		if (nodeSprite == null)
			return 0;
		return nodeSprite.getPivotPointY();
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

	public SpriteGraphNodeInst(SpriteGraphNodeDef pGraphNodeDef) {
		this(pGraphNodeDef, 0);
	}

	public SpriteGraphNodeInst(SpriteGraphNodeDef pGraphNodeDef, float pNodeDepth) {
		this();
		name = pGraphNodeDef.name;
		type = pGraphNodeDef.type;
		nodeDepth = pNodeDepth;
		spriteSheetNameRef = pGraphNodeDef.defaultSpriteSheetName;

		int COUNT_SIZE = pGraphNodeDef.childParts.size();
		for (int i = 0; i < COUNT_SIZE; i++) {
			SpriteGraphNodeDef lNodeDef = pGraphNodeDef.childParts.get(i);
			SpriteGraphNodeInst lNewNodeInst = new SpriteGraphNodeInst(lNodeDef, nodeDepth + 1);

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
			if (pParentGraphNode != null) {
				lAnchorPoint = pParentGraphNode.getAnchorPoint(name);
				if (lAnchorPoint != null) {
					// Anchor point needs to be rotated with the parent node ..
					float dx = -pParentGraphNode.pivotX() + lAnchorPoint.x * sx;
					float dy = -pParentGraphNode.pivotY() + lAnchorPoint.y * sy;

					lX += (dx * cos - (dy * 1f) * sin);
					lY += (dx * sin + (dy * 1f) * cos);

				}

			}

			lX += pParentGraphNode.centerX;
			lY += pParentGraphNode.centerY;

		} else {
			lX = centerX;
			lY = centerY;
		}

		// Update the spritegraphnode
		SpriteSheet lNodeSpriteSheet = pCore.resources().spriteSheetManager().getSpriteSheet(spriteSheetNameRef);
		if (lNodeSpriteSheet != null) {
			// e.g. Torso00_idle
			String lResolvedName = name + type + pParentGraph.objectState;
			if (!lResolvedName.equals(currentSpriteName)) {
				nodeSprite = lNodeSpriteSheet.getAnimation(lResolvedName);
				if (nodeSprite instanceof AnimatedSprite) {
					AnimatedSprite lAnim = (AnimatedSprite) nodeSprite;
					lAnim.playFromBeginning();

				}

				currentSpriteName = lResolvedName;

				// If the sprite equals null, then try and fallback to some default (the first sprite in the SpriteMap).
				if (nodeSprite == null) {
					nodeSprite = lNodeSpriteSheet.getAnimation(name + type + DEFAULT_ACTION_STATE);
				}
			}

		}

		if (nodeSprite != null) {
			if (nodeSprite instanceof AnimatedSprite) {
				((AnimatedSprite) nodeSprite).update(pCore, 1f);
			}

			rot = (float) Math.toRadians(nodeSprite.getRotation());

			// Set the pivot point of this GraphNode to that of the current Sprite's.
			pivotX(nodeSprite.getPivotPointX());
			pivotY(nodeSprite.getPivotPointY());
			rotateAbs(rot);
			set(lX, lY, nodeSprite.getSrcWidth(), nodeSprite.getSrcHeight());
			if (pParentGraphNode != null)
				rotateAbs(lRot + rot);

		} else {
			setPosition(lX, lY);

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
			return nodeSprite.getAnchorPoint(pName);
		}

		return SpriteGraphAnchorDef.ZERO_ANCHOR;
	}

}

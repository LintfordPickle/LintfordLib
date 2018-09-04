package net.lintford.library.core.geometry.spritegraph.animators;

import java.io.Serializable;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Anchor;
import net.lintford.library.core.geometry.spritegraph.ISpriteNodeInstanceAnimator;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;

public class SpriteSheetAnimator implements ISpriteNodeInstanceAnimator, Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -4374629987867291856L;

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void animate(LintfordCore pCore, SpriteGraphInst pParentGraph, SpriteGraphNodeInst pParentNode, SpriteGraphNodeInst pNode, SpriteSheetDef pSpriteSheet) {
		// Update our position, relative to the parent
		float lX = 0;
		float lY = 0;
		float lRot = 0;

		if (pParentNode != null) {
			lRot = pParentNode.rot;

			float sin = (float) (Math.sin(lRot));
			float cos = (float) (Math.cos(lRot));

			Anchor lAnchorPoint = null;
			lAnchorPoint = pParentNode.getAnchorPoint(pNode.name);
			if (lAnchorPoint != null) {
				// Anchor point needs to be rotated with the parent node ..
				float dx = -pParentNode.pivotX() + (lAnchorPoint.x * (pNode.flipH() ? -1f : 1f)) * pNode.scaleX();
				float dy = -pParentNode.pivotY() + (lAnchorPoint.y * (pNode.flipV() ? -1f : 1f)) * pNode.scaleY();

				lX += (dx * cos - (dy * 1f) * sin);
				lY += (dx * sin + (dy * 1f) * cos);
				lRot += (float) Math.toRadians(lAnchorPoint.rot);

			}

			lX += pParentNode.x + pParentNode.pivotX();
			lY += pParentNode.y + pParentNode.pivotY();

		} else {
			lX = pNode.x;
			lY = pNode.y;
			lRot = pNode.rot;

		}

		// TODO: Why the fuck we are resolving the sprite sheet here and in the renderer
		// Update the spritegraphnode
		SpriteSheetDef lNodeSpriteSheet = pCore.resources().spriteSheetManager().getSpriteSheet(pNode.spriteSheetNameRef);
		if (lNodeSpriteSheet != null) {

			// The states are needed to change the sprite animations/variations being used when rendering sprite graphs.
			// The states can originate from two places, either on the node (i.e. locally) or on the graph object (i.e. globally).
			// stats on the local node take precedence.
			// e.g. Torso00_idle
			// TODO: ---> Overide with local node states (e.g. ARM_SWING, ARM_STAB)
			String lResolvedName = pNode.name + pNode.type;
			if (pNode.mActionStateName != null)
				lResolvedName += pNode.mActionStateName;
			else if (pParentGraph.objectState != null)
				lResolvedName += pParentGraph.objectState;

			// Check to see if the animation state of this node has changed
			if (!lResolvedName.equals(pNode.currentStateName)) {
				pNode.nodeSprite = lNodeSpriteSheet.getSpriteInstance(lResolvedName);

				if (pNode.nodeSprite != null) {
					// When we change to a new animation, we need to
					pNode.nodeSprite.animatedSpriteListender(pNode);
					pNode.nodeSprite.playFromBeginning();

				} else {
					// If the sprite equals null, then try and fallback to some default (the first sprite in the SpriteMap).
					pNode.nodeSprite = lNodeSpriteSheet.getSpriteInstance(pNode.name + pNode.type + SpriteGraphNodeInst.DEFAULT_ACTION_STATE);

				}

				// Only do this once
				pNode.currentStateName = lResolvedName;

			}

		}

		pNode.flipH(pParentGraph.mFlipHorizontal);

		if (pNode.nodeSprite != null) {
			pNode.nodeSprite.update(pCore);

			SpriteFrame lCurrentFrame = pNode.nodeSprite.getFrame();

			pNode.rot = (float) Math.toRadians(lCurrentFrame.getDefaultRotation());
			pNode.pivotX(lCurrentFrame.getPivotPointX());
			pNode.pivotY(lCurrentFrame.getPivotPointY());

			float signum = pNode.flipH() ? -1f : 1f;
			if (pNode.angToPointEnabled) {
				pNode.rotateAbs(pNode.rot * signum);
				pNode.rotateRel(pNode.angToPoint);
				pNode.rotateRel(pNode.staticRotationOffset);
			} else {
				pNode.rotateAbs(pNode.rot * signum);
				pNode.rotateRel(pNode.staticRotationOffset);

			}

			pNode.setCenter(lX, lY, lCurrentFrame.w, lCurrentFrame.h);

			pNode.rotateAbs(lRot);

		} else {
			pNode.setPosition(lX, lY);
			// rotateAbs(lRot);

		}
	}

}

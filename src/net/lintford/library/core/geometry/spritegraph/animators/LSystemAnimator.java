package net.lintford.library.core.geometry.spritegraph.animators;

import java.io.Serializable;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Anchor;
import net.lintford.library.core.geometry.spritegraph.ISpriteNodeInstanceAnimator;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;

public class LSystemAnimator implements ISpriteNodeInstanceAnimator, Serializable {

	private static final long serialVersionUID = 203467900029147613L;

	@Override
	public void animate(LintfordCore pCore, SpriteGraphInst pParentGraph, SpriteGraphNodeInst pParentNode, SpriteGraphNodeInst pNode, SpriteSheetDef pSpriteSheet) {
		// Update our position, relative to the parent
		float lX = 0;
		float lY = 0;
		float lRot = 0;

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

		if (pNode.nodeSprite != null) {
			pNode.nodeSprite.update(pCore);

			SpriteFrame lCurrentFrame = pNode.nodeSprite.getFrame();

			pNode.pivotX(lCurrentFrame.getPivotPointX());
			pNode.pivotY(lCurrentFrame.getPivotPointY());

			pNode.set(lX, lY, lCurrentFrame.w, lCurrentFrame.h);

		}

	}

}

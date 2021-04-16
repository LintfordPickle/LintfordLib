package net.lintford.library.core.graphics.sprites.spritegraph;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphInstance;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInstance;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;

public class SpriteGraphRenderer extends SpriteBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static boolean RENDER_COLLIBABLES = false;
	public static boolean RENDER_DEBUG = false;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public int entityGroupUid;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphRenderer() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void drawSpriteGraphList(LintfordCore pCore, SpriteGraphInstance pSpriteGraphInstance) {
		if (pSpriteGraphInstance == null || !pSpriteGraphInstance.isAssigned())
			return;

		// Create a graph of nodes in the correct draw order
		final var lReorderedList = pSpriteGraphInstance.getZOrderedFlatList(SpriteGraphInstance.SpriteGraphNodeInstanceZComparator);

		final int lNumNodes = lReorderedList.size();
		for (int i = 0; i < lNumNodes; i++) {
			final var lSpriteGraphNodeInstance = lReorderedList.get(i);

			if (lSpriteGraphNodeInstance.attachedItemInstance != null) {
				renderSpriteGraphNodeInstance(pCore, pSpriteGraphInstance, lSpriteGraphNodeInstance);

			}

		}

		if (RENDER_DEBUG)
			renderSpriteTreeNodeDebug(pCore, pSpriteGraphInstance, pSpriteGraphInstance.rootNode);

	}

	private void renderSpriteGraphNodeInstance(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pSpriteGraphNode) {
		if (pSpriteGraphNode.attachedItemInstance != null) {
			final var lAttachment = pSpriteGraphNode.attachedItemInstance;

			// Resolve the sprite sheet
			var lSpriteSheetDefinition = lAttachment.spriteSheetDefinition();
			if (lSpriteSheetDefinition == null) {
				final var lResourceManager = pCore.resources();
				lSpriteSheetDefinition = lResourceManager.spriteSheetManager().getSpriteSheet(lAttachment.spriteGraphSpriteSheetName(), entityGroupUid);

				if (lSpriteSheetDefinition == null) {
					return;

				}

				lAttachment.spriteSheetDefinition(lSpriteSheetDefinition);

			}

			// Update the sprite animation
			if (pSpriteGraphNode.currentSpriteActionName == null || !pSpriteGraphNode.currentSpriteActionName.equals(pSpriteGraph.currentAnimation())) {
				// First try to get the sprite graph animation frame
				var lFoundSprintInstance = lSpriteSheetDefinition.getSpriteInstance(pSpriteGraph.currentAnimation());
				if (lFoundSprintInstance != null) {
					pSpriteGraphNode.mSpriteInstance = lFoundSprintInstance;

				} else {
					// otherwise, take the default name of the attachment
					lFoundSprintInstance = lSpriteSheetDefinition.getSpriteInstance(lAttachment.defaultSpriteName());
					if (lFoundSprintInstance != null) {
						pSpriteGraphNode.mSpriteInstance = lFoundSprintInstance;
					}

				}

				pSpriteGraphNode.currentSpriteActionName = pSpriteGraph.currentAnimation();

			}

			// Render the sprite instance
			if (pSpriteGraphNode.mSpriteInstance != null) {
				pSpriteGraphNode.update(pCore, pSpriteGraph, pSpriteGraphNode);

				draw(lSpriteSheetDefinition, pSpriteGraphNode.mSpriteInstance, pSpriteGraphNode.mSpriteInstance, -0.1f, ColorConstants.WHITE);

			}

			if (RENDER_DEBUG) {
				end();
				begin(pCore.gameCamera());

			}

			if (RENDER_DEBUG) {
				renderSpriteTreeNodeDebug(pCore, pSpriteGraph, pSpriteGraphNode);

			}

		}

	}

	private void renderSpriteTreeNodeDebug(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pSpriteGraphNode) {
		final var lSpriteInstance = pSpriteGraphNode.spriteInstance();

		final float lPointSize = 2f;
		GL11.glPointSize(lPointSize * pCore.gameCamera().getZoomFactor());

		// Debug.debugManager().drawers().drawPolyImmediate(pCore.gameCamera(), pSpriteGraphNode.spriteInstance);

		{ // center - green
			final var lPositionX = pSpriteGraphNode.positionX();
			final var lPositionY = pSpriteGraphNode.positionY();

			Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lPositionX, lPositionY, -0.01f, 0f, 1f, 0f, 1f);
		}

		{ // anchors - yellow
			if (lSpriteInstance != null) {
				final var lSpriteFrame = lSpriteInstance.currentSpriteFrame();
				final int lAnchorCount = lSpriteFrame.anchorCount();
				for (int i = 0; i < lAnchorCount; i++) {
					final var lAnchorPoint = lSpriteFrame.getAnchorByIndex(i);

					final var lFlipHorizontal = pSpriteGraph.mFlipHorizontal;
					final var lFlipVertical = pSpriteGraph.mFlipVertical;

					final float lAnchorWorldX = pSpriteGraphNode.positionX() + (lFlipHorizontal ? -lAnchorPoint.x : lAnchorPoint.x) * lSpriteFrame.scaleX();
					final float lAnchorWorldY = pSpriteGraphNode.positionY() + (lFlipVertical ? -lAnchorPoint.y : lAnchorPoint.y) * lSpriteFrame.scaleY();

					Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lAnchorWorldX, lAnchorWorldY, -0.01f, 1f, 1f, 0f, 1f);
				}
			}

		}

	}

}

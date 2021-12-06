package net.lintford.library.core.graphics.sprites.spritegraph;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphInstance;
import net.lintford.library.core.geometry.spritegraph.instances.SpriteGraphNodeInstance;
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
			renderSpriteGraphNodeInstance(pCore, pSpriteGraphInstance, lReorderedList.get(i));
		}

		if (RENDER_DEBUG) {
			renderSpriteTreeNodeDebug(pCore, pSpriteGraphInstance, pSpriteGraphInstance.rootNode);
			Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), pSpriteGraphInstance.positionX, pSpriteGraphInstance.positionY, -0.01f, 1f, 1f, 1f, 1f);
		}
	}

	private void renderSpriteGraphNodeInstance(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pSpriteGraphNode) {
		if (pSpriteGraphNode.attachedItemInstance != null) {
			final var lAttachment = pSpriteGraphNode.attachedItemInstance;
			var lSpritesheetDefinition = lAttachment.spritesheetDefinition();

			if (pSpriteGraphNode.mSpriteInstance != null) {
				var lAttachmentColor = ColorConstants.getColor(lAttachment.colorTint());
				draw(lSpritesheetDefinition, pSpriteGraphNode.mSpriteInstance, pSpriteGraphNode.mSpriteInstance, -0.1f, lAttachmentColor);
			}

			if (RENDER_DEBUG) {
				end();
				begin(pCore.gameCamera());
				renderSpriteTreeNodeDebug(pCore, pSpriteGraph, pSpriteGraphNode);
			}
		}
	}

	private void renderSpriteTreeNodeDebug(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pSpriteGraphNode) {
		final var lSpriteInstance = pSpriteGraphNode.spriteInstance();

		final float lPointSize = 1f;
		GL11.glPointSize(lPointSize * pCore.gameCamera().getZoomFactor());

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
package net.lintford.library.core.graphics.sprites.spritegraph;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphInstance;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInstance;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;

public class SpriteGraphRenderer extends SpriteBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static boolean RENDER_COLLIBABLES = false;
	public static boolean RENDER_DEBUG = false;

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
		final var lReorderedList = pSpriteGraphInstance.getZOrderedFlatList();

		final int lNumNodes = lReorderedList.size();
		for (int i = 0; i < lNumNodes; i++) {
			final var lSpriteGraphNodeInstance = lReorderedList.get(i);
			renderSpriteGraphNodeInstance(pCore, pSpriteGraphInstance, lSpriteGraphNodeInstance);

		}

		if (RENDER_DEBUG)
			renderSpriteTreeNodeDebug(pCore, pSpriteGraphInstance, pSpriteGraphInstance.rootNode);

	}

	public void drawSpriteGraphTree(LintfordCore pCore, SpriteGraphInstance pSpriteGraphInstance) {
		if (pSpriteGraphInstance == null || !pSpriteGraphInstance.isAssigned())
			return;

		renderSpriteTreeNode(pCore, pSpriteGraphInstance, pSpriteGraphInstance.rootNode);

		if (RENDER_DEBUG)
			renderSpriteTreeNode(pCore, pSpriteGraphInstance, pSpriteGraphInstance.rootNode);

	}

	private void renderSpriteGraphNodeInstance(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pSpriteGraphNode) {
		if (pSpriteGraphNode.spriteSheetDefinition != null && pSpriteGraphNode.spriteInstance != null) {
			draw(pSpriteGraphNode.spriteSheetDefinition, pSpriteGraphNode.spriteInstance, pSpriteGraphNode.spriteInstance, -0.1f, 1f, 1f, 1f, 1f);

			if (RENDER_DEBUG) {
				end();
				begin(pCore.gameCamera());

			}

		}

		if (RENDER_DEBUG)
			renderSpriteTreeNodeDebug(pCore, pSpriteGraph, pSpriteGraphNode);

	}

	private void renderSpriteTreeNode(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pSpriteGraphNode) {

		if (pSpriteGraphNode.spriteSheetDefinition != null && pSpriteGraphNode.spriteInstance != null) {
			draw(pSpriteGraphNode.spriteSheetDefinition, pSpriteGraphNode.spriteInstance, pSpriteGraphNode.spriteInstance, -0.1f, 1f, 1f, 1f, 1f);

			end();
			begin(pCore.gameCamera());

		}

		final int lChildNodeList = pSpriteGraphNode.childNodes.size();
		for (int i = 0; i < lChildNodeList; i++) {
			final var lSpriteGraphNodeInst = pSpriteGraphNode.childNodes.get(i);

			renderSpriteTreeNode(pCore, pSpriteGraph, lSpriteGraphNodeInst);

		}

		renderSpriteTreeNodeDebug(pCore, pSpriteGraph, pSpriteGraphNode);

	}

	private void renderSpriteTreeNodeDebug(LintfordCore pCore, SpriteGraphInstance pSpriteGraph, SpriteGraphNodeInstance pSpriteGraphNode) {
		final float lPointSize = 2f;
		GL11.glPointSize(lPointSize * pCore.gameCamera().getZoomFactor());

		// Debug.debugManager().drawers().drawPolyImmediate(pCore.gameCamera(), pSpriteGraphNode.spriteInstance);

		{ // center - green
			final var lPositionX = pSpriteGraphNode.positionX();
			final var lPositionY = pSpriteGraphNode.positionY();

			Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lPositionX, lPositionY, -0.01f, 0f, 1f, 0f, 1f);
		}

		{ // anchors - yellow
			if (pSpriteGraphNode.spriteInstance != null) {
				final var lSpriteFrame = pSpriteGraphNode.spriteInstance.currentSpriteFrame();
				final int lAnchorCount = lSpriteFrame.anchorCount();
				for (int i = 0; i < lAnchorCount; i++) {
					final var lAnchorPoint = lSpriteFrame.getAnchorByIndex(i);

					final var lFlipHorizontal = pSpriteGraph.mFlipHorizontal;
					final var lFlipVertical = pSpriteGraph.mFlipVertical;

					final float lAnchorWorldX = pSpriteGraphNode.positionX() + (lFlipHorizontal ? -lAnchorPoint.x : lAnchorPoint.x) * lSpriteFrame.scaleX;
					final float lAnchorWorldY = pSpriteGraphNode.positionY() + (lFlipVertical ? -lAnchorPoint.y : lAnchorPoint.y) * lSpriteFrame.scaleY;

					Debug.debugManager().drawers().drawPointImmediate(pCore.gameCamera(), lAnchorWorldX, lAnchorWorldY, -0.01f, 1f, 1f, 0f, 1f);
				}
			}

		}

	}

}

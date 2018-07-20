package net.lintford.library.core.graphics.sprites.spritegraph;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;

public class SpriteGraphRenderer extends SpriteBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final boolean RENDER_COLLIBABLES = false;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphRenderer() {
		// TODO Auto-generated constructor stub
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(LintfordCore pCore, SpriteGraphInst pSpriteGraphInst) {
		if (pSpriteGraphInst == null || pSpriteGraphInst.isFree())
			return;

		renderSpriteTreeNode(pCore, pSpriteGraphInst, pSpriteGraphInst.rootNode);

	}

	private void renderSpriteTreeNode(LintfordCore pCore, SpriteGraphInst pSpriteGraph, SpriteGraphNodeInst pSpriteGraphNode) {

		// Render this node
		if (pSpriteGraphNode.nodeSprite != null) {
			SpriteSheetDef lNodeSpriteSheet = pCore.resources().spriteSheetManager().getSpriteSheet(pSpriteGraphNode.spriteSheetNameRef);

			draw(lNodeSpriteSheet, pSpriteGraphNode.nodeSprite, pSpriteGraphNode, -0.1f, 1f, 1f, 1f, 1f);

		}

		if (RENDER_COLLIBABLES) {
			DebugManager.DEBUG_MANAGER.drawers().drawPoly(pCore.gameCamera(), pSpriteGraphNode);

		}

		final int CHILD_NODES = pSpriteGraphNode.childNodes.size();
		for (int i = 0; i < CHILD_NODES; i++) {
			SpriteGraphNodeInst lChildNode = pSpriteGraphNode.childNodes.get(i);

			renderSpriteTreeNode(pCore, pSpriteGraph, lChildNode);

		}

	}

}

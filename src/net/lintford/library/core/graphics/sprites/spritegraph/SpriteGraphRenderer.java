package net.lintford.library.core.graphics.sprites.spritegraph;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.instance.SpriteGraphNodeInst;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;

public class SpriteGraphRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final boolean RENDER_COLLIBABLES = false;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mEntityGroupId;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphRenderer(int pEntityGroupId) {
		mEntityGroupId = pEntityGroupId;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteGraphInst pSpriteGraphInst) {
		if (pSpriteGraphInst == null || pSpriteGraphInst.isFree() || !pSpriteGraphInst.isLoaded())
			return;

		renderSpriteTreeNode(pCore, pSpriteBatch, pSpriteGraphInst, pSpriteGraphInst.rootNode);

	}

	private void renderSpriteTreeNode(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteGraphInst pSpriteGraph, SpriteGraphNodeInst pSpriteGraphNode) {

		if (pSpriteGraphNode.nodeSprite != null) {
			final var lSpriteSheetManager = pCore.resources().spriteSheetManager();
			final var lSpriteSheetDef = lSpriteSheetManager.getSpriteSheet(pSpriteGraphNode.spriteSheetNameRef, mEntityGroupId);

			pSpriteBatch.draw(lSpriteSheetDef, pSpriteGraphNode.nodeSprite, pSpriteGraphNode, -0.1f, 1f, 1f, 1f, 1f);

		}

		if (RENDER_COLLIBABLES) {
			Debug.debugManager().drawers().drawPolyImmediate(pCore.gameCamera(), pSpriteGraphNode);

		}

		final int lChildNodeList = pSpriteGraphNode.childNodes.size();
		for (int i = 0; i < lChildNodeList; i++) {
			final var lSpriteGraphNodeInst = pSpriteGraphNode.childNodes.get(i);

			renderSpriteTreeNode(pCore, pSpriteBatch, pSpriteGraph, lSpriteGraphNodeInst);

		}

	}

}

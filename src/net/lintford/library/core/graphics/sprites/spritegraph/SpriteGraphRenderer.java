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

	private int mEntityGroupUid;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entityGroupUId() {
		return mEntityGroupUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphRenderer(int entityGroupUid) {
		mEntityGroupUid = entityGroupUid;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reassignedEntityGroupUid(int newEntityGroupUid) {
		mEntityGroupUid = newEntityGroupUid;
	}

	public void drawSpriteGraphList(LintfordCore core, SpriteGraphInstance spriteGraphInstance) {
		if (spriteGraphInstance == null || !spriteGraphInstance.isAssigned())
			return;

		final var lReorderedList = spriteGraphInstance.getZOrderedFlatList(SpriteGraphInstance.SpriteGraphNodeInstanceZComparator);

		final int lNumNodes = lReorderedList.size();
		for (int i = 0; i < lNumNodes; i++) {
			renderSpriteGraphNodeInstance(core, spriteGraphInstance, lReorderedList.get(i));
		}

		if (RENDER_DEBUG) {
			renderSpriteTreeNodeDebug(core, spriteGraphInstance, spriteGraphInstance.rootNode());
			Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), spriteGraphInstance.x, spriteGraphInstance.y, -0.01f, 1f, 1f, 1f, 1f);
		}
	}

	private void renderSpriteGraphNodeInstance(LintfordCore core, SpriteGraphInstance spriteGraph, SpriteGraphNodeInstance spriteGraphNode) {
		if (spriteGraphNode.spritegraphAttachmentInstance() != null) {
			final var lAttachment = spriteGraphNode.spritegraphAttachmentInstance();
			var lSpritesheetDefinition = lAttachment.spritesheetDefinition();

			if (spriteGraphNode.spriteInstance() != null) {
				var lAttachmentColor = ColorConstants.getColor(lAttachment.attachmentColorTint());
				draw(lSpritesheetDefinition, spriteGraphNode.spriteInstance(), spriteGraphNode.spriteInstance(), -0.1f, lAttachmentColor);
			}

			if (RENDER_DEBUG) {
				end();
				begin(core.gameCamera());
				renderSpriteTreeNodeDebug(core, spriteGraph, spriteGraphNode);
			}
		}
	}

	private void renderSpriteTreeNodeDebug(LintfordCore core, SpriteGraphInstance spriteGraph, SpriteGraphNodeInstance spriteGraphNode) {
		final var lSpriteInstance = spriteGraphNode.spriteInstance();

		final float lPointSize = 1f;
		GL11.glPointSize(lPointSize * core.gameCamera().getZoomFactor());

		{ // center - green
			final var lPositionX = spriteGraphNode.positionX();
			final var lPositionY = spriteGraphNode.positionY();

			Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), lPositionX, lPositionY, -0.01f, 0f, 1f, 0f, 1f);
		}

		{ // anchors - yellow
			if (lSpriteInstance != null) {
				final var lSpriteFrame = lSpriteInstance.currentSpriteFrame();
				final int lAnchorCount = lSpriteFrame.anchorCount();
				for (int i = 0; i < lAnchorCount; i++) {
					final var lAnchorPoint = lSpriteFrame.getAnchorByIndex(i);

					final var lFlipHorizontal = spriteGraph.flipHorizontal;
					final var lFlipVertical = spriteGraph.flipVertical;

					final var lAnchorWorldX = spriteGraphNode.positionX() + (lFlipHorizontal ? -lAnchorPoint.localX() : lAnchorPoint.localX()) * lSpriteFrame.scaleX();
					final var lAnchorWorldY = spriteGraphNode.positionY() + (lFlipVertical ? -lAnchorPoint.localY() : lAnchorPoint.localY()) * lSpriteFrame.scaleY();

					Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), lAnchorWorldX, lAnchorWorldY, -0.01f, 1f, 1f, 0f, 1f);
				}
			}
		}
	}
}
package net.lintford.library.core.graphics.sprites.spritegraph;

import java.util.ArrayDeque;
import java.util.Deque;

import net.lintford.library.core.geometry.spritegraph.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;
import net.lintford.library.core.maths.RandomNumbers;

/** Builds SpriteGraphDefintions based on L-Systems. */
public class SpriteGraphBuilder {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public class LCursor {
		public float x, y;
		public float curRotation;
		public int depth;
		public SpriteGraphNodeInst currentNode;

		public LCursor() {
			x = 0;
			y = 0;
			curRotation = 0;
			currentNode = null;
			depth = 0;

		}

		public LCursor(float pX, float pY, float pCurRotation, SpriteGraphNodeInst pCurSprite, int pDepth) {
			x = pX;
			y = pY;
			curRotation = pCurRotation;
			currentNode = pCurSprite;
			depth = pDepth;

		}

		public Deque<LCursor> cursorStack = new ArrayDeque<>();

		public void saveState() {
			cursorStack.push(new LCursor(x, y, curRotation, currentNode, depth));
		}

		public void restoreState() {
			LCursor lSavedState = cursorStack.poll();

			if (lSavedState != null) {
				x = lSavedState.x;
				y = lSavedState.y;
				curRotation = lSavedState.curRotation;
				currentNode = lSavedState.currentNode;
				depth = lSavedState.depth;

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteGraphInst createSpriteGraphFromLSystem(final String pLSystemString, SpriteSheetDef pSpriteSheetDef) {
		// We require a pSpriteSheetDef for the construction of the SpriteGraph because we need to know the visual properties
		// of each of the SpriteGraph nodes. The origin of the SpriteSheetDef is un-important.

		SpriteGraphInst lNewInst = new SpriteGraphInst();
		lNewInst.spriteGraphName = "Tree";

		lNewInst.rootNode = new SpriteGraphNodeInst(lNewInst, "TreeSpriteSheet", "TreeRoot", "", 0);
		lNewInst.rootNode.setPosition(0, 250);

		// Now we need to 'draw' the L-System (i.e. create the sprites and the graph)
		LCursor lCursor = new LCursor(0, 250, -90, lNewInst.rootNode, 0);

		for (int i = 0; i < pLSystemString.length(); i++) {
			char lCurrentInstruction = pLSystemString.charAt(i);

			// Update the cursor position ready for the next node in the tree
			switch (lCurrentInstruction) {
			case 'F':
				String lSpriteName = "TreeRoot";
				if (lCursor.depth > 0)
					lSpriteName = "TreeTrunk";

				SpriteFrame lSectionSpriteFrame = pSpriteSheetDef.getSpriteFrame(lSpriteName);

				// TODO: Get the height of the current sprite used for this node
				final float lHeight = lSectionSpriteFrame.h;

				lCursor.x += (float) Math.cos(Math.toRadians(lCursor.curRotation)) * lHeight;
				lCursor.y += (float) Math.sin(Math.toRadians(lCursor.curRotation)) * lHeight;

				// Create a new anchor position in the current sprite ready for the next node of the graph

				SpriteGraphNodeInst lNewNodeInst = new SpriteGraphNodeInst(lNewInst, "TreeSpriteSheet", lSpriteName, "", lCursor.depth);
				lNewNodeInst.setPosition(lCursor.x, lCursor.y);
				lNewNodeInst.setDimensions(lSectionSpriteFrame.w, lSectionSpriteFrame.h);
				lNewNodeInst.rotateAbs((float) Math.toRadians(lCursor.curRotation + 90));

				lCursor.currentNode.childNodes.add(lNewNodeInst);
				lCursor.currentNode = lNewNodeInst;

				lCursor.depth++;

				break;
			case '-':
				lCursor.curRotation -= RandomNumbers.random(20, 45);
				break;
			case '+':
				lCursor.curRotation += RandomNumbers.random(20, 45);
				break;
			case '[':
				lCursor.saveState();
				break;
			case ']':
				lCursor.restoreState();
				break;
			}

		}

		// MAGIC

		return lNewInst;
	}

}

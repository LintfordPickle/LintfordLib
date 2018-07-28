package net.lintford.library.core.fractal;

import java.util.ArrayDeque;
import java.util.Deque;

import net.lintford.library.core.geometry.spritegraph.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;
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

		public LCursor(SpriteGraphNodeInst pCurGraphNode, int pDepth) {
			currentNode = pCurGraphNode;
			x = currentNode.centerX();
			y = currentNode.centerY();
			curRotation = currentNode.rotation();
			depth = pDepth;

		}

		public Deque<LCursor> cursorStack = new ArrayDeque<>();

		public void saveState() {
			cursorStack.push(new LCursor(currentNode, depth));
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

	public SpriteGraphInst createSpriteGraphFromLSystem(final String pLSystemString, LSystemDefinition pLSystemDef) {
		// Create a new instance of a SpriteGraph
		SpriteGraphInst lNewInst = new SpriteGraphInst();
		pLSystemDef.onGraphCreation(lNewInst);

		// Add a root node from which to build on
		lNewInst.rootNode = new SpriteGraphNodeInst(lNewInst, pLSystemDef.SpriteSheetName, pLSystemDef.rootNodeSpriteName, "", 0);
		pLSystemDef.onRootNodeCreation(lNewInst.rootNode);

		// Now we need to 'draw' the L-System (i.e. create the sprites and the graph)
		LCursor lCursor = new LCursor(lNewInst.rootNode, 1);

		for (int i = 0; i < pLSystemString.length(); i++) {
			char lCurrentInstruction = pLSystemString.charAt(i);

			// Update the cursor position ready for the next node in the tree
			switch (lCurrentInstruction) {
			case 'F':
				final String lSpriteName = lCursor.depth > 5 ? pLSystemDef.branchNodeSpriteName : pLSystemDef.leafNodeSpriteName;
				SpriteGraphNodeInst lNewNodeInst = new SpriteGraphNodeInst(lNewInst, "TreeSpriteSheet", lSpriteName, "", lCursor.depth);
				lNewNodeInst.rotateRel((float)Math.toRadians(lCursor.curRotation));
				pLSystemDef.onNodeCreation(lNewNodeInst);

				lCursor.currentNode.childNodes.add(lNewNodeInst);
				lCursor.currentNode = lNewNodeInst;

				// All rotations are relative to the current cursor heading
				// lCursor.curRotation = 0;

				lCursor.depth++;
				break;

			case '-':
				lCursor.curRotation -= RandomNumbers.random(pLSystemDef.getMinAngle(lCursor.depth), pLSystemDef.getMaxAngle(lCursor.depth));
				break;

			case '+':
				lCursor.curRotation += RandomNumbers.random(pLSystemDef.getMinAngle(lCursor.depth), pLSystemDef.getMaxAngle(lCursor.depth));
				break;

			case '[':
				lCursor.saveState();
				break;

			case ']':
				lCursor.restoreState();
				break;

			}

		}

		return lNewInst;

	}

}

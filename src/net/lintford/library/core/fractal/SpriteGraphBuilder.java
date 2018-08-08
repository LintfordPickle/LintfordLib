package net.lintford.library.core.fractal;

import net.lintford.library.core.geometry.spritegraph.SpriteGraphInst;
import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;

/** Builds SpriteGraphDefintions based on L-Systems. */
public class SpriteGraphBuilder {

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SpriteGraphInst createSpriteGraphFromLSystem(final String pLSystemString, LSystemDefinition pLSystemDef) {
		SpriteGraphInst lNewInst = new SpriteGraphInst();
		pLSystemDef.onGraphCreation(lNewInst);

		LCursor lCursor = new LCursor();

		// Create the rootnode of the tree
		lNewInst.rootNode = new SpriteGraphNodeInst(lNewInst, pLSystemDef.SpriteSheetName, pLSystemDef.rootNodeSpriteName, "", 0);
		pLSystemDef.onRootNodeCreation(lNewInst.rootNode);
		buildSpriteNode(pLSystemDef, lCursor, lNewInst.rootNode, null);
		lCursor.currentNode = lNewInst.rootNode;
		lCursor.depth++;

		for (int i = 0; i < pLSystemString.length(); i++) {
			char lCurrentInstruction = pLSystemString.charAt(i);

			// Update the cursor position ready for the next node in the tree
			switch (lCurrentInstruction) {
			case 'F':
				final String lSpriteName = lCursor.depth > 5 ? pLSystemDef.branchNodeSpriteName : pLSystemDef.leafNodeSpriteName;
				SpriteGraphNodeInst lNewNodeInst = new SpriteGraphNodeInst(lNewInst, "TreeSpriteSheet", lSpriteName, "", lCursor.depth);
				lNewNodeInst.rot = lCursor.curRotation;

				pLSystemDef.onNodeCreation(lNewNodeInst, lCursor.currentNode);

				buildSpriteNode(pLSystemDef, lCursor, lNewNodeInst, lCursor.currentNode);

				lCursor.currentNode.childNodes.add(lNewNodeInst);
				lCursor.currentNode = lNewNodeInst;

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

		// Iterate over the tree again, and change some leaf nodes to leaves
		assignLeafNodes(pLSystemDef, lNewInst, lNewInst.rootNode);

		return lNewInst;

	}

	private void assignLeafNodes(LSystemDefinition pLSystemDef, SpriteGraphInst pInst, SpriteGraphNodeInst pParentNode) {
		if (pParentNode == null)
			return;

		final int CHILD_COUNT = pParentNode.childNodes.size();

		// If this is a neaf node, attempt to make it a leaf
		if (CHILD_COUNT == 0 && RandomNumbers.getRandomChance(pParentNode.nodeDepth * 5)) {
			SpriteGraphNodeInst lNewNodeInst = new SpriteGraphNodeInst(pInst, "TreeSpriteSheet", "", "", pParentNode.nodeDepth + 1);

			Vector2f lBottomLeft = new Vector2f(pParentNode.getVertices()[2]);
			Vector2f lBottomRight = new Vector2f(pParentNode.getVertices()[3]);
			float lBottomEdgeCenterX = lBottomLeft.x + (lBottomRight.x - lBottomLeft.x) / 2;
			float lBottomEdgeCenterY = lBottomLeft.y + (lBottomRight.y - lBottomLeft.y) / 2;

			final float lHalfLeafSize = RandomNumbers.random(48f, 84f);
			lNewNodeInst.rotateAbs(RandomNumbers.random(0, (float) Math.PI * 2));
			lNewNodeInst.setVertices(
					new Vector2f[] { new Vector2f(lBottomEdgeCenterX + -lHalfLeafSize, lBottomEdgeCenterY + -lHalfLeafSize), new Vector2f(lBottomEdgeCenterX + lHalfLeafSize, lBottomEdgeCenterY + -lHalfLeafSize),
							new Vector2f(lBottomEdgeCenterX + -lHalfLeafSize, lBottomEdgeCenterY + lHalfLeafSize), new Vector2f(lBottomEdgeCenterX + lHalfLeafSize, lBottomEdgeCenterY + lHalfLeafSize) });

			pLSystemDef.onLeafCreation(lNewNodeInst, pParentNode);

			pParentNode.childNodes.add(lNewNodeInst);

		}

		// else check the children
		else {
			for (int i = 0; i < CHILD_COUNT; i++) {
				assignLeafNodes(pLSystemDef, pInst, pParentNode.childNodes.get(i));

			}
		}

	}

	private void buildSpriteNode(LSystemDefinition pLSystemDef, LCursor pCursor, SpriteGraphNodeInst pCurrentNode, SpriteGraphNodeInst pParentNode) {
		final float lBaseTrunkHalfWidth = 16f;

		if (pParentNode == null) {
			// Construct the root node (the tree trunk)
			pCurrentNode.setVertices(new Vector2f[] { new Vector2f(-32, -32), new Vector2f(32, -32), new Vector2f(-32, 32), new Vector2f(32, 32) });

			pCurrentNode.px = 0;
			pCurrentNode.py = 0;

		} else {

			// Maybe add a further definition of how the leaves of the trees should appear

			// line up the bottom of the sprite with the previous node
			Vector2f lBottomLeft = new Vector2f(pParentNode.getVertices()[0]);
			Vector2f lBottomRight = new Vector2f(pParentNode.getVertices()[1]);

			// if previous node was a root, then adjust width
			if (pCursor.depth == 1) {
				lBottomLeft.x = -lBaseTrunkHalfWidth;
				lBottomRight.x = lBaseTrunkHalfWidth;

			}

			float angleCos = (float) Math.cos(Math.toRadians(pCursor.curRotation));
			float angleSin = (float) Math.sin(Math.toRadians(pCursor.curRotation));

			float lBottomEdgeCenterX = lBottomLeft.x + (lBottomRight.x - lBottomLeft.x) / 2;
			float lBottomEdgeCenterY = lBottomLeft.y + (lBottomRight.y - lBottomLeft.y) / 2;

			float lTopEdgeCenterX = lBottomEdgeCenterX + angleCos * 32f;
			float lTopEdgeCenterY = lBottomEdgeCenterY + angleSin * 32f;

			// Move the top vertices into the correct position
			Vector2f lTopLeft = new Vector2f(-edgeHalfWidth(pLSystemDef, pCursor.depth), 0);
			lTopLeft.rotate(pCursor.curRotation + 90);
			lTopLeft.add(lTopEdgeCenterX, lTopEdgeCenterY);
			Vector2f lTopRight = new Vector2f(edgeHalfWidth(pLSystemDef, pCursor.depth), 0);
			lTopRight.rotate(pCursor.curRotation + 90);
			lTopRight.add(lTopEdgeCenterX, lTopEdgeCenterY);

			// Construct the root node (the tree trunk)
			pCurrentNode.setVertices(new Vector2f[] { lTopLeft, lTopRight, lBottomLeft, lBottomRight });

		}

	}

	private float edgeHalfWidth(LSystemDefinition pLSystemDef, int pNodeDepth) {
		return 16 - (1.2f * pNodeDepth);
	}

}

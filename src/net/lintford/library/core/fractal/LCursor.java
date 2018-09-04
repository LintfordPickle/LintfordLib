package net.lintford.library.core.fractal;

import java.util.ArrayDeque;
import java.util.Deque;

import net.lintford.library.core.geometry.spritegraph.SpriteGraphNodeInst;

public class LCursor {
	public Deque<LCursor> cursorStack = new ArrayDeque<>();
	public float x, y;
	public float curRotation;
	public int depth;
	public SpriteGraphNodeInst currentNode;

	public LCursor() {
		x = 0;
		y = 0;
		curRotation = -90;
		currentNode = null;
		depth = 0;

	}

	public LCursor(SpriteGraphNodeInst pCurGraphNode, int pDepth) {
		currentNode = pCurGraphNode;
		x = currentNode.x;
		y = currentNode.y;
		curRotation = currentNode.rotation();
		depth = pDepth;

	}

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

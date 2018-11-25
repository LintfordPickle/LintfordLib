package net.lintford.library.core.lindenmeyer;

import java.util.ArrayDeque;
import java.util.Deque;

public class LCursor {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public Deque<LCursor> cursorStack = new ArrayDeque<>();
	public float x, y;
	public float rotation;
	public int depth;

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public LCursor() {
		x = 0;
		y = 0;
		rotation = 0;
		depth = 0;

	}

	public LCursor(float pX, float pY, float pRotation, int pDepth) {
		x = pX;
		y = pY;
		rotation = pRotation;
		depth = pDepth;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void saveState() {
		cursorStack.push(new LCursor(x, y, rotation, depth));

	}

	public void restoreState() {
		LCursor lSavedState = cursorStack.poll();

		if (lSavedState != null) {
			x = lSavedState.x;
			y = lSavedState.y;
			rotation = lSavedState.rotation;
			depth = lSavedState.depth;

		}

	}

}

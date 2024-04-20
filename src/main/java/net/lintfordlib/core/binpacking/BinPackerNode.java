package net.lintfordlib.core.binpacking;

class BinPackerNode {

	// --------------------------------------
	// Variables
	// --------------------------------------

	boolean used;
	boolean isPlaced;
	int x, y;
	int width, height;
	BinPackerNode down;
	BinPackerNode right;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	BinPackerNode(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

}

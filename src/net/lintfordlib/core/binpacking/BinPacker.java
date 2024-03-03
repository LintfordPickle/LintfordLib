package net.lintfordlib.core.binpacking;

import java.util.List;

import net.lintfordlib.core.debug.Debug;

public class BinPacker {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final BinPackerNode mRootNode;
	private int mWidth;
	private int mHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	// --------------------------------------
	// COnstructor
	// --------------------------------------

	public BinPacker(int width, int height) {
		mWidth = width;
		mHeight = height;
		mRootNode = new BinPackerNode(0, 0, width, height);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void fitNodes(List<IBinPackedItem> nodesToPack) {
		final var lNumNodes = nodesToPack.size();
		for (int i = 0; i < lNumNodes; i++) {
			final var lBin = nodesToPack.get(i);
			final var lBinWidth = lBin.binWidth();
			final var lBinHeight = lBin.binHeight();

			final var foundNode = findNode(mRootNode, (int) lBinWidth, (int) lBinHeight);
			if (foundNode != null) {
				splitNode(foundNode, (int) lBinWidth, (int) lBinHeight);
				lBin.assignToBin((int) foundNode.x, (int) foundNode.y, lBinWidth, lBinHeight);

			} else {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't fit bin into texture");
			}
		}
	}

	private BinPackerNode findNode(BinPackerNode nodeToCheck, int width, int height) {
		// go down the tree and find the next unused node
		if (nodeToCheck.used) {
			var returnNode = findNode(nodeToCheck.right, width, height);

			if (returnNode != null)
				return returnNode;

			return findNode(nodeToCheck.down, width, height);
		} else if ((width <= nodeToCheck.width) && (height <= nodeToCheck.height)) {
			return nodeToCheck; // this node is big enough
		}

		return null; // not enough space
	}

	private void splitNode(BinPackerNode node, int width, int height) {
		node.used = true;
		node.down = new BinPackerNode(node.x, node.y + height, node.width, node.height - height);
		node.right = new BinPackerNode(node.x + width, node.y, node.width - width, height);
	}

}

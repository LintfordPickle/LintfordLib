package net.lintfordlib.core.binpacking;

import java.util.List;

import net.lintfordlib.core.debug.Debug;

public class BinPacker {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final BinPackerNode mRootNode;
	private final String mBinName;
	private final int mWidth;
	private final int mHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mBinName;
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	// --------------------------------------
	// COnstructor
	// --------------------------------------

	public BinPacker(String binName, int width, int height) {
		mWidth = width;
		mHeight = height;
		mBinName = binName;
		mRootNode = new BinPackerNode(0, 0, width, height);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public List<IBinPackedItem> fitNodes(List<IBinPackedItem> nodesToPack) {
		var lLastInsertionSuccessful = true;
		while (lLastInsertionSuccessful && !nodesToPack.isEmpty()) {

			final var lItem = nodesToPack.remove(0);

			final var lItemWidth = lItem.itemWidth();
			final var lItemHeight = lItem.itemHeight();

			final var foundNode = findNode(mRootNode, (int) lItemWidth, (int) lItemHeight);
			if (foundNode != null) {
				splitNode(foundNode, (int) lItemWidth, (int) lItemHeight);
				lItem.assignToBin(mBinName, foundNode.x, foundNode.y, lItemWidth, lItemHeight);

			} else {
				nodesToPack.add(lItem);
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Failed to fit node in bin! No more space");
				lLastInsertionSuccessful = false;
			}
		}

		// Return the list of remaining (unassigned) items
		return nodesToPack;
	}

	private BinPackerNode findNode(BinPackerNode nodeToCheck, int width, int height) {
		if (nodeToCheck.used) {

			// As this node is being used, we can check down the tree to the right and below for free space.
			var returnNode = findNode(nodeToCheck.right, width, height);

			if (returnNode != null)
				return returnNode;

			return findNode(nodeToCheck.down, width, height);

		} else if ((width <= nodeToCheck.width) && (height <= nodeToCheck.height)) {

			// As this node is not being used, and has enough space, we will use it.
			return nodeToCheck;
		}

		// Nothing found on this branch.
		return null;
	}

	private void splitNode(BinPackerNode node, int width, int height) {
		node.used = true;

		node.right = new BinPackerNode(node.x + width, node.y, node.width - width, height);
		node.down = new BinPackerNode(node.x, node.y + height, node.width, node.height - height);
	}

}
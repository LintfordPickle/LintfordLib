package net.lintfordlib.core.graphics.decalbin;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.binpacking.IBinPackedItem;

public class DecalReceiverManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_UID_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<IBinPackedItem> mDecalReceivers = new ArrayList<>();
	private float mDecalMapSizeWidth;
	private float mDecalMapSizeHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float decalMapSizeWidth() {
		return mDecalMapSizeWidth;
	}

	public float decalMapSizeHeight() {
		return mDecalMapSizeHeight;
	}

	public List<IBinPackedItem> decalReceivers() {
		return mDecalReceivers;
	}

	public IBinPackedItem getDecalBinByUid(int binUid) {
		if (binUid < 0 || binUid >= mDecalReceivers.size())
			return null;

		return mDecalReceivers.get(binUid);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DecalReceiverManager() {
		mDecalMapSizeWidth = 2048;
		mDecalMapSizeHeight = 2048;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addDecalReceiver(IBinPackedItem receiver) {
		if (mDecalReceivers.contains(receiver) == false)
			mDecalReceivers.add(receiver);
	}

	public void removeDecalReceiver(IBinPackedItem receiver) {
		if (mDecalReceivers.contains(receiver))
			mDecalReceivers.remove(receiver);
	}

}

package net.lintfordlib.core.graphics.decalbin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class DecalManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_UID_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName("DecalBins")
	protected final Map<String, DecalBin> mBins = new HashMap<>();

	// This list of receivers is only used to mark which items should be considered for packing into decal bins - it is not serialized with the track data
	// (the individual items will contain their atlas UVs and atlas reference data).
	private final List<IDecalBinPackedItem> mDecalReceivers = new ArrayList<>();

	protected int mDecalBinWidth;
	protected int mDecalBinHeight;

	private IDecalManagerListener mListener;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Map<String, DecalBin> bins() {
		return mBins;
	}

	public void addListener(IDecalManagerListener listener) {
		mListener = listener;
	}

	public IDecalManagerListener addListener() {
		return mListener;
	}

	public List<IDecalBinPackedItem> decalReceivers() {
		return mDecalReceivers;
	}

	public IDecalBinPackedItem getDecalBinByUid(int binUid) {
		if (binUid < 0 || binUid >= mDecalReceivers.size())
			return null;

		return mDecalReceivers.get(binUid);
	}

	public int binWidth() {
		return mDecalBinWidth;
	}

	public void binWidth(int newWidth) {
		if (newWidth < 128)
			newWidth = 128;

		if (newWidth > 2048)
			newWidth = 2048;

		mDecalBinWidth = newWidth;
	}

	public int binHeight() {
		return mDecalBinHeight;
	}

	public void binHeight(int newHeight) {
		if (newHeight < 128)
			newHeight = 128;

		if (newHeight > 2048)
			newHeight = 2048;

		mDecalBinHeight = newHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DecalManager() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean isReceivingDecals(IDecalBinPackedItem receiver) {
		return mDecalReceivers.contains(receiver);
	}

	public void addDecalReceiver(IDecalBinPackedItem receiver) {
		if (mDecalReceivers.contains(receiver) == false) {
			mDecalReceivers.add(receiver);

			if (mListener != null)
				mListener.onItemAdded();

		}
	}

	public void removeDecalReceiver(IDecalBinPackedItem receiver) {
		if (mDecalReceivers.contains(receiver)) {
			mDecalReceivers.remove(receiver);

			if (mListener != null)
				mListener.onItemRemoved();

		}
	}

	public void clearBins() {
		mBins.clear();

		if (mListener != null)
			mListener.onItemRemoved();
	}
}

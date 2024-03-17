package net.lintfordlib.core.graphics.decalbin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.binpacking.BinPacker;

public class DecalManager {

	public class DecalBin {
		private BinPacker _BinPacker;

		public BinPacker binPacker() {
			if (_BinPacker == null)
				_BinPacker = new BinPacker(binName, decalAtlasWidth, decalAtlasHeight);

			return _BinPacker;
		}

		public String binName;
		public String decalAtlasTextureName;
		public int decalAtlasWidth;
		public int decalAtlasHeight;

		public DecalBin(String name, String textureFilename, int width, int height) {
			binName = name;
			decalAtlasTextureName = textureFilename;
			decalAtlasWidth = width;
			decalAtlasHeight = height;
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_UID_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName("DecalBins")
	private final Map<String, DecalBin> mBins = new HashMap<>();

	private final List<IDecalBinPackedItem> mDecalReceivers = new ArrayList<>();
	private int mDecalMapSizeWidth;
	private int mDecalMapSizeHeight;

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

	public int decalMapSizeWidth() {
		return mDecalMapSizeWidth;
	}

	public int decalMapSizeHeight() {
		return mDecalMapSizeHeight;
	}

	public List<IDecalBinPackedItem> decalReceivers() {
		return mDecalReceivers;
	}

	public IDecalBinPackedItem getDecalBinByUid(int binUid) {
		if (binUid < 0 || binUid >= mDecalReceivers.size())
			return null;

		return mDecalReceivers.get(binUid);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DecalManager() {
		mDecalMapSizeWidth = 2048;
		mDecalMapSizeHeight = 2048;
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
	}

	public DecalBin getOrCreateBinPacker(String binName, int width, int height) {
		var lBin = mBins.get(binName);

		if (lBin == null) {
			lBin = new DecalBin(binName, "res/textures/decalAtlases/" + binName + ".png", width, height);
			mBins.put(binName, lBin);
		}

		return lBin;

	}
}

package net.lintfordlib.core.entities.definitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.storage.FileUtils;

public class MetaFileHeader implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 1750417953009665723L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Defines the directory within which the individual items are located. */
	@SerializedName(value = "AssetRootDirectory")
	private String mAssetRootDirectory;

	/** The filepaths of the individual items included in this meta pack. */
	@SerializedName(value = "ItemFilepaths")
	private List<String> mItemFilepaths;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns the asset root directory, within which all the assets are contained.
	 */
	public String assetRootDirectory() {
		return mAssetRootDirectory;
	}

	/**
	 * Sets the asset root directory, within which all the assets will be contained (all items will be contained within the directory, i.e. no sub-dirs).
	 */
	public void assetRootDirectory(String newAssetRootDirectory) {
		mAssetRootDirectory = newAssetRootDirectory;

		if (mAssetRootDirectory.endsWith(FileUtils.FILE_SEPERATOR) == false)
			mAssetRootDirectory += FileUtils.FILE_SEPERATOR;
	}

	/**
	 * Returns an array of item filepaths contain within this asset pack.
	 */
	public List<String> itemFilepaths() {
		return mItemFilepaths;
	}

	/**
	 * Overrides the previous array of item filepaths with the given array.
	 */
	public void setNetAssetFilepaths(List<String> newItemFilepaths) {
		if (mItemFilepaths == null)
			mItemFilepaths = new ArrayList<String>();

		mItemFilepaths.clear();
		mItemFilepaths = newItemFilepaths;
	}

	/**
	 * Returns the number of individual items in this asset pack.
	 */
	public int numItems() {
		if (mItemFilepaths == null)
			return 0;

		return mItemFilepaths.size();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MetaFileHeader() {
		mItemFilepaths = new ArrayList<>();
	}

}

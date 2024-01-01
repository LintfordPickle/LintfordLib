package net.lintfordlib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;

/**
 * The {@link DataManager} is a container for {@link BaseDataManager} objects. It provides methods to store and retrieve (by casting) the data managers at runtime. This class takes no action in the life-cycle of the contained instances.
 */
public class DataManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LintfordCore mCore;
	private int mDataManagerIdCounter;
	private Map<Integer, List<BaseDataManager>> mDataManagers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Map<Integer, List<BaseDataManager>> allDataManagers() {
		return mDataManagers;
	}

	/** Returns a list of data managers currently registered with the {@link DataManager} identified by the given data group id. */
	public List<BaseDataManager> dataManagers(int dataGroupId) {
		return mDataManagers.get(dataGroupId);
	}

	public LintfordCore core() {
		return mCore;
	}

	public int getNewDataManagerId() {
		return mDataManagerIdCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DataManager(LintfordCore core) {
		mCore = core;
		mDataManagerIdCounter = 0;
		mDataManagers = new HashMap<>();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public BaseDataManager getDataManagerByType(Class<?> dataManagerClass, int entityGroupUid) {
		final var lBaseDataManagerList = dataManagers(entityGroupUid);
		if (lBaseDataManagerList == null)
			return null;

		final var lCount = lBaseDataManagerList.size();
		for (int i = 0; i < lCount; i++) {
			if (lBaseDataManagerList.get(i).getClass().equals(dataManagerClass)) {
				return lBaseDataManagerList.get(i);
			}
		}

		return null;
	}

	public List<BaseDataManager> getDataManagerGroupByUid(int entityGroupUid) {
		return dataManagers(entityGroupUid);
	}

	/**
	 * Returns the {@link BaseDataManager} with the given name. In case no {@link BaseDataManager} instance with the given name is found (i.e. has not been registered), an exception will be thrown.
	 */
	public BaseDataManager getDataManagerByNameRequired(String dataManagerName, int entityGroupUid) {
		final var lDataManager = getDataManagerByName(dataManagerName, entityGroupUid);

		if (lDataManager == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Required DataManager not found: " + dataManagerName);
			throw new RuntimeException(String.format("Required DataManager not found: %s. Check you are using the correct entityGroupUid", dataManagerName));
		}

		return lDataManager;
	}

	/** Returns the {@link BaseDataManager} with the given name. If no data manager is found, null is returned. */
	public BaseDataManager getDataManagerByName(String dataManagerName, int entityGroupUid) {
		if (dataManagerName == null || dataManagerName.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "A DataManager was requested but no name was specified");
			return null;
		}

		final var lDataManagerList = dataManagers(entityGroupUid);
		if (lDataManagerList == null)
			return null;

		final var lCount = lDataManagerList.size();
		for (int i = 0; i < lCount; i++) {
			if (lDataManagerList.get(i).dataManagerName().equals(dataManagerName)) {
				return lDataManagerList.get(i);
			}
		}

		return null;
	}

	/** Returns true if a {@link BaseDataManager} has been registered with the given name. */
	public boolean dataManagerExists(final String dataManagerName, int entityGroupUid) {
		return getDataManagerByName(dataManagerName, entityGroupUid) != null;
	}

	public void addDataManager(BaseDataManager dataManager, int entityGroupUid) {
		if (getDataManagerByName(dataManager.dataManagerName(), entityGroupUid) == null) {
			var lDataManagerList = dataManagers(entityGroupUid);
			if (lDataManagerList == null) {
				lDataManagerList = new ArrayList<>();
				mDataManagers.put(entityGroupUid, lDataManagerList);
			}

			lDataManagerList.add(dataManager);
		}
	}

	public void removeDataManager(BaseDataManager dataManager, int entityGroupUid) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Removing DataManager " + dataManager.dataManagerName() + " from id:" + entityGroupUid);

		final var lDataManagerList = dataManagers(entityGroupUid);
		if (lDataManagerList == null)
			return;

		if (lDataManagerList.contains(dataManager))
			lDataManagerList.remove(dataManager);

		dataManager.unload();
	}

	/** Unloads all {@link BaseDataManager} instances registered to this {@link DataManager} which have the given group ID assigned to them. */
	public void removeDataManagerGroup(final int entityGroupUid) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Removing DataManagerGroup id:" + entityGroupUid);

		final var lDataManagerList = mDataManagers.get(entityGroupUid);
		if (lDataManagerList == null)
			return;

		final var lDataManagerCount = lDataManagerList.size();
		for (int i = 0; i < lDataManagerCount; i++) {
			lDataManagerList.get(i).unload();
		}

		lDataManagerList.clear();
		mDataManagers.remove(entityGroupUid);
	}

	public void removeAllDataManagers() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Removing all data managers");

		for (var lDataManagerEntry : mDataManagers.entrySet()) {
			final var lDataManagerGroupKey = lDataManagerEntry.getKey();
			removeDataManagerGroup(lDataManagerGroupKey);
		}

		mDataManagers.clear();
	}

}

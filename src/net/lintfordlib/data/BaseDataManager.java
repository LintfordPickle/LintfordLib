package net.lintfordlib.data;

public abstract class BaseDataManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mEntityGroupUid;
	protected String mDataManagerName;
	protected DataManager mDataManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entityGroupUid() {
		return mEntityGroupUid;
	}

	public String dataManagerName() {
		return mDataManagerName;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseDataManager(DataManager dataManager, String dataManagerName, int entityGroupUid) {
		if (dataManager == null)
			throw new RuntimeException("DataManager cannot be null!");

		if (dataManagerName.length() == 0)
			throw new RuntimeException("DataManager names cannot be null or empty when registering with a DataManager");

		if (dataManager.dataManagerExists(dataManagerName, entityGroupUid))
			throw new RuntimeException("Cannot register two data managers with the same name");

		mDataManager = dataManager;
		mDataManagerName = dataManagerName;
		mDataManager.addDataManager(this, entityGroupUid);
		mEntityGroupUid = entityGroupUid;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void unload() {

	}

	protected BaseDataManager getDataManagerByNameRequired(Class<?> classType) {
		return mDataManager.getDataManagerByNameRequired(classType.getSimpleName(), mEntityGroupUid);
	}

	protected BaseDataManager getDataManagerByNameRequired(String typeName) {
		return mDataManager.getDataManagerByNameRequired(typeName, mEntityGroupUid);
	}
}

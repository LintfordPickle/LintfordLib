package net.lintfordlib.core.entities.definitions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.storage.FileUtils;

public abstract class DefinitionManager<T extends BaseDefinition> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final short NO_DEFINITION = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Map<String, T> mDefinitions;
	protected short mDefinitionUIDCounter;

	// TODO: The DefinitionLookUp was supposed to help
	protected DefinitionLookUp mDefinitionsLookupTable;

	private final List<IDefinitionManagerChangeListener> mChangeListeners = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void addChangeListener(IDefinitionManagerChangeListener listener) {
		if (mChangeListeners.contains(listener) == false)
			mChangeListeners.add(listener);
	}

	public void removeChangeListener(IDefinitionManagerChangeListener listener) {
		if (mChangeListeners.contains(listener))
			mChangeListeners.remove(listener);
	}

	public Collection<T> definitions() {
		return mDefinitions.values();
	}

	public DefinitionLookUp definitionsLookupTable() {
		return mDefinitionsLookupTable;
	}

	public int definitionCount() {
		return mDefinitions.size();
	}

	public short getNewDefinitionUID() {
		return mDefinitionUIDCounter++;
	}

	// --------------------------------------

	public T getByUid(short definitionUid) {
		return mDefinitions.get(mDefinitionsLookupTable.getDefinitionNameByUid(definitionUid));
	}

	public T getByName(String definitionName) {
		return mDefinitions.get(definitionName);
	}

	public T getByDisplayName(final String displayName) {
		final var lDefinitions = mDefinitions.values();
		for (final var definition : lDefinitions) {
			if (definition.displayName != null && definition.displayName.equals(displayName)) {
				return definition;
			}
		}

		return null;
	}

	public short getUidByName(String definitionName) {
		return mDefinitionsLookupTable.getDefinitionUidByName(definitionName);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DefinitionManager() {
		mDefinitions = new HashMap<>();
		mDefinitionsLookupTable = new DefinitionLookUp();
		mDefinitionUIDCounter = 0;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void clearDefinitions() {
		mDefinitions.clear();
	}

	public abstract void loadDefinitionsFromFolderWatcher(EntityLocationProvider entityLocationProvider);

	public abstract void loadDefinitionsFromMetaFile(File file);

	public abstract T loadDefinitionFromFile(File file);

	public void afterDefinitionLoaded(T definition) {
		notifyListenersOfChange();
	}

	protected void loadDefinitionsFromFolderWatcherItems(EntityLocationProvider entityLocationProvider, final Gson gson, Class<T> classType) {
		final var lFolderFileIterator = entityLocationProvider.getFileLocationIterator();
		for (Iterator<String> lFileIterator = lFolderFileIterator; lFileIterator.hasNext();) {

			final var lFile = new File(lFileIterator.next());

			loadDefinitionFromFile(lFile, gson, classType);
		}
	}

	protected void loadDefinitionsFromMetaFileItems(File metaFile, final Gson gson, Class<T> classType) {
		final var lMetaItems = MetaFileHeaderIo.loadFromFilepath(metaFile);

		loadDefinitionsFromMetaFileItems(lMetaItems, gson, classType);
	}

	protected void loadDefinitionsFromMetaFileItems(MetaFileHeader assetPackHeader, final Gson gson, Class<T> classType) {
		if (assetPackHeader == null || assetPackHeader.numItems() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Cannot load definition types %s, the given MetaFileItems contains no data", classType.getSimpleName()));
			return;
		}

		var lDefRootDirectory = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);

		for (int i = 0; i < assetPackHeader.numItems(); i++) {
			final var lDefinitionFilepath = lDefRootDirectory + FileUtils.FILE_SEPERATOR + assetPackHeader.assetRootDirectory() + assetPackHeader.itemFilepaths().get(i);
			final var lFile = new File(lDefinitionFilepath);
			final var lNewDef = loadDefinitionFromFile(lFile, gson, classType);

			if (lNewDef != null) {
				afterDefinitionLoaded(lNewDef);
			}
		}
	}

	protected T loadDefinitionFromFile(File lDefinitionFile, final Gson gson, Class<T> classType) {
		if (!lDefinitionFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Error loading %s from file: %s (file not found)", classType.getSimpleName(), lDefinitionFile.getAbsoluteFile()));
			return null;
		}

		try {
			Debug.debugManager().logger().v(getClass().getSimpleName(), String.format("Loading Definition type %s from file: %s", classType.getSimpleName(), lDefinitionFile.getAbsoluteFile()));

			final var lFileContents = new String(Files.readAllBytes(lDefinitionFile.toPath()));
			final var lNewDefinition = gson.fromJson(lFileContents, classType);

			if (lNewDefinition != null) {
				if (lNewDefinition.name == null) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Definition at path doesn't contain definition name: %s", lDefinitionFile.getAbsoluteFile()));
				}

				lNewDefinition.filepath = lDefinitionFile.getAbsolutePath();

				addDefintion(lNewDefinition);

				notifyListenersOfChange();
				
				return lNewDefinition;
			} else {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse %s from file: %s", classType.getSimpleName(), lDefinitionFile.getAbsoluteFile()));
			}
		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (JsonSyntaxException): %s", classType.getSimpleName(), lDefinitionFile.getAbsoluteFile()));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (IOException): %s", classType.getSimpleName(), lDefinitionFile.getAbsoluteFile()));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		} catch (NumberFormatException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (NumberFormatException): %s", classType.getSimpleName(), lDefinitionFile.getAbsoluteFile()));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		}

		return null;
	}

	public void addDefintion(T newDefinition) {
		if (newDefinition == null)
			return;

		if (newDefinition.name == null)
			return;

		short lDefinitionUid = 0;
		if (mDefinitionsLookupTable.containsDefinitionName(newDefinition.name)) {
			lDefinitionUid = mDefinitionsLookupTable.getDefinitionUidByName(newDefinition.name);
		} else {
			lDefinitionUid = getNewDefinitionUID();
			mDefinitionsLookupTable.addNewDefinition(lDefinitionUid, newDefinition.name);
		}

		newDefinition.initialize(lDefinitionUid);
		mDefinitions.put(newDefinition.name, newDefinition);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void notifyListenersOfChange() {
		final var lNumListeners = mChangeListeners.size();
		for (int i = 0; i < lNumListeners; i++) {
			mChangeListeners.get(i).definitionManagerChanged();
		}
	}

	private String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	protected String getSHA(Object obj) throws IOException, NoSuchAlgorithmException {
		if (obj == null)
			return "";

		final var baos = new ByteArrayOutputStream();
		final var oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();

		final var lMessageDigest = MessageDigest.getInstance("SHA1");
		lMessageDigest.update(baos.toByteArray());

		return bytesToHex(lMessageDigest.digest()).toString();
	}

	public void setDefinitionLookupTable(DefinitionLookUp definitionLookup) {
		mDefinitionsLookupTable = definitionLookup;

		validateDefinitions();
	}

	private void validateDefinitions() {

	}
}
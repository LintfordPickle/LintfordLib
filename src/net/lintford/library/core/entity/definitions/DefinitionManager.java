package net.lintford.library.core.entity.definitions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.EntityLocationProvider;

public abstract class DefinitionManager<T extends BaseDefinition> {

	public static class MetaFileItems {
		public String rootDirectory;
		public String[] itemFileLocations;
		public int itemCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final short NO_DEFINITION = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Map<String, T> mDefinitions;

	protected short mDefinitionUIDCounter;
	protected DefinitionLookUp mDefinitionsLookupTable;

	// --------------------------------------
	// Properties
	// --------------------------------------

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
		final var lDefName = mDefinitionsLookupTable.getDefinitionNameByUid(definitionUid);
		return mDefinitions.get(lDefName);
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

	public abstract void loadDefinitionsFromFolderWatcher(EntityLocationProvider pEntityLocationProvider);

	public abstract void loadDefinitionsFromMetaFile(String pMetaFilepath);

	public abstract void loadDefinitionFromFile(String pFilepath);

	public void afterDefinitionLoaded(T definition) {
	}

	protected MetaFileItems loadMetaFileItemsFromFilepath(final String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load definitions files from a <null> Metafile!");
			return null;
		}

		try {
			final var lGson = new GsonBuilder().create();
			final var lFileContents = new String(Files.readAllBytes(Paths.get(pFilepath)));
			final var lItemsFileLocations = lGson.fromJson(lFileContents, MetaFileItems.class);

			if (lItemsFileLocations == null || lItemsFileLocations.itemFileLocations == null || lItemsFileLocations.itemFileLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load item filepaths from the Metafile!");

				return null;
			}

			lItemsFileLocations.itemCount = lItemsFileLocations.itemFileLocations.length;
			return lItemsFileLocations;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Error while loading metafile filepaths.");
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

		}

		return null;
	}

	protected void loadDefinitionsFromFolderWatcherItems(EntityLocationProvider pEntityLocationProvider, final Gson pGson, Class<T> pClassType) {
		final var lFolderFileIterator = pEntityLocationProvider.getFileLocationIterator();
		for (Iterator<String> lFileIterator = lFolderFileIterator; lFileIterator.hasNext();) {
			loadDefinitionFromFile(lFileIterator.next(), pGson, pClassType);
		}
	}

	protected void loadDefinitionsFromMetaFileItems(String pMetaFilepath, final Gson pGson, Class<T> pClassType) {
		final var lMetaItems = loadMetaFileItemsFromFilepath(pMetaFilepath);
		if (lMetaItems == null || lMetaItems.itemCount == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Cannot load definition types %s, the given MetaFileItems contains no data", pClassType.getSimpleName()));
			return;
		}

		for (int i = 0; i < lMetaItems.itemCount; i++) {
			final var lDefinitionFilepath = lMetaItems.rootDirectory + lMetaItems.itemFileLocations[i] + ".json";

			final var lNewDef = loadDefinitionFromFile(lDefinitionFilepath, pGson, pClassType);
			if (lNewDef != null) {
				afterDefinitionLoaded(lNewDef);
			}
		}
	}

	protected T loadDefinitionFromFile(String pFilepath, final Gson pGson, Class<T> pClassType) {
		final var lDefinitionFile = new File(pFilepath);

		if (!lDefinitionFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Error loading %s from file: %s (file not found)", pClassType.getSimpleName(), pFilepath));
			return null;
		}

		try {
			Debug.debugManager().logger().v(getClass().getSimpleName(), String.format("Loading Definition type %s from file: %s", pClassType.getSimpleName(), pFilepath));

			final var lFileContents = new String(Files.readAllBytes(lDefinitionFile.toPath()));
			final var lNewDefinition = pGson.fromJson(lFileContents, pClassType);

			if (lNewDefinition != null) {
				addDefintion(lNewDefinition);

				return lNewDefinition;
			} else {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse %s from file: %s", pClassType.getSimpleName(), pFilepath));
			}

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (JsonSyntaxException): %s", pClassType.getSimpleName(), pFilepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (IOException): %s", pClassType.getSimpleName(), pFilepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		} catch (NumberFormatException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (NumberFormatException): %s", pClassType.getSimpleName(), pFilepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		}

		return null;
	}

	public void addDefintion(T newDefinition) {
		if (newDefinition == null)
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
		if (obj == null) {
			return "";
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();

		MessageDigest lMessageDigest = MessageDigest.getInstance("SHA1");
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
package net.lintfordlib.core.entities.definitions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.storage.FileUtils;

public abstract class DefinitionManager<T extends BaseDefinition> {

	public static class MetaFileItems implements Serializable {

		private static final long serialVersionUID = 1750417953009665723L;

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

	// TODO: The DefinitionLookUp was supposed to help
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

//
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

	public abstract void loadDefinitionsFromMetaFile(String metaFilepath);

	public abstract void loadDefinitionFromFile(String filepath);

	public void afterDefinitionLoaded(T definition) {
	}

	protected MetaFileItems loadMetaFileItemsFromFilepath(final String filepath) {
		if (filepath == null || filepath.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load definitions files from a <null> Metafile!");
			return null;
		}

		try {
			final var lGson = new GsonBuilder().create();
			final var lFileContents = new String(Files.readAllBytes(Paths.get(filepath)));
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

	public boolean isFileValidMetadataFile(String metaFilepath) {
		if (metaFilepath == null || metaFilepath.length() == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Metadata Filename is incorrectly formatted or null.");
			return false;
		}

		try {
			final var lGson = new GsonBuilder().create();
			final var lFileContents = new String(Files.readAllBytes(Paths.get(metaFilepath)));
			final var lItemsFileLocations = lGson.fromJson(lFileContents, MetaFileItems.class);

			if (lItemsFileLocations == null || lItemsFileLocations.itemFileLocations == null || lItemsFileLocations.itemFileLocations.length == 0) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load item filepaths from the Metafile!");
				return false;
			}

			lItemsFileLocations.itemCount = lItemsFileLocations.itemFileLocations.length;
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Metadata file contains " + lItemsFileLocations.itemCount + " definitions");
			return true;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Error while loading metafile filepaths.");
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}

		return false;
	}

	public void saveDefinitionsToMetadataFile(String metaFilepath) {
		final var lMetaItemsList = new MetaFileItems();
		lMetaItemsList.itemCount = mDefinitions.size();

		int counter = 0;
		for (var value : mDefinitions.values()) {
			lMetaItemsList.itemFileLocations[counter] = value.filename;
			counter++;
		}

		final var gson = new Gson();
		try {
			gson.toJson(lMetaItemsList, new FileWriter(metaFilepath));
		} catch (JsonIOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save meta data file - incorrect Json!");
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to save meta data file - incorrect Json!");
			Debug.debugManager().logger().printException(getClass().getSimpleName(), e);
		}
	}

	protected void loadDefinitionsFromFolderWatcherItems(EntityLocationProvider entityLocationProvider, final Gson gson, Class<T> classType) {
		final var lFolderFileIterator = entityLocationProvider.getFileLocationIterator();
		for (Iterator<String> lFileIterator = lFolderFileIterator; lFileIterator.hasNext();) {
			loadDefinitionFromFile(lFileIterator.next(), gson, classType);
		}
	}

	protected void loadDefinitionsFromMetaFileItems(String metaFilepath, final Gson gson, Class<T> classType) {
		final var lMetaItems = loadMetaFileItemsFromFilepath(metaFilepath);

		if (lMetaItems == null || lMetaItems.itemCount == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Cannot load definition types %s, the given MetaFileItems contains no data", classType.getSimpleName()));
			return;
		}

		for (int i = 0; i < lMetaItems.itemCount; i++) {
			var lDefRootDirectory = System.getProperty("user.dir"); // If the user.dir was set (by the editor), then use it

			final var lDefinitionFilepath = lDefRootDirectory + FileUtils.FILE_SEPERATOR + lMetaItems.rootDirectory + lMetaItems.itemFileLocations[i] + ".json";

			final var lNewDef = loadDefinitionFromFile(lDefinitionFilepath, gson, classType);
			if (lNewDef != null) {
				afterDefinitionLoaded(lNewDef);
			}
		}
	}

	protected T loadDefinitionFromFile(String filepath, final Gson gson, Class<T> classType) {
		final var lDefinitionFile = new File(filepath);

		if (!lDefinitionFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Error loading %s from file: %s (file not found)", classType.getSimpleName(), filepath));
			return null;
		}

		try {
			Debug.debugManager().logger().v(getClass().getSimpleName(), String.format("Loading Definition type %s from file: %s", classType.getSimpleName(), filepath));

			final var lFileContents = new String(Files.readAllBytes(lDefinitionFile.toPath()));
			final var lNewDefinition = gson.fromJson(lFileContents, classType);

			if (lNewDefinition != null) {
				if (lNewDefinition.name == null) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Definition at path doesn't contain definition name: %s", filepath));
				}

				lNewDefinition.filename = filepath;

				addDefintion(lNewDefinition);

				return lNewDefinition;
			} else {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse %s from file: %s", classType.getSimpleName(), filepath));
			}
		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (JsonSyntaxException): %s", classType.getSimpleName(), filepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (IOException): %s", classType.getSimpleName(), filepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());
		} catch (NumberFormatException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (NumberFormatException): %s", classType.getSimpleName(), filepath));
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
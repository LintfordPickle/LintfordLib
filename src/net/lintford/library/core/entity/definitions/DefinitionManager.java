package net.lintford.library.core.entity.definitions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.BaseData;

public abstract class DefinitionManager<T extends BaseDefinition> extends BaseData {

	public static class MetaFileItems {
		public String rootDirectory;
		public String[] itemFileLocations;
		public int itemCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1729184288330735542L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<T> mDefinitions;

	protected transient short mDefinitionUIDCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<T> definitions() {
		return mDefinitions;
	}

	public short getNewDefinitionUID() {
		return mDefinitionUIDCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DefinitionManager() {
		mDefinitions = new ArrayList<>();
		mDefinitionUIDCounter = 0;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public abstract void loadDefinitionsFromMetaFile(String pMetaFilepath);

	public abstract void loadDefinitionFromFile(String pFilepath);

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

	protected void loadDefinitionsFromMetaFileItems(String pMetaFilepath, final Gson pGson, Class<T> pClassType) {
		final var lMetaItems = loadMetaFileItemsFromFilepath(pMetaFilepath);
		if (lMetaItems == null || lMetaItems.itemCount == 0) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Cannot load definition types %s, the given MetaFileItems contains no data", pClassType.getSimpleName()));
			return;

		}

		for (int i = 0; i < lMetaItems.itemCount; i++) {
			final var lDefinitionFilepath = lMetaItems.rootDirectory + lMetaItems.itemFileLocations[i] + ".json";

			loadDefinitionFromFile(lDefinitionFilepath, pGson, pClassType);
		}
	}

	protected void loadDefinitionFromFile(String pFilepath, final Gson pGson, Class<T> pClassType) {
		final var lDefinitionFile = new File(pFilepath);

		if (!lDefinitionFile.exists()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Error loading %s from file: %s", pClassType.getSimpleName(), pFilepath));
			return;
		}

		try {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Loading Definition type %s from file: %s", pClassType.getSimpleName(), pFilepath));

			final var lFileContents = new String(Files.readAllBytes(lDefinitionFile.toPath()));
			final var lNewDefinition = pGson.fromJson(lFileContents, pClassType);

			if (lNewDefinition != null) {
				lNewDefinition.initialize(getNewDefinitionUID());
				mDefinitions.add(lNewDefinition);

			} else {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse %s from file: %s", pClassType.getSimpleName(), pFilepath));

			}

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (JsonSyntaxException): %s", pClassType.getSimpleName(), pFilepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

			return;

		} catch (IOException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (IOException): %s", pClassType.getSimpleName(), pFilepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

			return;
		} catch (NumberFormatException e) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to parse Json %s (NumberFormatException): %s", pClassType.getSimpleName(), pFilepath));
			Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

			return;
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public T getDefinitionByName(String pName) {
		final int lDefinitionCount = mDefinitions.size();
		for (int i = 0; i < lDefinitionCount; i++) {
			if (mDefinitions.get(i).name.equals(pName)) {
				return mDefinitions.get(i);
			}

		}

		return null;

	}

	public T getDefinitionByID(int pDefID) {
		final int lDefinitionCount = mDefinitions.size();
		for (int i = 0; i < lDefinitionCount; i++) {
			if (mDefinitions.get(i).definitionID == pDefID) {
				return mDefinitions.get(i);
			}

		}

		return null;

	}

	public T getDefinitionByIndex(int pDefIndex) {
		if (pDefIndex >= 0 && pDefIndex < mDefinitions.size())
			return mDefinitions.get(pDefIndex);

		return null;

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

}
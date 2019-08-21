package net.lintford.library.core.entity.definitions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.BaseData;

public abstract class DefinitionManager<T extends BaseDefinition> extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1729184288330735542L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<T> mDefinitions;

	private transient int mDefinitionUIDCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<T> definitions() {
		return mDefinitions;
	}

	public int getNewDefinitionUID() {
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

	protected abstract void loadDefinitions(String pMetaFilepath);

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
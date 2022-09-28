package net.lintford.library.core.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class EntityLoader implements EntityLocationProvider {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String FILE_EXTENSION_TO_WATCH = ".json";

	// --------------------------------------
	// Varibles
	// --------------------------------------

	protected String mFolderToWatch;
	protected List<String> mFilesInFolder;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EntityLoader(String folderToWatch) {
		mFolderToWatch = folderToWatch;
		mFilesInFolder = new ArrayList<>();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Iterator<String> getFileLocationIterator() {
		mFilesInFolder.clear();

		try (final var lWalk = Files.walk(Paths.get(mFolderToWatch))) {
			mFilesInFolder = lWalk.filter(Files::isRegularFile).filter(f -> f.endsWith(FILE_EXTENSION_TO_WATCH)).map(x -> x.toString()).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();

		}

		return mFilesInFolder.iterator();
	}
}

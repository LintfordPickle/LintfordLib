package net.lintford.library.core.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityLoader implements EntityLocationProvider {

	private static final String FILE_EXTENSION_TO_WATCH = ".json";

	// --------------------------------------
	// Varibles
	// --------------------------------------

	protected String mFolderToWatch;
	protected List<String> mFilesInFolder;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EntityLoader(String pFolderToWatch) {
		mFolderToWatch = pFolderToWatch;
		mFilesInFolder = new ArrayList<>();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public Iterator<String> getFileLocationIterator() {
		mFilesInFolder.clear();

		try (Stream<Path> walk = Files.walk(Paths.get(mFolderToWatch))) {

			mFilesInFolder = walk.filter(Files::isRegularFile).filter(f -> f.endsWith(FILE_EXTENSION_TO_WATCH)).map(x -> x.toString()).collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();

		}

		return mFilesInFolder.iterator();
	}

}

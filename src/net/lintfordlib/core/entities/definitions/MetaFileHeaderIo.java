package net.lintfordlib.core.entities.definitions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import net.lintfordlib.core.debug.Debug;

public class MetaFileHeaderIo {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static MetaFileHeaderIo assetHeaderLoader = new MetaFileHeaderIo();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MetaFileHeaderIo() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static MetaFileHeader loadFromFilepath(String filepath) {
		if (filepath == null || filepath.length() == 0)
			return null;

		return loadFromFilepath(new File(filepath));
	}

	public static MetaFileHeader loadFromFilepath(File file) {
		if (file == null || file.exists() == false) {
			Debug.debugManager().logger().e(MetaFileHeaderIo.class.getSimpleName(), "Couldn't load definitions files from the given file!");
			return null;
		}

		try {
			final var lGson = new GsonBuilder().create();
			final var lFileContents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
			final var lItemsFileLocations = lGson.fromJson(lFileContents, MetaFileHeader.class);

			if (lItemsFileLocations == null) {
				Debug.debugManager().logger().w(MetaFileHeaderIo.class.getSimpleName(), "Couldn't load item filepaths from the AssetPackHeader!");

				return null;
			}

			return lItemsFileLocations;
		} catch (IOException e) {
			Debug.debugManager().logger().e(MetaFileHeaderIo.class.getSimpleName(), "Error while loading AssetPackHeader filepaths.");
			Debug.debugManager().logger().printException(MetaFileHeaderIo.class.getSimpleName(), e);
		}

		return null;
	}

	public static boolean saveDefinitionsToMetadataFile(MetaFileHeader header, String metaFilepath) {
		final var gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter lFileWriter = null;
		try {
			lFileWriter = new FileWriter(metaFilepath);
			gson.toJson(header, lFileWriter);

		} catch (JsonIOException e) {
			Debug.debugManager().logger().e(MetaFileHeaderIo.class.getSimpleName(), "Failed to save meta data file - incorrect Json!");
			Debug.debugManager().logger().printException(MetaFileHeaderIo.class.getSimpleName(), e);
			return false;
		} catch (IOException e) {
			Debug.debugManager().logger().e(MetaFileHeaderIo.class.getSimpleName(), "Failed to save meta data file - incorrect Json!");
			Debug.debugManager().logger().printException(MetaFileHeaderIo.class.getSimpleName(), e);
			return false;
		} finally {
			if (lFileWriter != null) {
				try {
					lFileWriter.flush();
					lFileWriter.close();
				} catch (IOException e) {
					Debug.debugManager().logger().e(MetaFileHeaderIo.class.getSimpleName(), "Failed to save meta data file - problem flushing FileWriter!");
					Debug.debugManager().logger().printException(MetaFileHeaderIo.class.getSimpleName(), e);
				}
			}
		}

		return true;
	}
}

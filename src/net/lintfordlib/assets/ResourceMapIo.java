package net.lintfordlib.assets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintfordlib.core.debug.Debug;

public class ResourceMapIo {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ResourceMapIo() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static ResourceMap tryLoadResourceMapFromFile(File filepath) {
		if (filepath == null || filepath.exists() == false) {
			Debug.debugManager().logger().w(ResourceMapIo.class.getSimpleName(), String.format("Error loading %s from file: %s (file not found)", ResourceMap.class.getSimpleName(), filepath));
			return null;
		}

		final var lDemarshallClass = ResourceMap.class;

		final var lGsonBuilder = new GsonBuilder();
		final var lGson = lGsonBuilder.create();

		try {
			Debug.debugManager().logger().v(ResourceMapIo.class.getSimpleName(), String.format("Loading Definition type %s from file: %s", lDemarshallClass.getSimpleName(), filepath));

			final var lFileContents = new String(Files.readAllBytes(filepath.toPath()));
			final var lLoadedResourceMap = lGson.fromJson(lFileContents, lDemarshallClass);

			if (lLoadedResourceMap != null) {
				return lLoadedResourceMap;
			} else {
				Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), String.format("Failed to parse %s from file: %s", lDemarshallClass.getSimpleName(), filepath));
			}

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), String.format("Failed to parse Json %s (JsonSyntaxException): %s", lDemarshallClass.getSimpleName(), filepath));
			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), String.format("Failed to parse Json %s (IOException): %s", lDemarshallClass.getSimpleName(), filepath));
			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), e.getMessage());
		} catch (NumberFormatException e) {
			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), String.format("Failed to parse Json %s (NumberFormatException): %s", lDemarshallClass.getSimpleName(), filepath));
			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), e.getMessage());
		}

		return null;
	}

	public static boolean trySaveResourceMap(ResourceMap resmap, File filepath) {
		try (final var lWriter = new FileWriter(filepath)) {
			final var lGson = new GsonBuilder().setPrettyPrinting().create();
			lGson.toJson(resmap, lWriter);
		} catch (IOException e) {

			final var lDemarshallClass = ResourceMap.class;

			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), String.format("Failed to parse Json %s (NumberFormatException): %s", lDemarshallClass.getSimpleName(), filepath));
			Debug.debugManager().logger().e(ResourceMapIo.class.getSimpleName(), e.getMessage());
		}

		return false;
	}

	public static ResourceMap tryCreateNewResourceMap(File file) {
		final var lResultResourceMap = createResourceMap();

		final var lParentFile = file.getParentFile();
		if (lParentFile.exists() == false) {
			lParentFile.mkdirs();
		}

		try (Writer writer = new FileWriter(file)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(lResultResourceMap, writer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return lResultResourceMap;
	}

	private static ResourceMap createResourceMap() {
		return new ResourceMap();
	}
}

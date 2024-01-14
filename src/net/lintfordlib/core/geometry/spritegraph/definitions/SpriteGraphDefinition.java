package net.lintfordlib.core.geometry.spritegraph.definitions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.definitions.BaseDefinition;

public class SpriteGraphDefinition extends BaseDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 9084295319658097867L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "filename")
	private String mFilename;

	@SerializedName(value = "rootNode")
	private SpriteGraphNodeDefinition mRootNode;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String filename() {
		return mFilename;
	}

	public void filename(String filename) {
		mFilename = filename;
	}

	public SpriteGraphNodeDefinition rootNode() {
		return mRootNode;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	SpriteGraphDefinition() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static SpriteGraphDefinition load(String filepath) {
		if (filepath == null || filepath.length() == 0)
			return null;

		final var lFile = new File(filepath);
		if (!lFile.exists()) {
			Debug.debugManager().logger().w(SpriteGraphDefinition.class.getSimpleName(), "Error: SpriteGraphDef file " + filepath + " doesn't exist!");
			return null;
		}

		return load(lFile);
	}

	public static SpriteGraphDefinition load(File file) {
		if (!file.exists()) {
			Debug.debugManager().logger().w(SpriteGraphDefinition.class.getSimpleName(), "Error: SpriteGraphDef file. File doesn't exist!");
			return null;
		}

		final var lGson = new GsonBuilder().create();

		try {
			final var lFileContents = new String(Files.readAllBytes(file.toPath()));
			final var lSpriteGraphDefinition = lGson.fromJson(lFileContents, SpriteGraphDefinition.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteGraphDefinition == null || lSpriteGraphDefinition.rootNode() == null) {
				Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), String.format("Error loading SpriteGraphDef '%s' from json", file.getPath()));
				return null;
			}

			if (ConstantsApp.getBooleanValueDef("DEBUG_APP", false)) {
				Debug.debugManager().logger().v(SpriteGraphDefinition.class.getSimpleName(), "SpriteGraphDef " + file.getPath() + " loaded (" + lSpriteGraphDefinition.name + ")");
			}

			return lSpriteGraphDefinition;
		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (Syntax): " + file.getPath());
			Debug.debugManager().logger().printException(SpriteGraphDefinition.class.getSimpleName(), e);
		} catch (IllegalArgumentException e) {
			Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (IO): " + file.getPath());
			Debug.debugManager().logger().printException(SpriteGraphDefinition.class.getSimpleName(), e);
		} catch (IOException e) {
			Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (IO): " + file.getPath());
			Debug.debugManager().logger().printException(SpriteGraphDefinition.class.getSimpleName(), e);
		}

		return null;
	}

	public static void save(SpriteGraphDefinition spriteGraphDefinition, String filePath) {
		try (final var lFileWriter = new FileWriter(filePath)) {
			final var lGson = new GsonBuilder().create();
			lGson.toJson(spriteGraphDefinition, lFileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unload() {
		mRootNode = null;
	}
}

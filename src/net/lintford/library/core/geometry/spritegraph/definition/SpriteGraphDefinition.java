package net.lintford.library.core.geometry.spritegraph.definition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.definitions.BaseDefinition;

public class SpriteGraphDefinition extends BaseDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String filename;
	public SpriteGraphNodeDefinition rootNode;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpriteGraphNodeDefinition rootNode() {
		return rootNode;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	SpriteGraphDefinition() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static SpriteGraphDefinition load(String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			Debug.debugManager().logger().w(SpriteGraphDefinition.class.getSimpleName(), "Error: SpriteGraphDef file " + pFilepath + " doesn't exist!");
			return null;

		}

		return load(lFile);

	}

	public static SpriteGraphDefinition load(File pFile) {
		if (!pFile.exists()) {
			Debug.debugManager().logger().w(SpriteGraphDefinition.class.getSimpleName(), "Error: SpriteGraphDef file. File doesn't exist!");
			return null;

		}

		final var GSON = new GsonBuilder().create();

		try {

			final String lFileContents = new String(Files.readAllBytes(pFile.toPath()));
			final var lSpriteGraphDefinition = GSON.fromJson(lFileContents, SpriteGraphDefinition.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteGraphDefinition == null || lSpriteGraphDefinition.rootNode() == null) {
				Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), String.format("Error loading SpriteGraphDef '%s'", pFile.getPath()));
				return null;

			}

			if (ConstantsApp.getBooleanValueDef("DEBUG_APP", false)) {
				Debug.debugManager().logger().v(SpriteGraphDefinition.class.getSimpleName(), "SpriteGraphDef " + pFile.getPath() + " loaded (" + lSpriteGraphDefinition.name + ")");

			}

			return lSpriteGraphDefinition;

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (Syntax): " + pFile.getPath());
			Debug.debugManager().logger().printException(SpriteGraphDefinition.class.getSimpleName(), e);
			return null;

		} catch (IllegalArgumentException e) {
			Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (IO): " + pFile.getPath());
			Debug.debugManager().logger().printException(SpriteGraphDefinition.class.getSimpleName(), e);
			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(SpriteGraphDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (IO): " + pFile.getPath());
			Debug.debugManager().logger().printException(SpriteGraphDefinition.class.getSimpleName(), e);
			return null;

		}

	}

	public static void save(SpriteGraphDefinition pSpriteGraph, String pPath) {
		try (Writer writer = new FileWriter(pPath)) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(pSpriteGraph, writer);

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public void unload() {
		rootNode = null;
	}

}

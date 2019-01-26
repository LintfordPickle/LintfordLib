package net.lintford.library.core.geometry.spritegraph.definition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.debug.Debug;

/**
 * GraphObectDefinition
 */
public class GraphObjectDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public String filename;
	public GraphNodeDef rootNode;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public GraphNodeDef rootNode() {
		return rootNode;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	GraphObjectDefinition() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static GraphObjectDefinition load(String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			Debug.debugManager().logger().w(GraphObjectDefinition.class.getSimpleName(), "Error: SpriteGraphDef file " + pFilepath + " doesn't exist!");
			return null;

		}

		return load(lFile);

	}

	public static GraphObjectDefinition load(File pFile) {
		if (!pFile.exists()) {
			Debug.debugManager().logger().w(GraphObjectDefinition.class.getSimpleName(), "Error: SpriteGraphDef file. File doesn't exist!");
			return null;

		}

		final Gson GSON = new GsonBuilder().create();

		try {

			final String lFileContents = new String(Files.readAllBytes(pFile.toPath()));
			final GraphObjectDefinition lSpriteGraphDef = GSON.fromJson(lFileContents, GraphObjectDefinition.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteGraphDef == null || lSpriteGraphDef.rootNode() == null) {
				System.err.println("Error loading SpriteGraphDef " + pFile.getPath());
				return null;

			}

			if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
				Debug.debugManager().logger().v(GraphObjectDefinition.class.getSimpleName(), "SpriteGraphDef " + pFile.getPath() + " loaded (" + lSpriteGraphDef.name + ")");

			}

			return lSpriteGraphDef;

		} catch (JsonSyntaxException e) {
			Debug.debugManager().logger().e(GraphObjectDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (Syntax): " + pFile.getPath());
			return null;

		} catch (IOException e) {
			Debug.debugManager().logger().e(GraphObjectDefinition.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (IO): " + pFile.getPath());
			return null;

		}

	}

	public static void save(GraphObjectDefinition pSpriteGraph, String pPath) {
		try (Writer writer = new FileWriter(pPath)) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(pSpriteGraph, writer);

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

}

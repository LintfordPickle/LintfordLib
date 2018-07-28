package net.lintford.library.core.geometry.spritegraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.debug.DebugManager;

public class SpriteGraphDef {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public String filename;
	public SpriteGraphNodeDef rootNode;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SpriteGraphNodeDef rootNode() {
		return rootNode;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphDef() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static SpriteGraphDef load(String pFilepath) {
		if (pFilepath == null || pFilepath.length() == 0)
			return null;

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			DebugManager.DEBUG_MANAGER.logger().w(SpriteGraphDef.class.getSimpleName(), "Error: SpriteGraphDef file " + pFilepath + " doesn't exist!");
			return null;

		}

		return load(lFile);

	}

	public static SpriteGraphDef load(File pFile) {
		if (!pFile.exists()) {
			DebugManager.DEBUG_MANAGER.logger().w(SpriteGraphDef.class.getSimpleName(), "Error: SpriteGraphDef file. File doesn't exist!");
			return null;

		}

		final Gson GSON = new GsonBuilder().create();

		try {

			final String lFileContents = new String(Files.readAllBytes(pFile.toPath()));
			final SpriteGraphDef lSpriteGraphDef = GSON.fromJson(lFileContents, SpriteGraphDef.class);

			// Check the integrity of the loaded spritsheet
			if (lSpriteGraphDef == null || lSpriteGraphDef.rootNode() == null) {
				System.err.println("Error loading SpriteGraphDef " + pFile.getPath());
				return null;

			}

			if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
				DebugManager.DEBUG_MANAGER.logger().v(SpriteGraphDef.class.getSimpleName(), "SpriteGraphDef " + pFile.getPath() + " loaded (" + lSpriteGraphDef.name + ")");

			}

			return lSpriteGraphDef;

		} catch (JsonSyntaxException e) {
			DebugManager.DEBUG_MANAGER.logger().e(SpriteGraphDef.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (Syntax): " + pFile.getPath());
			DebugManager.DEBUG_MANAGER.logger().printException(SpriteGraphDef.class.getSimpleName(), e);
			return null;

		} catch (IOException e) {
			DebugManager.DEBUG_MANAGER.logger().e(SpriteGraphDef.class.getSimpleName(), "Failed to parse JSON SpriteGraphDef (IO): " + pFile.getPath());
			DebugManager.DEBUG_MANAGER.logger().printException(SpriteGraphDef.class.getSimpleName(), e);
			return null;

		}

	}

	public static void save(SpriteGraphDef pSpriteGraph, String pPath) {
		try (Writer writer = new FileWriter(pPath)) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(pSpriteGraph, writer);

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

}

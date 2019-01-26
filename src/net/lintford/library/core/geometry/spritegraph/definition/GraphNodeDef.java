package net.lintford.library.core.geometry.spritegraph.definition;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;

public class GraphNodeDef {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;

	/** List of possible types is loaded from the {@link SpriteSheetDef}. */
	public String type;

	/**
	 * If this sprite node represents some object (like a steelshirt being worn on the torso), then add the object reference link here. Any object referenced here will be included as default on all {@link GraphNodeDef} instantiated from this
	 * definition file.
	 */
	public int mObjectReferenceType;

	/** A list of child parts which are anchored on this {@link GraphNodeDef}. */
	public List<GraphNodeDef> childParts;

	public String defaultSpriteSheetName;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GraphNodeDef() {
		childParts = new ArrayList<>();

	}

	public GraphNodeDef(String pName) {
		this();
		name = pName;
		type = "";

	}

	public GraphNodeDef(String pName, String pType) {
		this(pName);
		type = pType;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addChild(GraphNodeDef pPart) {
		if (!childParts.contains(pPart)) {
			childParts.add(pPart);

		}

	}

}

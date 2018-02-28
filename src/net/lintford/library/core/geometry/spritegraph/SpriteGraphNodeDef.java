package net.lintford.library.core.geometry.spritegraph;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheet;

public class SpriteGraphNodeDef {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;

	/** List of possible types is loaded from the {@link SpriteSheet}. */
	public String type;

	/**
	 * If this sprite node represents some object (like a steelshirt being worn on the torso), then add the object reference link here. Any object referenced here will be included as default on all {@link SpriteGraphNodeDef} instantiated from this
	 * definition file.
	 */
	public int mObjectReferenceType;

	/** A list of child parts which are anchored on this {@link SpriteGraphNodeDef}. */
	public List<SpriteGraphNodeDef> childParts;

	public String defaultSpriteSheetName;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphNodeDef() {
		childParts = new ArrayList<>();

	}

	public SpriteGraphNodeDef(String pName) {
		this();
		name = pName;
		type = "";

	}

	public SpriteGraphNodeDef(String pName, String pType) {
		this(pName);
		type = pType;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addChild(SpriteGraphNodeDef pPart) {
		if (!childParts.contains(pPart)) {
			childParts.add(pPart);

		}

	}

}

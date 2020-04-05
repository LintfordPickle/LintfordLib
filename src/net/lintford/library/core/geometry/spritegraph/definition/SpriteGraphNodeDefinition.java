package net.lintford.library.core.geometry.spritegraph.definition;

import java.util.ArrayList;
import java.util.List;

public class SpriteGraphNodeDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public String anchorNodeName;
	public float x, y, r;
	public int zDepth;

	/** nodes are tied to an anchor */
	public boolean isPlaceholder;

	/** A list of child parts which are anchored on this {@link SpriteGraphNodeDefinition}. */
	public List<SpriteGraphNodeDefinition> childParts;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphNodeDefinition() {
		childParts = new ArrayList<>();

	}

	public SpriteGraphNodeDefinition(String pName) {
		this();
		name = pName;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addChild(SpriteGraphNodeDefinition pPart) {
		if (!childParts.contains(pPart)) {
			childParts.add(pPart);

		}

	}

}

package net.lintford.library.core.geometry.spritegraph;

import java.io.Serializable;

public class SpriteGraphAnchorDef implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------
	
	public static final SpriteGraphAnchorDef ZERO_ANCHOR = new SpriteGraphAnchorDef();
	
	private static final long serialVersionUID = 5862113195698770627L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public float x;
	public float y;
	public float rot; // default rotation of anchored sprite

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SpriteGraphAnchorDef() {
		
	}

}

package net.lintford.library.options;

public class RenderConfig extends BaseConfig {

	// --------------------------------------
	// Variables
	// --------------------------------------
	
	public boolean VBOS_ENABLED = true;
	public boolean FBOS_ENABLED = true;
	
	public boolean RENDER_NODE_COLLISION_OUTLINE = false;

	public boolean RENDER_NODE_SHADOWS = false;
	public boolean RENDER_CELL_LIGHTING = true;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RenderConfig(String pConfigFilename) {
		super(pConfigFilename);
	}

}

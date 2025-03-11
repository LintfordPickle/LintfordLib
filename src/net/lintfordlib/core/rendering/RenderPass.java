package net.lintfordlib.core.rendering;

public class RenderPass {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int RENDER_PASS_DEFAULT = 0;
	public static final int RENDER_PASS_COLOR0 = 1;
	public static final int RENDER_PASS_COLOR1 = 2;
	public static final int RENDER_PASS_LIGHT0 = 10;
	public static final int RENDER_PASS_LIGHT1 = 11;
	public static final int RENDER_PASS_CUSTOM0 = 20;
	public static final int RENDER_PASS_CUSTOM1 = 21;
	public static final int RENDER_PASS_CUSTOM2 = 22;

	public static final RenderPass DEFAULT = new RenderPass("default", RENDER_PASS_DEFAULT);
	public static final RenderPass COLOR0 = new RenderPass("color0", RENDER_PASS_COLOR0);
	public static final RenderPass COLOR1 = new RenderPass("color1", RENDER_PASS_COLOR1);
	public static final RenderPass LIGHTS0 = new RenderPass("light0", RENDER_PASS_LIGHT0);
	public static final RenderPass LIGHTS1 = new RenderPass("light1", RENDER_PASS_LIGHT1);
	public static final RenderPass CUSTOM0 = new RenderPass("custom0", RENDER_PASS_CUSTOM0);
	public static final RenderPass CUSTOM1 = new RenderPass("custom1", RENDER_PASS_CUSTOM1);
	public static final RenderPass CUSTOM2 = new RenderPass("custom2", RENDER_PASS_CUSTOM2);

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final String name;
	public final int typeIndex;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private RenderPass(String name, int typeIndex) {
		this.name = name;
		this.typeIndex = typeIndex;
	}
}

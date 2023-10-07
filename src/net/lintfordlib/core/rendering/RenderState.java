package net.lintfordlib.core.rendering;

/**
 * The {@link RenderState} class contains information and object references for use when rendering in OpenGL.
 */
public class RenderState {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mRenderPass;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns an integer which can be used to identify a rendering pass (i.e. 1 = diffuse) on the application side.
	 */
	public int renderPass() {
		return mRenderPass;
	}

	/**
	 * Sets an integer which can be used to identify a rendering pass (i.e. 1 = diffuse) on the application side.
	 */
	public void renderPass(int passUid) {
		mRenderPass = passUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/**
	 * Creates a new instance of {@link RenderState}. Sets the current {@link RENDER_PASS} to diffuse.
	 */
	public RenderState() {
		mRenderPass = 0;

	}

}

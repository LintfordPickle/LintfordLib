package net.lintford.library.core.rendering;

/**
 * The {@link RenderState} class contains information and object references for use when rendering in OpenGL.
 */
// TODO: it would make sense to improve this class to allow it to be easily
// extended for adding things like RENDER_PASSES for game specific cases
public class RenderState {

	// --------------------------------------
	// Constants
	// --------------------------------------

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
	public void renderPass(int pNewPassID) {
		mRenderPass = pNewPassID;
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

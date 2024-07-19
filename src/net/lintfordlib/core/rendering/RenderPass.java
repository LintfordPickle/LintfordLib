package net.lintfordlib.core.rendering;

import net.lintfordlib.core.graphics.rendertarget.RenderTarget;

// TOOD: ---> Need some test apps to see if we can make this work with different rendering paradigms. e.g. forward and deferred rendering

public class RenderPass {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int RENDER_PASS_DEFAULT = 0;
	public static final int RENDER_PASS_COLOR = 1;
	public static final int RENDER_PASS_LIGHT = 2;

	public static final RenderPass DefaultRenderPass = new RenderPass(RENDER_PASS_DEFAULT);

	// --------------------------------------
	// Variables
	// --------------------------------------

	/**
	 * The id of the current buffer being rendered into. This is not final, because there are potentially several (one for each player viewport).
	 */
	public int mBufferUid;

	private final int mRenderPassTypeIndex;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Identifies the current pass type. Different pass types have different {@link RenderTarget}s associated with them. See {@link RenderPass} for stanard type indices.
	 */

	public int passTypeIndex() {
		return mRenderPassTypeIndex;
	}

	/**
	 * Returns the OpenGL buffer id of the buffer for this render pass.
	 */
	public int getBufferId() {
		return mBufferUid;
	}

	public void setBufferId(int bufferId) {
		mBufferUid = bufferId;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RenderPass(int passTypeIndex) {
		mRenderPassTypeIndex = passTypeIndex;
	}

}

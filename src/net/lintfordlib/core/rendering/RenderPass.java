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

	public RenderTarget mPassRt;

	private final int mRenderPassTypeIndex;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns true if this RenderPass is the default RenderPass. That is, if this RenderPass is not a custom one. Otherwise false.
	 */
	public boolean isDefaultRenderPass() {
		return mRenderPassTypeIndex == RENDER_PASS_DEFAULT;
	}

	/**
	 * Identifies the current pass type. Different pass types have different {@link RenderTarget}s associated with them. See {@link RenderPass} for stanard type indices.
	 */

	public int passTypeIndex() {
		return mRenderPassTypeIndex;
	}

	/**
	 * Returns the {@link RenderTarget} associated with this RenderPass.
	 */
	public RenderTarget passRenderTarget() {
		return mPassRt;
	}

	public void passRenderTarget(RenderTarget rt) {
		mPassRt = rt;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RenderPass(int passTypeIndex) {
		mRenderPassTypeIndex = passTypeIndex;
	}

}

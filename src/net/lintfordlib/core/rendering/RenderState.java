package net.lintfordlib.core.rendering;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link RenderState} class contains information and object references for use when rendering in OpenGL.
 */
public class RenderState {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<RenderPass> mRenderPasses = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<RenderPass> renderPasses() {
		return mRenderPasses;
	}

	public RenderPass getRenderPassByTypeIndex(int passTypeIndex) {
		final var lNumRegisteredPasses = mRenderPasses.size();
		for (int i = 0; i < lNumRegisteredPasses; i++) {
			if (mRenderPasses.get(i).passTypeIndex() == passTypeIndex)
				return mRenderPasses.get(i);
		}

		return null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/**
	 * Creates a new instance of {@link RenderState}. Sets the current {@link RENDER_PASS} to diffuse.
	 */
	public RenderState() {
		mRenderPasses.add(RenderPass.DefaultRenderPass);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setCustomRenderPasses(RenderPass... newPasses) {
		setCustomRenderPasses(true, newPasses);
	}

	/**
	 * Allows setting up custom render
	 */
	public void setCustomRenderPasses(boolean removePrevious, RenderPass... newPasses) {
		if (newPasses == null)
			return;

		if (removePrevious)
			mRenderPasses.clear();

		final var lNumNewPasses = newPasses.length;
		for (int i = 0; i < lNumNewPasses; i++) {
			addRenderPass(newPasses[i]);
		}

		if (mRenderPasses.size() == 0)
			mRenderPasses.add(RenderPass.DefaultRenderPass);

	}

	private void addRenderPass(RenderPass renderPass) {
		if (renderPass == null)
			return;

		// TODO: Make sure the same indexed render pass type isn't in the list

		// TODO: Log issues with render passes - otherwise we'll have nothing rendering and not know why

		mRenderPasses.add(renderPass);

	}

}
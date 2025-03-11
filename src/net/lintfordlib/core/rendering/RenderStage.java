package net.lintfordlib.core.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.renderers.BaseRenderer;

public class RenderStage {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RenderPass mRenderPass;
	private RenderTarget mRenderTarget;

	public final int stageUid;
	public final String stageName;

	private List<BaseRenderer> mRenderers = new ArrayList<>();
	private List<BaseRenderer> mReadOnlyRenderers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<BaseRenderer> renderers() {
		return mReadOnlyRenderers;
	}

	public RenderPass renderPass() {
		return mRenderPass;
	}

	public RenderTarget renderTarget() {
		return mRenderTarget;
	}

	public void renderTarget(RenderTarget newRt) {
		mRenderTarget = newRt;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RenderStage(String name, RenderPass pass, int stageUid) {
		this.stageName = name;
		this.stageUid = stageUid;

		mRenderPass = pass;
		mReadOnlyRenderers = Collections.unmodifiableList(mRenderers);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void addRenderTarget(RenderTarget rt) {
		mRenderTarget = rt;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addRenderer(BaseRenderer renderer) {
		mRenderers.add(renderer);
	}

	public boolean hasSharedDepthBuffer() {
		return mRenderTarget != null && mRenderTarget.isSharedDepthBuffer();
	}

}

package net.lintfordlib.core.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.BaseRenderer;

public class RenderStage {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RenderPass mRenderPass;
	private RenderTarget mRenderTarget;

	public final int stageUid;
	public final String stageName;
	public float clearColorR;
	public float clearColorG;
	public float clearColorB;
	public float clearColorA;

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

	public void setClearColorRGB(float r, float g, float b) {
		clearColorR = MathHelper.clamp(r, 0, 1);
		clearColorG = MathHelper.clamp(g, 0, 1);
		clearColorB = MathHelper.clamp(b, 0, 1);
	}

	public void setClearColorA(float a) {
		clearColorA = MathHelper.clamp(a, 0, 1);
	}

	public void setClearColorRGBA(float r, float g, float b, float a) {
		clearColorR = MathHelper.clamp(r, 0, 1);
		clearColorG = MathHelper.clamp(g, 0, 1);
		clearColorB = MathHelper.clamp(b, 0, 1);
		clearColorA = MathHelper.clamp(a, 0, 1);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RenderStage(String name, RenderPass pass, int stageUid) {
		this.stageName = name;
		this.stageUid = stageUid;

		mRenderPass = pass;
		mReadOnlyRenderers = Collections.unmodifiableList(mRenderers);

		clearColorR = 0.f;
		clearColorG = 0.f;
		clearColorB = 0.f;
		clearColorA = 1.f;
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
		if (renderer == null)
			return;

		mRenderers.add(renderer);
	}

	public boolean hasSharedDepthBuffer() {
		return mRenderTarget != null && mRenderTarget.isSharedDepthBuffer();
	}

}

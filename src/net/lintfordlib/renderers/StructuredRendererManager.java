package net.lintfordlib.renderers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.core.rendering.RenderStage;

public class StructuredRendererManager extends RendererManagerBase {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RenderTarget mActiveRenderTarget;
	private List<RenderStage> mRenderStages;
	public Object userData; // hack

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public StructuredRendererManager(LintfordCore core, int entityGroupUid) {
		super(core, entityGroupUid);

		mRenderStages = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		// Handle the base renderer input
		final int lNumRenderers = mRenderers.size();
		for (int i = lNumRenderers - 1; i >= 0; i--) {
			mRenderers.get(i).handleInput(core);

			// TODO: Window renderers need to be processed first, and can have 'exclusive' input (onHandledInput return).

		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

	}

	@Override
	public void draw(LintfordCore core) {
		if (mActiveRenderTarget != null) { // current player rt (split screen)
			mActiveRenderTarget.bind();

			GL11.glClearColor(.06f, 0.18f, 0.31f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}

		// TODO: render all stages and passes
		final int lNumStages = mRenderStages.size();
		for (int i = 0; i < lNumStages; i++) {

			final var lStage = mRenderStages.get(i);
			final var lRenderPass = lStage.renderPass();

			if (lStage.renderTarget() != null) {
				lStage.renderTarget().bind();
				lRenderPass.currentRt = lStage.renderTarget();

				GL11.glClearColor(lStage.clearColorR, lStage.clearColorG, lStage.clearColorB, lStage.clearColorA);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

				if (!lStage.hasSharedDepthBuffer())
					GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

			}

			final var lRenderers = lStage.renderers();
			final var lNumRenderers = lRenderers.size();
			for (int j = 0; j < lNumRenderers; j++) {
				final var lRenderer = lRenderers.get(j);

				lRenderer.draw(core, lRenderPass);
			}

			if (lStage.renderTarget() != null) {
				lStage.renderTarget().unbind();

				core.config().display().reapplyGlViewport();

				if (mActiveRenderTarget != null) {
					mActiveRenderTarget.bind();
					lRenderPass.currentRt = mActiveRenderTarget;
				} else
					lRenderPass.currentRt = null;
			}
		}

		if (mActiveRenderTarget != null) {
			mActiveRenderTarget.unbind();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** draws the given rt (by uid) into the currently bound texture buffer. */
	public void drawStageRtByUid(LintfordCore core, int stageUid) {
		final var lDesiredStage = getRenderStageByUid(stageUid);
		if (lDesiredStage == null)
			return;

		final var lRenderTarget = lDesiredStage.renderTarget();
		if (lRenderTarget == null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "RenderStage '" + lDesiredStage.stageName + "' does not have a dedicated render target. Nothing will be rendered!");
			return;
		}

		final var rtw = mActiveRenderTarget.width() * .8f;
		final var rth = mActiveRenderTarget.height() * .8f;
		final var dx = -rtw * .5f;
		final var dy = -rth * .5f;

		mSharedResources.drawRenderTargetImmediate(core, dx, dy, rtw, rth, 0.01f, mActiveRenderTarget);
	}

	/** draws the given rt (by uid) into the currently bound texture buffer. */
	public void drawStageByUid(LintfordCore core, int stageUid) {
		final var lDesiredStage = getRenderStageByUid(stageUid);
		if (lDesiredStage == null)
			return;

		final var lRenderPass = lDesiredStage.renderPass();
		final var lRenderers = lDesiredStage.renderers();
		final var lNumRenderers = lRenderers.size();
		for (int j = 0; j < lNumRenderers; j++) {
			final var lRenderer = lRenderers.get(j);

			lRenderer.draw(core, lRenderPass);
		}
	}

	public void setRenderTarget(RenderTarget rt) {
		mActiveRenderTarget = rt;
	}

	public RenderStage getRenderStageByUid(int stageUid) {
		final var lNumStages = mRenderStages.size();
		for (int i = 0; i < lNumStages; i++) {
			final var lStage = mRenderStages.get(i);
			if (lStage.stageUid == stageUid)
				return lStage;
		}

		return null;
	}

	// RenderStageType type
	public RenderStage addRenderStage(String name, RenderPass pass, int stageUid) {
		final var lNewStage = new RenderStage(name, pass, stageUid);

		// TODO: sort the list by Z-Order.
		// TODO: ensure no duplicate names.
		// TODO: ensure no duplicate uids.
		mRenderStages.add(lNewStage);
		return lNewStage;
	}

	public void addRenderTargetToStage(RenderStage stage, int width, int height, Integer sharedDepthId) {
		final var rt = createRenderTarget(stage.stageName, width, height, 1.f, GL11.GL_NEAREST, true, sharedDepthId);
		stage.renderTarget(rt);
	}

	public void addToStage(BaseRenderer renderer, RenderPass renderPass) {

	}

	public void setRenderTargetByName(String name) {
		if (name == null) {
			if (mActiveRenderTarget != null) {
				mActiveRenderTarget.unbind();
				return;
			}
		}

		final var lResult = getRenderTarget(name);
		if (lResult != null) {
			if (mActiveRenderTarget != null) {
				mActiveRenderTarget.unbind();
			}
		}

		lResult.bind();
	}
}
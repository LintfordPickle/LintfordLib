package net.lintfordlib.renderers.particles;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.core.particles.ParticleFrameworkController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

// TODO: The RENDERER_POOL_SIZE constant is arbitrary
// TODO: We only need to render (and therefore assign) renderers to particle systems when they are visible. Add frustum culling (Ps/Pe needs AABB).

public class ParticleFrameworkRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "Game Particle Renderer";

	private static int RENDERER_ID = 0;
	private static final int RENDERER_POOL_SIZE = 200;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<ParticleRenderer> mParticleRenderers;
	protected ParticleFrameworkController mParticleSystemController;
	private int mEntityGroupID;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return mParticleSystemController != null;
	}

	public int numAssignedParticleRenderers() {
		var lNumAssignedRenderers = 0;
		for (int i = 0; i < lNumAssignedRenderers; i++) {
			if (mParticleRenderers.get(i).isAssigned())
				lNumAssignedRenderers++;

		}
		return lNumAssignedRenderers;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkRenderer(RendererManagerBase rendererManager, int entityGroupUid) {
		super(rendererManager, RENDERER_NAME, entityGroupUid);

		mParticleRenderers = new ArrayList<>();
		mEntityGroupID = entityGroupUid;

		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.add(new ParticleRenderer(getNewRendererId(), mEntityGroupID));
		}
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mParticleSystemController = (ParticleFrameworkController) core.controllerManager().getControllerByNameRequired(ParticleFrameworkController.CONTROLLER_NAME, mEntityGroupID);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {

		for (int i = 0; i < RENDERER_POOL_SIZE; i++)
			mParticleRenderers.get(i).loadResources(resourceManager);

		mResourcesLoaded = true;
	}

	@Override
	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		for (int i = 0; i < RENDERER_POOL_SIZE; i++)
			mParticleRenderers.get(i).unloadResources();

		mResourcesLoaded = false;
	}

	@Override
	public void update(LintfordCore core) {
		if (mParticleSystemController == null)
			return;

		final var lParticleSystemManager = mParticleSystemController.particleFrameworkData().particleSystemManager();
		final var lInstances = lParticleSystemManager.particleSystems();
		if (lInstances != null && !lInstances.isEmpty()) {
			final int lNumParticleSystems = lInstances.size();
			for (int i = 0; i < lNumParticleSystems; i++) {
				if (!lInstances.get(i).isAssigned())
					continue;

				maintainParticleSystemRenderer(lInstances.get(i));
			}
		}

		cleanParticleSystemRenderers();
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		int cacheSrcBlendFactor = -1;
		int cacheDestBlendFactor = -1;

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);

		final var lSpriteBatch = core.sharedResources().uiSpriteBatch();

		final int lNumParticleRenderers = mParticleRenderers.size();
		for (int i = 0; i < lNumParticleRenderers; i++) {
			final var lParticleRenderer = mParticleRenderers.get(i);

			if (!lParticleRenderer.isAssigned())
				continue;

			// TODO: Need to modify the ParticleSystems to use the new RenderPass and the pass indices.
			
			if (renderPass.typeIndex != lParticleRenderer.renderPassId())
				continue;

			final var lSrcBlendFactor = lParticleRenderer.srcBlendFactor();
			final var lDestBlendFactor = lParticleRenderer.destBlendFactor();

			if (cacheSrcBlendFactor != lSrcBlendFactor || cacheDestBlendFactor != lDestBlendFactor) {
				cacheSrcBlendFactor = lSrcBlendFactor;
				cacheDestBlendFactor = lDestBlendFactor;

				lSpriteBatch.setGlBlendEnabled(true);
				lSpriteBatch.setGlBlendFactor(lSrcBlendFactor, lDestBlendFactor);
			}

			lSpriteBatch.begin(core.gameCamera());

			lParticleRenderer.draw(core, lSpriteBatch);

			lSpriteBatch.end();
		}

		GL11.glDepthMask(true);

		// restore blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static int getNewRendererId() {
		return RENDERER_ID++;
	}

	private void cleanParticleSystemRenderers() {
		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			if (!mParticleRenderers.get(i).isAssignedParticleSystemAlive())
				mParticleRenderers.get(i).unassignParticleSystem();
		}
	}

	public void maintainParticleSystemRenderer(ParticleSystemInstance particleSystemInstance) {
		if (particleSystemInstance.rendererId() != ParticleSystemInstance.NO_RENDERER_ASSIGNED)
			return;

		final var particleRenderer = getFreeParticleSystemRenderer();
		if (particleRenderer != null) {
			particleSystemInstance.assignedRendererId(particleRenderer.particleRendererId());
			particleRenderer.assignParticleSystem(particleSystemInstance);
		}
	}

	/**
	 * Returns an unassigned {@link ParticleRenderer}. null is returned if there are no unassigned particle renderers remaining and the system resources do not allow the pool to be expanded.
	 */
	public ParticleRenderer getFreeParticleSystemRenderer() {
		final int PARTICLE_RENDERER_COUNT = mParticleRenderers.size();
		for (int i = 0; i < PARTICLE_RENDERER_COUNT; i++) {
			if (!mParticleRenderers.get(i).isAssigned()) {
				return mParticleRenderers.get(i);
			}
		}

		return null;
	}
}

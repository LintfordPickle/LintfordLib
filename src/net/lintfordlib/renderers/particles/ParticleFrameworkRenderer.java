package net.lintfordlib.renderers.particles;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.controllers.core.particles.ParticleFrameworkController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.graphics.batching.TextureBatchPCT;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class ParticleFrameworkRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "Game Particle Renderer";

	private static int RENDERER_ID;
	private static final int RENDERER_POOL_SIZE = 32;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected TextureBatchPCT mTextureBatch;
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkRenderer(RendererManager rendererManager, int entityGroupUid) {
		super(rendererManager, RENDERER_NAME, entityGroupUid);

		mParticleRenderers = new ArrayList<>();
		mEntityGroupID = entityGroupUid;
		mTextureBatch = new TextureBatchPCT();

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
		mTextureBatch.loadResources(resourceManager);

		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.get(i).loadResources(resourceManager);
		}

		mResourcesLoaded = true;
	}

	@Override
	public void unloadResources() {
		mTextureBatch.unloadResources();

		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.get(i).unloadResources();
		}

		mResourcesLoaded = false;
	}

	@Override
	public void update(LintfordCore core) {
		if (mParticleSystemController == null)
			return;

		final var lParticleSystemManager = mParticleSystemController.particleFrameworkData().particleSystemManager();
		final var lInstances = lParticleSystemManager.particleSystems();
		if (lInstances != null && lInstances.size() > 0) {
			final int lNumParticleSystems = lInstances.size();
			for (int i = 0; i < lNumParticleSystems; i++) {
				maintainParticleSystemRenderer(lInstances.get(i));
			}
		}

		cleanParticleSystemRenderers();
	}

	@Override
	public void draw(LintfordCore core) {
		mTextureBatch.begin(core.gameCamera());

		final int lNumParticleRenderers = mParticleRenderers.size();
		for (int i = 0; i < lNumParticleRenderers; i++) {
			if (mParticleRenderers.get(i).isAssigned())
				mParticleRenderers.get(i).draw(core, mTextureBatch);
		}

		mTextureBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public int getNewRendererId() {
		return RENDERER_ID++;
	}

	private void cleanParticleSystemRenderers() {
		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			if (mParticleRenderers.get(i).isAssignedParticleSystemAlive() == false)
				mParticleRenderers.get(i).unassignParticleSystem();
		}
	}

	public void maintainParticleSystemRenderer(ParticleSystemInstance particleSystemInstance) {
		if (particleSystemInstance.rendererId() != ParticleSystemInstance.NO_RENDERER_ASSIGNED) {
			
			return;
		}
		
		if(particleSystemInstance.definition() == null)
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

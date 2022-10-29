package net.lintford.library.renderers.particles;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.core.particles.ParticleFrameworkController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.batching.TextureBatchPCT;
import net.lintford.library.core.particles.particlesystems.ParticleSystemInstance;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

// FIXME: improve the performance by storing an ID integer between the controller and the renderers.
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

	private TextureBatchPCT mTextureBatch;
	private List<ParticleRenderer> mParticleRenderers;
	private ParticleFrameworkController mParticleSystemController;
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

		final var lInstances = mParticleSystemController.particleFrameworkData().particleSystemManager().particleSystems();
		if (lInstances != null && lInstances.size() > 0) {
			final int lNumParticleSystems = lInstances.size();
			for (int i = 0; i < lNumParticleSystems; i++) {
				maintainParticleSystemRenderer(lInstances.get(i));
			}
		}
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

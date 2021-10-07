package net.lintford.library.renderers.particles;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.core.particles.ParticleFrameworkController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.particles.particlesystems.ParticleSystemInstance;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

// FIXME: improve the performance by storing an ID integer between the controller and the renderers.
public class ParticleFrameworkRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "GameParticleRenderer";
	private static int RENDERER_ID;
	private static final int RENDERER_POOL_SIZE = 32;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<ParticleRenderer> mParticleRenderers;
	private ParticleFrameworkController mParticleSystemController;

	private int mEntityGroupID;

	public int getNewRendererId() {
		return RENDERER_ID++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mParticleRenderers = new ArrayList<>();

		mEntityGroupID = pEntityGroupID;

		// Fill the pool
		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.add(new ParticleRenderer(getNewRendererId(), mEntityGroupID));

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mParticleSystemController = (ParticleFrameworkController) pCore.controllerManager().getControllerByNameRequired(ParticleFrameworkController.CONTROLLER_NAME, mEntityGroupID);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.get(i).loadGLContent(pResourceManager);

		}

		mIsLoaded = true;

	}

	@Override
	public void unloadGLContent() {
		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.get(i).unloadGLContent();

		}

		mIsLoaded = false;

	}

	@Override
	public void update(LintfordCore pCore) {
		if (mParticleSystemController == null)
			return;

		// Monitor and update any particlesystems needing renderers.
		final List<ParticleSystemInstance> lInstances = mParticleSystemController.particleFrameworkData().particleSystemManager().particleSystems();

		if (lInstances != null && lInstances.size() > 0) {
			final int lNumParticleSystems = lInstances.size();

			for (int i = 0; i < lNumParticleSystems; i++) {
				maintainParticleSystemRenderer(lInstances.get(i));

			}

		}

	}

	@Override
	public void draw(LintfordCore pCore) {
		final int lNumParticleRenderers = mParticleRenderers.size();
		for (int i = 0; i < lNumParticleRenderers; i++) {
			if (mParticleRenderers.get(i).isAssigned())
				mParticleRenderers.get(i).draw(pCore);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void maintainParticleSystemRenderer(ParticleSystemInstance pPSInstance) {
		if (pPSInstance.rendererId() != ParticleSystemInstance.NO_RENDERER_ASSIGNED)
			return;

		final var particleRenderer = getFreeParticleSystemRenderer();
		if (particleRenderer != null) {
			pPSInstance.assignedRendererId(particleRenderer.particleRendererId());
			particleRenderer.assignParticleSystem(pPSInstance);

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

		// TODO: Before returning null, we need to check if it is possible to expand the PARTICLE_SYSTEM_RENDERER pool.
		return null;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

}

package net.lintford.library.controllers.core.particles;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.ParticleFrameworkData;

public class ParticleFrameworkController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Particle Framework Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleFrameworkData mParticleFrameworkData;
	private boolean mIsAssigned;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleFrameworkData particleFrameworkData() {
		return mParticleFrameworkData;
	}

	public boolean isAssigned() {
		return mIsAssigned;
	}

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInitialized() {
		return mParticleFrameworkData != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkController(final ControllerManager controllerManager, final ParticleFrameworkData particleFrameworkData, final int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mParticleFrameworkData = particleFrameworkData;
		mIsAssigned = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {

	}

	public void update(LintfordCore core) {
		if (!isInitialized())
			return;

		final var lEmitterInstanceList = mParticleFrameworkData.emitterManager().emitterInstances();
		final var lSystemInstanceList = mParticleFrameworkData.particleSystemManager().particleSystems();

		final int lNumParticleEmitters = lEmitterInstanceList.size();
		for (int i = 0; i < lNumParticleEmitters; i++) {
			final var lParticleEmitterInstance = lEmitterInstanceList.get(i);
			if (!lParticleEmitterInstance.isAssigned())
				continue;

			if (!lParticleEmitterInstance.isEnabled())
				continue;

			if (lParticleEmitterInstance.parentEntity() != null) {
				lParticleEmitterInstance.worldPositionX(lParticleEmitterInstance.parentEntity().worldPositionX());
				lParticleEmitterInstance.worldPositionY(lParticleEmitterInstance.parentEntity().worldPositionY());
			}

			lParticleEmitterInstance.update(core);
		}

		final int lNumParticleSystems = lSystemInstanceList.size();
		for (int i = 0; i < lNumParticleSystems; i++) {
			final var lParticleSystemInstance = lSystemInstanceList.get(i);
			if (!lParticleSystemInstance.isAssigned())
				continue;

			lParticleSystemInstance.update(core);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void unload() {

	}
}

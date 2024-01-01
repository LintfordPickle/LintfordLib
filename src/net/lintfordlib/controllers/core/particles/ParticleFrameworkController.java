package net.lintfordlib.controllers.core.particles;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.ParticleFrameworkData;

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
				// FIXME: take the position from the parentEntity's body (not implemented)
//				lParticleEmitterInstance.x = lParticleEmitterInstance.parentEntity().body().x;
//				lParticleEmitterInstance.y = lParticleEmitterInstance.parentEntity().body().y;
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
}

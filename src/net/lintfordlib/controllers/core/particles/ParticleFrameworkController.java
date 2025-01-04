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

	protected ParticleFrameworkData mParticleFrameworkData;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleFrameworkData particleFrameworkData() {
		return mParticleFrameworkData;
	}

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return mParticleFrameworkData != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkController(ControllerManager controllerManager, ParticleFrameworkData particleFrameworkData, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mParticleFrameworkData = particleFrameworkData;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core) {
		if (!isInitialized())
			return;

		final var lEmitterInstanceList = mParticleFrameworkData.particleEmitterManager().emitterInstances();
		final var lSystemInstanceList = mParticleFrameworkData.particleSystemManager().particleSystems();

		final int lNumParticleEmitters = lEmitterInstanceList.size();
		for (int i = 0; i < lNumParticleEmitters; i++) {
			final var lParticleEmitterInstance = lEmitterInstanceList.get(i);
			if (!lParticleEmitterInstance.isAssigned())
				continue;

			if (!lParticleEmitterInstance.isEnabled())
				continue;

			if (lParticleEmitterInstance.parentEmitterInst() != null) {
				final var lParentInst = lParticleEmitterInstance.parentEmitterInst();

				if (!lParentInst.isEnabled())
					continue;

				final var lEmitterDef = lParticleEmitterInstance.emitterDefinition();

				lParticleEmitterInstance.aabb.x(lParentInst.aabb.x() + lEmitterDef.positionRelOffsetX);
				lParticleEmitterInstance.aabb.y(lParentInst.aabb.y() + lEmitterDef.positionRelOffsetY);
				lParticleEmitterInstance.zDepth = lParentInst.zDepth;
				lParticleEmitterInstance.globalRotRads = lParentInst.globalRotRads + lEmitterDef.positionRelOffsetRot;

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

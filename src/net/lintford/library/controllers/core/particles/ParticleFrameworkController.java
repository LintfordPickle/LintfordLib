package net.lintford.library.controllers.core.particles;

import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.ParticleFrameworkData;
import net.lintford.library.core.particles.particleemitters.ParticleEmitterInstance;
import net.lintford.library.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleFrameworkController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "ParticleFrameworkController";

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

	public ParticleFrameworkController(final ControllerManager pControllerManager, final ParticleFrameworkData pParticleFrameworkData, final int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mParticleFrameworkData = pParticleFrameworkData;
		mIsAssigned = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	public void update(LintfordCore pCore) {
		if (!isInitialized())
			return;

		final List<ParticleEmitterInstance> lEmitterInstanceList = mParticleFrameworkData.emitterManager().emitterInstances();
		final List<ParticleSystemInstance> lSystemInstanceList = mParticleFrameworkData.particleSystemManager().particleSystems();

		final int lNumParticleEmitters = lEmitterInstanceList.size();
		for (int i = 0; i < lNumParticleEmitters; i++) {
			ParticleEmitterInstance lEmitterInst = lEmitterInstanceList.get(i);
			if (!lEmitterInst.isAssigned())
				continue;

			if (!lEmitterInst.enabled)
				continue;

			// Apply the position of the root emitter directly, so that each emitter instance can apply their own positional offsets
			// based on the emitter definitions.
			if (lEmitterInst.parentEntity() != null) {
				lEmitterInst.worldPositionX = lEmitterInst.parentEntity().worldPositionX;
				lEmitterInst.worldPositionY = lEmitterInst.parentEntity().worldPositionY;

			}

			lEmitterInst.update(pCore);

		}

		final int lNumParticleSystems = lSystemInstanceList.size();
		for (int i = 0; i < lNumParticleSystems; i++) {
			ParticleSystemInstance lSystemInst = lSystemInstanceList.get(i);
			if (!lSystemInst.isAssigned())
				continue;

			lSystemInst.update(pCore);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void unload() {

	}

}

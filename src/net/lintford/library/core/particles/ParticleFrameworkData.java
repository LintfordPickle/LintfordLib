package net.lintford.library.core.particles;

import net.lintford.library.core.entity.BaseData;
import net.lintford.library.core.particles.particleemitters.ParticleEmitterManager;
import net.lintford.library.core.particles.particlesystems.ParticleSystemManager;

public class ParticleFrameworkData extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2902482955273095395L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleSystemManager mParticleSystemManager;
	private ParticleEmitterManager mParticleEmitterManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleSystemManager particleSystemManager() {
		return mParticleSystemManager;
	}

	public ParticleEmitterManager emitterManager() {
		return mParticleEmitterManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkData() {
		mParticleSystemManager = new ParticleSystemManager(this);
		mParticleEmitterManager = new ParticleEmitterManager(this);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Object pParent) {
		mParticleSystemManager.initialize(this);
		mParticleEmitterManager.initialize(this);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadSystemMetaDefinitionFile(String pFilename) {
		mParticleSystemManager.loadDefinitionMetaFile(pFilename);

	}

	public void loadEmitterMetaDefinitionFile(String pFilename) {
		mParticleEmitterManager.loadDefinitionMetaFile(pFilename);

	}

}

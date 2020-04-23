package net.lintford.library.core.particles;

import net.lintford.library.core.entity.BaseInstanceData;
import net.lintford.library.core.particles.particleemitters.ParticleEmitterManager;
import net.lintford.library.core.particles.particlesystems.ParticleSystemManager;

public class ParticleFrameworkData extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2902482955273095395L;

	public static String PARTICLE_SYSTEM_META_FILE = "res//defs//particles//systems//psystems_meta.json";
	public static String EMITTER_META_FILE = "res//defs//particles//emitters//emitters_meta.json";

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

		loadSystemMetaDefinitionFile(PARTICLE_SYSTEM_META_FILE);
		loadEmitterMetaDefinitionFile(EMITTER_META_FILE);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadSystemMetaDefinitionFile(String pFilepath) {
		mParticleSystemManager.definitionManager().loadDefinitionsFromMetaFile(pFilepath);

	}

	public void loadEmitterMetaDefinitionFile(String pFilepath) {
		mParticleEmitterManager.definitionManager().loadDefinitionsFromMetaFile(pFilepath);

	}

}

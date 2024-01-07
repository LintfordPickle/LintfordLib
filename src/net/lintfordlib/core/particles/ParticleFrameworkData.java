package net.lintfordlib.core.particles;

import net.lintfordlib.core.particles.particleemitters.ParticleEmitterManager;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemManager;
import net.lintfordlib.data.BaseDataManager;
import net.lintfordlib.data.DataManager;

public class ParticleFrameworkData extends BaseDataManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public final static String DATA_MANAGER_NAME = ParticleFrameworkData.class.getSimpleName();

	public static String PARTICLE_SYSTEM_META_FILE = "res/def/particles/systems/_meta.json";
	public static String PARTICLE_EMITTER_META_FILE = "res/def/particles/emitters/_meta.json";

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

	public ParticleFrameworkData(DataManager dataManager, int entityGroupUid) {
		super(dataManager, DATA_MANAGER_NAME, entityGroupUid);

		mParticleSystemManager = new ParticleSystemManager(this);
		mParticleEmitterManager = new ParticleEmitterManager(this);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadFromMetaFiles() {
		loadFromMetaFiles(PARTICLE_SYSTEM_META_FILE, PARTICLE_EMITTER_META_FILE);
	}

	public void loadParticleEmittersFromMetaFile(String particleEmitterMetaFile) {
		mParticleEmitterManager.definitionManager().loadDefinitionsFromMetaFile(particleEmitterMetaFile);
	}

	public void loadParticleSystemsFromMetaFile(String particleSystemMetaFile) {
		mParticleSystemManager.definitionManager().loadDefinitionsFromMetaFile(particleSystemMetaFile);
	}

	public void loadFromMetaFiles(String particleSystemMetaFile, String particleEmitterMetaFile) {
		loadParticleSystemsFromMetaFile(particleSystemMetaFile);
		loadParticleEmittersFromMetaFile(particleEmitterMetaFile);
	}

}

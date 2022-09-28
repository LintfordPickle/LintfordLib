package net.lintford.library.core.particles.particleemitters;

import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.entity.EntityLocationProvider;
import net.lintford.library.core.entity.definitions.DefinitionManager;
import net.lintford.library.core.entity.instances.PoolInstanceManager;
import net.lintford.library.core.particles.ParticleFrameworkData;

public class ParticleEmitterManager extends PoolInstanceManager<ParticleEmitterInstance> {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class EmitterDefinitionManager extends DefinitionManager<ParticleEmitterDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public EmitterDefinitionManager() {
			loadDefinitionsFromMetaFile(ParticleEmitterConstants.PARTICLE_EMITTER_META_FILENAME);
		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		public void initialize(Object parent) {

		}

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider entityLocationProvider) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromFolderWatcherItems(entityLocationProvider, lGson, ParticleEmitterDefinition.class);
		}

		@Override
		public void loadDefinitionsFromMetaFile(String metaFilepath) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromMetaFileItems(metaFilepath, lGson, ParticleEmitterDefinition.class);
		}

		@Override
		public void loadDefinitionFromFile(String filepath) {
			final var lGson = new GsonBuilder().create();

			loadDefinitionFromFile(filepath, lGson, ParticleEmitterDefinition.class);
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -831550615078707748L;

	public static final int PARTICLE_EMIITER_NOT_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected EmitterDefinitionManager mEmitterDefinitionManager;
	protected ParticleFrameworkData mParticleFrameworkData;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleFrameworkData particleFrameworkData() {
		return mParticleFrameworkData;
	}

	public EmitterDefinitionManager definitionManager() {
		return mEmitterDefinitionManager;
	}

	public List<ParticleEmitterInstance> emitterInstances() {
		return mInstances;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterManager(ParticleFrameworkData particleFrameworkData) {
		mParticleFrameworkData = particleFrameworkData;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void afterLoaded(Object parent) {
		mEmitterDefinitionManager = new EmitterDefinitionManager();

		// Resolve all the ParticleSystems within the emitters to the ParticleSystem instances.
		if (parent instanceof ParticleFrameworkData) {
			final var lFramework = (ParticleFrameworkData) parent;
			mEmitterDefinitionManager.initialize(lFramework);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public ParticleEmitterInstance getNewParticleEmitterInstanceByDefName(String emitterDefName) {
		final var lEmitterDef = definitionManager().getByName(emitterDefName);

		if (lEmitterDef == null) {
			return null;
		}

		final var lNewEmitterInst = getFreePooledItem();
		lNewEmitterInst.assignEmitterDefinitionAndResolveParticleSystem(lEmitterDef, mParticleFrameworkData);

		if (!mInstances.contains(lNewEmitterInst)) {
			mInstances.add(lNewEmitterInst);
		}

		return lNewEmitterInst;
	}

	public ParticleEmitterInstance getParticleEmitterByIndex(int emitterIndex) {
		final var lNumParticleEmitterCount = mInstances.size();
		for (var i = 0; i < lNumParticleEmitterCount; i++) {
			if (mInstances.get(i).emitterInstanceId() == emitterIndex) {
				return mInstances.get(i);
			}
		}

		return null;
	}

	public void addParticleEmitterInstance(final ParticleEmitterInstance particleEmitterInstance) {
		if (!mInstances.contains(particleEmitterInstance)) {
			mInstances.add(particleEmitterInstance);
		}
	}

	public void removeParticleEmitterInstance(final ParticleEmitterInstance particleEmitterInstance) {
		particleEmitterInstance.reset();

		if (mInstances.contains(particleEmitterInstance)) {
			mInstances.remove(particleEmitterInstance);
		}

		returnPooledItem(particleEmitterInstance);
	}

	@Override
	protected ParticleEmitterInstance createPoolObjectInstance() {
		return new ParticleEmitterInstance();
	}
}
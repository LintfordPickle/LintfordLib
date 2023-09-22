package net.lintford.library.core.particles.particleemitters;

import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.entities.EntityLocationProvider;
import net.lintford.library.core.entities.definitions.DefinitionManager;
import net.lintford.library.core.entities.instances.ClosedPoolInstanceManager;
import net.lintford.library.core.particles.ParticleFrameworkData;

public class ParticleEmitterManager extends ClosedPoolInstanceManager<ParticleEmitterInstance> {

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

	public static final int PARTICLE_EMIITER_NOT_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final EmitterDefinitionManager mEmitterDefinitionManager = new EmitterDefinitionManager();
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

	public void initialize(Object parent) {
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
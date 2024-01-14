package net.lintfordlib.core.particles.particleemitters;

import java.io.File;
import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;
import net.lintfordlib.core.entities.instances.ClosedPoolInstanceManager;
import net.lintfordlib.core.particles.ParticleFrameworkData;

public class ParticleEmitterManager extends ClosedPoolInstanceManager<ParticleEmitterInstance> {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class EmitterDefinitionManager extends DefinitionManager<ParticleEmitterDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public EmitterDefinitionManager() {
			loadDefinitionsFromMetaFile(new File(ParticleEmitterConstants.PARTICLE_EMITTER_META_FILENAME));
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
		public void loadDefinitionsFromMetaFile(File file) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromMetaFileItems(file, lGson, ParticleEmitterDefinition.class);
		}

		@Override
		public ParticleEmitterDefinition loadDefinitionFromFile(File file) {
			final var lGson = new GsonBuilder().create();

			return loadDefinitionFromFile(file, lGson, ParticleEmitterDefinition.class);
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
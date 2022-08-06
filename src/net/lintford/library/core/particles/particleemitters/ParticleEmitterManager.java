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

		public void initialize(Object pParent) {

		}

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider pEntityLocationProvider) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromFolderWatcherItems(pEntityLocationProvider, lGson, ParticleEmitterDefinition.class);

		}

		@Override
		public void loadDefinitionsFromMetaFile(String pMetaFilepath) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromMetaFileItems(pMetaFilepath, lGson, ParticleEmitterDefinition.class);

		}

		@Override
		public void loadDefinitionFromFile(String pFilepath) {
			final var lGson = new GsonBuilder().create();

			loadDefinitionFromFile(pFilepath, lGson, ParticleEmitterDefinition.class);
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

	public ParticleEmitterManager(ParticleFrameworkData pParticleFrameworkData) {
		mParticleFrameworkData = pParticleFrameworkData;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void afterLoaded(Object pParent) {
		mEmitterDefinitionManager = new EmitterDefinitionManager();

		// Resolve all the ParticleSystems within the emitters to the ParticleSystem instances.
		if (pParent instanceof ParticleFrameworkData) {
			final var lFramework = (ParticleFrameworkData) pParent;
			mEmitterDefinitionManager.initialize(lFramework);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public ParticleEmitterInstance getNewParticleEmitterInstanceByDefName(String pEmitterDefName) {
		final var lEmitterDef = definitionManager().getByName(pEmitterDefName);

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

	public ParticleEmitterInstance getParticleEmitterByIndex(int pEmitterIndex) {
		final var lNumParticleEmitterCount = mInstances.size();
		for (var i = 0; i < lNumParticleEmitterCount; i++) {
			if (mInstances.get(i).emitterInstanceId() == pEmitterIndex) {
				return mInstances.get(i);

			}
		}

		return null;
	}

	public void addParticleEmitterInstance(final ParticleEmitterInstance pParticleEmitterInstance) {
		if (!mInstances.contains(pParticleEmitterInstance)) {
			mInstances.add(pParticleEmitterInstance);

		}
	}

	public void removeParticleEmitterInstance(final ParticleEmitterInstance pParticleEmitterInstance) {
		pParticleEmitterInstance.reset();

		if (mInstances.contains(pParticleEmitterInstance)) {
			mInstances.remove(pParticleEmitterInstance);

		}

		returnPooledItem(pParticleEmitterInstance);
	}

	@Override
	protected ParticleEmitterInstance createPoolObjectInstance() {
		return new ParticleEmitterInstance();
	}

}
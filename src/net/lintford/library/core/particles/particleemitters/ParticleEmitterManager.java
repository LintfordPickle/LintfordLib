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
		// Constants
		// --------------------------------------

		public ParticleEmitterDefinition getDefinitionByName(String pEmitterName) {
			if (pEmitterName == null || pEmitterName.isEmpty())
				return null;

			final int lNumEmitters = mDefinitions.size();
			for (int i = 0; i < lNumEmitters; i++) {
				if (mDefinitions.get(i).name.equals(pEmitterName))
					return mDefinitions.get(i);
			}

			return null;
		}

		public ParticleEmitterDefinition getDefinitionByDefId(int pEmitterDefId) {
			final int lNumEmitters = mDefinitions.size();

			if (pEmitterDefId < 0 || pEmitterDefId >= lNumEmitters)
				return null;

			for (int i = 0; i < lNumEmitters; i++) {
				if (mDefinitions.get(i).definitionUid == pEmitterDefId)
					return mDefinitions.get(i);
			}

			return null;
		}

		public ParticleEmitterDefinition getDefinitionByIndex(int pEmitterIndex) {
			final int lNumEmitters = mDefinitions.size();

			if (pEmitterIndex < 0 || pEmitterIndex >= lNumEmitters)
				return null;

			return mDefinitions.get(pEmitterIndex);
		}

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

	public static final int PARTICLE_EMITTER_NO_ID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected EmitterDefinitionManager mEmitterDefinitionManager;
	protected ParticleFrameworkData mParticleFrameworkData;
	protected int mParticleEmitterInstanceCounter;

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
	public void initialize(Object pParent) {
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

	// returns a new ParticleEmitterInstance attached to the given emitter definition (by name)
	public ParticleEmitterInstance getNewParticleEmitterInstanceByDefName(String pEmitterDefName) {
		// Find the specified definition
		final var lEmitterDef = definitionManager().getDefinitionByName(pEmitterDefName);

		if (lEmitterDef == null) {
			return null;

		}

		// Retrieve or create a new instance
		final var lNewEmitterInst = getFreePooledItem();
		lNewEmitterInst.emitterInstanceId(getNewInstanceUID());
		lNewEmitterInst.assignEmitterDefinitionAndResolveParticleSystem(lEmitterDef, mParticleFrameworkData);

		if (!mInstances.contains(lNewEmitterInst)) {
			mInstances.add(lNewEmitterInst);

		}

		return lNewEmitterInst;

	}

	// Returns a ParticleEmitterInstance, if one exists, based on the EmitterID.
	public ParticleEmitterInstance getParticleEmitterByIndex(int pEmitterIndex) {
		final var lNumParticleEmitterCount = mInstances.size();
		for (var i = 0; i < lNumParticleEmitterCount; i++) {
			if (mInstances.get(i).poolUid == pEmitterIndex) {
				return mInstances.get(i);

			}
		}

		return null;
	}

	@Override
	protected ParticleEmitterInstance createPoolObjectInstance() {
		return new ParticleEmitterInstance(mParticleEmitterInstanceCounter++);

	}

	public ParticleEmitterInstance getParticleEmitterInstance() {
		final var lParticleEmitterInstance = getFreePooledItem();
		lParticleEmitterInstance.emitterInstanceId(getNewInstanceUID());

		return lParticleEmitterInstance;

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

}

package net.lintford.library.core.particles.particlesystems;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.definitions.DefinitionManager;
import net.lintford.library.core.entity.instances.PooledInstanceManager;
import net.lintford.library.core.particles.ParticleFrameworkData;
import net.lintford.library.core.particles.particlesystems.deserializer.ParticleSystemDeserializer;

public class ParticleSystemManager extends PooledInstanceManager<ParticleSystemInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5013183501163339554L;

	public class ParticleSystemDefinitionManager extends DefinitionManager<ParticleSystemDefinition> {

		// --------------------------------------
		// Constants
		// --------------------------------------

		private static final long serialVersionUID = 2651760892817072383L;

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ParticleSystemDefinitionManager() {

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromMetaFile(String pMetaFilepath) {
			final var gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ParticleSystemDefinition.class, new ParticleSystemDeserializer());
			final var lGson = gsonBuilder.create();

			final var lMetaItems = loadMetaFileItemsFromFilepath(pMetaFilepath, lGson);

			loadDefinitionsFromMetaFileItems(lMetaItems, lGson, ParticleSystemDefinition.class);

		}

		@Override
		public void loadDefinitionFromFile(String pFilepath) {
			final var gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ParticleSystemDefinition.class, new ParticleSystemDeserializer());
			final var lGson = gsonBuilder.create();

			loadDefinitionFromFile(pFilepath, lGson, ParticleSystemDefinition.class);
		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ParticleFrameworkData mParticleFrameworkData;
	protected ParticleSystemDefinitionManager mParticleSystemDefinitionManager;
	protected List<ParticleSystemInstance> mParticleSystems;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the number of {@link ParticleSystemInstance}s in this {@link GameParticleSystem} instance. */
	public int getNumParticleSystems() {
		return mParticleSystems.size();
	}

	public ParticleSystemDefinitionManager definitionManager() {
		return mParticleSystemDefinitionManager;
	}

	public List<ParticleSystemInstance> particleSystems() {
		return mParticleSystems;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystemManager(ParticleFrameworkData pParticleFrameworkData) {
		super();

		mParticleFrameworkData = pParticleFrameworkData;
		mParticleSystemDefinitionManager = new ParticleSystemDefinitionManager();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Object pParent) {
		mParticleSystems = new ArrayList<>();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the {@link ParticleController} whose {@link ParticleSystemInstance}'s name matches the given {@link String}. null is returned if the ParticleController is not found. */
	public ParticleSystemInstance getParticleSystemByName(final String pParticleSystemName) {
		final var lNumParticleSystems = mParticleSystems.size();
		for (var i = 0; i < lNumParticleSystems; i++) {
			ParticleSystemInstance lPSInstance = mParticleSystems.get(i);
			if (!lPSInstance.isInitialized())
				continue;

			if (lPSInstance.definition().name.equals(pParticleSystemName)) {
				return mParticleSystems.get(i);

			}

		}

		final var lParticleSystemDefinition = mParticleSystemDefinitionManager.getDefinitionByName(pParticleSystemName);
		if (lParticleSystemDefinition != null) {
			final var lNewParticleSystem = createPoolObjectInstance();
			lNewParticleSystem.initialize(getNewInstanceUID(), lParticleSystemDefinition);

			mParticleSystems.add(lNewParticleSystem);

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Created new ParticleSystemInstance for ParticleSystemDefinition '%s'", lParticleSystemDefinition.name));

			return lNewParticleSystem;
		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't find ParticleSystemDefinition '%s'", pParticleSystemName));

		return null;

	}

	@Override
	protected ParticleSystemInstance createPoolObjectInstance() {
		return new ParticleSystemInstance();

	}

}

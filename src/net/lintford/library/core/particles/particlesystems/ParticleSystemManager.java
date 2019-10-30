package net.lintford.library.core.particles.particlesystems;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

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

	// Definition

	public class ParticleSystemDefinitionMetaData {
		public String rootDirectory;
		public String[] particleSystemFileLocations;

	}

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
		protected void loadDefinitions(String pMetaFilepath) {

			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ParticleSystemDefinition.class, new ParticleSystemDeserializer());

			final Gson gson = gsonBuilder.create();

			// Load the ItemDefiniion meta data (file locations)
			String lFileContents = null;
			ParticleSystemDefinitionMetaData lItemsFileLocations = null;
			try {
				lFileContents = new String(Files.readAllBytes(Paths.get(pMetaFilepath)));
				lItemsFileLocations = gson.fromJson(lFileContents, ParticleSystemDefinitionMetaData.class);

				if (lItemsFileLocations == null || lItemsFileLocations.particleSystemFileLocations == null || lItemsFileLocations.particleSystemFileLocations.length == 0) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load ParticleSystems from the ParticleSystems definition metafile!");

					return;

				}

			} catch (IOException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Error while loading ParticleSystem definitions metafile.");
				Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

				return;

			}

			ParticleSystemDefinition lParticleSystemDefinition = null;
			final int lParticleSystemDefinitionMetaCount = lItemsFileLocations.particleSystemFileLocations.length;
			for (int i = 0; i < lParticleSystemDefinitionMetaCount; i++) {
				var lParticleSystemDefinitionFilepath = lItemsFileLocations.rootDirectory + lItemsFileLocations.particleSystemFileLocations[i] + ".json";
				final var lParticleSystemDefinitionFile = new File(lParticleSystemDefinitionFilepath);

				if (!lParticleSystemDefinitionFile.exists()) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading ParticleSystemDefinition from file " + lParticleSystemDefinitionFilepath + ". File doesn't exist!");

					continue;
				}

				try {
					lFileContents = new String(Files.readAllBytes(lParticleSystemDefinitionFile.toPath()));

					lParticleSystemDefinition = gson.fromJson(lFileContents, ParticleSystemDefinition.class);

					if (lParticleSystemDefinition != null) {
						lParticleSystemDefinition.initialize(getNewDefinitionUID());
						mDefinitions.add(lParticleSystemDefinition);

					} else {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse ParticleSystemDefinition from file: " + lParticleSystemDefinitionFilepath);

					}

				} catch (JsonSyntaxException e) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON ParticleSystemDefinition (Syntax): " + lParticleSystemDefinitionFilepath);
					Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

					continue;

				} catch (IOException e) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON ParticleSystemDefinition (IO): " + lParticleSystemDefinitionFilepath);
					Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

					continue;

				}

			}

		}

		public void loadParticleSystemDefinitionsFIle(String pMetaFilepath) {
			loadDefinitions(pMetaFilepath);

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

	@Override
	public void beforeSerialization() {
		super.beforeSerialization();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadDefinitionMetaFile(String pMetaFilename) {
		mParticleSystemDefinitionManager.loadDefinitions(pMetaFilename);

		// FIXME: By this stage, all particle definitions loaded should have an accompanying ParticleSystemInstance!

	}

	/** Returns the {@link ParticleController} whose {@link ParticleSystemInstance}'s name matches the given {@link String}. null is returned if the ParticleController is not found. */
	public ParticleSystemInstance getParticleSystemByName(final String pParticleSystemName) {
		final int lNumParticleSystems = mParticleSystems.size();
		for (int i = 0; i < lNumParticleSystems; i++) {
			ParticleSystemInstance lPSInstance = mParticleSystems.get(i);
			if (!lPSInstance.isInitialized())
				continue;

			if (lPSInstance.definition().name.equals(pParticleSystemName)) {
				return mParticleSystems.get(i);

			}

		}

		ParticleSystemDefinition pd = mParticleSystemDefinitionManager.getDefinitionByName(pParticleSystemName);
		if (pd != null) {
			ParticleSystemInstance ps = createPoolObjectInstance();
			ps.initialize(getNewUID(), pd);

			mParticleSystems.add(ps);

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Created new ParticleSystemInstance for ParticleSystemDefinition '%s'", pd.name));

			return ps;
		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't find ParticleSystemDefinition '%s'", pParticleSystemName));

		return null;

	}

	@Override
	protected ParticleSystemInstance createPoolObjectInstance() {
		return new ParticleSystemInstance();

	}

}

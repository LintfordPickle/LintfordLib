package net.lintford.library.core.particles.particlesystems.initializers;

import java.util.Random;

import net.lintford.library.core.particles.Particle;
import net.lintford.library.core.particles.particlesystems.modifiers.ParticleModifierBase;

/**
 * This {@link ParticleModifierBase} updates {@link Particle} instances to mimic the behavior of clouds/mist within a city. Particles with this modifier will only die when they
 * are not visible.
 */
public class ParticleCityCloudInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1175761993738222781L;

	public static final String INITIALIZER_NAME = "ParticleCityCloudInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	// WeatherManager // Use humidity to dictate the number of cloud particles and the base alpha values.
	protected Random mRandom = new Random();

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	public ParticleCityCloudInitializer() {
		super(INITIALIZER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		// Set default color and alpha
		pParticle.color.setRGBA(1.f, 1.f, 1.f, 1.f);

		final int lParticleSourceDimensions = 256;
		final int lParticleDestinationDimensions = 512;

		// Set default texture regions
		pParticle.sx = pParticle.sy = 0;
		pParticle.sw = pParticle.sh = lParticleSourceDimensions;
		pParticle.width = lParticleDestinationDimensions;
		pParticle.height = lParticleDestinationDimensions;
		pParticle.rox = pParticle.roy = lParticleDestinationDimensions / 2;
		pParticle.sx = pParticle.sy = 1;

	}

}

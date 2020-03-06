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
		pParticle.r = pParticle.g = pParticle.b = 1;
		pParticle.a = 1;

		final int SOURCE_DIMENSIONS = 256;
		final int DEST_DIMENSIONS = 512;

		// Set default texture regions
		pParticle.sx = pParticle.sy = 0;
		pParticle.sw = pParticle.sh = SOURCE_DIMENSIONS;
		pParticle.radius = DEST_DIMENSIONS;
		pParticle.rox = pParticle.roy = DEST_DIMENSIONS / 2;
		pParticle.sx = pParticle.sy = 1;

//		// Set default position which should be off-screen and taking into consideration the wind direction (TODO: WeatherManager)
//		final float MIN_X_POSITION = mGameCamera.getMinX() - mRandom.nextFloat() * DEST_DIMENSIONS * 3;
//		final float cellX = MIN_X_POSITION + mRandom.nextFloat() * mGameCamera.getWidth() * 2f;
//
//		final float MIN_Y_POSITION = mGameCamera.getMinY() - mGameCamera.getHeight() / 2;
//		final float cellY = MIN_Y_POSITION + mRandom.nextFloat() * mGameCamera.getHeight() * 2f;
//
//		pParticle.setPosition(cellX, cellY);
//		pParticle.rotv = (float) Math.toRadians(mRandom.nextFloat() * 10f - 5f);

	}

}

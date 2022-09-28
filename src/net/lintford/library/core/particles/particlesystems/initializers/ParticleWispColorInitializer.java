package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

/** A specific implementation fo the {@link IParticleinitializer} interface which initializes particles with a color reminiscnt of a fire (orange / yellow) */
public class ParticleWispColorInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8461840463880690660L;

	public static final String INITIALIZER_NAME = "ParticleWispColorInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final float mRMin, mRMax;
	public final float mGMin, mBMax;
	public final float mBMin, mGMax;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleWispColorInitializer() {
		super(INITIALIZER_NAME);

		mRMin = 0.8f;
		mRMax = 1f;
		mGMin = 0.8f;
		mGMax = 1f;
		mBMin = 0.6f;
		mBMax = 1f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		particle.color.r = getRandomValue(mRMin, mRMax);
		particle.color.g = getRandomValue(mGMin, mGMax);
		particle.color.b = getRandomValue(mBMin, mBMax);
		particle.color.a = 0.5f + getRandomValue(.0f, .5f);
	}

	protected float getRandomValue(final float minValue, final float maxValue) {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}

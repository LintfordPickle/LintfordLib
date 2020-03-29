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
	public void initialize(Particle pParticle) {
		pParticle.r = getRandomValue(mRMin, mRMax);
		pParticle.g = getRandomValue(mGMin, mGMax);
		pParticle.b = getRandomValue(mBMin, mBMax);
		pParticle.a = 0.5f + getRandomValue(0, 0.5f);

	}

	protected float getRandomValue(final float pMin, final float pMax) {
		if (pMin == pMax) {
			return pMax;

		} else {
			return RandomNumbers.random(pMin, pMax);

		}

	}

}

package net.lintford.library.core.particles.particlesystems.initializers;

import java.util.Random;

import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

/** A specific implementation fo the {@link IParticleinitializer} interface which initializes particles with a color reminiscnt of a fire (orange / yellow) */
public class ParticleBlockColorInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 9089664680748165001L;

	public static final String INITIALIZER_NAME = "ParticleBlockColorInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient Random mRandom = new Random();
	public float mRMin, mRMax;
	public float mGMin, mBMax;
	public float mBMin, mGMax;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleBlockColorInitializer() {
		super(INITIALIZER_NAME);

		mRMin = 0.34f;
		mRMax = 1f;
		mGMin = 0.05f;
		mGMax = 0.6f;
		mBMin = 0.03f;
		mBMax = 0.1f;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setBlockColor(final float pR, final float pG, final float pB) {
		final float L = mRandom.nextFloat() * 0.8f;
		final float H = mRandom.nextFloat() * 0.15f;

		mRMin = pR - L;
		mRMax = pR + H;

		mGMin = pG - L;
		mGMax = pG + H;

		mBMin = pB - L;
		mBMax = pB + H;

		mRMin = MathHelper.clamp(mRMin, 0, 1);
		mRMax = MathHelper.clamp(mRMax, 0, 1);

		mGMin = MathHelper.clamp(mGMin, 0, 1);
		mGMax = MathHelper.clamp(mGMax, 0, 1);

		mBMin = MathHelper.clamp(mBMin, 0, 1);
		mBMax = MathHelper.clamp(mBMax, 0, 1);

	}

	@Override
	public void initialize(Particle pParticle) {
		pParticle.r = getRandomValue(mRMin, mRMax);
		pParticle.g = getRandomValue(mGMin, mGMax);
		pParticle.b = getRandomValue(mBMin, mBMax);

	}

	protected float getRandomValue(final float pMin, final float pMax) {
		if (pMin == pMax) {
			return pMax;

		} else {
			return RandomNumbers.random(pMin, pMax);

		}

	}

}

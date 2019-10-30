package net.lintford.library.core.particles.particlesystems.initializers;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.Particle;

/** A specific implementation fo the {@link IParticleinitializer} interface which initializes particles with a color reminiscnt of a fire (orange / yellow) */
public class ParticleFireColorInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8966764514890493840L;

	public static final String INITIALIZER_NAME = "ParticleFireColorInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final float redMin, redMax;
	public final float greenMin, greenMax;
	public final float blueMin, blueMax;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFireColorInitializer() {
		super(INITIALIZER_NAME);

		redMin = 209f / 255f;
		redMax = 229f / 255f;

		greenMin = 160f / 255f;
		blueMax = 180f / 255f;

		blueMin = 0f / 255f;
		greenMax = 16f / 255f;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		pParticle.r = getRandomValue(redMin / 255f, redMax / 255f);
		pParticle.g = getRandomValue(greenMin / 255f, greenMax / 255f);
		pParticle.b = getRandomValue(blueMin / 255f, blueMax / 255f);

	}

	protected float getRandomValue(final float pMin, final float pMax) {
		if (pMin == pMax) {
			return pMax;

		} else {
			return RandomNumbers.random(pMin, pMax);

		}

	}

}

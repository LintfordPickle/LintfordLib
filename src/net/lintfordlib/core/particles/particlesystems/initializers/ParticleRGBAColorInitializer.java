package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;

/** A specific implementation fo the {@link IParticleinitializer} interface which initializes particles with a color reminiscnt of a fire (orange / yellow) */
public class ParticleRGBAColorInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8461840463880690660L;

	public static final String INITIALIZER_NAME = "ParticleWispColorInitializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final float redMin, redMax;
	public final float greenMin, greenMax;
	public final float blueMin, blueMax;
	public final float alphaMinOffset, alphaMaxOffset, alphaDef;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRGBAColorInitializer() {
		super(INITIALIZER_NAME);

		redMin = 0.f;
		redMax = 1f;
		greenMin = 0.f;
		greenMax = 1f;
		blueMin = 0.f;
		blueMax = 1f;

		alphaDef = .5f;
		alphaMinOffset = 0.f;
		alphaMaxOffset = .5f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		particle.color.r = getRandomValue(redMin, redMax);
		particle.color.g = getRandomValue(greenMin, greenMax);
		particle.color.b = getRandomValue(blueMin, blueMax);
		particle.color.a = alphaDef + getRandomValue(alphaMinOffset, alphaMaxOffset);
	}

	protected float getRandomValue(final float minValue, final float maxValue) {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}

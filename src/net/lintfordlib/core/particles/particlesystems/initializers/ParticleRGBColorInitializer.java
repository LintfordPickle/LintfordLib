package net.lintfordlib.core.particles.particlesystems.initializers;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;

public class ParticleRGBColorInitializer extends ParticleInitializerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8470227714120105949L;

	public static final String INITIALIZER_NAME = "RGB Color Initializer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final float redMin, redMax;
	public final float greenMin, greenMax;
	public final float blueMin, blueMax;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRGBColorInitializer() {
		super(INITIALIZER_NAME);

		redMin = 0f / 255f;
		redMax = 255f / 255f;

		greenMin = 0f / 255f;
		blueMax = 255f / 255f;

		blueMin = 0f / 255f;
		greenMax = 255f / 255f;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		particle.color.r = getRandomValue(redMin / 255f, redMax / 255f);
		particle.color.g = getRandomValue(greenMin / 255f, greenMax / 255f);
		particle.color.b = getRandomValue(blueMin / 255f, blueMax / 255f);
	}

	protected float getRandomValue(final float minValue, final float maxValue) {
		if (minValue == maxValue) {
			return maxValue;
		} else {
			return RandomNumbers.random(minValue, maxValue);
		}
	}
}

package net.lintford.library.core.particles.particlesystems.modifiers;

import java.util.Random;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.particles.Particle;

/**
 * This {@link ParticleModifierBase} updates {@link Particle} instances to mimic the behaviour of clouds/mist within a city. Particles with this modifier will only die when they
 * are not visible.
 */
public class ParticleCityCloudModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6281089117274583766L;

	public static final String MODIFIER_NAME = "ParticleCityCloudModifier";

	// --------------------------------------
	// Variables
	// --------------------------------------

	// WeatherManager // Use humidity to dictate the number of cloud particles and the base alpha values.
	protected Random mRandom = new Random();
	protected Rectangle mKillRectangle;

	// --------------------------------------
	// Constrcutor
	// --------------------------------------

	public ParticleCityCloudModifier() {
		super(MODIFIER_NAME);

		mKillRectangle = new Rectangle();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {
		// Update the position of the camera kill rectangle
		// mKillRectangle.set(mGameCamera.boundingRectangle(), 2f, 1.5f);

	}

	// TODO: Physics cloud movement
	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		// Move the clouds around the player's location in the world.

	}

	public boolean hasCollision(int PCX, int pCY) {
		// TODO: Collision check elsewhere
		return false;
	}

}

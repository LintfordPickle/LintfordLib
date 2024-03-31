package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleWanderMovementModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6797729588507702695L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float speed;
	public float wanderAmtRad;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleWanderMovementModifier() {
		this(1.f, 5.f);

	}

	public ParticleWanderMovementModifier(float speed, float wanderAmtRad) {
		super(ParticleWanderMovementModifier.class.getSimpleName());

		this.speed = speed;
		this.wanderAmtRad = wanderAmtRad;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {
		// initialize with random direction
		final float lMaxRange = (float) Math.PI * 2f;
		particle.angVel = RandomNumbers.nextFloat() * lMaxRange;

		// lauch particle into current heading
		particle.vx += (float) Math.cos(particle.angVel);
		particle.vy += (float) Math.sin(particle.angVel);
	}

	@Override
	public void update(LintfordCore core) {

	}

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		particle.angVel += RandomNumbers.random(-wanderAmtRad, wanderAmtRad);
//		float lAngle = (float) Math.toRadians(particle.dr);// (float) Math.atan2(particle.dy, particle.dx);

		particle.vx += (float) Math.cos(particle.angVel) * speed;
		particle.vy += (float) Math.sin(particle.angVel) * speed;
	}
}

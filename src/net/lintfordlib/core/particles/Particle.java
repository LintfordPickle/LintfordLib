package net.lintfordlib.core.particles;

import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleInitializerBase;

public class Particle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final float DO_NOT_DESPAWN_LIFETIME = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsFree;
	public float timeSinceStart;
	private float mLifeTimeInMs;

	public float width;
	public float height;
	public float sx, sy, sw, sh;

	public float rox;
	public float roy;

	public float dx, dy, dr;
	public final Color color = new Color();
	public float scale;

	public float worldPositionX;
	public float worldPositionY;
	public float rotationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAssigned() {
		return !mIsFree;
	}

	/** Returns the amount of lifetime this particle was given when spawned */
	public float lifeTime() {
		return mLifeTimeInMs;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Particle() {
		reset();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Setups the source texture area this particle will draw from, and the width/height of the particle. */
	public void setupSourceTexture(float sourceX, float sourceY, float sourceW, float sourceH) {
		sx = sourceX;
		sy = sourceY;
		sw = sourceW;
		sh = sourceH;
	}

	public void setupDestTexture(float destinationWidth, float destinationHeight) {
		width = destinationWidth;
		height = destinationHeight;
	}

	public void spawnParticle(float worldX, float worldY, float velocityX, float velocityY, float lifetimeInMs) {
		mIsFree = false;
		mLifeTimeInMs = lifetimeInMs;
		timeSinceStart = 0;

		sx = sy = 1;
		color.setRGBA(1.f, 1.f, 1.f, 1.f);

		worldPositionX = worldX;
		worldPositionY = worldY;
		dx = velocityX;
		dy = velocityY;
	}

	public void reset() {
		mIsFree = true;
		mLifeTimeInMs = 0;
		timeSinceStart = 0;
		scale = 1f;

		worldPositionX = 0;
		worldPositionY = 0;

		dx = 0f;
		dy = 0f;
	}

	public void applyInitializer(ParticleInitializerBase particleInitializerBase) {
		if (particleInitializerBase == null)
			return;

		particleInitializerBase.initialize(this);
	}
}

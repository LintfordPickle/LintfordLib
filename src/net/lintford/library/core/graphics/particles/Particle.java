package net.lintford.library.core.graphics.particles;

import net.lintford.library.core.graphics.particles.initializers.IParticleInitializer;
import net.lintford.library.data.entities.CircleEntity;

public class Particle extends CircleEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 820164057821427990L;

	public static final float DO_NOT_DESPAWN_LIFETIME = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsFree;
	public float timeSinceStart;
	private float mLifeTime;

	public float sx, sy, sw, sh; // The src tex rect

	public float rox, roy, rot, rotv;

	public float dx, dy, dr;
	public float r, g, b, a;
	public float scale;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the amount of lifetime this particle was given when spawned */
	public float lifeTime() {
		return mLifeTime;
	}

	public boolean isFree() {
		return mIsFree;
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
	public void setupSourceTexture(float pSX, float pSY, float pSW, float pSH) {
		sx = pSX;
		sy = pSY;
		sw = pSW;
		sh = pSH;

	}

	public void setupDestTexture(float pSize) {
		radius = pSize;

	}

	public void spawnParticle(float pWorldX, float pWorldY, float pVelX, float pVelY, float pLife) {
		mIsFree = false;
		mLifeTime = pLife;
		timeSinceStart = 0;

		sx = sy = 1;
		r = g = b = a = 1;

		x = pWorldX;
		y = pWorldY;
		dx = pVelX;
		dy = pVelY;

	}

	public void reset() {
		mIsFree = true;
		mLifeTime = 0;
		timeSinceStart = 0;
		scale = 1f;

		x = 0;
		y = 0;

		dx = 0f;
		dy = 0f;

	}

	public void applyinitializer(IParticleInitializer p) {
		if (p == null)
			return;

		p.initialize(this);

	}

}

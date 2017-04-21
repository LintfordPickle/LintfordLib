package net.ld.library.core.graphics.particles;

import net.ld.library.cellworld.CircleEntity;
import net.ld.library.core.graphics.particles.initialisers.IParticleInitialiser;

public class Particle extends CircleEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final float DO_NOT_DESPAWN_LIFETIME = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsFree;
	public float timeSinceStart;
	private float mLifeTime;

	public float sx, sy, sw, sh; // The src tex rect

	public float rox, roy, rot, rotv;

	public float r, g, b, a;
	public float scaleX;
	public float scaleY;

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

		// TODO (John) Hard coded CELL_SIZE!
		setPosition(pWorldX, pWorldY, 64);
		setVelocity(pVelX, pVelY);
		sx = sy = 1;
		r = g = b = a = 1;

		dx = pVelX;
		dy = pVelY;
	}

	public void reset() {
		mIsFree = true;
		mLifeTime = 0;
		timeSinceStart = 0;

		cx = 0;
		cy = 0;

		dx = 0f;
		dy = 0f;
		
		scaleX = 1f;
		scaleY = 1f;

	}

	public void applyInitialiser(IParticleInitialiser p) {
		if (p == null)
			return;

		p.initialise(this);

	}

}

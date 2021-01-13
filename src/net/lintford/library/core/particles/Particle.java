package net.lintford.library.core.particles;

import net.lintford.library.core.entity.instances.PreAllocatedInstanceData;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.particles.particlesystems.initializers.ParticleInitializerBase;

public class Particle extends PreAllocatedInstanceData {

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

	public float width;
	public float height;
	public float sx, sy, sw, sh; // The src tex rect

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
		return mLifeTime;
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

	public void setupDestTexture(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;

	}

	public void spawnParticle(float pWorldX, float pWorldY, float pVelX, float pVelY, float pLife) {
		mIsFree = false;
		mLifeTime = pLife;
		timeSinceStart = 0;

		sx = sy = 1;
		color.setRGBA(1.f, 1.f, 1.f, 1.f);

		worldPositionX = pWorldX;
		worldPositionY = pWorldY;
		dx = pVelX;
		dy = pVelY;

	}

	public void reset() {
		mIsFree = true;
		mLifeTime = 0;
		timeSinceStart = 0;
		scale = 1f;

		worldPositionX = 0;
		worldPositionY = 0;

		dx = 0f;
		dy = 0f;

	}

	public void applyInitializer(ParticleInitializerBase pInitializer) {
		if (pInitializer == null)
			return;

		pInitializer.initialize(this);

	}

}

package net.lintford.library.core.graphics.particles;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.initialisers.IParticleInitializer;
import net.lintford.library.core.graphics.particles.modifiers.IParticleModifier;

public class ParticleSystem {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final String mParticleSystemName;

	private List<Particle> mParticles;
	private List<IParticleInitializer> mInitializers;
	private List<IParticleModifier> mModifiers;
	private final int mCapacity;

	private String mTextureName;
	private String mTextureFilename;

	/** Particle Systems must be initialized with the name and location of the texture to use for rendering. */
	private boolean mIsinitialized;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the collection of {@link Particle}s in this {@link ParticleSystem}. */
	public List<Particle> particles() {
		return mParticles;
	}

	public boolean isinitialized() {
		return mIsinitialized;
	}

	public String name() {
		return mParticleSystemName;
	}

	/** Returns the internal texture name. */
	public String textureName() {
		return mTextureName;
	}

	/** Returns the filename of the texture. */
	public String textureFilename() {
		return mTextureFilename;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystem(String pParticleSystemName) {
		this(pParticleSystemName, 64);
	}

	public ParticleSystem(String pParticleSystemName, int pCap) {
		mParticleSystemName = pParticleSystemName;

		mParticles = new ArrayList<>();
		mInitializers = new ArrayList<>();
		mModifiers = new ArrayList<>();

		if (pCap <= 0)
			pCap = 32;
		mCapacity = pCap;

		for (int i = 0; i < mCapacity; i++) {
			mParticles.add(new Particle());
		}

		mIsinitialized = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final String pTextureName, final String pTextureFilename) {
		mTextureName = pTextureName;
		mTextureFilename = pTextureFilename;

		mIsinitialized = true;

	}

	public void update(LintfordCore pCore) {
		// Update the modifiers independently of the particles
		final int lNumModifiers = mModifiers.size();
		for (int j = 0; j < lNumModifiers; j++) {
			mModifiers.get(j).update(pCore);
		}

		for (int i = 0; i < mCapacity; i++) {
			Particle p = mParticles.get(i);

			if (p.isFree())
				continue;

			// Kill the particle if it exceeds its lifetime (unless lifeTime is NO_DO_DESPAWN
			if (p.lifeTime() != Particle.DO_NOT_DESPAWN_LIFETIME) {
				p.timeSinceStart += pCore.time().elapseGameTimeMilli();
				if (p.timeSinceStart >= p.lifeTime()) {
					// kill the particle
					p.reset();

				}
			}

			if (p.isFree())
				continue;

			for (int j = 0; j < lNumModifiers; j++) {
				mModifiers.get(j).updateParticle(pCore, mParticles.get(i));
			}
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Spawns a new {@link Particle} instance, foregoing the {@link IParticleinitializer}s attached to this {@link ParticleSystem}. Insteadm you can specifiy the individual components
	 * of the particles.
	 */
	public Particle spawnParticle(float pX, float pY, float pVelX, float pVelY, float pLife, float pSX, float pSY, float pSW, float pSH, float pRadius) {
		Particle lNewParticle = spawnParticle(pX, pY, pVelX, pVelY, pLife);
		if (lNewParticle != null) {
			lNewParticle.setupSourceTexture(pSX, pSY, pSW, pSH);
			lNewParticle.setupDestTexture(pRadius);

			return lNewParticle;
		}

		// No particle created.
		return null;

	}

	/** Applies all the {@link IParticleinitializer}s attached to this system to the given {@link Particle} instance. */
	public void applyinitializers(Particle pParticle) {
		if (pParticle == null)
			return;

		final int NUM_initializeRS = mInitializers.size();
		for (int j = 0; j < NUM_initializeRS; j++) {
			mInitializers.get(j).initialize(pParticle);

		}

	}

	/** Spawns a new {@link Particle} and applys the {@link IParticleinitializer} attached to this {@link ParticleSystem}. */
	public Particle spawnParticle(float pX, float pY, float pVelX, float pVelY, float pLife) {
		for (int i = 0; i < mCapacity; i++) {
			Particle p = mParticles.get(i);
			if (!p.isFree())
				continue;

			p.spawnParticle(pX, pY, pVelX, pVelY, pLife);

			final int NUM_initializeRS = mInitializers.size();
			for (int j = 0; j < NUM_initializeRS; j++) {
				mInitializers.get(j).initialize(p);

			}

			return p;
		}

		return null;

	}

	public void addinitializer(IParticleInitializer pinitializer) {
		if (pinitializer == null)
			return;

		if (!mInitializers.contains(pinitializer)) {
			mInitializers.add(pinitializer);

		}

	}

	public void addModifier(IParticleModifier pModifier) {
		if (pModifier == null)
			return;

		if (!mModifiers.contains(pModifier)) {
			mModifiers.add(pModifier);
		}
	}

	public void reset() {
		for (int i = 0; i < mCapacity; i++) {
			Particle p = mParticles.get(i);
			p.reset();

		}

	}

}

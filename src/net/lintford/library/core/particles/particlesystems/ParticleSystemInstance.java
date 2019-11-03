package net.lintford.library.core.particles.particlesystems;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.BaseData;
import net.lintford.library.core.particles.Particle;
import net.lintford.library.core.particles.particlesystems.initializers.ParticleInitializerBase;
import net.lintford.library.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleSystemInstance extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8190016634061373178L;

	public static final int NO_RENDERER_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mParticleSystemID;
	protected ParticleSystemDefinition mParticleSystemDefinition;
	private List<Particle> mParticles;
	private transient int mRendererId;

	private int mCapacity;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInitialized() {
		return mParticleSystemDefinition != null;
	}

	/** Returns the collection of {@link Particle}s in this {@link ParticleSystemInstance}. */
	public List<Particle> particles() {
		return mParticles;
	}

	public ParticleSystemDefinition definition() {
		return mParticleSystemDefinition;
	}

	public int rendererId() {
		return mRendererId;
	}

	public boolean isAssigned() {
		return mParticleSystemDefinition != null;
	}

	public int getPoolID() {
		return mParticleSystemID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystemInstance() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final int pInstId, final ParticleSystemDefinition pParticleSystemDefinition) {
		mParticleSystemDefinition = pParticleSystemDefinition;
		mParticleSystemID = pInstId;
		mCapacity = mParticleSystemDefinition.maxParticleCount();

		mRendererId = NO_RENDERER_ASSIGNED;

		mParticles = new ArrayList<>();
		for (int i = 0; i < mCapacity; i++) {
			mParticles.add(new Particle());

		}

	}

	public void update(LintfordCore pCore) {
		if (!isInitialized())
			return;

		// Update the modifiers independently of the particles
		List<ParticleModifierBase> lModifers = mParticleSystemDefinition.modifiers();
		final int lNumModifiers = lModifers.size();
		for (int j = 0; j < lNumModifiers; j++) {
			lModifers.get(j).update(pCore);

		}

		for (int i = 0; i < mCapacity; i++) {
			Particle p = mParticles.get(i);

			if (!p.isAssigned())
				continue;

			// Kill the particle if it exceeds its lifetime (unless lifeTime is NO_DO_DESPAWN
			if (p.lifeTime() != Particle.DO_NOT_DESPAWN_LIFETIME) {
				p.timeSinceStart += pCore.time().elapseGameTimeMilli();
				if (p.timeSinceStart >= p.lifeTime()) {
					// kill the particle
					p.reset();

				}
			}

			if (!p.isAssigned())
				continue;

			for (int j = 0; j < lNumModifiers; j++) {
				lModifers.get(j).updateParticle(pCore, mParticles.get(i));
			}
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignedRendererId(final int pRendererId) {
		mRendererId = pRendererId;

	}

	/**
	 * Spawns a new {@link Particle} instance, foregoing the {@link IParticleinitializer}s attached to this {@link ParticleSystemInstance}. Insteadm you can specifiy the individual
	 * components of the particles.
	 */
	public Particle spawnParticle(float pX, float pY, float pVelX, float pVelY, float pSX, float pSY, float pSW, float pSH, float pRadius) {
		Particle lNewParticle = spawnParticle(pX, pY, pVelX, pVelY);
		if (lNewParticle != null) {
			lNewParticle.setupSourceTexture(pSX, pSY, pSW, pSH);
			lNewParticle.setupDestTexture(pRadius);

			return lNewParticle;
		}

		// No particle created.
		return null;

	}

	/** Applies all the {@link IParticleinitializer}s attached to this system to the given {@link Particle} instance. */
	public void applyInitializers(Particle pParticle) {
		if (pParticle == null)
			return;

		List<ParticleInitializerBase> lInitializers = mParticleSystemDefinition.initializers();
		final int lNumInitializers = lInitializers.size();
		for (int j = 0; j < lNumInitializers; j++) {
			lInitializers.get(j).initialize(pParticle);

		}

	}

	/** Spawns a new {@link Particle} and applys the {@link IParticleinitializer} attached to this {@link ParticleSystemInstance}. */
	public Particle spawnParticle(float pX, float pY, float pVelX, float pVelY) {
		for (int i = 0; i < mCapacity; i++) {
			Particle lSpawnedParticle = mParticles.get(i);
			if (lSpawnedParticle.isAssigned())
				continue;

			lSpawnedParticle.spawnParticle(pX, pY, pVelX, pVelY, mParticleSystemDefinition.particleLife);

			applyInitializers(lSpawnedParticle);

			return lSpawnedParticle;
		}

		return null;

	}

	public void addInitializer(ParticleInitializerBase pInitializer) {
		if (pInitializer == null)
			return;

		if (!mParticleSystemDefinition.initializers().contains(pInitializer)) {
			mParticleSystemDefinition.initializers().add(pInitializer);

		}

	}

	public void addModifier(ParticleModifierBase pModifier) {
		if (pModifier == null)
			return;

		if (!mParticleSystemDefinition.modifiers().contains(pModifier)) {
			mParticleSystemDefinition.modifiers().add(pModifier);
		}
	}

	public void reset() {
		for (int i = 0; i < mCapacity; i++) {
			Particle p = mParticles.get(i);
			p.reset();

		}

	}

}

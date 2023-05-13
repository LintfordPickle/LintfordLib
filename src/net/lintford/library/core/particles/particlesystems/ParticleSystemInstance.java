package net.lintford.library.core.particles.particlesystems;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;
import net.lintford.library.core.particles.particlesystems.initializers.ParticleInitializerBase;
import net.lintford.library.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleSystemInstance {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_RENDERER_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mParticleSystemUid;
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
		return mParticleSystemUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystemInstance() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final int particleSystemUid, final ParticleSystemDefinition particleSystemDefinition) {
		mParticleSystemDefinition = particleSystemDefinition;
		mParticleSystemUid = particleSystemUid;
		mCapacity = mParticleSystemDefinition.maxParticleCount();

		mRendererId = NO_RENDERER_ASSIGNED;

		mParticles = new ArrayList<>();
		for (int i = 0; i < mCapacity; i++) {
			mParticles.add(new Particle());
		}
	}

	public void update(LintfordCore core) {
		if (!isInitialized())
			return;

		final var lParticleModifers = mParticleSystemDefinition.modifiers();
		final int lNumModifiers = lParticleModifers.size();
		for (int j = 0; j < lNumModifiers; j++) {
			lParticleModifers.get(j).update(core);
		}

		for (int i = 0; i < mCapacity; i++) {
			final var particle = mParticles.get(i);

			if (!particle.isAssigned())
				continue;

			// Kill the particle if it exceeds its lifetime (unless lifeTime is NO_DO_DESPAWN
			if (particle.lifeTime() != Particle.DO_NOT_DESPAWN_LIFETIME) {
				particle.timeSinceStart += core.appTime().elapsedTimeMilli();
				if (particle.timeSinceStart >= particle.lifeTime()) {
					particle.reset();
					continue;
				}
			}

			if (!particle.isAssigned())
				continue;

			for (int j = 0; j < lNumModifiers; j++) {
				lParticleModifers.get(j).updateParticle(core, mParticles.get(i));
			}
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignedRendererId(final int rendererId) {
		mRendererId = rendererId;
	}

	/**
	 * Spawns a new {@link Particle} instance, foregoing the {@link IParticleinitializer}s attached to this {@link ParticleSystemInstance}. Insteadm you can specifiy the individual components of the particles.
	 */
	public Particle spawnParticle(float worldX, float worldY, float velocityX, float velocityY, float sourceX, float sourceY, float sourceW, float sourceH, float destWidth, float destHeight) {
		final var lNewParticle = spawnParticle(worldX, worldY, velocityX, velocityY);
		if (lNewParticle != null) {
			lNewParticle.setupSourceTexture(sourceX, sourceY, sourceW, sourceH);
			lNewParticle.setupDestTexture(destWidth, destHeight);

			return lNewParticle;
		}

		return null;
	}

	/** Spawns a new {@link Particle} and applys the {@link IParticleinitializer} attached to this {@link ParticleSystemInstance}. */
	public Particle spawnParticle(float worldX, float worldY, float velocityX, float velocityY) {
		for (int i = 0; i < mCapacity; i++) {
			final var lSpawnedParticle = mParticles.get(i);
			if (lSpawnedParticle.isAssigned())
				continue;

			lSpawnedParticle.spawnParticle(worldX, worldY, velocityX, velocityY, mParticleSystemDefinition.particleLife);

			applyInitializers(lSpawnedParticle);

			return lSpawnedParticle;
		}

		return null;
	}

	/** Applies all the {@link IParticleinitializer}s attached to this system to the given {@link Particle} instance. */
	public void applyInitializers(Particle particle) {
		if (particle == null)
			return;

		final var lInitializers = mParticleSystemDefinition.initializers();
		final int lNumInitializers = lInitializers.size();
		for (int i = 0; i < lNumInitializers; i++) {
			lInitializers.get(i).initialize(particle);
		}
	}

	public void addInitializer(ParticleInitializerBase particleInitializerBase) {
		if (particleInitializerBase == null)
			return;

		if (!mParticleSystemDefinition.initializers().contains(particleInitializerBase)) {
			mParticleSystemDefinition.initializers().add(particleInitializerBase);
		}
	}

	public void addModifier(ParticleModifierBase particleModifierBase) {
		if (particleModifierBase == null)
			return;

		if (!mParticleSystemDefinition.modifiers().contains(particleModifierBase)) {
			mParticleSystemDefinition.modifiers().add(particleModifierBase);
		}
	}

	public void reset() {
		for (int i = 0; i < mCapacity; i++) {
			mParticles.get(i).reset();
		}
	}
}

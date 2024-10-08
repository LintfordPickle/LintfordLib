package net.lintfordlib.core.particles.particlesystems;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particleemitters.ParticleEmitterInstance;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleInitializerBase;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleSystemInstance {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_RENDERER_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected boolean mIsAssigned;
	protected int mParticleSystemUid;
	protected transient ParticleSystemDefinition mParticleSystemDefinition;
	protected transient ParticleEmitterInstance mOnDeathEmitter;
	private List<Particle> mParticles;
	private int mCapacity;

	private transient int mRendererId;

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
	// Core-Methods
	// --------------------------------------

	public void assignSystemDefinitionAndResolveEmitters(int particleSystemUid, ParticleSystemDefinition particleSystemDefinition, ParticleFrameworkData frameworkData) {
		mParticleSystemUid = particleSystemUid;
		mIsAssigned = true;
		mParticleSystemDefinition = particleSystemDefinition;
		mCapacity = mParticleSystemDefinition.maxParticleCount;

		mRendererId = NO_RENDERER_ASSIGNED;

		mParticles = new ArrayList<>();
		for (int i = 0; i < mCapacity; i++) {
			mParticles.add(new Particle());
		}

		resolveOnDeathEmitter(frameworkData);
	}

	private void resolveOnDeathEmitter(ParticleFrameworkData frameworkData) {
		final var lParticleEmitterName = mParticleSystemDefinition.onDeathEmitterName;
		if (lParticleEmitterName == null || lParticleEmitterName.length() == 0)
			return;

		mOnDeathEmitter = frameworkData.particleEmitterManager().createNewParticleEmitterFromDefinitionName(lParticleEmitterName);

	}

	public void resyncWithDefinition(ParticleFrameworkData particleFramework) {
		if (mIsAssigned == false || mParticleSystemDefinition == null)
			return;

		// force reassign of renderer (and a reload of the texture)
		mRendererId = NO_RENDERER_ASSIGNED;

		final var lDesiredNumParticles = mParticleSystemDefinition.maxParticleCount;
		if (mParticles == null) {
			for (int i = 0; i < lDesiredNumParticles; i++) {
				mParticles.add(new Particle());
			}
		} else if (mParticles.size() < lDesiredNumParticles) {
			// increase particle count
			final var lCurCount = mParticles.size();
			for (int i = lCurCount; i < lDesiredNumParticles; i++) {
				mParticles.add(new Particle());
			}
		} else {
			// reduce particle count
			final var lCurCount = mParticles.size();
			final var lAmtToRemove = lCurCount - lDesiredNumParticles;
			for (int i = 0; i < lAmtToRemove; i++) {
				mParticles.removeFirst();
			}
		}

		// TODO: resync onDeathEmitter name
		final var lOnDeathEmitterName = mParticleSystemDefinition.onDeathEmitterName;
		if (lOnDeathEmitterName != null) {
			final var lEmitterDefinition = particleFramework.particleEmitterManager().definitionManager().getByName(lOnDeathEmitterName);
			if (lEmitterDefinition != null) {
				particleFramework.particleEmitterManager().getOrCreateNewParticleEmitterInstanceByDefiniton(lEmitterDefinition);
			} else {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot resolve onDeathEmitter from name : " + lOnDeathEmitterName);
			}
		} else {
			mOnDeathEmitter = null;
		}

		mCapacity = lDesiredNumParticles;
	}

	public void unload() {
		mIsAssigned = false;
		mParticleSystemDefinition = null;
		mRendererId = NO_RENDERER_ASSIGNED;
		mParticles.clear();
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

					if (mOnDeathEmitter != null) {
						mOnDeathEmitter.aabb.x(particle.worldPositionX);
						mOnDeathEmitter.aabb.y(particle.worldPositionY);
						mOnDeathEmitter.zDepth = particle.zDepth;

						mOnDeathEmitter.triggerSpawn(core);
					}

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

	public void assignedRendererId(int rendererId) {
		mRendererId = rendererId;
	}

	/**
	 * Spawns a new {@link Particle} instance, foregoing the {@link IParticleinitializer}s attached to this {@link ParticleSystemInstance}. Insteadm you can specifiy the individual components of the particles.
	 */
	public Particle spawnParticle(float worldX, float worldY, float zDepth, float velocityX, float velocityY, float sourceX, float sourceY, float sourceW, float sourceH, float destWidth, float destHeight) {
		if (!mIsAssigned)
			return null;

		final var lNewParticle = spawnParticle(worldX, worldY, zDepth, velocityX, velocityY);
		if (lNewParticle != null) {
			lNewParticle.setupSourceTexture(sourceX, sourceY, sourceW, sourceH);
			lNewParticle.setupDestTexture(destWidth, destHeight);

			return lNewParticle;
		}

		return null;
	}

	/** Spawns a new {@link Particle} and applys the {@link IParticleinitializer} attached to this {@link ParticleSystemInstance}. */
	public Particle spawnParticle(float worldX, float worldY, float zDepth, float velocityX, float velocityY) {
		if (!mIsAssigned)
			return null;

		for (int i = 0; i < mCapacity; i++) {
			final var lSpawnedParticle = mParticles.get(i);
			if (lSpawnedParticle.isAssigned())
				continue;

			float particleLifeTime = mParticleSystemDefinition.particleLifeMin;
			if (mParticleSystemDefinition.particleLifeMax != 0 && mParticleSystemDefinition.particleLifeMax > mParticleSystemDefinition.particleLifeMin)
				particleLifeTime = RandomNumbers.random(mParticleSystemDefinition.particleLifeMin, mParticleSystemDefinition.particleLifeMax);

			lSpawnedParticle.spawnParticle(worldX, worldY, zDepth, velocityX, velocityY, particleLifeTime);

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

		final var lModifiers = mParticleSystemDefinition.modifiers();
		final int lNumModifiers = lModifiers.size();
		for (int j = 0; j < lNumModifiers; j++) {
			lModifiers.get(j).initialize(particle);
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
		mIsAssigned = false;
		mRendererId = -1;
		mOnDeathEmitter = null;
		mParticleSystemDefinition = null;

		for (int i = 0; i < mCapacity; i++) {
			mParticles.get(i).reset();
		}
	}
}

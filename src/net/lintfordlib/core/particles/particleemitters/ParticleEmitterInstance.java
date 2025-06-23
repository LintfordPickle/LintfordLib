package net.lintfordlib.core.particles.particleemitters;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.geometry.partitioning.GridEntity;
import net.lintfordlib.core.geometry.partitioning.SpatialHashGrid;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterInstance extends GridEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 9168072331704352472L;

	public static final int EMITTER_NOT_ASSIGNED_ID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient ParticleEmitterDefinition mEmitterDefinition;

	private transient ParticleEmitterInstance mParentEmitterInstance;
	private transient List<ParticleEmitterInstance> mChildEmitterInstances;

	private int mParticleSystemId;
	private int mEmitterInstanceId;
	private int mEmitterDefinitionId;

	private float mEmitTimer;

	private boolean mTriggered;
	private float mTriggerCooldownTimer;
	private float mEmissionLengthMs;

	private boolean enabled;

	public transient ParticleSystemInstance particleSystemInstance;
	private float mEmitterEmitTimerModifier; // [0,1]

	// Global state settings
	public final Rectangle aabb = new Rectangle(0, 0, 1, 1);
	public float globalForceX;
	public float globalForceY;
	public float globalRotRads;
	public float zDepth;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isTriggedEmission() {
		return mEmitterDefinition.triggerType == ParticleEmitterTrigger.PARTICLE_EMITTER_TRIGGER_TYPE_TRIGGED;
	}

	public void triggerEmission() {
		if (!isCoolDowned())
			return;

		mEmissionLengthMs = mEmitterDefinition.triggeredEmissionLengthMs;

		mTriggered = true;
	}

	public boolean isCoolDowned() {
		final var lHasCooldown = mEmitterDefinition.triggerCooldown > 0;
		if (!lHasCooldown)
			return true;

		return mTriggerCooldownTimer <= 0;
	}

	public boolean isInitialized() {
		return mEmitterDefinition != null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean isEnabled) {
		if (enabled == isEnabled)
			return;

		enabled = isEnabled;

		// enable child emitters
		final int childEmitterCount = mChildEmitterInstances.size();
		for (int i = 0; i < childEmitterCount; i++) {
			if (mChildEmitterInstances.get(i) == null)
				continue;

			mChildEmitterInstances.get(i).enabled = isEnabled;
		}
	}

	public List<ParticleEmitterInstance> childEmitters() {
		return mChildEmitterInstances;
	}

	public ParticleEmitterInstance parentEmitterInst() {
		return mParentEmitterInstance;
	}

	public void parentEmitterInst(ParticleEmitterInstance parentEmitterIntsance) {
		mParentEmitterInstance = parentEmitterIntsance;
	}

	public void emitterInstanceId(final int emitterUid) {
		mEmitterInstanceId = emitterUid;
	}

	public int emitterInstanceId() {
		return mEmitterInstanceId;
	}

	public boolean isAssigned() {
		return mEmitterDefinition != null;
	}

	public int particleSystemId() {
		return mParticleSystemId;
	}

	public int particleEmitterDefId() {
		return mEmitterDefinitionId;
	}

	public ParticleEmitterDefinition emitterDefinition() {
		return mEmitterDefinition;
	}

	public float emitterEmitModifierNormalized() {
		return mEmitterEmitTimerModifier;
	}

	public void emitterEmitModifierNormalized(float newModifer) {
		mEmitterEmitTimerModifier = MathHelper.clamp(newModifer, 0.f, 1.f);

		final int lNumInnerInstances = mChildEmitterInstances.size();
		for (int i = 0; i < lNumInnerInstances; i++) {
			final var lChildParticleEmitterInstanceInst = mChildEmitterInstances.get(i);
			if (lChildParticleEmitterInstanceInst != null) {
				lChildParticleEmitterInstanceInst.emitterEmitModifierNormalized(mEmitterEmitTimerModifier);
			}
		}
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterInstance() {
		this(0);
	}

	public ParticleEmitterInstance(int entityUid) {
		super(entityUid, -1);

		mChildEmitterInstances = new ArrayList<>();
		enabled = true;
		mEmitterInstanceId = entityUid;
		mEmitterEmitTimerModifier = 1.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void unload() {
		reset();
	}

	public void update(LintfordCore core) {
		if (!enabled)
			return;

		if (mEmitterDefinition == null)
			return;

		aabb.x(aabb.x() + mEmitterDefinition.positionRelOffsetX);
		aabb.y(aabb.y() + mEmitterDefinition.positionRelOffsetY);

		switch (mEmitterDefinition.triggerType) {
		case ParticleEmitterTrigger.PARTICLE_EMITTER_TRIGGER_TYPE_TIMER:
			if (mEmitTimer > 0)
				mEmitTimer -= core.gameTime().elapsedTimeMilli() * mEmitterEmitTimerModifier;

			updateTimedEmitter(core);

			updateChildEmitters(core);

			break;
		case ParticleEmitterTrigger.PARTICLE_EMITTER_TRIGGER_TYPE_TRIGGED:
			if (mTriggerCooldownTimer > 0)
				mTriggerCooldownTimer -= core.gameTime().elapsedTimeMilli();

			updateTriggerEmitter(core);

			break;
		}

		// TODO: called update on the particle emitter shape, which can manage variables it needs and store them on the ParticleEmitterInstance (this)
		// For example about the implementation, this could involve the ParticleEmitterInstance having a key/value collection, which the individual
		// shapes can use to persists values over time - lifetime, position, target position ...

		if (mEmitterDefinition.ParticleEmitterShape != null) {
			mEmitterDefinition.ParticleEmitterShape.update(core, this);
		}

	}

	private void updateChildEmitters(LintfordCore core) {
		final int lNumInnerInstances = mChildEmitterInstances.size();
		for (int i = 0; i < lNumInnerInstances; i++) {
			final var lChildParticleEmitterInstanceInst = mChildEmitterInstances.get(i);

			if (lChildParticleEmitterInstanceInst == null)
				continue;

			lChildParticleEmitterInstanceInst.aabb.x(aabb.x());
			lChildParticleEmitterInstanceInst.aabb.y(aabb.y());
			lChildParticleEmitterInstanceInst.zDepth = zDepth;
			lChildParticleEmitterInstanceInst.globalForceX = globalForceX;
			lChildParticleEmitterInstanceInst.globalForceY = globalForceY;
			lChildParticleEmitterInstanceInst.globalRotRads = globalRotRads;

			// All emitters, regardless of their place in the hierarchy, are updated from the ParticleFrameworkController
			lChildParticleEmitterInstanceInst.update(core);

		}
	}

	private void updateTimedEmitter(LintfordCore core) {
		if (particleSystemInstance == null)
			return;

		if (mEmitTimer <= 0) {
			final var emitAmtMin = mEmitterDefinition.emitAmountMin;
			final var emitAmtMax = Math.max(emitAmtMin, mEmitterDefinition.emitAmountMax);

			if (emitAmtMin >= emitAmtMax)
				return;

			final int lAmtToSpawn = RandomNumbers.random(emitAmtMin, emitAmtMax);
			for (int i = 0; i < lAmtToSpawn; i++) {

				final float lHeadingRads = globalRotRads; // + objRotRads

				final var lObjForceMin = mEmitterDefinition.emitForceMin;
				final var lObjForceMax = Math.max(lObjForceMin + 1, mEmitterDefinition.emitForceMax);
				final var lObjForceX = RandomNumbers.random(lObjForceMin, lObjForceMax);
				final var lObjForceY = RandomNumbers.random(lObjForceMin, lObjForceMax);

				// global force is the force passed down from a parent emitter to a nested child. It can be 0 if not set, so as a coefficient, wie should take 1.
				final var fx = globalForceX == 0 ? 1.f : globalForceX;
				final var fy = globalForceY == 0 ? 1.f : globalForceY;

				// The position and velocity is handled by the emitter shape
				if (mEmitterDefinition.useSharedParticleSystem) {
					if (mEmitterDefinition.ParticleEmitterShape == null) {
						mEmitterDefinition.sharedParticleSystemInstance.spawnParticle(aabb.x(), aabb.y(), zDepth, 0, 0);
					} else {
						mEmitterDefinition.ParticleEmitterShape.spawn(mEmitterDefinition.sharedParticleSystemInstance, aabb.x(), aabb.y(), zDepth, lHeadingRads, lObjForceX * fx, lObjForceY * fy);
					}
				} else {
					if (mEmitterDefinition.ParticleEmitterShape == null) {

						final var lVelX = (float) Math.cos(lHeadingRads) * lObjForceX * fx;
						final var lVelY = (float) Math.sin(lHeadingRads) * lObjForceY * fy;

						particleSystemInstance.spawnParticle(aabb.x(), aabb.y(), zDepth, lVelX, lVelY);
					} else {

						// In the case that the global force* is not set, just take the object force and let the emitter shape work out the new velocities based on the heading etc.
						// *the global force is the force passed down from the parent emitter.

						mEmitterDefinition.ParticleEmitterShape.spawn(particleSystemInstance, aabb.x(), aabb.y(), zDepth, lHeadingRads, lObjForceX * fx, lObjForceY * fy);
					}
				}
			}

			mEmitTimer = RandomNumbers.random(mEmitterDefinition.emitTimeMin, mEmitterDefinition.emitTimeMax);
		}
	}

	private void updateTriggerEmitter(LintfordCore core) {
		if (!mEmitterDefinition.isHead() && particleSystemInstance == null)
			return;

		if (!mTriggered && mEmitterDefinition.isHead())
			return;

		mTriggerCooldownTimer = mEmitterDefinition.triggerCooldown;

		mEmissionLengthMs -= core.gameTime().elapsedTimeMilli();
		if (mEmissionLengthMs <= 0)
			mTriggered = false;

		final var emitAmtMin = mEmitterDefinition.emitAmountMin;
		final var emitAmtMax = Math.max(emitAmtMin + 1, mEmitterDefinition.emitAmountMax);

		final int lAmtToSpawn = RandomNumbers.random(emitAmtMin, emitAmtMax);
		for (int i = 0; i < lAmtToSpawn; i++) {

			final float lHeadingRads = globalRotRads; // + objRotRads

			final var lObjForceMin = mEmitterDefinition.emitForceMin;
			final var lObjForceMax = Math.max(lObjForceMin + 1, mEmitterDefinition.emitForceMax);
			final var lObjForceX = RandomNumbers.random(lObjForceMin, lObjForceMax);
			final var lObjForceY = RandomNumbers.random(lObjForceMin, lObjForceMax);

			final var fx = globalForceX == 0 ? 1.f : globalForceX;
			final var fy = globalForceY == 0 ? 1.f : globalForceY;

			// The position and velocity is handled by the emitter shape
			if (mEmitterDefinition.useSharedParticleSystem) {
				if (mEmitterDefinition.ParticleEmitterShape == null) {
					mEmitterDefinition.sharedParticleSystemInstance.spawnParticle(aabb.x(), aabb.y(), zDepth, 0, 0);
				} else {
					mEmitterDefinition.ParticleEmitterShape.spawn(mEmitterDefinition.sharedParticleSystemInstance, aabb.x(), aabb.y(), zDepth, lHeadingRads, lObjForceX * fx, lObjForceY * fy);
				}
			} else {
				if (mEmitterDefinition.ParticleEmitterShape == null) {

					final var lVelX = (float) Math.cos(lHeadingRads) * lObjForceX * fx;
					final var lVelY = (float) Math.sin(lHeadingRads) * lObjForceY * fy;

					particleSystemInstance.spawnParticle(aabb.x(), aabb.y(), zDepth, lVelX, lVelY);
				} else {
					mEmitterDefinition.ParticleEmitterShape.spawn(particleSystemInstance, aabb.x(), aabb.y(), zDepth, lHeadingRads, lObjForceX * fx, lObjForceY * fy);
				}
			}
		}

		updateChildEmitters(core);

	}

	public void triggerSpawn(LintfordCore core) {
		updateTriggerEmitter(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignEmitterDefinition(short definitionUid, ParticleFrameworkData particleFramework) {
		final var lEmitterDefintion = particleFramework.particleEmitterManager().definitionManager().getByUid(definitionUid);
		if (lEmitterDefintion == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - EmitterDefId '%d' has no definition defined!", definitionUid));
			return;
		}

		assignEmitterDefinition(lEmitterDefintion, particleFramework);
	}

	public void assignEmitterDefinition(ParticleEmitterDefinition emitterDefinition, ParticleFrameworkData particleFramework) {
		if (emitterDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to assign ParticleEmitter - given EmitterDefinition is null!");
			return;
		}

		mEmitterDefinitionId = emitterDefinition.definitionUid();
		mEmitterDefinition = emitterDefinition;

		resolveParticleSystem(emitterDefinition, particleFramework);

		resolveChildParticleEmitters(particleFramework);
	}

	private void resolveParticleSystem(ParticleEmitterDefinition emitterDefinition, ParticleFrameworkData particleFramework) {
		if (emitterDefinition.particleSystemName != null && emitterDefinition.particleSystemName.length() > 0) {
			if (emitterDefinition.useSharedParticleSystem) {
				particleSystemInstance = emitterDefinition.sharedParticleSystemInstance;

				if (particleSystemInstance == null) {
					particleSystemInstance = particleFramework.particleSystemManager().getParticleSystemByName(emitterDefinition.particleSystemName, true);
				} else if (!particleSystemInstance.isAssigned()) {

					// note: just because this is shared particle system (which may have even already been used) it can still have been unassigned,
					// especially in the editor where the scenes are cleared regularly. The PS don't necessarily know if they are attached to a
					// shared emitter, and so there is no 'protection' to prevent them being prematurely unassigned.

					particleFramework.particleSystemManager().assignSystemDefinitionAndResolveEmitters(particleSystemInstance, emitterDefinition.particleSystemName);
				}

			} else {
				particleSystemInstance = particleFramework.particleSystemManager().getParticleSystemByName(emitterDefinition.particleSystemName, false);
			}
		}
	}

	private void resolveChildParticleEmitters(ParticleFrameworkData particleFramework) {
		if (mEmitterDefinition == null)
			return;

		final var lParticleEmitterManager = particleFramework.particleEmitterManager();

		// clear and repool current emitter instances
		final var lNumChildEmitterInsts = mChildEmitterInstances.size();
		for (int i = 0; i < lNumChildEmitterInsts; i++) {
			final var lChildEmitterInst = mChildEmitterInstances.get(i);
			if (lChildEmitterInst == null)
				continue;

			lChildEmitterInst.reset();
			lParticleEmitterManager.returnInstance(lChildEmitterInst);
		}

		mChildEmitterInstances.clear();

		if (mEmitterDefinition.childEmitters() != null) {
			final var lDefinitionChildEmitters = mEmitterDefinition.childEmitters();
			final int lNumChildEmitters = lDefinitionChildEmitters.size();
			for (int i = 0; i < lNumChildEmitters; i++) {
				final var lChildEmitterDefinition = lDefinitionChildEmitters.get(i);
				if (lChildEmitterDefinition == null)
					continue;

				final var lNewChildEmitterInst = lParticleEmitterManager.createNewInstance();
				lNewChildEmitterInst.assignEmitterDefinition(lChildEmitterDefinition, particleFramework);
				mChildEmitterInstances.add(lNewChildEmitterInst);
			}
		}
	}

	public void reset() {
		mEmitterInstanceId = -1;
		mEmitterDefinitionId = -1;
		mParticleSystemId = -1;
		aabb.set(0, 0, 0, 0);
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void fillEntityBounds(SpatialHashGrid<?> grid) {
		minX = grid.getCellIndexX((int) aabb.left());
		minY = grid.getCellIndexY((int) aabb.top());

		maxX = grid.getCellIndexX((int) aabb.right());
		maxY = grid.getCellIndexY((int) aabb.bottom());
	}

	@Override
	public boolean isGridCacheOld(SpatialHashGrid<?> grid) {
		final float newMinX = grid.getCellIndexX((int) aabb.left());
		if (newMinX != minX)
			return true;

		final float newMinY = grid.getCellIndexY((int) aabb.top());
		if (newMinY != minY)
			return true;

		final float newMaxX = grid.getCellIndexX((int) aabb.right());
		if (newMaxX != maxX)
			return true;

		final float newMaxY = grid.getCellIndexY((int) aabb.bottom());
		if (newMaxY != maxY)
			return true;

		return false;
	}
}

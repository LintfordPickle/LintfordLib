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

	public static final int EMITTER_NOT_ASSIGNED_ID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient ParticleEmitterDefinition mEmitterDefinition;
	public final Rectangle aabb = new Rectangle(0, 0, 1, 1);
	private transient ParticleEmitterInstance mParentEmitterInstance;
	private transient List<ParticleEmitterInstance> mChildEmitterInstances;

	private int mParticleSystemId;
	private int mEmitterInstanceId;
	private int mEmitterDefinitionId;
	private float mEmitTimer;
	private boolean enabled;

	public transient ParticleSystemInstance particleSystemInstance;
	private float mEmitterEmitTimerModifier; // [0,1]

	public float rot;

	// --------------------------------------
	// Properties
	// --------------------------------------

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
		super(entityUid, -1); // TODO: Filter in hashgrid for Particle emitters

		mChildEmitterInstances = new ArrayList<>();
		enabled = true;
		mEmitterInstanceId = entityUid;
		mEmitterEmitTimerModifier = 1.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void resyncWithDefinition(ParticleFrameworkData particleFramework) {
		if (isAssigned() == false)
			return;

	}

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

		mEmitTimer -= core.gameTime().elapsedTimeMilli() * mEmitterEmitTimerModifier;

		switch (mEmitterDefinition.triggerType) {
		case ParticleEmitterTrigger.PARTICLE_EMITTER_TRIGGER_TYPE_TIMER:
			updateTimedEmitter(core);
			break;
		case ParticleEmitterTrigger.PARTICLE_EMITTER_TRIGGER_TYPE_TRIGGED:
			updateTriggerEmitter(core);
			break;
		}

		final int lNumInnerInstances = mChildEmitterInstances.size();
		for (int i = 0; i < lNumInnerInstances; i++) {
			final var lChildParticleEmitterInstanceInst = mChildEmitterInstances.get(i);

			if (lChildParticleEmitterInstanceInst == null)
				continue;

			lChildParticleEmitterInstanceInst.aabb.x(aabb.x());
			lChildParticleEmitterInstanceInst.aabb.y(aabb.y());
			lChildParticleEmitterInstanceInst.rot = rot;

			// All emitters, regardless of their place in the hierarchy, are updated from the ParticleFrameworkController
			lChildParticleEmitterInstanceInst.update(core);

		}
	}

	private void updateTimedEmitter(LintfordCore core) {
		if (particleSystemInstance == null)
			return;

		if (mEmitTimer < 0) {
			final var emitAmtMin = mEmitterDefinition.emitAmountMin;
			final var emitAmtMax = Math.max(emitAmtMin + 1, mEmitterDefinition.emitAmountMax);

			final int lAmtToSpawn = RandomNumbers.random(emitAmtMin, emitAmtMax);
			for (int i = 0; i < lAmtToSpawn; i++) {

				final float lHeading = 0.f;

				final var emitForceMin = mEmitterDefinition.emitForceMin;
				final var emitForceMax = Math.max(emitForceMin + 1, mEmitterDefinition.emitForceMax);
				final var lForce = RandomNumbers.random(emitForceMin, emitForceMax);

				final var lVelX = (float) Math.cos(lHeading) * lForce;
				final var lVelY = (float) Math.sin(lHeading) * lForce;

				// The position and velocity is handled by the emitter shape
				if (mEmitterDefinition.useSharedParticleSystem) {
					if (mEmitterDefinition.ParticleEmitterShape == null) {
						mEmitterDefinition.sharedParticleSystemInstance.spawnParticle(aabb.x(), aabb.y(), -.2f, 0, 0);
					} else {
						mEmitterDefinition.ParticleEmitterShape.spawn(mEmitterDefinition.sharedParticleSystemInstance, aabb.x(), aabb.y(), lHeading, lForce);
					}
				} else {
					if (mEmitterDefinition.ParticleEmitterShape == null) {
						particleSystemInstance.spawnParticle(aabb.x(), aabb.y(), -.2f, lVelX, lVelY);
					} else {
						mEmitterDefinition.ParticleEmitterShape.spawn(particleSystemInstance, aabb.x(), aabb.y(), lHeading, lForce);
					}
				}
			}

			mEmitTimer = RandomNumbers.random(mEmitterDefinition.emitTimeMin, mEmitterDefinition.emitTimeMax);
		}
	}

	private void updateTriggerEmitter(LintfordCore core) {
		if (particleSystemInstance == null)
			return;

		final var emitAmtMin = mEmitterDefinition.emitAmountMin;
		final var emitAmtMax = Math.max(emitAmtMin + 1, mEmitterDefinition.emitAmountMax);

		final int lAmtToSpawn = RandomNumbers.random(emitAmtMin, emitAmtMax);
		for (int i = 0; i < lAmtToSpawn; i++) {

			final float lHeading = 0.f;

			final var emitForceMin = mEmitterDefinition.emitForceMin;
			final var emitForceMax = Math.max(emitForceMin + 1, mEmitterDefinition.emitForceMax);
			final var lForce = RandomNumbers.random(emitForceMin, emitForceMax);

			final var lVelX = (float) Math.cos(lHeading) * lForce;
			final var lVelY = (float) Math.sin(lHeading) * lForce;

			// The position and velocity is handled by the emitter shape
			if (mEmitterDefinition.useSharedParticleSystem) {
				if (mEmitterDefinition.ParticleEmitterShape == null) {
					mEmitterDefinition.sharedParticleSystemInstance.spawnParticle(aabb.x(), aabb.y(), -.2f, 0, 0);
				} else {
					mEmitterDefinition.ParticleEmitterShape.spawn(mEmitterDefinition.sharedParticleSystemInstance, aabb.x(), aabb.y(), lHeading, lForce);
				}
			} else {
				if (mEmitterDefinition.ParticleEmitterShape == null) {
					particleSystemInstance.spawnParticle(aabb.x(), aabb.y(), -.2f, lVelX, lVelY);
				} else {
					mEmitterDefinition.ParticleEmitterShape.spawn(particleSystemInstance, aabb.x(), aabb.y(), lHeading, lForce);
				}
			}
		}
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
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - given EmitterDefinition is null!"));
			return;
		}

		mEmitterDefinitionId = emitterDefinition.definitionUid();
		mEmitterDefinition = emitterDefinition;

		resolveParticleSystem(emitterDefinition, particleFramework);

		resolveChildParticleEmitters(particleFramework);
	}

	private void resolveParticleSystem(ParticleEmitterDefinition emitterDefinition, ParticleFrameworkData particleFramework) {
		if (emitterDefinition.particleSystemName != null) {
			if (emitterDefinition.useSharedParticleSystem) {
				particleSystemInstance = emitterDefinition.sharedParticleSystemInstance;
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
		final float newMinY = grid.getCellIndexY((int) aabb.top());

		final float newMaxX = grid.getCellIndexX((int) aabb.right());
		final float newMaxY = grid.getCellIndexY((int) aabb.bottom());

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false; // early out

		return true;
	}

}

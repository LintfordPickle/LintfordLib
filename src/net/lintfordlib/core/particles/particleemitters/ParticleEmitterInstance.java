package net.lintfordlib.core.particles.particleemitters;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.Entity;
import net.lintfordlib.core.entities.instances.ClosedPooledBaseData;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterInstance extends ClosedPooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int MAX_NUM_CHILD_EMITTERS = 4;
	public static final int EMITTER_NOT_ASSIGNED_ID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient Entity mAttachedToEntity;
	private transient ParticleEmitterInstance[] mChildEmitters;
	private transient ParticleSystemInstance mParticleSystem;

	public float x;
	public float y;

	private int mParticleSystemId;
	protected int mEmitterInstanceId;
	protected int mEmitterDefinitionId;
	protected transient ParticleEmitterDefinition mEmitterDefinition;
	public float mEmitTimer;
	public float mEmitAmount;
	private boolean enabled;

	private float mEmitterEmitModifier; // [0,1]

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean isEnabled) {
		if (enabled == isEnabled)
			return;

		enabled = isEnabled;

		final int childEmitterCount = mChildEmitters.length;
		for (int i = 0; i < childEmitterCount; i++) {
			if (mChildEmitters[i] == null)
				continue;

			mChildEmitters[i].enabled = isEnabled;
		}
	}

	public ParticleSystemInstance particleSystem() {
		return mParticleSystem;
	}

	public Entity parentEntity() {
		return mAttachedToEntity;
	}

	public void parentEntity(Entity newValue) {
		mAttachedToEntity = newValue;
	}

	public void emitterInstanceId(final int emitterUid) {
		mEmitterInstanceId = emitterUid;
	}

	public int emitterInstanceId() {
		return mEmitterInstanceId;
	}

	public boolean isAssigned() {
		return mParticleSystem != null || (mChildEmitters != null && mChildEmitters.length > 0);
	}

	public int particleSystemId() {
		return mParticleSystemId;
	}

	public int particleEmitterDefId() {
		return mEmitterDefinitionId;
	}

	public float emitterEmitModifierNormalized() {
		return mEmitterEmitModifier;
	}

	public void emitterEmitModifierNormalized(float newModifer) {
		mEmitterEmitModifier = MathHelper.clamp(newModifer, 0.f, 1.f);

		final int lNumInnerInstances = mChildEmitters.length;
		for (int i = 0; i < lNumInnerInstances; i++) {
			final var lChildParticleEmitterInstanceInst = mChildEmitters[i];
			if (lChildParticleEmitterInstanceInst != null) {
				lChildParticleEmitterInstanceInst.emitterEmitModifierNormalized(mEmitterEmitModifier);
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
		super(entityUid);

		mChildEmitters = new ParticleEmitterInstance[MAX_NUM_CHILD_EMITTERS];
		enabled = true;
		mEmitterInstanceId = entityUid;
		mEmitterEmitModifier = 1.f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialise() {

	}

	public void update(LintfordCore core) {
		if (!enabled)
			return;

		if (mEmitterDefinition == null)
			return;

		x += mEmitterDefinition.positionRelOffsetX();
		y += mEmitterDefinition.positionRelOffsetY();

		mEmitTimer -= core.gameTime().elapsedTimeMilli() * mEmitterEmitModifier;

		if (mParticleSystem != null && mEmitTimer < 0) {
			final int lAmtToSpawn = RandomNumbers.random(mEmitterDefinition.emitAmountMin(), mEmitterDefinition.emitAmountMax());
			for (int i = 0; i < lAmtToSpawn; i++) {
				mParticleSystem.spawnParticle(x, y, 0, 0);
			}

			mEmitTimer = RandomNumbers.random(mEmitterDefinition.emitTimeMin(), mEmitterDefinition.emitTimeMax());
		}

		final int lNumInnerInstances = mChildEmitters.length;
		for (int i = 0; i < lNumInnerInstances; i++) {
			final var lChildParticleEmitterInstanceInst = mChildEmitters[i];

			if (lChildParticleEmitterInstanceInst == null)
				continue;

			lChildParticleEmitterInstanceInst.x = x;
			lChildParticleEmitterInstanceInst.y = y;

			lChildParticleEmitterInstanceInst.update(core);
		}
	}

	public void assignEmitterDefinitionAndResolveParticleSystem(final short definitionUid, ParticleFrameworkData particleFramework) {
		final var lEmitterDefintion = particleFramework.emitterManager().definitionManager().getByUid(definitionUid);
		if (lEmitterDefintion == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - EmitterDefId '%d' has no definition defined!", definitionUid));
			return;
		}

		assignEmitterDefinitionAndResolveParticleSystem(lEmitterDefintion, particleFramework);
	}

	public void assignEmitterDefinitionAndResolveParticleSystem(final ParticleEmitterDefinition emitterDefinition, ParticleFrameworkData particleFramework) {
		if (emitterDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - given EmitterDefinition is null!"));
			return;
		}

		mEmitterDefinitionId = emitterDefinition.definitionUid();
		mEmitterDefinition = emitterDefinition;

		resolveParticleSystems(mEmitterDefinition, particleFramework);
	}

	private void resolveParticleSystems(final ParticleEmitterDefinition emitterDefinition, ParticleFrameworkData particleFramework) {
		mParticleSystem = particleFramework.particleSystemManager().getParticleSystemByName(emitterDefinition.particleSystemName());

		if (mParticleSystem != null) {
			mParticleSystemId = mParticleSystem.getPoolID();
		}

		if (emitterDefinition.childEmitters() != null) {
			final var lDefinitionChildEmitters = emitterDefinition.childEmitters();
			final int lNumChildEmitters = Math.min(MAX_NUM_CHILD_EMITTERS, lDefinitionChildEmitters.length);
			for (int i = 0; i < lNumChildEmitters; i++) {
				final var lChildEmitterDefinition = lDefinitionChildEmitters[i];
				if (lChildEmitterDefinition == null)
					continue;

				final var lEmitterManager = particleFramework.emitterManager();
				final var lEmitterInstance = lEmitterManager.getFreePooledItem();
				mChildEmitters[i] = lEmitterInstance;
				mChildEmitters[i].assignEmitterDefinitionAndResolveParticleSystem(lChildEmitterDefinition, particleFramework);
			}
		}
	}

	public void reset() {
		mEmitterInstanceId = -1;
		mEmitterDefinitionId = -1;
		mParticleSystemId = -1;
		mParticleSystem = null;

		final int lNumChildEmitters = Math.min(MAX_NUM_CHILD_EMITTERS, mChildEmitters.length);
		for (int i = 0; i < lNumChildEmitters; i++) {
			final var lChildInst = mChildEmitters[i];

			if (lChildInst == null)
				continue;

			// FIXME: Check that reset emitters are picked up (pooled) again by the emitter manager
			lChildInst.reset();

			mChildEmitters[i] = null;
		}
	}
}
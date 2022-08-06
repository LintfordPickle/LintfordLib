package net.lintford.library.core.particles.particleemitters;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.WorldEntity;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.particles.ParticleFrameworkData;
import net.lintford.library.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterInstance extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2817782552539182940L;

	private static final int MAX_NUM_CHILD_EMITTERS = 4;
	public static final int EMITTER_NOT_ASSIGNED_ID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient WorldEntity mAttachedToEntity;
	private transient ParticleEmitterInstance[] mChildEmitters;
	private transient ParticleSystemInstance mParticleSystem;

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

	public void setEnabled(boolean pIsEnabled) {
		if (enabled == pIsEnabled)
			return;

		enabled = pIsEnabled;

		// This is a cop-out: the actual problem is that the child emitters are processed independantly of the parent emitter
		final int childEmitterCount = mChildEmitters.length;
		for (int i = 0; i < childEmitterCount; i++) {
			if (mChildEmitters[i] == null)
				continue;

			mChildEmitters[i].enabled = pIsEnabled;
		}

	}

	public ParticleSystemInstance particleSystem() {
		return mParticleSystem;
	}

	public WorldEntity parentEntity() {
		return mAttachedToEntity;
	}

	public void parentEntity(WorldEntity pNewValue) {
		mAttachedToEntity = pNewValue;
	}

	public void emitterInstanceId(final int pEmitterId) {
		mEmitterInstanceId = pEmitterId;

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

	public void emitterEmitModifierNormalized(float pNewModifer) {
		mEmitterEmitModifier = MathHelper.clamp(pNewModifer, 0.f, 1.f);

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

	public ParticleEmitterInstance(int pEmitterInstanceId) {
		super();

		mChildEmitters = new ParticleEmitterInstance[MAX_NUM_CHILD_EMITTERS];
		enabled = true;
		mEmitterInstanceId = pEmitterInstanceId;
		mEmitterEmitModifier = 1.f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void initialise() {
		// FIXME: Re-resolve object instances and references to other objects

	}

	public void update(LintfordCore pCore) {
		if (!enabled)
			return;

		// By this stage, the emitter should have just been updated with the absolute position of the parent entity to which it is attached.
		if (mEmitterDefinition == null)
			return;

		worldPositionX += mEmitterDefinition.PositionRelOffsetX;
		worldPositionY += mEmitterDefinition.PositionRelOffsetY;

		// Update this emitter
		mEmitTimer -= pCore.gameTime().elapsedTimeMilli() * mEmitterEmitModifier;

		if (mParticleSystem != null && mEmitTimer < 0) {
			final int lAmtToSpawn = RandomNumbers.random(mEmitterDefinition.emitAmountMin, mEmitterDefinition.emitAmountMax);
			for (int i = 0; i < lAmtToSpawn; i++) {
				mParticleSystem.spawnParticle(worldPositionX, worldPositionY, 0, 0);

			}

			// Set the time to wait until another round of spawns occurs
			mEmitTimer = RandomNumbers.random(mEmitterDefinition.emitTimeMin, mEmitterDefinition.emitTimeMax);

		}

		// Update the child emitters
		final int lNumInnerInstances = mChildEmitters.length;
		for (int i = 0; i < lNumInnerInstances; i++) {
			final var lChildParticleEmitterInstanceInst = mChildEmitters[i];

			if (lChildParticleEmitterInstanceInst == null)
				continue;

			lChildParticleEmitterInstanceInst.worldPositionX = worldPositionX;
			lChildParticleEmitterInstanceInst.worldPositionY = worldPositionY;

			lChildParticleEmitterInstanceInst.update(pCore);

		}

	}

	public void assignEmitterDefinitionAndResolveParticleSystem(final short pDefinitionID, ParticleFrameworkData pParticleFramework) {
		ParticleEmitterDefinition lEmitterDefintion = pParticleFramework.emitterManager().definitionManager().getByUid(pDefinitionID);
		if (lEmitterDefintion == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - EmitterDefId '%d' has no definition defined!", pDefinitionID));
			return;

		}

		assignEmitterDefinitionAndResolveParticleSystem(lEmitterDefintion, pParticleFramework);

	}

	public void assignEmitterDefinitionAndResolveParticleSystem(final ParticleEmitterDefinition pEmitterDefinition, ParticleFrameworkData pParticleFramework) {
		if (pEmitterDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - given EmitterDefinition is null!"));
			return;

		}

		// Problem here for children of the emitter definition (in which they have the same DefId as their parents). This
		// would prevent us from correctly deserializing such objects.
		mEmitterDefinitionId = pEmitterDefinition.definitionUid;
		mEmitterDefinition = pEmitterDefinition;

		resolveParticleSystems(mEmitterDefinition, pParticleFramework);

	}

	private void resolveParticleSystems(final ParticleEmitterDefinition pEmitterDefinition, ParticleFrameworkData pParticleFramework) {
		// Get a reference to the particle system attached to this emitter
		mParticleSystem = pParticleFramework.particleSystemManager().getParticleSystemByName(pEmitterDefinition.particleSystemName);

		if (mParticleSystem != null) {
			mParticleSystemId = mParticleSystem.getPoolID();
		}

		// assign the child emitters
		if (pEmitterDefinition.childEmitters != null) {
			final int lNumChildEmitters = Math.min(MAX_NUM_CHILD_EMITTERS, pEmitterDefinition.childEmitters.length);
			for (int i = 0; i < lNumChildEmitters; i++) {
				ParticleEmitterDefinition lChildEmitterDefinition = pEmitterDefinition.childEmitters[i];
				if (lChildEmitterDefinition == null)
					continue;

				ParticleEmitterManager lMan = pParticleFramework.emitterManager();
				ParticleEmitterInstance lInst = lMan.getFreePooledItem();
				mChildEmitters[i] = lInst;
				mChildEmitters[i].assignEmitterDefinitionAndResolveParticleSystem(lChildEmitterDefinition, pParticleFramework);
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
			ParticleEmitterInstance lChildInst = mChildEmitters[i];

			if (lChildInst == null)
				continue;

			// FIXME: Check that reset emitters are picked up (pooled) again by the emitter manager
			lChildInst.reset();

			mChildEmitters[i] = null;

		}

	}

}

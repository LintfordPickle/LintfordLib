package net.lintford.library.core.particles.particleemitters;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.WorldEntity;
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
	public boolean enabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	@Override
	public boolean isAssigned() {
		return mParticleSystem != null || (mChildEmitters != null && mChildEmitters.length > 0);
	}

	public int particleSystemId() {
		return mParticleSystemId;
	}

	public int particleEmitterDefId() {
		return mEmitterDefinitionId;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterInstance(final int pPoolUid) {
		super(pPoolUid);

		mChildEmitters = new ParticleEmitterInstance[MAX_NUM_CHILD_EMITTERS];
		enabled = true;
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

		x += mEmitterDefinition.PositionRelOffsetX;
		y += mEmitterDefinition.PositionRelOffsetY;

		// Update this emitter
		mEmitTimer -= pCore.time().elapseGameTimeMilli();

		if (mEmitTimer < 0) {
			if (mParticleSystem != null) {
				final int lAmtToSpawn = RandomNumbers.random(mEmitterDefinition.emitAmountMin, mEmitterDefinition.emitAmountMax);
				for (int i = 0; i < lAmtToSpawn; i++) {
					mParticleSystem.spawnParticle(x, y, 0, 0);

				}

				// Set the time to wait until another round of spawns occurs
				mEmitTimer = RandomNumbers.random(mEmitterDefinition.emitTimeMin, mEmitterDefinition.emitTimeMax);

			}

		}

		// Update the child emitters
		final int lNumInnerInstances = mChildEmitters.length;
		for (int i = 0; i < lNumInnerInstances; i++) {
			ParticleEmitterInstance lChildInst = mChildEmitters[i];

			if (lChildInst == null)
				continue;

			lChildInst.x = x;
			lChildInst.y = y;

			lChildInst.update(pCore);

		}

	}

	public void assign(final int pDefinitionID, ParticleFrameworkData pParticleFramework) {
		ParticleEmitterDefinition lEmitterDefintion = pParticleFramework.emitterManager().definitionManager().getDefinitionByID(pDefinitionID);
		if (lEmitterDefintion == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - EmitterDefId '%d' has no definition defined!", pDefinitionID));
			return;

		}

		assign(lEmitterDefintion, pParticleFramework);

	}

	public void assign(final ParticleEmitterDefinition pEmitterDefinition, ParticleFrameworkData pParticleFramework) {
		if (pEmitterDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - given EmitterDefinition is null!"));
			return;

		}

		// Problem here for children of the emitter definition (in which they have the same DefId as their parents). This
		// would prevent us from correctly deserializing such objects.
		mEmitterDefinitionId = pEmitterDefinition.definitionID;

		// assign this
		mEmitterDefinition = pEmitterDefinition;

		{
			// Get a reference to the particle system attached to this emitter
			mParticleSystem = pParticleFramework.particleSystemManager().getParticleSystemByName(pEmitterDefinition.particleSystemName);

			if (mParticleSystem != null) {
				mParticleSystemId = mParticleSystem.getPoolID();

			} else {
				// TODO: Only output a warning if this emitter has no children - ultimately, what is the point?
				Debug.debugManager().logger().i(getClass().getSimpleName(),
						String.format("ParticleEmitter '%s' could not resolve ParticleSystem '' to an instance of an object", pEmitterDefinition.name, pEmitterDefinition.particleSystemName));

			}

		}

		// assign the child emitters
		if (pEmitterDefinition.childEmitters != null) {
			final int lNumChildEmitters = Math.min(MAX_NUM_CHILD_EMITTERS, pEmitterDefinition.childEmitters.length);
			for (int i = 0; i < lNumChildEmitters; i++) {
				ParticleEmitterDefinition lChildEmitterDefinition = pEmitterDefinition.childEmitters[i];
				if (lChildEmitterDefinition == null)
					continue;

				ParticleEmitterManager lMan = pParticleFramework.emitterManager();
				ParticleEmitterInstance lInst = lMan.getParticleEmitterInstance();
				mChildEmitters[i] = lInst;
				mChildEmitters[i].assign(lChildEmitterDefinition, pParticleFramework);

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

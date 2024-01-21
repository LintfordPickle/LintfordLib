package net.lintfordlib.core.particles.particleemitters;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
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

	protected transient ParticleEmitterDefinition mEmitterDefinition;

	private transient ParticleEmitterInstance mParentEmitterInstance;
	private transient ParticleEmitterInstance[] mChildEmitters;
	private transient ParticleSystemInstance mParticleSystem;

	public float x;
	public float y;
	public float rot;

	private int mParticleSystemId;
	protected int mEmitterInstanceId;
	protected int mEmitterDefinitionId;
	public float mEmitTimer;
	public float mEmitAmount;
	private boolean enabled;

	private float mEmitterEmitModifier; // [0,1]

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

		final int childEmitterCount = mChildEmitters.length;
		for (int i = 0; i < childEmitterCount; i++) {
			if (mChildEmitters[i] == null)
				continue;

			mChildEmitters[i].enabled = isEnabled;
		}
	}

	public ParticleEmitterInstance[] childEmitters() {
		return mChildEmitters;
	}

	public ParticleSystemInstance particleSystem() {
		return mParticleSystem;
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
		return mParticleSystem != null || (mChildEmitters != null && mChildEmitters.length > 0);
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
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void resyncWithDefinition(ParticleFrameworkData particleFramework) {
		if (isAssigned() == false)
			return;

		// TODO: Check first
		resolveParticleSystems(particleFramework);
		
	}

	public void unload() {
		mEmitterDefinition = null;
	}

	public void update(LintfordCore core) {
		if (!enabled)
			return;

		if (mEmitterDefinition == null)
			return;

		x += mEmitterDefinition.positionRelOffsetX;
		y += mEmitterDefinition.positionRelOffsetY;

		mEmitTimer -= core.gameTime().elapsedTimeMilli() * mEmitterEmitModifier;

		if (mParticleSystem != null && mEmitTimer < 0) {
			final int lAmtToSpawn = RandomNumbers.random(mEmitterDefinition.emitAmountMin, mEmitterDefinition.emitAmountMax);
			for (int i = 0; i < lAmtToSpawn; i++) {

				// The position and velocity is handled by the emitter shape

				if (mEmitterDefinition.particleEmitterShape == null) {
					mParticleSystem.spawnParticle(x, y, -.2f, 0, 0);
				} else {
					final float lHeading = 0.f;
					final float lForce = 0.f;

					mEmitterDefinition.particleEmitterShape.spawn(mParticleSystem, x, y, lHeading, lForce);
				}
			}

			mEmitTimer = RandomNumbers.random(mEmitterDefinition.emitTimeMin, mEmitterDefinition.emitTimeMax);
		}

		final int lNumInnerInstances = mChildEmitters.length;
		for (int i = 0; i < lNumInnerInstances; i++) {
			final var lChildParticleEmitterInstanceInst = mChildEmitters[i];

			if (lChildParticleEmitterInstanceInst == null)
				continue;

			lChildParticleEmitterInstanceInst.x = x;
			lChildParticleEmitterInstanceInst.y = y;

			// all emitters, regardless of their place in the hierarchy, are updated from the ParticleFrameworkController
			// lChildParticleEmitterInstanceInst.update(core);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignEmitterDefinitionAndResolveParticleSystem(short definitionUid, ParticleFrameworkData particleFramework) {
		final var lEmitterDefintion = particleFramework.particleEmitterManager().definitionManager().getByUid(definitionUid);
		if (lEmitterDefintion == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - EmitterDefId '%d' has no definition defined!", definitionUid));
			return;
		}

		assignEmitterDefinitionAndResolveParticleSystem(lEmitterDefintion, particleFramework);
	}

	public void assignEmitterDefinitionAndResolveParticleSystem(ParticleEmitterDefinition emitterDefinition, ParticleFrameworkData particleFramework) {
		if (emitterDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Failed to assign ParticleEmitter - given EmitterDefinition is null!"));
			return;
		}

		mEmitterDefinitionId = emitterDefinition.definitionUid();
		mEmitterDefinition = emitterDefinition;

		resolveParticleSystems(particleFramework);
	}

	private void resolveParticleSystems(ParticleFrameworkData particleFramework) {
		if (mEmitterDefinition == null)
			return;

		final var lParticleSystemName = mEmitterDefinition.particleSystemName;
		mParticleSystem = particleFramework.particleSystemManager().getParticleSystemByName(lParticleSystemName);

		if (mParticleSystem == null && lParticleSystemName != null) {
			// ignore
		}

		if (mParticleSystem != null)
			mParticleSystemId = mParticleSystem.getPoolID();

		resolveChildParticleEmitters(particleFramework);
	}

	private void resolveChildParticleEmitters(ParticleFrameworkData particleFramework) {
		if (mEmitterDefinition == null)
			return;

		if (mEmitterDefinition.childEmitters() != null) {
			final var lDefinitionChildEmitters = mEmitterDefinition.childEmitters();
			final int lNumChildEmitters = Math.min(MAX_NUM_CHILD_EMITTERS, lDefinitionChildEmitters.length);
			for (int i = 0; i < lNumChildEmitters; i++) {
				final var lChildEmitterDefinition = lDefinitionChildEmitters[i];
				if (lChildEmitterDefinition == null)
					continue;

				final var lEmitterManager = particleFramework.particleEmitterManager();
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
	}
}

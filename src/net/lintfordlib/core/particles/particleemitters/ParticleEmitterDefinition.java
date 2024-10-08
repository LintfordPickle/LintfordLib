package net.lintfordlib.core.particles.particleemitters;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.entities.definitions.BaseDefinition;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particleemitters.shapes.ParticleEmitterShape;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterDefinition extends BaseDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -4696442942082963647L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	// see ParticleEmitterTrigger
	@SerializedName(value = "triggerType")
	public int triggerType;

	@SerializedName(value = "triggeredEmissionLengthMs")
	public float triggeredEmissionLengthMs;

	@SerializedName(value = "emitterShape")
	public ParticleEmitterShape ParticleEmitterShape;

	@SerializedName(value = "emitTimeMin")
	public float emitTimeMin;

	@SerializedName(value = "emitTimeMax")
	public float emitTimeMax;

	@SerializedName(value = "triggerCooldown")
	public float triggerCooldown;

	@SerializedName(value = "emitAmountMin")
	public int emitAmountMin;

	@SerializedName(value = "emitAmountMax")
	public int emitAmountMax;

	@SerializedName(value = "emitForceMin")
	public float emitForceMin;

	@SerializedName(value = "emitForceMax")
	public float emitForceMax;

	@SerializedName(value = "positionRelOffsetX")
	public float positionRelOffsetX;

	@SerializedName(value = "positionRelOffsetY")
	public float positionRelOffsetY;

	@SerializedName(value = "positionRelOffsetRot")
	public float positionRelOffsetRot;

	@SerializedName(value = "particleSystemName")
	public String particleSystemName;

	@SerializedName(value = "useSharedParticleSystem")
	public boolean useSharedParticleSystem;
	public transient ParticleSystemInstance sharedParticleSystemInstance;

	public transient ParticleEmitterDefinition parentEmitter;

	@SerializedName(value = "childEmitters")
	protected List<ParticleEmitterDefinition> mChildEmitters = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInitialized() {
		return particleSystemName == null || sharedParticleSystemInstance != null;
	}

	public List<ParticleEmitterDefinition> childEmitters() {
		return mChildEmitters;
	}

	public boolean isHead() {
		return parentEmitter == null;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(ParticleFrameworkData particleFramework) {
		if (!isInitialized())
			resolveParticleSystems(particleFramework);

		resolveChildEmitterParticleSystems(particleFramework);

		resolveParentChild();
	}

	private void resolveParentChild() {
		final var lNumChildEmitterDefs = mChildEmitters != null ? mChildEmitters.size() : 0;
		for (int i = 0; i < lNumChildEmitterDefs; i++) {
			final var lChildInst = mChildEmitters.get(i);
			lChildInst.parentEmitter = this;
			lChildInst.resolveParentChild();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void resolveParticleSystems(ParticleFrameworkData particleFramework) {
		sharedParticleSystemInstance = particleFramework.particleSystemManager().getParticleSystemByName(particleSystemName, useSharedParticleSystem);

		if (sharedParticleSystemInstance == null && particleSystemName != null) {
			// ignore
		}

		resolveChildEmitterParticleSystems(particleFramework);
	}

	private void resolveChildEmitterParticleSystems(ParticleFrameworkData particleFramework) {
		if (mChildEmitters == null)
			return;

		final int lNumChildEmitters = mChildEmitters.size();
		for (int i = 0; i < lNumChildEmitters; i++) {
			final var lChildEmitterDefinition = mChildEmitters.get(i);
			if (lChildEmitterDefinition == null)
				continue;

			lChildEmitterDefinition.resolveParticleSystems(particleFramework);
		}
	}

	public void addNewChildEmitter() {
		final var lNewChildEmitterDef = new ParticleEmitterDefinition();

		lNewChildEmitterDef.name = "not set";

		mChildEmitters.add(lNewChildEmitterDef);
	}

	public void duplicateNewChildEmitter(int particleEmitterIndexToDuplicate) {
		if (mChildEmitters == null)
			return;

		if (particleEmitterIndexToDuplicate < 0 || particleEmitterIndexToDuplicate >= mChildEmitters.size())
			return;

		final var lDefToDup = mChildEmitters.get(particleEmitterIndexToDuplicate);
		if (lDefToDup == null)
			return;

		mChildEmitters.add(createDuplicateDefinition(lDefToDup));

	}

	private ParticleEmitterDefinition createDuplicateDefinition(ParticleEmitterDefinition def) {
		final var lNewDef = new ParticleEmitterDefinition();

		lNewDef.parentEmitter = def.parentEmitter;
		lNewDef.name = def.name;
		lNewDef.particleSystemName = def.particleSystemName;
		lNewDef.displayName = def.displayName;
		lNewDef.emitTimeMin = def.emitTimeMin;
		lNewDef.emitTimeMax = def.emitTimeMax;
		lNewDef.emitAmountMin = def.emitAmountMin;
		lNewDef.emitAmountMax = def.emitAmountMax;

		lNewDef.useSharedParticleSystem = def.useSharedParticleSystem;
		lNewDef.positionRelOffsetX = def.positionRelOffsetX;
		lNewDef.positionRelOffsetY = def.positionRelOffsetY;
		lNewDef.positionRelOffsetRot = def.positionRelOffsetRot;
		lNewDef.triggerType = def.triggerType;
		lNewDef.triggerCooldown = def.triggerCooldown;
		lNewDef.triggeredEmissionLengthMs = def.triggeredEmissionLengthMs;

		// Duplicate child emitters?
		final var lNumChildren = def.childEmitters().size();
		for (int i = 0; i < lNumChildren; i++) {
			final var lChildEmitterToDup = def.childEmitters().get(i);
			lNewDef.childEmitters().add(createDuplicateDefinition(lChildEmitterToDup));
		}

		return lNewDef;
	}

	public void removeNewChildEmitter(ParticleEmitterDefinition def) {
		if (mChildEmitters == null)
			return;

		if (mChildEmitters.contains(def))
			mChildEmitters.remove(def);
	}

}
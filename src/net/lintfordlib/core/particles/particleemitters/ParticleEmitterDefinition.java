package net.lintfordlib.core.particles.particleemitters;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.entities.definitions.BaseDefinition;

public class ParticleEmitterDefinition extends BaseDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -4696442942082963647L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "childEmitters")
	protected ParticleEmitterDefinition[] mChildEmitters;

	@SerializedName(value = "particleSystemName")
	protected String mParticleSystemName;

	@SerializedName(value = "emitTimeMin")
	protected float mEmitTimeMin;

	@SerializedName(value = "emitTimeMax")
	protected float mEmitTimeMax;

	@SerializedName(value = "emitAmountMin")
	protected int mEmitAmountMin;

	@SerializedName(value = "emitAmountMax")
	protected int mEmitAmountMax;

	@SerializedName(value = "positionRelOffsetX")
	protected float mPositionRelOffsetX;

	@SerializedName(value = "positionRelOffsetY")
	protected float mPositionRelOffsetY;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleEmitterDefinition[] childEmitters() {
		return mChildEmitters;
	}

	public String particleSystemName() {
		return mParticleSystemName;
	}

	public float emitTimeMin() {
		return mEmitTimeMin;
	}

	public float emitTimeMax() {
		return mEmitTimeMax;
	}

	public int emitAmountMin() {
		return mEmitAmountMin;
	}

	public int emitAmountMax() {
		return mEmitAmountMax;
	}

	public float positionRelOffsetX() {
		return mPositionRelOffsetX;
	}

	public float positionRelOffsetY() {
		return mPositionRelOffsetY;
	}
}
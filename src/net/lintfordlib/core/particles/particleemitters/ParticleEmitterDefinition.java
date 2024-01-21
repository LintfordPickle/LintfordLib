package net.lintfordlib.core.particles.particleemitters;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.entities.definitions.BaseDefinition;
import net.lintfordlib.core.particles.particleemitters.shapes.ParticleEmitterShape;
import net.lintfordlib.core.particles.particleemitters.shapes.ParticleEmitterShape.EmitterType;

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

	public transient ParticleEmitterShape particleEmitterShape;

	@SerializedName(value = "particleSystemName")
	public String particleSystemName;

	@SerializedName(value = "emitTimeMin")
	public float emitTimeMin;

	@SerializedName(value = "emitTimeMax")
	public float emitTimeMax;

	@SerializedName(value = "emitAmountMin")
	public int emitAmountMin;

	@SerializedName(value = "emitAmountMax")
	public int emitAmountMax;

	@SerializedName(value = "positionRelOffsetX")
	public float positionRelOffsetX;

	@SerializedName(value = "positionRelOffsetY")
	public float positionRelOffsetY;

	@SerializedName(value = "positionRelOffsetRot")
	public float positionRelOffsetRot;

	@SerializedName(value = "emitterShape")
	public EmitterType mEmitterShape;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleEmitterDefinition[] childEmitters() {
		return mChildEmitters;
	}

}
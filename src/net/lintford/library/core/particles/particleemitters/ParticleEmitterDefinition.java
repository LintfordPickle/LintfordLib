package net.lintford.library.core.particles.particleemitters;

import net.lintford.library.core.entity.definitions.BaseDefinition;

public class ParticleEmitterDefinition extends BaseDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String displayName;
	public ParticleEmitterDefinition[] childEmitters;
	public String particleSystemName;
	public float emitTimeMin;
	public float emitTimeMax;
	public int emitAmountMin;
	public int emitAmountMax;
	public float PositionRelOffsetX;
	public float PositionRelOffsetY;

}

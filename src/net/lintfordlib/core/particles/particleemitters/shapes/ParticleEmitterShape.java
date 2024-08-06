package net.lintfordlib.core.particles.particleemitters.shapes;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.particleemitters.ParticleEmitterInstance;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public abstract class ParticleEmitterShape implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1696139574040982010L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "className")
	public final String shapeName;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	protected ParticleEmitterShape(String shapeName) {
		this.shapeName = shapeName;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void update(LintfordCore core, ParticleEmitterInstance inst) {

	}

	public abstract void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float zDepth, float heading, float forceX, float forceY);

}

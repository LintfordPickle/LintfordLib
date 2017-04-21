package net.ld.library.core.graphics.particles.initialisers;

import net.ld.library.core.graphics.particles.Particle;

public interface IParticleInitialiser {

	/** A method to initialize the state of a {@link Particle} on creation */
	public abstract void initialise(Particle pParticle);

}

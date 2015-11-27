package net.ld.library.core.particles;

import net.ld.library.core.time.GameTime;

public interface IParticleInitialiser {

	public abstract void initialise(Particle pParticle);

	public abstract void update(Particle pParticle, GameTime pGameTime);

}

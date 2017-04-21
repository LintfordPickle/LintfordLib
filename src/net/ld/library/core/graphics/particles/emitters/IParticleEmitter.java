package net.ld.library.core.graphics.particles.emitters;

import net.ld.library.core.time.GameTime;

public interface IParticleEmitter {

	public abstract void initialise();

	public abstract void update(GameTime pGameTime);

}

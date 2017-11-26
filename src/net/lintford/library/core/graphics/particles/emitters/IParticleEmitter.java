package net.lintford.library.core.graphics.particles.emitters;

import net.lintford.library.core.time.GameTime;

public interface IParticleEmitter {

	public abstract void initialise();

	public abstract void update(GameTime pGameTime);

}

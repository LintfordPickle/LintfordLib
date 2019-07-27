package net.lintford.library.core.graphics.particles.emitters;

import net.lintford.library.core.LintfordCore.GameTime;

public interface IParticleEmitter {

	public abstract void initialize();

	public abstract void update(GameTime pGameTime);

}

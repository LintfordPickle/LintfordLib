package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.time.GameTime;

public interface IParticleModifier {

	/** A method to initialise the state of a {@link Particle} on creation */
	public abstract void initialise(Particle pParticle);

	/** A method to update the state of the modifier itself */
	public abstract void update(GameTime pGameTime);

	/** A method to update the state of a {@link Particle} instance. */
	public abstract void updateParticle(Particle pParticle, GameTime pGameTime);

}

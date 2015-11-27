package net.ld.library.core.particles;

public interface IParticleReactor {

	public abstract void onParticleSpawn(Particle pParticle);
	// public abstract void onParticleupdate(Particle pParticle);
	public abstract void onParticleDeath(Particle pParticle);
	
}

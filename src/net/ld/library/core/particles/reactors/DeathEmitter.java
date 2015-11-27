package net.ld.library.core.particles.reactors;

import net.ld.library.core.graphics.sprites.ISprite;
import net.ld.library.core.particles.IParticleReactor;
import net.ld.library.core.particles.Particle;
import net.ld.library.core.particles.ParticleManager;

public class DeathEmitter implements IParticleReactor {

	// =============================================
	// Variables
	// =============================================

	private ParticleManager mParticleManager;
	private ISprite mDeathSprite;

	// =============================================
	// Constructor
	// =============================================
	
	public DeathEmitter(ParticleManager pParticleManager, ISprite pDeathSprite){
		mParticleManager = pParticleManager;
		mDeathSprite = pDeathSprite;
	}
	
	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void onParticleSpawn(Particle pParticle) {
		
	}

	@Override
	public void onParticleDeath(Particle pParticle) {
		mParticleManager.addParticle(pParticle.position.x, pParticle.position.y, 0.0f, 0.0f, mDeathSprite);
	}
	
}

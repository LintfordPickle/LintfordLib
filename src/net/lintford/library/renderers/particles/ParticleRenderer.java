package net.lintford.library.renderers.particles;

import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.graphics.particles.ParticleSystem;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

/**  */
public class ParticleRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleSystem mParticleSystem;

	private TextureBatch mTextureBatch;
	private Texture mTexture;

	private boolean mIsLoaded;
	private boolean mIsParticleLoaded;
	private boolean mIsAssigned;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLoaded() {
		return mIsLoaded;
	}

	/** Returns true if this {@link ParticleRenderer} has been assigned to a {@link ParticleController}, or false otherwise. */
	public boolean isAssigned() {
		return mIsAssigned;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRenderer() {
		mTextureBatch = new TextureBatch();
		mIsAssigned = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mTextureBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		mTextureBatch.unloadGLContent();
		mIsLoaded = false;

	}

	public void draw(LintfordCore pCore) {
		if (!mIsLoaded || !mIsParticleLoaded || !mIsAssigned)
			return;

		final List<Particle> PARTICLES = mParticleSystem.particles();
		final int PARTICLE_COUNT = PARTICLES.size();

		mTextureBatch.begin(pCore.gameCamera());
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			final Particle PART = PARTICLES.get(i);

			if (PART.isFree())
				continue;

			mTextureBatch.draw(mTexture, PART.sx, PART.sy, PART.sw, PART.sh, PART.x - PART.radius, PART.y - PART.radius, -2, PART.radius * 2, PART.radius * 2, PART.r, PART.g, PART.b, PART.a);

		}

		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignParticleSystem(final ParticleSystem pParticleSystem) {
		mParticleSystem = pParticleSystem;
		loadParticleContent(pParticleSystem);
		mIsAssigned = true;

	}

	public void unassignedParticleSystem() {
		mIsAssigned = false;
		mIsParticleLoaded = false;

	}

	private void loadParticleContent(final ParticleSystem pParticleSystem) {
		mTexture = TextureManager.textureManager().loadTexture(pParticleSystem.textureName(), pParticleSystem.textureFilename());
		mIsParticleLoaded = mTexture != null;

	}

}

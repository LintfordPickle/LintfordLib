package net.ld.library.renderers.particles;

import java.util.List;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.particles.Particle;
import net.ld.library.core.graphics.particles.ParticleSystem;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;

/**  */
public class ParticleRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleSystem mParticleSystem;

	private TextureBatch mSpriteBatch;
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
		mSpriteBatch = new TextureBatch();
		mIsAssigned = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		mIsLoaded = false;

	}

	public void draw(RenderState pRenderState) {
		if (!mIsLoaded || !mIsParticleLoaded || !mIsAssigned)
			return;

		final List<Particle> PARTICLES = mParticleSystem.particles();
		final int PARTICLE_COUNT = PARTICLES.size();

		mSpriteBatch.begin(pRenderState.gameCamera());
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			final Particle PART = PARTICLES.get(i);

			if (PART.isFree())
				continue;

			mSpriteBatch.draw(PART.sx, PART.sy, PART.sw, PART.sh, PART.xx - PART.radius, PART.yy - PART.radius, 1f, PART.radius * 2, PART.radius * 2, PART.r, PART.g, PART.b, PART.a, PART.rot, PART.rox, PART.roy,
					PART.scaleX, PART.scaleY, mTexture);

		}

		mSpriteBatch.end();

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
		mTexture = TextureManager.textureManager().loadTextureFromFile(pParticleSystem.textureName(), pParticleSystem.textureFilename());
		mIsParticleLoaded = mTexture != null;

	}

}

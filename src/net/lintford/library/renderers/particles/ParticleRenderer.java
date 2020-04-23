package net.lintford.library.renderers.particles;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.particles.Particle;
import net.lintford.library.core.particles.particlesystems.ParticleSystemDefinition;
import net.lintford.library.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleSystemInstance mParticleSystem;
	private ResourceManager mResourceManager;
	private TextureBatch mTextureBatch;
	private Texture mTexture;
	private int mParticleRendererId;
	private int mEntityGroupId;
	private boolean mIsLoaded;
	private boolean mIsParticleLoaded;
	private boolean mIsAssigned;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int particleRendererId() {
		return mParticleRendererId;
	}

	public ParticleSystemInstance particleSystemInstance() {
		return mParticleSystem;
	}

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

	public ParticleRenderer(final int pRendererId, int pEntityGroupID) {
		mEntityGroupId = pEntityGroupID;

		mParticleRendererId = pRendererId;
		mTextureBatch = new TextureBatch();
		mIsAssigned = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;
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

		final List<Particle> lParticleSystem = mParticleSystem.particles();
		final int lNumParticles = lParticleSystem.size();

		mTextureBatch.begin(pCore.gameCamera());

		for (int i = 0; i < lNumParticles; i++) {
			final Particle lParticleInst = lParticleSystem.get(i);

			if (!lParticleInst.isAssigned())
				continue;

			final float lRadiusScaled = lParticleInst.radius * lParticleInst.scale;

			mTextureBatch.draw(mTexture, lParticleInst.sx, lParticleInst.sy, lParticleInst.sw, lParticleInst.sh, lParticleInst.x - lRadiusScaled, lParticleInst.y - lRadiusScaled, lRadiusScaled * 2, lRadiusScaled * 2, -0.2f, lParticleInst.r, lParticleInst.g,
					lParticleInst.b, lParticleInst.a);

		}

		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignParticleSystem(final ParticleSystemInstance pParticleSystem) {
		mParticleSystem = pParticleSystem;
		loadParticleContent(pParticleSystem);
		mIsAssigned = true;

	}

	public void unassignedParticleSystem() {
		mIsAssigned = false;
		mIsParticleLoaded = false;

	}

	private void loadParticleContent(final ParticleSystemInstance pParticleSystemInst) {
		ParticleSystemDefinition lParticleDefinition = pParticleSystemInst.definition();

		mTexture = mResourceManager.textureManager().loadTexture(lParticleDefinition.textureName(), lParticleDefinition.textureFilename(), GL11.GL_NEAREST, mEntityGroupId);
		mIsParticleLoaded = mTexture != null;

	}

}

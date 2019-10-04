package net.lintford.library.renderers.particles;

import org.jbox2d.common.Vec2;
import org.jbox2d.particle.ParticleColor;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.particles.Box2dParticleSystemWrapper;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

/**  */
public class Box2dParticleRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Box2dParticleSystemWrapper mParticleSystem;
	private ResourceManager mResourceManager;
	private TextureBatch mTextureBatch;
	private Texture mTexture;

	private int mEntityGroupID;
	private boolean mIsLoaded;
	private boolean mIsParticleLoaded;
	private boolean mIsAssigned;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLoaded() {
		return mIsLoaded;
	}

	/** Returns true if this {@link Box2dParticleRenderer} has been assigned to a {@link ParticleController}, or false otherwise. */
	public boolean isAssigned() {
		return mIsAssigned;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dParticleRenderer(int pEntityGroupID) {
		mEntityGroupID = pEntityGroupID;

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

		// final ParticleColor[] lParticleColors = mParticleSystem.particleColorBuffer();
		final Vec2[] lParticlePositionBuffer = mParticleSystem.particlePositionBuffer();

		if (lParticlePositionBuffer == null || lParticlePositionBuffer.length == 0)
			return;

		final int lParticleCount = mParticleSystem.getParticleCount(); // ;
		mTextureBatch.begin(pCore.gameCamera());

		// This is the stuff which needs to be set by the ParticleSystem
		final float lRadius = 4.0f;
		final float lSX = 0;
		final float lSY = 0;
		final float lSW = 32;
		final float lSH = 32;

		final float lZ = -0.2f;

		for (int i = 0; i < lParticleCount; i++) {
			final Vec2 lParticlePosition = lParticlePositionBuffer[i];

			if (lParticlePosition == null)
				continue;

			final float lR = 255f;//lParticleColors[i].r;
			final float lG = 255f;//lParticleColors[i].g;
			final float lB = 255f;//lParticleColors[i].b;
			final float lA = 255f;//lParticleColors[i].a;

			mTextureBatch.draw(mTexture, lSX, lSY, lSW, lSH, lParticlePosition.x - lRadius, lParticlePosition.y - lRadius, lRadius, lRadius, lZ, lR, lG, lB, lA);

		}

		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignParticleSystem(final Box2dParticleSystemWrapper pParticleSystem) {
		mParticleSystem = pParticleSystem;
		loadParticleContent(pParticleSystem);
		mIsAssigned = true;

	}

	public void unassignedParticleSystem() {
		mIsAssigned = false;
		mIsParticleLoaded = false;

	}

	private void loadParticleContent(final Box2dParticleSystemWrapper pParticleSystem) {
		mTexture = mResourceManager.textureManager().loadTexture(pParticleSystem.textureName(), pParticleSystem.textureFilename(), mEntityGroupID);
		mIsParticleLoaded = mTexture != null;

	}

}

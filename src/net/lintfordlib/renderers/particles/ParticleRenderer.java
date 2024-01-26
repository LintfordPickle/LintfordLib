package net.lintfordlib.renderers.particles;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.graphics.batching.TextureBatchPCT;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleSystemInstance mParticleSystem;
	private ResourceManager mResourceManager;
	private Texture mTexture;
	private final int mParticleRendererId;
	private int mEntityGroupId;
	private boolean mResourcesLoaded;
	private boolean mIsParticleLoaded;
	private boolean mIsAssigned;

	private int mSrcBlendFactor;
	private int mDestBlendFactor;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int srcBlendFactor() {
		return mSrcBlendFactor;
	}

	public int destBlendFactor() {
		return mDestBlendFactor;
	}

	public int particleRendererId() {
		return mParticleRendererId;
	}

	public ParticleSystemInstance particleSystemInstance() {
		return mParticleSystem;
	}

	public boolean isLoaded() {
		return mResourcesLoaded;
	}

	/** Returns true if this {@link ParticleRenderer} has been assigned to a {@link ParticleController}, or false otherwise. */
	public boolean isAssigned() {
		return mIsAssigned;
	}

	public boolean isAssignedParticleSystemAlive() {
		return mIsAssigned && (mParticleSystem != null && mParticleSystem.rendererId() == mParticleRendererId);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRenderer(final int rendererUid, int entityGroupUid) {
		mEntityGroupId = entityGroupUid;

		mParticleRendererId = rendererUid;
		mIsAssigned = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		mResourceManager = null;

		mResourcesLoaded = false;
	}

	public void draw(LintfordCore core, TextureBatchPCT textureBatch) {
		if (!mResourcesLoaded || !mIsParticleLoaded || !mIsAssigned)
			return;

		if (textureBatch.isDrawing() == false)
			return;

		final var lParticleSystem = mParticleSystem.particles();
		final var lNumParticles = lParticleSystem.size();

		final var lCamBounds = textureBatch.camera().boundingRectangle();

		for (int i = 0; i < lNumParticles; i++) {
			final var lParticleInst = lParticleSystem.get(i);

			if (!lParticleInst.isAssigned())
				continue;

			final float lWidthScaled = lParticleInst.width * lParticleInst.scale;
			final float lHeightScaled = lParticleInst.height * lParticleInst.scale;

			if (lCamBounds.intersectsAA(lParticleInst.worldPositionX, lParticleInst.worldPositionY, lParticleInst.width, lParticleInst.height) == false)
				continue;

			textureBatch.drawAroundCenter(mTexture, lParticleInst.sx, lParticleInst.sy, lParticleInst.sw, lParticleInst.sh, lParticleInst.worldPositionX, lParticleInst.worldPositionY, lWidthScaled, lHeightScaled, lParticleInst.worldPositionZ, lParticleInst.rotationInRadians, lParticleInst.rox, lParticleInst.roy,
					lParticleInst.scale, lParticleInst.color);
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignParticleSystem(ParticleSystemInstance particleSystem) {
		if (particleSystem.isAssigned() == false)
			return;

		mParticleSystem = particleSystem;
		loadParticleContent(particleSystem);
		mIsAssigned = true;
	}

	public void unassignParticleSystem() {
		mTexture = null;
		mParticleSystem = null;
		mIsParticleLoaded = false;
		mIsAssigned = false;
	}

	private void loadParticleContent(ParticleSystemInstance particleSystemInst) {
		if (!mResourcesLoaded)
			return;

		final var lParticleDefinition = particleSystemInst.definition();
		mIsParticleLoaded = lParticleDefinition != null;

		final var lTextureName = lParticleDefinition.textureName();
		final var lTextureFilepath = lParticleDefinition.textureFilename();

		if (lTextureName != null && lTextureFilepath != null) {
			mTexture = mResourceManager.textureManager().loadTexture(lTextureName, lTextureFilepath, GL11.GL_NEAREST, mEntityGroupId);
		} else {
			// Fallback to engine white
			mTexture = mResourceManager.textureManager().getTexture("TEXTURE_WHITE", LintfordCore.CORE_ENTITY_GROUP_ID);
		}

		// Set default blend factors
		if (lParticleDefinition.glSrcBlendFactor == 0)
			lParticleDefinition.glSrcBlendFactor = GL11.GL_SRC_ALPHA;

		if (lParticleDefinition.glDestBlendFactor == 0)
			lParticleDefinition.glDestBlendFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;

		mSrcBlendFactor = lParticleDefinition.glSrcBlendFactor;
		mDestBlendFactor = lParticleDefinition.glDestBlendFactor;

	}
}

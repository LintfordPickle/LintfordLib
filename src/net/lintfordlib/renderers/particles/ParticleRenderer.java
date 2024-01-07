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

		final var lParticleSystem = mParticleSystem.particles();
		final var lNumParticles = lParticleSystem.size();

		for (int i = 0; i < lNumParticles; i++) {
			final var lParticleInst = lParticleSystem.get(i);

			if (!lParticleInst.isAssigned())
				continue;

			final float lWidthScaled = lParticleInst.width * lParticleInst.scale;
			final float lHeightScaled = lParticleInst.height * lParticleInst.scale;

			textureBatch.drawAroundCenter(mTexture, lParticleInst.sx, lParticleInst.sy, lParticleInst.sw, lParticleInst.sh, lParticleInst.worldPositionX, lParticleInst.worldPositionY, lWidthScaled, lHeightScaled, lParticleInst.worldPositionZ, lParticleInst.rotationInRadians, lParticleInst.rox, lParticleInst.roy,
					lParticleInst.scale, lParticleInst.color);
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignParticleSystem(final ParticleSystemInstance particleSystem) {
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

	private void loadParticleContent(final ParticleSystemInstance particleSystemInst) {
		if (!mResourcesLoaded)
			return;

		final var lParticleDefinition = particleSystemInst.definition();

		// TODO: The texture filter mode needs to come from the ParticleSystemDefinition
		mTexture = mResourceManager.textureManager().loadTexture(lParticleDefinition.textureName(), lParticleDefinition.textureFilename(), GL11.GL_NEAREST, mEntityGroupId);
		mIsParticleLoaded = mTexture != null;
	}
}

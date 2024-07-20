package net.lintfordlib.renderers.particles;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.sprites.SpriteFrame;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;
import net.lintfordlib.core.rendering.RenderPass;

public class ParticleRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleSystemInstance mParticleSystem;
	private ResourceManager mResourceManager;
	private SpriteSheetDefinition mSpritesheetDefinition;
	private SpriteFrame mSpriteFrame;
	private final int mParticleRendererId;
	private int mEntityGroupId;
	private boolean mResourcesLoaded;
	private boolean mIsParticleLoaded;
	private boolean mIsAssigned;

	private int mSrcBlendFactor;
	private int mDestBlendFactor;

	private int mRenderPassId;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int srcBlendFactor() {
		return mSrcBlendFactor;
	}

	public int destBlendFactor() {
		return mDestBlendFactor;
	}

	/**
	 * Indicates on which {@link RenderPass} this @link {@link ParticleSystemInstance} is designated to render on. See {@link} for more RenderPass Ids, including the default render pass.
	 */
	public int renderPassId() {
		return mRenderPassId;
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

	public void draw(LintfordCore core, SpriteBatch textureBatch) {
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

			if (lCamBounds.intersectsAA(lParticleInst.worldPositionX, lParticleInst.worldPositionY, lParticleInst.width, lParticleInst.height) == false)
				continue;

			float lWidthScaled = mSpriteFrame != null ? mSpriteFrame.width() : lParticleInst.width;
			float lHeightScaled = mSpriteFrame != null ? mSpriteFrame.height() : lParticleInst.height;

			lWidthScaled *= lParticleInst.scale;
			lHeightScaled *= lParticleInst.scale;

			textureBatch.drawAroundCenter(mSpritesheetDefinition, mSpriteFrame, lParticleInst.worldPositionX, lParticleInst.worldPositionY, lWidthScaled, lHeightScaled, lParticleInst.rotationInRadians, lParticleInst.rox, lParticleInst.roy, lParticleInst.worldPositionZ, lParticleInst.color);
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
		mSpritesheetDefinition = null;
		mParticleSystem = null;
		mIsParticleLoaded = false;
		mIsAssigned = false;
	}

	private void loadParticleContent(ParticleSystemInstance particleSystemInst) {
		if (!mResourcesLoaded)
			return;

		final var lParticleDefinition = particleSystemInst.definition();
		mIsParticleLoaded = lParticleDefinition != null;

		final var lSpritesheetName = lParticleDefinition.spritesheetName;
		final var lSpriteName = lParticleDefinition.spriteName;

		if (lSpritesheetName != null) {
			mSpritesheetDefinition = mResourceManager.spriteSheetManager().getSpriteSheet(lSpritesheetName, mEntityGroupId);
			final var lSpritesheetFilepath = lParticleDefinition.spritesheetFilepath;
			if (mSpritesheetDefinition == null && lSpritesheetFilepath != null) {
				mSpritesheetDefinition = mResourceManager.spriteSheetManager().loadSpriteSheet(lSpritesheetName, lSpritesheetFilepath, mEntityGroupId);
			}
		}

		if (mSpritesheetDefinition == null)
			mSpritesheetDefinition = mResourceManager.spriteSheetManager().coreSpritesheet();

		mSpriteFrame = mSpritesheetDefinition.getSpriteFrame(lSpriteName);

		// Set default blend factors
		if (lParticleDefinition.glSrcBlendFactor == 0)
			lParticleDefinition.glSrcBlendFactor = GL11.GL_SRC_ALPHA;

		if (lParticleDefinition.glDestBlendFactor == 0)
			lParticleDefinition.glDestBlendFactor = GL11.GL_ONE_MINUS_SRC_ALPHA;

		mSrcBlendFactor = lParticleDefinition.glSrcBlendFactor;
		mDestBlendFactor = lParticleDefinition.glDestBlendFactor;

		mRenderPassId = lParticleDefinition.renderPassId;

	}
}

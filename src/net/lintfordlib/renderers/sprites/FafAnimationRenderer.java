package net.lintfordlib.renderers.sprites;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.core.FafAnimationController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.sprites.AnimatedSpriteListener;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public abstract class FafAnimationRenderer extends BaseRenderer implements AnimatedSpriteListener {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected FafAnimationController mFafAnimationController;

	protected final List<SpriteInstance> mFafAnimationUpdateList = new ArrayList<>(); // just update
	protected final List<SpriteInstance> mFafAnimationInstances = new ArrayList<>();

	private SpriteSheetDefinition mSpriteSheetdefinition;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return mFafAnimationController != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FafAnimationRenderer(RendererManagerBase rendererManager, String rendererName, FafAnimationController animationController, int entityGroupUid) {
		super(rendererManager, rendererName, entityGroupUid);

		mFafAnimationController = animationController;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		// The Fire-and-Forget animation controller to sync with is passed in the constructor

	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mSpriteSheetdefinition = loadSpriteSheetDefinition(resourceManager);
		if (mSpriteSheetdefinition == null)
			throw new RuntimeException("Could not load SpriteSheetDefinition!");
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		// process new insertions
		final var lAnimationQueue = mFafAnimationController.animationQueue();
		final var lNumAnimationsInQueue = lAnimationQueue.size();
		for (int i = lNumAnimationsInQueue - 1; i > 0; --i) {
			final var lAnimation = lAnimationQueue.removeLast();

			final var lAnimationInstance = mSpriteSheetdefinition.getSpriteInstance(lAnimation.name);
			if (lAnimationInstance == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Could not find animation / sprite instance with name " + lAnimation.name);
				continue;
			}

			lAnimationInstance.setPosition(lAnimation.wcx, lAnimation.wcy);
			lAnimationInstance.setScale(lAnimation.scale, lAnimation.scale);

			lAnimationInstance.playFromBeginning();
			lAnimationInstance.animatedSpriteListender(this);

			// init sprite instance
			mFafAnimationInstances.add(lAnimationInstance);
			mFafAnimationController.returnAnimationToPool(lAnimation);
		}

		mFafAnimationUpdateList.clear();
		mFafAnimationUpdateList.addAll(mFafAnimationInstances);
		final int lNumAnimations = mFafAnimationUpdateList.size();
		for (int i = 0; i < lNumAnimations; i++) {
			final var lAnimInstance = mFafAnimationUpdateList.get(i);
			lAnimInstance.update(core);

			if (!lAnimInstance.animationEnabled())
				onStopped(lAnimInstance);
		}
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		if (!isInitialized())
			return;

		if (mSpriteSheetdefinition == null)
			return;

		final var lSpriteBatch = core.sharedResources().uiSpriteBatch();

		lSpriteBatch.begin(core.gameCamera());
		lSpriteBatch.setColorWhite();

		final int lNumAnimations = mFafAnimationInstances.size();
		for (int i = 0; i < lNumAnimations; i++) {

			final var lAnimInstance = mFafAnimationInstances.get(i);

			final float lDstW = lAnimInstance.width();
			final float lDstH = lAnimInstance.height();
			final float lDstX = lAnimInstance.x() - lDstW * .5f;
			final float lDstY = lAnimInstance.y() - lDstH * .5f;

			lSpriteBatch.draw(mSpriteSheetdefinition, lAnimInstance.currentSpriteFrame(), lDstX, lDstY, lDstW, lDstH, 0.4f);
		}

		lSpriteBatch.end();
	}

	protected abstract SpriteSheetDefinition loadSpriteSheetDefinition(ResourceManager resourceManager);

	// --------------------------------------
	// Interface Methods
	// --------------------------------------

	@Override
	public void onStarted(SpriteInstance spriteInstance) {
		// ignore
	}

	@Override
	public void onLooped(SpriteInstance spriteInstance) {
		// ignore
	}

	@Override
	public void onStopped(SpriteInstance spriteInstance) {
		if (mSpriteSheetdefinition != null) {
			mSpriteSheetdefinition.releaseInstance(spriteInstance);
		}

		spriteInstance.animatedSpriteListender(null);
		mFafAnimationInstances.remove(spriteInstance);
	}
}
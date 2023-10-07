package net.lintfordlib.renderers.sprites;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.controllers.core.FafAnimationController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.sprites.AnimatedSpriteListener;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class FafAnimationRenderer extends BaseRenderer implements AnimatedSpriteListener {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FafAnimationController mFafAnimationController;

	private final List<SpriteInstance> animationUpdateList = new ArrayList<>();

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

	public FafAnimationRenderer(RendererManager rendererManager, String rendererName, FafAnimationController animationController, int entityGroupUid) {
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
	public void update(LintfordCore core) {
		super.update(core);

		animationUpdateList.clear();
		animationUpdateList.addAll(mFafAnimationController.animations());
		final int lNumAnimations = animationUpdateList.size();
		for (int i = 0; i < lNumAnimations; i++) {
			final var lAnimInstance = animationUpdateList.get(i);
			lAnimInstance.animatedSpriteListender(this);
			lAnimInstance.update(core);
		}
	}

	@Override
	public void draw(LintfordCore core) {
		if (!isInitialized())
			return;

		final var lSpritesheetDefinition = mFafAnimationController.spritesheetDefintion();

		if (lSpritesheetDefinition == null) {
			return;
		}

		final var lSpriteBatch = rendererManager().uiSpriteBatch();
		lSpriteBatch.begin(core.gameCamera());
		final int lNumAnimations = animationUpdateList.size();
		for (int i = 0; i < lNumAnimations; i++) {

			final var lAnimInstance = animationUpdateList.get(i);

			final float lDstW = lAnimInstance.width() * 2.f;
			final float lDstH = lAnimInstance.height() * 2.f;
			final float lDstX = lAnimInstance.x() - lDstW * .5f;
			final float lDstY = lAnimInstance.y() - lDstH * .5f;

			lSpriteBatch.draw(lSpritesheetDefinition, lAnimInstance.currentSpriteFrame(), lDstX, lDstY, lDstW, lDstH, -0.4f, ColorConstants.WHITE);
		}

		lSpriteBatch.end();
	}

	// --------------------------------------
	// Interface Methods
	// --------------------------------------

	@Override
	public void onStarted(SpriteInstance spriteInstance) {

	}

	@Override
	public void onLooped(SpriteInstance spriteInstance) {

	}

	@Override
	public void onStopped(SpriteInstance spriteInstance) {
		mFafAnimationController.animations().remove(spriteInstance);
		if (mFafAnimationController.spritesheetDefintion() != null) {
			mFafAnimationController.spritesheetDefintion().releaseInstance(spriteInstance);
		}
	}
}
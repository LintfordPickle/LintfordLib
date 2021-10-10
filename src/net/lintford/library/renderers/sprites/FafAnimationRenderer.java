package net.lintford.library.renderers.sprites;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.core.FafAnimationController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.AnimatedSpriteListener;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

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

	public FafAnimationRenderer(RendererManager pRendererManager, String pRendererName, FafAnimationController pAnimationController, int pEntityGroupID) {
		super(pRendererManager, pRendererName, pEntityGroupID);

		mFafAnimationController = pAnimationController;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		// The Fire-and-Forget animation controller to sync with is passed in the constructor

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		animationUpdateList.clear();
		animationUpdateList.addAll(mFafAnimationController.animations());
		final int lNumAnimations = animationUpdateList.size();
		for (int i = 0; i < lNumAnimations; i++) {
			final var lAnimInstance = animationUpdateList.get(i);
			lAnimInstance.animatedSpriteListender(this);
			lAnimInstance.update(pCore);
		}
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isInitialized())
			return;

		final var lSpritesheetDefinition = mFafAnimationController.spritesheetDefintion();

		if (lSpritesheetDefinition == null) {
			return;
		}

		// TODO: Add to engine
		final var lSpriteBatch = rendererManager().uiSpriteBatch();
		lSpriteBatch.begin(pCore.gameCamera());
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
	public void onStarted(SpriteInstance pSender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLooped(SpriteInstance pSender) {

	}

	@Override
	public void onStopped(SpriteInstance pSender) {
		mFafAnimationController.animations().remove(pSender);
		if (mFafAnimationController.spritesheetDefintion() != null) {
			mFafAnimationController.spritesheetDefintion().releaseInstance(pSender);
		}
	}
}

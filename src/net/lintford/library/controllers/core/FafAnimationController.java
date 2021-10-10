package net.lintford.library.controllers.core;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public abstract class FafAnimationController extends BaseController {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class FafAnimationInstance {
		public FafAnimationInstance(String pAnimationName, float pWorldX, float pWorldY) {
			worldX = pWorldX;
			worldY = pWorldY;
			animName = pAnimationName;
		}

		public String animName;
		public float worldX;
		public float worldY;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mSpritesheetName;
	protected SpriteSheetDefinition mSpritesheetDefintion;

	private final List<SpriteInstance> mFafAnimationInstances = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<SpriteInstance> animations() {
		return mFafAnimationInstances;
	}

	public SpriteSheetDefinition spritesheetDefintion() {
		return mSpritesheetDefintion;
	}

	public void spritesheetDefinition(SpriteSheetDefinition pNewSpritesheetDefinition) {
		mSpritesheetDefintion = pNewSpritesheetDefinition;
	}

	public String spritesheetName() {
		return mSpritesheetName;
	}

	@Override
	public boolean isInitialized() {
		return mSpritesheetName != null;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public FafAnimationController(ControllerManager pControllerManager, String pAnimationControllerName, int pEntityGroupUid) {
		super(pControllerManager, pAnimationControllerName, pEntityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unload() {
		if (mSpritesheetDefintion == null)
			return;

		final int lNumAnimations = mFafAnimationInstances.size();
		for (int i = 0; i < lNumAnimations; i++) {
			mSpritesheetDefintion.releaseInstance(mFafAnimationInstances.get(i));
		}
		mFafAnimationInstances.clear();
		mSpritesheetDefintion = null;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void playFafAnimation(String pAnimationName, float pWorldX, float pWorldY) {
		if (mSpritesheetDefintion == null)
			return;

		final var lNewAnimation = mSpritesheetDefintion.getSpriteInstance(pAnimationName);
		lNewAnimation.setFrame(0);
		lNewAnimation.x(pWorldX);
		lNewAnimation.y(pWorldY);
		mFafAnimationInstances.add(lNewAnimation);

	}
}

package net.lintfordlib.controllers.core;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public abstract class FafAnimationController extends BaseController {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class FafAnimationInstance {

		public FafAnimationInstance(String animationName, float worldPositionX, float worldPositionY) {
			worldX = worldPositionX;
			worldY = worldPositionY;
			animName = animationName;
		}

		public String animName;
		public float worldX;
		public float worldY;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mSpritesheetName;
	private SpriteSheetDefinition mSpritesheetDefintion;
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

	public void spritesheetDefinition(SpriteSheetDefinition spritesheetDefinition) {
		mSpritesheetDefintion = spritesheetDefinition;
	}

	public String spritesheetName() {
		return mSpritesheetName;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public FafAnimationController(ControllerManager controllerManager, String animationControllerName, int entityGroupUid) {
		super(controllerManager, animationControllerName, entityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unloadController() {
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

	protected void playFafAnimation(String animationName, float worldPositionX, float worldPositionY) {
		if (mSpritesheetDefintion == null)
			return;

		final var lNewAnimation = mSpritesheetDefintion.getSpriteInstance(animationName);
		lNewAnimation.setFrame(0);
		lNewAnimation.x(worldPositionX);
		lNewAnimation.y(worldPositionY);
		mFafAnimationInstances.add(lNewAnimation);
	}
}
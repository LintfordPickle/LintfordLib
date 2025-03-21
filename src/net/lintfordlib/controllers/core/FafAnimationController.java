package net.lintfordlib.controllers.core;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;

public abstract class FafAnimationController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NUM_ANIMATIONS = 50;

	public class FafAnimation {
		public final int animationIndex;
		public String name;
		public float wcx;
		public float wcy;
		public float scale;

		public FafAnimation(int index) {
			animationIndex = index;
		}
	}

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
	private final List<FafAnimation> mAnimationPool = new ArrayList<>();
	private final List<FafAnimation> mAnimationQueue = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<FafAnimation> animationQueue() {
		return mAnimationQueue;
	}

	public String spritesheetName() {
		return mSpritesheetName;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public FafAnimationController(ControllerManager controllerManager, String animationControllerName, int entityGroupUid) {
		super(controllerManager, animationControllerName, entityGroupUid);

		for (int i = 0; i < NUM_ANIMATIONS; i++) {
			mAnimationPool.add(new FafAnimation(i));
		}
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unloadController() {
		mAnimationPool.clear();
		mAnimationQueue.clear();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public int numAnimationsInQueue() {
		return mAnimationQueue.size();
	}

	public void playAnimationByName(String animationName, float wcx, float wcy) {
		playAnimationByName(animationName, wcx, wcy, 1.f);
	}

	public void playAnimationByName(String animationName, float wcx, float wcy, float scale) {
		final var lFafAnimationItem = mAnimationPool.removeLast();
		if (lFafAnimationItem == null)
			return;

		if (scale == 0.f)
			scale = 1.f;

		lFafAnimationItem.name = animationName;
		lFafAnimationItem.wcx = wcx;
		lFafAnimationItem.wcy = wcy;
		lFafAnimationItem.scale = scale;

		mAnimationQueue.add(lFafAnimationItem);
	}

	public void returnAnimationToPool(FafAnimation animation) {
		mAnimationPool.add(animation);
	}
}
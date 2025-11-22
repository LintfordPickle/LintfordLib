package net.lintfordlib.screenmanager.layouts;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.screenmanager.MenuScreen;

/**
 * The list layout lays out all the menu entries linearly down the layout.
 */
public class FloatingLayout extends BaseLayout implements IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7568188688210642680L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mClickTimer;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FloatingLayout(MenuScreen parentScreen) {
		super(parentScreen);

		// inevitably, there is some portion of the background graphic which
		// shouldn't have content rendered over it. that's this
		mCropPaddingBottom = 0.f;
		mCropPaddingTop = 0.f;
	}

	public FloatingLayout(MenuScreen parentScreen, float x, float y) {
		this(parentScreen);

		mX = x;
		mY = y;
	}

	public FloatingLayout(MenuScreen parentScreen, float x, float y, float width, float height) {
		this(parentScreen, x, y);

		mW = width;
		mH = height;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mMenuEntries == null || mMenuEntries.isEmpty())
			return false; // nothing to do

		// limit mouse interaction within the baseLayout to within the contentDisplayArea
		// due to the constraints imposed by the title bar, via the crop top and crop bottom, the contentDisplayArea is a subset of the layout
		final var mouseMenuSelectedEnabled = core.input().mouse().isMouseMenuSelectionEnabled();
		final var mouseIntersectsUs = mouseMenuSelectedEnabled && contentDisplayArea().intersectsAA(core.HUD().getMouseCameraSpace());
		if (mouseIntersectsUs) {
			final int lCount = mMenuEntries.size();
			for (int i = 0; i < lCount; i++) {
				var lInputHandled = false;
				lInputHandled = mMenuEntries.get(i).onHandleMouseInput(core);

				if (lInputHandled)
					return lInputHandled;
			}

			if (core.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				final float scrollAccelerationAmt = core.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(scrollAccelerationAmt);
			}
		}

		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {

			final var menuEntry = mMenuEntries.get(i);

			// note: different input methods have precedence.

			var lInputHandled = false;
			lInputHandled = menuEntry.onHandleInputActions(core);
			lInputHandled = lInputHandled || menuEntry.onHandleKeyboardInput(core);
			lInputHandled = lInputHandled || menuEntry.onHandleGamepadInput(core);

			if (lInputHandled)
				return lInputHandled;
		}

		mScrollBar.scrollBarEnabled(false);
		if (mScrollBar.scrollBarEnabled())
			mScrollBar.handleInput(core, screenManager);

		return false;
	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		// The floating layout doesn't change the size or posiiton of its entries, they are free to set themselves.
		// But, we do need to update their width/height to their set desired width/height

		final int entryCount = mMenuEntries.size();
		for (int i = 0; i < entryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);

			// TODO: Wtf is this supposed to be doing? Setting itself with .. itself ..
			lMenuEntry.set(lMenuEntry.x(), lMenuEntry.y(), lMenuEntry.desiredWidth(), lMenuEntry.desiredHeight());

		}
	}

	@Override
	public void update(LintfordCore core) {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			final var menuEntry = mMenuEntries.get(i);
			menuEntry.update(core, parentScreen);
		}

		mContentArea.set(mX, mY, mW, mH);
		contentDisplayRectange.set(mX, mY, mW, mH);

		mScrollBar.scrollBarEnabled(false);

		if (mClickTimer >= 0) {
			mClickTimer -= core.appTime().elapsedTimeMilli();
		}
	}

	@Override
	public void draw(LintfordCore core, float componentDepth) {
		super.draw(core, componentDepth);

		final var spriteBatch = parentScreen.spriteBatch();
		final var spriteSheetCore = core.resources().spriteSheetManager().coreSpritesheet();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(spriteSheetCore, CoreTextureNames.TEXTURE_WHITE, contentDisplayArea(), ZLayers.LAYER_DEBUG);
			spriteBatch.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(spriteSheetCore, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, ZLayers.LAYER_DEBUG);
			spriteBatch.end();
		}

	}

	// --------------------------------------
	// IProcessMouseInput-Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mClickTimer < 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mClickTimer = cooldownInMs;
	}

	@Override
	public boolean allowGamepadInput() {
		return parentScreen.allowGamepadInput();
	}

	@Override
	public boolean allowKeyboardInput() {
		return parentScreen.allowKeyboardInput();
	}

	@Override
	public boolean allowMouseInput() {
		return parentScreen.allowMouseInput();
	}
}

package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;

public class UISlider extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String NO_LABEL_TEXT = "Slider";

	// --------------------------------------
	// Variables
	// --------------------------------------

	IMenuEntryClickListener mCallback;
	private int mClickID;
	private String mSliderLabel;
	private float mR, mG, mB;

	public float mMinValue;
	public float mMaxValue;

	private float mCurrentPosition;
	public float mCurrentValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float currentValue() {
		return mCurrentValue;
	}

	public String sliderLabel() {
		return mSliderLabel;
	}

	public void buttonLabel(final String pNewLabel) {
		mSliderLabel = pNewLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UISlider(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UISlider(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mSliderLabel = NO_LABEL_TEXT;
		width = 200;
		height = 25;

		mR = 0.19f;
		mG = 0.13f;
		mB = 0.3f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersects(pCore.HUD().getMouseCameraSpace())) {
			if (pCore.input().tryAquireLeftClickOwnership(hashCode())) {

				float lWindowX = x;
				float lMouseX = pCore.HUD().getMouseCameraSpace().x;
				mCurrentPosition = MathHelper.clamp(lMouseX - lWindowX, 0, width);
				mCurrentValue = MathHelper.scaleToRange(mCurrentPosition, 0, width, mMinValue, mMaxValue);

				if (mCallback != null) {
					// Notify subscribers that something changes
					mCallback.onClick(mClickID);
				}

				return true;

			}

		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

	}

	@Override
	public void draw(LintfordCore pCore) {

		// Seconds
		mMinValue = 0;
		mMaxValue = 86400;

		final float SLIDER_RAIL_HEIGHT = 4;
		final float SLIDER_WIDTH = 10;

		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiSpriteBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pCore.HUD());
		SPRITE_BATCH.draw(0, 0, 32, 32, x, y + height / 2 - SLIDER_RAIL_HEIGHT / 2, 0f, width, SLIDER_RAIL_HEIGHT, 1f, mR, mG, mB, 1f, TextureManager.TEXTURE_CORE_UI);
		SPRITE_BATCH.draw(0, 0, 32, 32, x + mCurrentPosition - SLIDER_WIDTH / 2, y, 0f, SLIDER_WIDTH, height, 1f, mB, mG, mR, 1f, TextureManager.TEXTURE_CORE_UI);

		SPRITE_BATCH.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final IMenuEntryClickListener pCallbackObject, final int pNewLIstenerID) {
		mCallback = pCallbackObject;
		mClickID = pNewLIstenerID;

	}

	public void removeClickListener(final IMenuEntryClickListener pCallbackObject) {
		mCallback = null;
	}

}

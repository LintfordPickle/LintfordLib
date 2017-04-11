package net.ld.library.renderers.windows.components;

import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.MathHelper;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.renderers.windows.UIWindow;
import net.ld.library.screenmanager.entries.IMenuEntryClickListener;

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
	public boolean handleInput(final  InputState pInputState) {
		if (intersects(pInputState.HUD().getMouseCameraSpace())) {
			if (pInputState.tryAquireLeftClickOwnership(hashCode())) {

				float lWindowX = x;
				float lMouseX = pInputState.HUD().getMouseCameraSpace().x;
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
	public void update(final GameTime pGameTime) {
		super.update(pGameTime);

	}

	@Override
	public void draw(final RenderState pRenderState) {

		// Seconds
		mMinValue = 0;
		mMaxValue = 86400;

		final float SLIDER_RAIL_HEIGHT = 4;
		final float SLIDER_WIDTH = 10;
		
		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiSpriteBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pRenderState.hudCamera());
		SPRITE_BATCH.draw(0, 96, 32, 32, x, y + height / 2 - SLIDER_RAIL_HEIGHT / 2, 0f, width, SLIDER_RAIL_HEIGHT, 1f, mR, mG, mB, 1f, TextureManager.CORE_TEXTURE);
		SPRITE_BATCH.draw(0, 96, 32, 32, x + mCurrentPosition - SLIDER_WIDTH / 2, y, 0f, SLIDER_WIDTH, height, 1f, mB, mG, mR, 1f, TextureManager.CORE_TEXTURE);

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

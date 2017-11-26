package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;

public class UIRadioButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	IMenuEntryClickListener mCallback;
	private int mClickID;
	private String mButtonLabel;
	private boolean mIsSelected;
	private float mValue;
	private boolean mIsClicked; // click animation/delay
	private float mClickTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isSelected() {
		return mIsSelected;
	}

	public void isSelected(final boolean pNewValue) {
		mIsSelected = pNewValue;
	}

	public float value() {
		return mValue;
	}

	public void value(final float pNewValue) {
		mValue = pNewValue;
	}

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(final String pNewLabel) {
		mButtonLabel = pNewLabel;
	}

	public int buttonListenerID() {
		return mClickID;
	}

	public void buttonListenerID(final int pNewID) {
		mClickID = pNewID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIRadioButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIRadioButton(final UIWindow pParentWindow, final int pClickID) {
		this(pParentWindow, NO_LABEL_TEXT, pClickID);

	}

	public UIRadioButton(final UIWindow pParentWindow, final String pLabel, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = pLabel;
		width = 200;
		height = 25;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(final InputState pInputState, ICamera pHUDCamera) {
		if (intersects(pHUDCamera.getMouseCameraSpace()) && !mIsClicked && pInputState.tryAquireLeftClickOwnership(hashCode())) {
			mIsClicked = true;
			final float MINIMUM_CLICK_TIMER = 1000;
			// Callback to the listener and pass our ID
			if (mCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
				mClickTimer = 0;
				mCallback.onClick(mClickID);
				return true;

			}

		}

		if (!pInputState.mouseLeftClick()) {
			mIsClicked = false;

		}

		return false;
	}

	@Override
	public void update(final GameTime pGameTime) {
		super.update(pGameTime);

		mClickTimer += pGameTime.elapseGameTimeMilli();
	}

	@Override
	public void draw(final RenderState pRenderState) {

		float lR = mIsSelected ? 0.4f : 0.3f;
		float lG = mIsSelected ? 0.4f : 0.34f;
		float lB = mIsSelected ? 0.4f : 0.65f;

		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiSpriteBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pRenderState.HUDCamera());
		SPRITE_BATCH.draw(0, 0, 32, 32, x, y, 0f, width, height, 1f, lR, lG, lB, 1f, TextureManager.TEXTURE_CORE_UI);
		SPRITE_BATCH.end();

		FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = lFontRenderer.bitmap().getStringWidth(lButtonText);

		lFontRenderer.begin(pRenderState.HUDCamera());
		lFontRenderer.draw(lButtonText, x + width / 2f - lTextWidth / 2f, y + height / 2f - lFontRenderer.bitmap().fontHeight() / 4f, 1f);
		lFontRenderer.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final IMenuEntryClickListener pCallbackObject) {
		mCallback = pCallbackObject;
	}

	public void removeClickListener(final IMenuEntryClickListener pCallbackObject) {
		mCallback = null;
	}

}

package net.ld.library.renderers.windows.components;

import net.ld.library.core.graphics.fonts.FontUnit;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.renderers.windows.UIWindow;
import net.ld.library.screenmanager.entries.IMenuEntryClickListener;

public class UITextButton extends UIWidget {

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
	private float mR, mG, mB;
	private boolean mHoveredOver;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(final String pNewLabel) {
		mButtonLabel = pNewLabel;
	}

	public int buttonListenerID() {
		return mClickID;
	}

	public void buttonListenerID(final int pNewLabel) {
		mClickID = pNewLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UITextButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UITextButton(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = NO_LABEL_TEXT;
		width = 200;
		height = 25;

		mR = mG = mB = 1f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(final InputState pInputState) {
		if (intersects(pInputState.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pInputState.mouseLeftClick()) {
				// Callback to the listener and pass our ID
				if (mCallback != null) {
					mCallback.onClick(mClickID);

				}

				return true;

			}

		} else {
			mHoveredOver = false;
		}

		return false;
	}

	@Override
	public void draw(final RenderState pRenderState) {

		mR = 0.19f;
		mG = 0.13f;
		mB = 0.3f;

		float lR = mHoveredOver ? 0.3f : mR;
		float lG = mHoveredOver ? 0.34f : mG;
		float lB = mHoveredOver ? 0.65f : mB;
		
		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiSpriteBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pRenderState.hudCamera());
		SPRITE_BATCH.draw(0, 96, 32, 32, x, y, 0f, width, height, 1f, lR, lG, lB, 1f, TextureManager.CORE_TEXTURE);
		SPRITE_BATCH.end();

		FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = lFontRenderer.bitmap().getStringWidth(lButtonText);

		lFontRenderer.begin(pRenderState.hudCamera());
		lFontRenderer.draw(lButtonText, x + width / 2f - lTextWidth / 2f, y + height / 2f - lFontRenderer.bitmap().fontHeight() / 4f, 1f, 1f);
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

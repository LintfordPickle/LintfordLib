package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;

public class UIIconButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient IMenuEntryClickListener mCallback;
	private transient int mClickID;
	private transient String mButtonLabel;
	private transient float mR, mG, mB;
	private transient boolean mHoveredOver;

	private transient Texture mButtonTexture;
	private transient Rectangle mSourceRectangle;

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

	public UIIconButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIIconButton(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = NO_LABEL_TEXT;
		width = 200;
		height = 25;

		mR = mG = mB = 1f;
		mSourceRectangle = new Rectangle();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(final InputState pInputState, ICamera pHUDCamera) {
		if (intersects(pHUDCamera.getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pInputState.tryAquireLeftClickOwnership(hashCode())) {
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
		SPRITE_BATCH.begin(pRenderState.HUDCamera());
		SPRITE_BATCH.draw(0, 0, 32, 32, x, y, 0f, width, height, 1f, lR, lG, lB, 1f, TextureManager.TEXTURE_CORE_UI);

		if (mButtonTexture != null) {
			SPRITE_BATCH.draw(mSourceRectangle.x, mSourceRectangle.y, mSourceRectangle.width, mSourceRectangle.height, x, y, 0f, width, height, 1f, lR, lG, lB, 1f, mButtonTexture);
		}

		SPRITE_BATCH.end();

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

	public void setTextureSource(final Texture pTexture, final float pSrcX, final float pSrcY, final float pSrcW, final float pSrcH) {
		mButtonTexture = pTexture;

		mSourceRectangle.x = pSrcX;
		mSourceRectangle.y = pSrcY;
		mSourceRectangle.width = pSrcW;
		mSourceRectangle.height = pSrcH;

	}

}
package net.ld.library.renderers.windows.components;


import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.renderers.windows.UIWindow;
import net.ld.library.screenmanager.entries.IMenuEntryClickListener;

public class UIToggleButton extends UIWidget {

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

	private Texture mButtonTexture;
	private Rectangle mSourceRectangle;

	private boolean mIsEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isEnabled() {
		return mIsEnabled;
	}

	public void isEnabled(final boolean pNewValue) {
		mIsEnabled = pNewValue;
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

	public void buttonListenerID(final int pNewLabel) {
		mClickID = pNewLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIToggleButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIToggleButton(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = NO_LABEL_TEXT;
		width = 200;
		height = 25;

		mR = 0.3f;
		mG = 0.34f;
		mB = 0.65f;

		mSourceRectangle = new Rectangle();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(final InputState pInputState) {
		if (intersects(pInputState.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pInputState.tryAquireLeftClickOwnership(hashCode())) {

				// Callback to the listener and pass our ID
				if (mCallback != null) {
					System.out.println("here");
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
		float lR = mIsEnabled ? 0.3f : mHoveredOver ? 0.3f : mR;
		float lG = mIsEnabled ? 0.13f : mHoveredOver ? 0.34f : mG;
		float lB = mIsEnabled ? 0.19f : mHoveredOver ? 0.65f : mB;
		
		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiSpriteBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pRenderState.hudCamera());
		SPRITE_BATCH.draw(0, 96, 32, 32, x, y, 0f, width, height, 1f, lR, lG, lB, 1f, TextureManager.CORE_TEXTURE);

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

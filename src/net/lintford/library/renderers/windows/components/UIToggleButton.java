package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;

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
	public boolean handleInput(LintfordCore pCore) {
		if (intersects(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pCore.input().tryAquireLeftClickOwnership(hashCode())) {

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
	public void draw(LintfordCore pCore) {
		float lR = mIsEnabled ? 0.3f : mHoveredOver ? 0.3f : mR;
		float lG = mIsEnabled ? 0.13f : mHoveredOver ? 0.34f : mG;
		float lB = mIsEnabled ? 0.19f : mHoveredOver ? 0.65f : mB;
		
		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiSpriteBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pCore.HUD());
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

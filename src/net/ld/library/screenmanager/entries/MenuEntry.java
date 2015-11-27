package net.ld.library.screenmanager.entries;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.spritebatch.SpriteBatch;
import net.ld.library.core.graphics.spritebatch.SpriteBatch9Patch;
import net.ld.library.core.graphics.sprites.SpriteSheet;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.sounds.SoundManager;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.MenuScreen;
import net.ld.library.screenmanager.Screen;
import net.ld.library.screenmanager.ScreenManager;

public class MenuEntry {

	// =============================================
	// Constants
	// =============================================

	protected static final float MENUENTRY_WIDTH = 310; // half width
	protected static final float MENUENTRY_HEIGHT = 16; // half height
	protected static final float FOCUS_TIMER = .050f; // seconds

	public enum BUTTON_SIZE {
		narrow, normal, wide;
	}

	// =============================================
	// Variables
	// =============================================

	protected ScreenManager mScreenManager;
	protected DisplayConfig mDisplayConfig;
	protected MenuScreen mParentScreen;
	protected boolean mEnabled;
	protected String mText;
	protected Vector2f mPosition;
	protected float mScale;
	private float mScaleCounter;
	protected IMenuEntryClickListener mClickListener;
	protected int mMenuEntryID;
	protected Rectangle mBounds;
	protected boolean mHoveredOver;
	protected boolean mToolTipEnabled;
	protected float mToolTipTimer;
	protected String mToolTip;
	protected boolean mHasFocus;
	protected boolean mFocusLocked;
	protected float mClickTimer;
	protected BUTTON_SIZE mButtonSize = BUTTON_SIZE.normal;
	private SpriteBatch mSpriteBatch;
	private SpriteBatch9Patch m9Patch;
	private boolean mIsInitialised, mIsLoaded;
	private float mZ;
	public boolean drawBackground;

	// =============================================
	// Properties
	// =============================================

	public void entryID(int pNewValue) {
		mMenuEntryID = pNewValue;
	}

	public String entryText() {
		return mText;
	}

	public void entryText(String pNewValue) {
		mText = pNewValue;
	}

	public void buttonSize(BUTTON_SIZE pNewSize) {
		mButtonSize = pNewSize;
	}

	public float entryWidth() {
		return MENUENTRY_WIDTH;
	}

	public float entryHeight() {
		return MENUENTRY_HEIGHT;
	}

	public boolean hoveredOver() {
		return mHoveredOver;
	}

	public void hoveredOver(boolean pNewValue) {
		mHoveredOver = pNewValue;
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean pNewValue) {
		mHasFocus = pNewValue;
	}

	public boolean focusLocked() {
		return mFocusLocked;
	}

	public void focusLocked(boolean pNewValue) {
		mFocusLocked = pNewValue;
	}

	public Rectangle bounds() {
		return mBounds;
	}

	public boolean enabled() {
		return mEnabled;
	}

	public void enabled(boolean pEnabled) {
		mEnabled = pEnabled;
	}

	public int entryID() {
		return mMenuEntryID;
	}

	public Vector2f position() {
		return mPosition;
	}

	// =============================================
	// Constructors
	// =============================================

	public MenuEntry(ScreenManager pScreenManager, MenuScreen pParentScreen, String pMenuEntryLabel) {
		mScreenManager = pScreenManager;
		mDisplayConfig = pScreenManager.displayConfig();
		mParentScreen = pParentScreen;
		mPosition = new Vector2f();
		mText = pMenuEntryLabel;
		mEnabled = true;
		mBounds = new Rectangle();
		mSpriteBatch = new SpriteBatch();
		m9Patch = new SpriteBatch9Patch();
		mZ = -0.5f;
		drawBackground = true;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise() {

		mIsInitialised = true;
	}

	public void loadContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadContent(pResourceManager);
		m9Patch.loadContent(pResourceManager);

		mIsLoaded = true;
	}

	public boolean handleInput(GameTime pGameTime, InputState pInputState) {
		if (bounds().intersects(pInputState.mouseScreenCoords())) {

			if (pInputState.mouseTimedLeftClick()) {
				if (mEnabled) {
					mParentScreen.setFocusOn(pInputState, this, false);

					// play menu click sound
					SoundManager.soundManager().playSound("UI_CLICK");

				}
			} else {

				hasFocus(true);
			}

			mParentScreen.setHoveringOn(this);

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pGameTime.elapseGameTime();
			}

			return true;

		} else {

			hoveredOver(false);
			mToolTipTimer = 0;
		}

		return false;
	}

	public void update(GameTime pGameTime, MenuScreen pScreen, boolean pIsSelected) {

		mClickTimer += pGameTime.elapseGameTime();

		if (mHasFocus) {
			mScaleCounter += pGameTime.elapseGameTime() / 500.0f;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);
		}

		else if (mHoveredOver) {
			mScaleCounter += pGameTime.elapseGameTime() / 500.0f;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);
		}

		else {

			mScale = 0.75f;
		}

		mBounds.mX = (mPosition.x - MENUENTRY_WIDTH * 0.5f);
		mBounds.mY = (mPosition.y - MENUENTRY_HEIGHT * 0.5f);

		mBounds.mWidth = MENUENTRY_WIDTH;
		mBounds.mHeight = MENUENTRY_HEIGHT;

	}

	public void draw(Screen pScreen, DisplayConfig display, boolean pIsSelected) {
		if (!mEnabled || !mIsInitialised || !mIsLoaded)
			return;

		SpriteSheet lSpriteSheet = mScreenManager.resources().spriteSheetManager().getSpriteSheet("menutextures");

		if (drawBackground) {
			m9Patch.begin(mScreenManager.HUD());

			/* Draw the element background */
			float lMenuEntryWidthHalf = MENUENTRY_WIDTH * 0.5f;

			if (mButtonSize == BUTTON_SIZE.narrow) {
				lMenuEntryWidthHalf *= 0.5f;
			} else if (mButtonSize == BUTTON_SIZE.wide) {
				lMenuEntryWidthHalf *= 1.5f;
			}

			// draw 9 patch
			m9Patch.draw9Patch(mPosition.x - lMenuEntryWidthHalf, mPosition.y - MENUENTRY_HEIGHT, mZ, lMenuEntryWidthHalf * 2f, MENUENTRY_HEIGHT * 2f, 0.5f, lSpriteSheet, "panelFancy");
			m9Patch.end();

			/* Draw the button highlight when this element has focus. */
			if (mHasFocus) {
				mSpriteBatch.begin(mScreenManager.HUD());
				mSpriteBatch.draw(lSpriteSheet.getSprite("MenuEntryBackgroundSelected"), mPosition.x - lMenuEntryWidthHalf, mPosition.y - MENUENTRY_HEIGHT, mZ, lMenuEntryWidthHalf * 2f, MENUENTRY_HEIGHT * 2f, lSpriteSheet.texture());
				mSpriteBatch.end();
			}
		}

		/* render the element text */
		mSpriteBatch.modelMatrix(Matrix4f.IDENTITY);
		mSpriteBatch.begin(mScreenManager.HUD());
		mSpriteBatch.draw(mText, mPosition.x - getTextWidth(mText, 0.5f) * 0.5f, mPosition.y - (getTextHeight(mText, 0.5f) - 10) * 0.5f,-0.5f, .5f, TextureManager.textureManager().getTexture("Font"));
		mSpriteBatch.end();

	}

	// =============================================
	// Methods
	// =============================================

	public void setToolTip(String pToolTipText) {
		if (pToolTipText == null || pToolTipText.length() == 0) {
			mToolTipEnabled = false;
			return;
		}

		mToolTipEnabled = true;
		mToolTip = pToolTipText;
	}

	public float getWidth() {
		return MENUENTRY_WIDTH;
	}

	public float getTextWidth(String pText, float pScale) {
		return (pText.length() - 1) * 32f * pScale;
	}

	public float getTextHeight(String pText, float pScale) {
		return 32f * pScale;
	}

	public void registerClickListener(IMenuEntryClickListener pListener, int pID) {
		mMenuEntryID = pID;
		mClickListener = pListener;
	}

	public void onClick(InputState pInputState) {
		if (mClickListener == null || mMenuEntryID == -1)
			return;

		if (mClickTimer < FOCUS_TIMER)
			return;

		mClickListener.onClick(pInputState, mMenuEntryID);
	}
}

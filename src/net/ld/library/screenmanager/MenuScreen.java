package net.ld.library.screenmanager;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.spritebatch.SpriteBatch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.entries.IMenuEntryClickListener;
import net.ld.library.screenmanager.entries.MenuEntry;

public abstract class MenuScreen extends Screen implements IMenuEntryClickListener {

	// =============================================
	// Variables
	// =============================================

	private ArrayList<MenuEntry> mMenuEntries;
	private int mSelectedEntry = 0;
	private String mMenuTitle;
	protected float mEntryOffsetFromTop;
	protected boolean mDisplayTitle;
	protected SpriteBatch mSpriteBatch;

	// =============================================
	// Properties
	// =============================================

	protected ArrayList<MenuEntry> menuEntries() {
		return mMenuEntries;
	}

	protected float transitionOffset() {
		// Make the menu slide into place during transitions, using a
		// power curve to make things look more interesting (this makes
		// the movement slow down as it nears the end).
		return (float) Math.pow(mTransitionPosition, 2) * 256;
	}

	public boolean displayTitle() {
		return mDisplayTitle;
	}

	public void displayTitle(boolean pNewValue) {
		mDisplayTitle = pNewValue;
	}

	// =============================================
	// Constructors
	// =============================================

	public MenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager);

		mMenuTitle = pMenuTitle;

		mMenuEntries = new ArrayList<>();
		mEntryOffsetFromTop = 90.0f;

		mSpriteBatch = new SpriteBatch();
	}

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void initialise() {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).initialise();
		}
	}

	@Override
	public void loadContent(ResourceManager pResourceManager) {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).loadContent(pResourceManager);
		}

		mSpriteBatch.loadContent(pResourceManager);
	}

	@Override
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		final int lCount = menuEntries().size();

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			if (mScreenState == ScreenState.Active) {
				exitScreen();
				return;
			}
		}

		if (menuEntries() == null || menuEntries().size() == 0)
			return; // nothing to do

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_UP)) {
			boolean lFound = false;
			int lIterCount = 0;
			while (!lFound && lIterCount < lCount) {
				mSelectedEntry--;
				if (mSelectedEntry < 0)
					mSelectedEntry = menuEntries().size() - 1;

				// if this new button is not deactivated, then use it
				if (menuEntries().get(mSelectedEntry).enabled()) {
					for (int i = 0; i < lCount; i++) {
						menuEntries().get(i).hasFocus(false);
						menuEntries().get(i).hoveredOver(false);
					}
					menuEntries().get(mSelectedEntry).hasFocus(true);

					lFound = true; // exit while
				}

				lIterCount++;
			}

			// TODO: play sound for menu entry changed
		}

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_DOWN)) {
			boolean lFound = false;
			int lIterCount = 0;
			while (!lFound && lIterCount < lCount) {
				mSelectedEntry++;
				if (mSelectedEntry > menuEntries().size() - 1)
					mSelectedEntry = 0;

				// if this new button is not deactivated, then use it
				if (menuEntries().get(mSelectedEntry).enabled()) {
					for (int i = 0; i < lCount; i++) {
						menuEntries().get(i).hasFocus(false);
						menuEntries().get(i).hoveredOver(false);
					}
					menuEntries().get(mSelectedEntry).hasFocus(true);

					lFound = true; // exit while
				}

				lIterCount++;
			}

			// TODO: play sound for menu entry changed
		}

		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_ENTER)) {

			// TODO: Play menu click sound

			menuEntries().get(mSelectedEntry).onClick(pInputState);

		}

		for (int i = 0; i < lCount; i++) {
			MenuEntry lMenuEntry = menuEntries().get(i);
			if (lMenuEntry.handleInput(pGameTime, pInputState))
				break;
		}
	}

	@Override
	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pGameTime, pOtherScreenHasFocus, pCoveredByOtherScreen);

		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {

			boolean lIsSelected = isActive() && (i == mSelectedEntry);

			mMenuEntries.get(i).update(pGameTime, this, lIsSelected);
		}
	}

	@Override
	public void draw(RenderState pRenderState) {

		// make sure our entries are in the right place before we draw them
		updateMenuEntryLocations(pRenderState);

		// Draw each menu entry in turn.
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			menuEntries().get(i).draw(this, pRenderState.displayConfig(), mSelectedEntry == i);
		}

		if (mDisplayTitle) {
			mSpriteBatch.begin(pRenderState.hudCamera());
			mSpriteBatch.draw(mMenuTitle, 5 + transitionOffset(), 20, -0.5f, 1f, TextureManager.textureManager().getTexture("Font"));
			mSpriteBatch.end();
		}
	}

	// =============================================
	// Methods
	// =============================================

	protected void onCancel() {
		exitScreen();
	}

	protected void updateMenuEntryLocations(RenderState pRenderState) {

		int lWindowWidth = mDisplayConfig.windowWidth();
		int lOffsetFromTop = (int) mEntryOffsetFromTop;

		// Make the menu slide into place during transitions, using a
		// power curve to make things look more interesting (this makes
		// the movement slow down as it nears the end).
		float lTransitionOffset = transitionOffset();

		float lPosY = 20.0f;

		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {

			MenuEntry lEntry = menuEntries().get(i);

			// each entry is centered horizontally
			float lX = lWindowWidth / 2;

			lX += lTransitionOffset;

			lEntry.position().x = lX;
			lEntry.position().y = lPosY + lOffsetFromTop;

			lPosY += (lEntry.entryHeight() + 35);
		}
	}

	public void setFocusOn(InputState pInputState, MenuEntry pMenuEntry, boolean pForce) {

		// If another entry has locked the focus (e.g. input entries), then don't change focus
		if (!pForce && focusLocked())
			return;

		// Set focus to this entry
		pMenuEntry.onClick(pInputState);

		// and disable focus on the rest
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (!menuEntries().get(i).equals(pMenuEntry)) {
				// reset other element focuses
				menuEntries().get(i).hasFocus(false);
				menuEntries().get(i).hoveredOver(false);
			} else {
				// Remember which element we have just focused on
				mSelectedEntry = i;
			}
		}
	}

	public void setHoveringOn(MenuEntry pMenuEntry) {
		if (focusLocked())
			return;

		// if no other entry has locked the focus (e.g. input entries)
		// Set focus to this entry
		pMenuEntry.hoveredOver(true);

		// and disable focus on the rest
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (!menuEntries().get(i).equals(pMenuEntry)) {
				menuEntries().get(i).hasFocus(false);
				menuEntries().get(i).hoveredOver(false);
			} else {
				mSelectedEntry = i;
			}
		}
	}

	public void setFocusOnAll(boolean pValue) {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			menuEntries().get(i).hasFocus(pValue);
		}
	}

	public void setHoveringOnAll(boolean pValue) {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			menuEntries().get(i).hoveredOver(pValue);
		}
	}

	public boolean focusLocked() {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (menuEntries().get(i).focusLocked())
				return true;
		}
		return false;
	}

}

package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class HorizontalEntryGroup extends MenuEntry {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<MenuEntry> mChildEntries;

	public List<MenuEntry> entries() {
		return mChildEntries;
	}

	@Override
	public float getWidth() {
		if (mChildEntries == null || mChildEntries.size() == 0)
			return 0;

		int lCount = mChildEntries.size();
		float lTotalWidth = 0;
		for (int i = 0; i < lCount; i++) {
			lTotalWidth += mChildEntries.get(i).paddingHorizontal();
			lTotalWidth += mChildEntries.get(i).w;
			lTotalWidth += mChildEntries.get(i).paddingHorizontal();
		}

		return lTotalWidth;
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		// if(!pNewValue){
		// int lCount = entries().size();
		// for(int i = 0; i < lCount; i++) {
		// entries().get(i).hasFocus(false);
		// }
		// }
		super.hasFocus(pNewValue);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public HorizontalEntryGroup(ScreenManager pScreenManager, MenuScreen pParentScreen) {
		super(pScreenManager, pParentScreen, "");

		mChildEntries = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).initialise();
		}

		updateEntries();

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).loadGLContent(pResourceManager);
		}
	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).unloadGLContent();
		}
	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersects(pCore.HUD().getMouseCameraSpace())) {
			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				if (mChildEntries.get(i).handleInput(pCore)) {
					return true;
				}

			}

			return true;

		} else {
			mToolTipTimer = 0;

			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				mChildEntries.get(i).hasFocus(false);
				mChildEntries.get(i).hoveredOver(false);

			}

		}

		return false;

	}

	@Override
	public void updateStructure() {
		super.initialise();

		updateEntries();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).updateStructure();
		}
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).update(pCore, pScreen, pIsSelected);
		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			mTextureBatch.begin(pCore.HUD());
			final float ALPHA = 0.3f;
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, pParentZDepth + .1f, 0.5f * mParentScreen.r(), 0.2f * mParentScreen.g(), ALPHA * mParentScreen.b(), mParentScreen.a());
			mTextureBatch.end();

		}

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).draw(pCore, pScreen, pIsSelected, pParentZDepth);

		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateEntries() {
		// Here we will use the position given to us by the parent screen and use it
		// to orientation our children (for now just horizontally).
		if (mChildEntries == null || mChildEntries.size() == 0)
			return;

		int lCount = mChildEntries.size();
		float lTotalWidth = 0;
		float lTotalHeight = 0;
		for (int i = 0; i < lCount; i++) {
			lTotalWidth += mChildEntries.get(i).paddingHorizontal();
			lTotalWidth += mChildEntries.get(i).w;
			lTotalWidth += mChildEntries.get(i).paddingHorizontal();

			if (mChildEntries.get(i).h + mChildEntries.get(i).paddingVertical() * 2 > lTotalHeight) {
				lTotalHeight = mChildEntries.get(i).h + mChildEntries.get(i).paddingVertical() * 2;
			}
		}

		final float lHSpace = lTotalWidth / lCount;

		for (int i = 0; i < lCount; i++) {

			// we have to manually take away the half screen width because the entries on
			// the menu screen are placed in the middle.
			float lPosX = x + mChildEntries.get(i).paddingHorizontal() + lHSpace * i;
			float lPosY = y;

			mChildEntries.get(i).x = lPosX;
			mChildEntries.get(i).y = lPosY + mChildEntries.get(i).paddingVertical();

		}

		w = lTotalWidth;
		h = lTotalHeight;
	}

	public void addEntry(MenuEntry pEntry) {
		mChildEntries.add(pEntry);
	}

	public void removeEntry(MenuEntry pEntry) {
		if (mChildEntries.contains(pEntry)) {
			mChildEntries.remove(pEntry);
		}
	}

}

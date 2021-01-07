package net.lintford.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.renderers.windows.UiWindow;

public class UIHorizontalEntryGroup extends UIWidget {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static final long serialVersionUID = 4358093208729572051L;

	private List<UIWidget> mChildEntries;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<UIWidget> entries() {
		return mChildEntries;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIHorizontalEntryGroup(final UiWindow pParentWindow) {
		super(pParentWindow);

		mChildEntries = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).initialize();
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
		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				if (mChildEntries.get(i).handleInput(pCore)) {
					return true;
				}

			}

			return true;

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		updateEntries();

		int lCount = mChildEntries.size();
		for (int i = 0; i < lCount; i++) {
			mChildEntries.get(i).update(pCore);
		}

	}

	@Override
	public void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y, w, h, pComponentZDepth + .1f, ColorConstants.WHITE);

		}

		final int lNumChildEntries = mChildEntries.size();
		for (int i = 0; i < lNumChildEntries; i++) {
			mChildEntries.get(i).draw(pCore, pTextureBatch, pUITexture, pTextFont, pComponentZDepth + 0.01f);

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

		final int PADDING = 15;

		int lCount = mChildEntries.size();
		float lTotalWidth = 0;
		float lTotalHeight = 0;
		for (int i = 0; i < lCount; i++) {
			lTotalWidth += PADDING;
			lTotalWidth += mChildEntries.get(i).w();
			lTotalWidth += PADDING;

			if (mChildEntries.get(i).h() + PADDING * 2 > lTotalHeight) {
				lTotalHeight = mChildEntries.get(i).h() + PADDING * 2;
			}
		}

		final float lHSpace = lTotalWidth / lCount;

		for (int i = 0; i < lCount; i++) {
			// we have to manually take away the half screen width because the entries on
			// the menu screen are placed in the middle.
			final float lPosX = x + PADDING + lHSpace * i;
			final float lPosY = y + PADDING;

			mChildEntries.get(i).setPosition(lPosX, lPosY);

		}

		w = lTotalWidth;
		h = lTotalHeight;
	}

}

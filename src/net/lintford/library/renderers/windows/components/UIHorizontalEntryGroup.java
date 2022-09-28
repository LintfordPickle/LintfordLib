package net.lintford.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
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

	public UIHorizontalEntryGroup(final UiWindow parentWindow) {
		super(parentWindow);

		mChildEntries = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final int lChildEntryCount = mChildEntries.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildEntries.get(i).initialize();
		}

		updateEntries();
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		final int lChildEntryCount = mChildEntries.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildEntries.get(i).loadResources(resourceManager);
		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		final int lChildEntryCount = mChildEntries.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildEntries.get(i).unloadResources();
		}
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			int lCount = mChildEntries.size();
			for (int i = 0; i < lCount; i++) {
				if (mChildEntries.get(i).handleInput(core)) {
					return true;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		updateEntries();

		final int lChildEntryCount = mChildEntries.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildEntries.get(i).update(core);
		}
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, componentZDepth + .1f, ColorConstants.WHITE);
		}

		final int lNumChildEntries = mChildEntries.size();
		for (int i = 0; i < lNumChildEntries; i++) {
			mChildEntries.get(i).draw(core, spriteBatch, coreSpritesheetDefinition, textFont, componentZDepth + 0.01f);
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

		final float lPadding = 15.f;

		final int lChildEntryCount = mChildEntries.size();
		float lTotalWidth = 0;
		float lTotalHeight = 0;
		for (int i = 0; i < lChildEntryCount; i++) {
			lTotalWidth += lPadding;
			lTotalWidth += mChildEntries.get(i).width();
			lTotalWidth += lPadding;

			if (mChildEntries.get(i).height() + lPadding * 2 > lTotalHeight) {
				lTotalHeight = mChildEntries.get(i).height() + lPadding * 2;
			}
		}

		final float lHSpace = lTotalWidth / lChildEntryCount;
		for (int i = 0; i < lChildEntryCount; i++) {
			// we have to manually take away the half screen width because the entries on
			// the menu screen are placed in the middle.
			final float lPosX = mX + lPadding + lHSpace * i;
			final float lPosY = mY + lPadding;

			mChildEntries.get(i).setPosition(lPosX, lPosY);
		}

		mW = lTotalWidth;
		mH = lTotalHeight;
	}

}

package net.lintford.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;

public class UiHorizontalEntryGroup extends UIWidget {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static final long serialVersionUID = 4358093208729572051L;

	private List<UIWidget> mChildWidgets;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<UIWidget> widgets() {
		return mChildWidgets;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiHorizontalEntryGroup(final UiWindow parentWindow) {
		super(parentWindow);

		mChildWidgets = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final int lChildEntryCount = mChildWidgets.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildWidgets.get(i).initialize();
		}

		updateWidgets();
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		final int lChildEntryCount = mChildWidgets.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildWidgets.get(i).loadResources(resourceManager);
		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		final int lChildEntryCount = mChildWidgets.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildWidgets.get(i).unloadResources();
		}
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			int lCount = mChildWidgets.size();
			for (int i = 0; i < lCount; i++) {
				if (mChildWidgets.get(i).handleInput(core)) {
					return true;
				}
			}

			return true;
		} else {
			final int lChildEntryCount = mChildWidgets.size();
			for (int i = 0; i < lChildEntryCount; i++) {
				mChildWidgets.get(i).isHoveredOver(false);
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		updateWidgets();

		final int lChildEntryCount = mChildWidgets.size();
		for (int i = 0; i < lChildEntryCount; i++) {
			mChildWidgets.get(i).update(core);
		}
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, -0.01f, ColorConstants.Debug_Transparent_Magenta);
		}

		final int lNumChildEntries = mChildWidgets.size();
		for (int i = 0; i < lNumChildEntries; i++) {
			mChildWidgets.get(i).draw(core, spriteBatch, coreSpritesheetDefinition, textFont, componentZDepth + 0.01f);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateWidgets() {
		if (mChildWidgets == null || mChildWidgets.size() == 0)
			return;

		final int lChildEntryCount = mChildWidgets.size();

		final float lContentWidth = mW;
		final float lSpacingW = 5.0f;
		final float lWidgetWidth = (lContentWidth / lChildEntryCount) - lSpacingW * 2.f;

		for (int i = 0; i < lChildEntryCount; i++) {
			final var lWidget = mChildWidgets.get(i);
			lWidget.setPosition(mX + i * lWidgetWidth + lSpacingW * 2 * i, mY);
			lWidget.width(lWidgetWidth);
			lWidget.height(mH);
		}
	}
}

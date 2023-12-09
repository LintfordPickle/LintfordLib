package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiHorizontalEntryGroup extends UIWidget {

	public enum SPACING_TYPE {
		even, weighted
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private static final long serialVersionUID = 4358093208729572051L;

	private List<UIWidget> mChildWidgets;
	private SPACING_TYPE mSpacingType = SPACING_TYPE.even;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SPACING_TYPE spacingType() {
		return mSpacingType;
	}

	public void spacingType(SPACING_TYPE type) {
		if (type == null)
			return;

		mSpacingType = type;
	}

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
			spriteBatch.begin(core.HUD());
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, -0.01f, ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.end();
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

		switch (mSpacingType) {
		default:
		case even:
			arrangeWidgetsEvenly();
			break;
		case weighted:
			arrangeWidgetsWeighted();
			break;
		}
	}

	private void arrangeWidgetsEvenly() {
		if (mChildWidgets == null || mChildWidgets.size() == 0)
			return;

		final int lChildEntryCount = mChildWidgets.size();

		final float lInnerPadding = 5.f;
		final float lContentWidth = mW + lInnerPadding;
		final float lWidgetWidth = (lContentWidth / lChildEntryCount) - lInnerPadding;

		for (int i = 0; i < lChildEntryCount; i++) {
			final var lWidget = mChildWidgets.get(i);
			lWidget.setPosition(mX + (i * lWidgetWidth) + (i * lInnerPadding), mY);
			lWidget.width(lWidgetWidth);
			lWidget.height(mH);
		}
	}

	private void arrangeWidgetsWeighted() {
		if (mChildWidgets == null || mChildWidgets.size() == 0)
			return;

		final int lChildEntryCount = mChildWidgets.size();
		float lWeightTotal = 0.f;
		for (int i = 0; i < lChildEntryCount; i++) {
			lWeightTotal += mChildWidgets.get(i).layoutWeight();
		}
		final float lWeightCoefficient = lWeightTotal == 0.f ? 0.f : 1.f / lWeightTotal;

		final float lContentWidth = mW;
		final float lSpacingW = 5.0f;

		final float lAdjustedContentWidth = lContentWidth - lSpacingW * 2.f - lChildEntryCount * lSpacingW;
		float xx = mX + lSpacingW;
		for (int i = 0; i < lChildEntryCount; i++) {
			final var lWidget = mChildWidgets.get(i);

			final float lAdjustedWeight = lWidget.layoutWeight() * lWeightCoefficient;
			final float lAdjustedWidth = lAdjustedContentWidth * lAdjustedWeight;
			lWidget.setPosition(xx, mY);
			lWidget.width(lAdjustedWidth);
			lWidget.height(mH);

			xx += lAdjustedWidth + lSpacingW;
		}
	}
}

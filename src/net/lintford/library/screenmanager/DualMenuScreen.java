package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class DualMenuScreen extends MenuScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<BaseLayout> mRightLayouts;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DualMenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager, pMenuTitle);

		mRightLayouts = new ArrayList<>();

		mPaddingLeftNormalized = 0.f;
		mPaddingRightNormalized = 0.f;
		mPaddingTopNormalized = 0.f;
		mPaddingBottomNormalized = 0.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final int lCount = mRightLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mRightLayouts.get(i).initialize();
		}

	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		final int lLayoutCount = mRightLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mRightLayouts.get(i).loadResources(pResourceManager);
		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		final int lCount = mRightLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mRightLayouts.get(i).unloadResources();

		}

	}

	@Override
	public void handleInput(LintfordCore pCore) {
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		if (mESCBackEnabled && pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			if (mScreenState == ScreenState.Active) {
				exitScreen();
				return;
			}
		}

		if (mLayouts.size() == 0 && mRightLayouts.size() == 0)
			return; // nothing to do

		// Respond to UP key
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP)) {

			if (mSelectedEntry > 0) {
				mSelectedEntry--; //
			}

			final var lLayout = mLayouts.get(mSelectedLayout);

			if (!lLayout.hasEntry(mSelectedEntry))
				return;

			// Set focus on the new entry
			if (lLayout.getMenuEntryByIndex(mSelectedEntry).enabled()) {
				lLayout.updateFocusOffAllBut(pCore, lLayout.getMenuEntryByIndex(mSelectedEntry));
				lLayout.getMenuEntryByIndex(mSelectedEntry).hasFocus(true);
			}

			// TODO: play sound for menu entry changed

		}

		// Respond to DOWN key
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN)) {
			final var lLayout = mLayouts.get(mSelectedLayout);

			if (mSelectedEntry < lLayout.getMenuEntryCount() - 1) {
				mSelectedEntry++;
			}

			if (!lLayout.hasEntry(mSelectedEntry))
				return;

			// Set focus on the new entry
			if (lLayout.getMenuEntryByIndex(mSelectedEntry).enabled()) {
				lLayout.updateFocusOffAllBut(pCore, lLayout.getMenuEntryByIndex(mSelectedEntry));
				lLayout.getMenuEntryByIndex(mSelectedEntry).hasFocus(true);
			}

			// TODO: play sound for menu entry changed

		}

		// Process ENTER key press
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER)) {
			final var lLayout = mLayouts.get(mSelectedLayout);
			if (!lLayout.hasEntry(mSelectedEntry))
				return;

			lLayout.getMenuEntryByIndex(mSelectedEntry).onClick(pCore.input());
		}

		// Process Mouse input
		int lLeftLayoutCount = mLayouts.size();
		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lLayout = mLayouts.get(i);
			lLayout.handleInput(pCore);
		}

		int lRightLayoutCount = mRightLayouts.size();
		for (int i = 0; i < lRightLayoutCount; i++) {
			final var lLayout = mRightLayouts.get(i);
			lLayout.handleInput(pCore);
		}

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.handleInput(pCore);

		}

		footerLayout().handleInput(pCore);

		return;
	}

	@Override
	public void updateLayoutSize(LintfordCore pCore) {
		final int lLeftCount = mLayouts.size();
		for (int i = 0; i < lLeftCount; i++) {
			mLayouts.get(i).layoutWidth(LAYOUT_WIDTH.HALF);
		}

		updateLayout(pCore, mLayouts, LAYOUT_ALIGNMENT.LEFT);

		final int lRightCount = mRightLayouts.size();
		for (int i = 0; i < lRightCount; i++) {
			mRightLayouts.get(i).layoutWidth(LAYOUT_WIDTH.HALF);
		}

		updateLayout(pCore, mRightLayouts, LAYOUT_ALIGNMENT.RIGHT);

		// *** FOOTER *** //
		final var lHUDController = mRendererManager.uiStructureController();

		final float lWidthOfPage = lHUDController.menuFooterRectangle().width();
		final float lHeightOfPage = lHUDController.menuFooterRectangle().height();

		final float lLeftOfPage = lHUDController.menuFooterRectangle().centerX() - lWidthOfPage / 2;
		final float lTopOfPage = lHUDController.menuFooterRectangle().top();

		footerLayout().set(lLeftOfPage, lTopOfPage, lWidthOfPage, lHeightOfPage);
		footerLayout().updateStructure();
	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		final int lCount = mRightLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mRightLayouts.get(i).update(pCore);
		}
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		// Because we're not called super.draw, we need to manually call mRenderManager.draw
		mRendererManager.draw(pCore);

		final float lMenuScreenZDepth = ZLayers.LAYER_SCREENMANAGER;

		drawMenuTitle(pCore);

		// Draw each layout in turn.
		final int lLeftCount = mLayouts.size();
		for (int i = 0; i < lLeftCount; i++) {
			mLayouts.get(i).draw(pCore, lMenuScreenZDepth + (i * 0.001f));
		}

		final int lRightCount = mRightLayouts.size();
		for (int i = 0; i < lRightCount; i++) {
			mRightLayouts.get(i).draw(pCore, lMenuScreenZDepth + (i * 0.001f));
		}

		footerLayout().draw(pCore, lMenuScreenZDepth);

		final int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.draw(pCore, this, false, lMenuScreenZDepth + (i * 0.001f));
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		// TODO Auto-generated method stub

	}

}

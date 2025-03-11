package net.lintfordlib.renderers.editor.panels;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.RendererManagerBase;
import net.lintfordlib.renderers.windows.UiWindow;

public abstract class UiDockedWindow extends UiWindow {

	public enum UiDockOrientation {
		Left, Right,
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static int DOCKED_WINDOW_WIDTH = 260;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<UiPanel> mEditorPanels;
	private UiDockOrientation mOrientation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<UiPanel> editorPanels() {
		return mEditorPanels;
	}

	public void orientation(UiDockOrientation newOrientation) {
		if (newOrientation == null)
			return;

		mOrientation = newOrientation;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiDockedWindow(RendererManagerBase rendererManager, String rendererName, int entityGroupUid) {
		super(rendererManager, rendererName, entityGroupUid);

		mOrientation = UiDockOrientation.Right;

		mEditorPanels = new ArrayList<>();
		mIsWindowMoveable = false;
		mScrollBar.autoHide(false);

		isOpen(true);

		createGuiPanels();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		initializeGuiPanels(core);

		super.initialize(core);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		// TODO: Load editor specific resources - allowing hooking into this

		loadPanelResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		unloadPanelResources();
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		final int lNumPanels = mEditorPanels.size();
		for (int i = 0; i < lNumPanels; i++) {
			final var lPanel = mEditorPanels.get(i);

			lPanel.handleInput(core);
		}

		final var lMouseX = core.HUD().getMouseWorldSpaceX();
		final var lMouseY = core.HUD().getMouseWorldSpaceY();

		boolean editorResult = super.handleInput(core);

		if (mWindowArea.intersectsAA(lMouseX, lMouseY)) {
			if (core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
				// prevent futher renderers from using clicks over the editor window
			}
		}

		return editorResult;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		arrangePanels(core);

		mContentDisplayArea.set(mWindowArea);

		final int lNumPanels = mEditorPanels.size();
		for (int i = 0; i < lNumPanels; i++) {
			final var lPanel = mEditorPanels.get(i);

			lPanel.update(core);
		}
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		super.draw(core, renderPass);

		final int lNumPanels = mEditorPanels.size();
		for (int i = 0; i < lNumPanels; i++) {
			final var lPanel = mEditorPanels.get(i);

			lPanel.draw(core);
		}
	}

	// --------------------------------------

	// EditorGui arranges the panels down the height of the window
	private void arrangePanels(LintfordCore core) {
		float currentPositionX = mWindowArea.x() + 5.f;
		float lTitlebarHeight = mRenderWindowTitle ? mTitleBarHeight : 0.f;
		float currentPositionY = mScrollBar.currentYPos() + mWindowArea.y() + lTitlebarHeight + 5.f;

		float panelWidth = mWindowArea.width() - 5.f * 2.f - mScrollBar.width();
		float lTotalContentHeight = lTitlebarHeight;

		final int lNumPanels = mEditorPanels.size();
		for (int i = 0; i < lNumPanels; i++) {
			final var lPanel = mEditorPanels.get(i);

			lPanel.mPanelArea.setPosition(currentPositionX, currentPositionY);
			lPanel.mPanelArea.width(panelWidth);

			final float lPanelHeight = lPanel.getPanelFullHeight();
			lPanel.mPanelArea.height(lPanelHeight);
			lTotalContentHeight += lPanelHeight + 15.f;

			currentPositionY += lPanel.getPanelFullHeight() + 15.f;
		}

		mFullContentRectangle.height(lTotalContentHeight);
	}

	// --------------------------------------

	@Override
	public void updateWindowPosition(LintfordCore core) {
		super.updateWindowPosition(core);

		final var lHudBounds = core.HUD().boundingRectangle();
		final var lGuiWidth = DOCKED_WINDOW_WIDTH;

		if (mOrientation == UiDockOrientation.Left) {
			mWindowArea.x(lHudBounds.left());
			mWindowArea.y(lHudBounds.top());

			mWindowArea.width(lGuiWidth);
			mWindowArea.height(lHudBounds.height());
		} else { // right
			mWindowArea.x(lHudBounds.right() - lGuiWidth);
			mWindowArea.y(lHudBounds.top());

			mWindowArea.width(lGuiWidth);
			mWindowArea.height(lHudBounds.height());
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void createGuiPanels() {

	}

	private void initializeGuiPanels(LintfordCore core) {
		final int lNumPanels = mEditorPanels.size();
		for (int i = 0; i < lNumPanels; i++) {
			final var lPanel = mEditorPanels.get(i);

			lPanel.initialize(core);
		}
	}

	private void loadPanelResources(ResourceManager resourceManager) {
		final int lNumPanels = mEditorPanels.size();
		for (int i = 0; i < lNumPanels; i++) {
			final var lPanel = mEditorPanels.get(i);

			lPanel.loadResources(resourceManager);
		}
	}

	private void unloadPanelResources() {
		final int lNumPanels = mEditorPanels.size();
		for (int i = 0; i < lNumPanels; i++) {
			final var lPanel = mEditorPanels.get(i);

			lPanel.unloadResources();
		}
	}
}

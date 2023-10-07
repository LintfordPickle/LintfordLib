package net.lintfordlib.controllers.debug;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;
import net.lintfordlib.renderers.windows.UiWindow;

public class DebugRendererTreeController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "RendererTreeController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected RendererManager mRendererManager;
	protected final List<BaseRendererWidget> mDebugTreeComponents;
	protected int mCountAtLastUpdate = -1;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<BaseRendererWidget> treeComponents() {
		return mDebugTreeComponents;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugRendererTreeController(ControllerManager controllerManager, int controllerGroup) {
		super(controllerManager, CONTROLLER_NAME, controllerGroup);

		mDebugTreeComponents = new ArrayList<BaseRendererWidget>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void trackRendererManager(RendererManager rendererManager) {
		mRendererManager = rendererManager;

		unloadController();

		if (mRendererManager != null && mRendererManager.equals(rendererManager)) {
			// TODO: Unload current RendererManager ...
		}

		if (mRendererManager != null) {
			final List<BaseRenderer> lListOfBaseRenderers = mRendererManager.baseRenderers();
			maintainRendererWidgetList(lListOfBaseRenderers);

			final List<UiWindow> lListOfBaseWindowRenderers = mRendererManager.windows();
			maintainWindowRendererWidgetList(lListOfBaseWindowRenderers);
		} else {
			clearRendererWidgetList();
		}
	}

	@Override
	public void unloadController() {
		mDebugTreeComponents.clear();
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (!isInitialized())
			return;

		final var lBaseRendererList = mRendererManager.baseRenderers();
		final var lBaseWindowRendererList = mRendererManager.windows();

		maintainRendererWidgetList(lBaseRendererList);
		maintainWindowRendererWidgetList(lBaseWindowRendererList);

		final var lControllerWidgetCount = mDebugTreeComponents.size();
		for (int i = 0; i < lControllerWidgetCount; i++) {
			final var lWidget = mDebugTreeComponents.get(i);

			if (lWidget.baseRenderer == null) {
				// FIXME: Remove the RendererWidget from the list, the BaseRenderer attached has been destroyed.

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// makes sure that every controller gets its own rendering widget
	private void maintainRendererWidgetList(final List<BaseRenderer> renderers) {
		int lPositionCounter = 0;

		final var lNumBaseRenderers = renderers.size();
		for (var i = 0; i < lNumBaseRenderers; i++) {
			final var lRenderer = renderers.get(i);

			if (lRenderer == null)
				continue;

			if (!debugTreeContainsRendererId(lRenderer.rendererId())) {
				addBaseRendererToDebugTree(lRenderer, lPositionCounter, 0);
				lPositionCounter++;
			}
		}
	}

	private void maintainWindowRendererWidgetList(final List<UiWindow> windowRenderers) {
		int lPositionCounter = 0;

		final var lNumBaseRenderers = windowRenderers.size();
		for (var i = 0; i < lNumBaseRenderers; i++) {
			final var lRenderer = windowRenderers.get(i);

			if (lRenderer == null)
				continue;

			if (!debugTreeContainsRendererId(lRenderer.rendererId())) {
				addBaseRendererToDebugTree(lRenderer, lPositionCounter, 0);
				lPositionCounter++;
			}
		}
	}

	private void clearRendererWidgetList() {

	}

	private boolean debugTreeContainsRendererId(final int rendererId) {
		final int lNumBaseControllerAreas = mDebugTreeComponents.size();
		for (var i = 0; i < lNumBaseControllerAreas; i++) {
			if (mDebugTreeComponents.get(i).rendererId == rendererId)
				return true;
		}

		return false;
	}

	private void addBaseRendererToDebugTree(BaseRenderer renderer, final int atIndex, final int indentation) {
		if (renderer == null)
			return;

		final var lNewDebugArea = new BaseRendererWidget();
		lNewDebugArea.rendererId = renderer.rendererId();
		lNewDebugArea.baseRenderer = renderer;
		lNewDebugArea.displayName = renderer.rendererName();
		lNewDebugArea.rendererLevel = indentation;

		mDebugTreeComponents.add(lNewDebugArea);
	}

	public void addDebugComponent() {

	}
}

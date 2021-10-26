package net.lintford.library.controllers.debug;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.windows.UiWindow;

public class DebugRendererTreeController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "RendererTreeController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected RendererManager mRendererManager;
	protected List<BaseRendererWidget> mDebugTreeComponents;
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

	public DebugRendererTreeController(ControllerManager pControllerManager, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		mDebugTreeComponents = new ArrayList<BaseRendererWidget>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void trackRendererManager(RendererManager pRendererManager) {
		mRendererManager = pRendererManager;

		if (mRendererManager != null && mRendererManager.equals(pRendererManager)) {
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
	public void unload() {
		if (mDebugTreeComponents != null) {
			mDebugTreeComponents.clear();
			mDebugTreeComponents = null;

		}

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

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
	private void maintainRendererWidgetList(final List<BaseRenderer> pRenderers) {
		int lPositionCounter = 0;

		final var lNumBaseRenderers = pRenderers.size();
		for (var i = 0; i < lNumBaseRenderers; i++) {
			final var lRenderer = pRenderers.get(i);

			if (lRenderer == null)
				continue;

			if (!debugTreeContainsRendererId(lRenderer.rendererId())) {
				addBaseRendererToDebugTree(lRenderer, lPositionCounter, 0);
				lPositionCounter++;
			}

		}

	}

	private void maintainWindowRendererWidgetList(final List<UiWindow> pWindowRenderers) {
		int lPositionCounter = 0;

		final var lNumBaseRenderers = pWindowRenderers.size();
		for (var i = 0; i < lNumBaseRenderers; i++) {
			final var lRenderer = pWindowRenderers.get(i);

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

	private boolean debugTreeContainsRendererId(final int pRendererId) {
		final int lNumBaseControllerAreas = mDebugTreeComponents.size();
		for (var i = 0; i < lNumBaseControllerAreas; i++) {
			if (mDebugTreeComponents.get(i).rendererId == pRendererId)
				return true;
		}
		return false;

	}

	private void addBaseRendererToDebugTree(BaseRenderer pRenderer, final int pAtIndex, final int pIndentation) {
		if (pRenderer == null)
			return;

		final var lNewDebugArea = new BaseRendererWidget();
		lNewDebugArea.rendererId = pRenderer.rendererId();
		lNewDebugArea.baseRenderer = pRenderer;
		lNewDebugArea.displayName = pRenderer.rendererName();
		lNewDebugArea.rendererLevel = pIndentation;

		mDebugTreeComponents.add(lNewDebugArea);
	}

	public void addDebugComponent() {

	}

}

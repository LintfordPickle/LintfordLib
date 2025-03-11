package net.lintfordlib.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.windows.UiWindow;

public class SimpleRendererManager extends RendererManagerBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NO_WINDOW_INDEX = -1;
	public static final int WINDOW_ALREADY_REGISTERED = -2;

	/** This refers to the BaseRenderers responsible for rendering the game components. */
	public static final boolean RENDER_GAME_RENDERABLES = true;

	/** This refers to the BaseRenderers responsible for rendering the UI components. */
	public static final boolean RENDER_UI_WINDOWS = true;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<UiWindow> mWindowRenderers;

	// TODO: Need a way to be able to set the render pass indices to use for rendering 'all-passes'.
	private int[] renderpasses = new int[] { RenderPass.RENDER_PASS_DEFAULT, RenderPass.RENDER_PASS_COLOR0 };

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<UiWindow> windows() {
		return mWindowRenderers;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SimpleRendererManager(LintfordCore core, int entityGroupUid) {
		super(core, entityGroupUid);

		mWindowRenderers = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initializeRenderers() {
		super.initializeRenderers();

		final int lUiRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lUiRendererCount; i++) {
			mWindowRenderers.get(i).initialize(mCore);
		}
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		final int lUiRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lUiRendererCount; i++) {
			if (!mWindowRenderers.get(i).isLoaded()) {
				mWindowRenderers.get(i).loadResources(resourceManager);
			}
		}
	}

	@Override
	public void unloadRenderTarget(RenderTarget renderTarget) {
		super.unloadRenderTarget(renderTarget);

		final int luiRendererCount = mWindowRenderers.size();
		for (int i = 0; i < luiRendererCount; i++) {
			mWindowRenderers.get(i).unloadResources();
		}
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		final int lNumWindowRenderers = mWindowRenderers.size();

		// We handle the input to the UI Windows in the game with priority.
		for (int i = 0; i < lNumWindowRenderers; i++) {
			final var lWindow = mWindowRenderers.get(i);
			final var lResult = lWindow.handleInput(core);
			if (lResult && lWindow.exclusiveHandleInput()) {
				// return true;
			}
		}

		// Handle the base renderer input
		final int lNumRenderers = mRenderers.size();
		for (int i = lNumRenderers - 1; i >= 0; i--) {
			mRenderers.get(i).handleInput(core);
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final int lWindowRendererCount = mWindowRenderers.size();
		for (int i = 0; i < lWindowRendererCount; i++) {
			final var lWindowRenderer = mWindowRenderers.get(i);
			if (!lWindowRenderer.isActive())
				continue;

			lWindowRenderer.update(core);
		}
	}

	@Override
	public void draw(LintfordCore core) {
		if (RENDER_GAME_RENDERABLES) {
			drawRenderersAllPasses(core);
		}

		if (RENDER_UI_WINDOWS) {
			drawWindowRenderers(core);
		}
	}

	public void drawRenderersWithPass(LintfordCore core, RenderPass renderPass) {
		final int lNumBaseRenderers = mRenderers.size();

		for (int j = 0; j < lNumBaseRenderers; j++) {
			final var lRenderer = mRenderers.get(j);
			if (!lRenderer.isActive() || !lRenderer.isManagedDraw())
				continue;

			lRenderer.draw(core, renderPass);
		}
	}

	public void drawRenderersAllPasses(LintfordCore core) {

		if (renderpasses == null || renderpasses.length == 0)
			return;

		final int lNumBaseRenderers = mRenderers.size();
		final var lNumRenderPasses = renderpasses.length;
		for (int i = 0; i < lNumRenderPasses; i++) {
			final var lRenderPass = RenderPass.getRenderPass(renderpasses[i]);

			for (int j = 0; j < lNumBaseRenderers; j++) {
				final var lRenderer = mRenderers.get(j);
				if (!lRenderer.isActive() || !lRenderer.isManagedDraw())
					continue;

				lRenderer.draw(core, lRenderPass);
			}
		}
	}

	public void drawWindowRenderers(LintfordCore core) {
		final int lNumWindowRenderers = mWindowRenderers.size();
		for (int i = 0; i < lNumWindowRenderers; i++) {
			final var lWindow = mWindowRenderers.get(i);
			if (!lWindow.isActive() || !lWindow.isOpen())
				continue;

			lWindow.draw(core, RenderPass.COLOR0);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Adds a renderer to the manager, differentiating between UiRenderer and BaseRenderer instances. This automatically re-orders the renderers to take into consideration their relative z-depths.
	 */
	@Override
	public void addRenderer(BaseRenderer renderer) {
		if (getRenderer(renderer.rendererName()) == null) {
			if (renderer instanceof UiWindow) {
				mWindowRenderers.add((UiWindow) renderer);
				Collections.sort(mWindowRenderers, new ZLayerComparator());
			}

			else {
				mRenderers.add(renderer);
				Collections.sort(mRenderers, new ZLayerComparator());
			}

		} else {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot add the same renderer twice! (" + renderer.getClass().getSimpleName() + "/" + renderer.mRendererName + ")");
		}
	}

	@Override
	public BaseRenderer getRenderer(String rendererName) {
		final var lFoundRenderer = super.getRenderer(rendererName);
		if (lFoundRenderer != null)
			return lFoundRenderer;

		final int lNumUiRenderers = mWindowRenderers.size();
		for (int i = 0; i < lNumUiRenderers; i++) {
			if (mWindowRenderers.get(i).rendererName().equals(rendererName)) {
				return mRenderers.get(i);
			}
		}

		Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot find (ui) renderer with the name: '" + rendererName + "'.");
		return null;
	}

}
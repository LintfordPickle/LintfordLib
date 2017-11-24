package net.ld.library.core.graphics.rendertarget;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.config.IResizeListener;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.texturebatch.TextureBatch;

public class RenderTargetManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<RenderTarget> mRenderTargets;
	private List<RenderTarget> mRenderTargetAutoResize;
	private RenderTarget mCurrentTarget;

	private TextureBatch mSpriteBatch;
	private DisplayConfig mDisplayConfig;

	private boolean mIsLoaded;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public RenderTargetManager() {
		mRenderTargets = new ArrayList<>();
		mRenderTargetAutoResize = new ArrayList<>();

		mSpriteBatch = new TextureBatch();

		mIsLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		mSpriteBatch.loadGLContent(pResourceManager);

		// Some windows will use this to orientate themselves to the window
		mDisplayConfig = pResourceManager.displayConfig();

		// Register a window resize listener so we can reload the RenderTargets when the
		// window size changes
		mDisplayConfig.addResizeListener(new IResizeListener() {

			@Override
			public void onResize(final int pWidth, final int pHeight) {
				reloadRenderTargets(pWidth, pHeight);

			}

		});

		mIsLoaded = true;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public RenderTarget setRenderTarget(String pName) {
		if (pName == null) {
			if (mCurrentTarget != null) {
				mCurrentTarget.unbind();
				return null;
			}
		}

		RenderTarget lResult = getRenderTarget(pName);

		if (lResult != null) {
			if (mCurrentTarget != null) {
				mCurrentTarget.unbind();

			}

		}

		lResult.bind();
		
		return lResult;

	}

	public RenderTarget createRenderTarget(String pName, int pWidth, int pHeight, boolean pResizeWithWindow) {
		// First check to see if the render target exists
		RenderTarget lResult = getRenderTarget(pName);

		if (lResult != null)
			return lResult;

		lResult = new RenderTarget();
		lResult.loadGLContent(pWidth, pHeight);
		lResult.targetName = pName;

		mRenderTargets.add(lResult);

		System.out.println("Rendertargets size: " + mRenderTargets.size());

		if (pResizeWithWindow) {
			mRenderTargetAutoResize.add(lResult);

		}

		return lResult;

	}

	public void releaseRenderTarget(String pName) {
		RenderTarget lResult = getRenderTarget(pName);

		if (lResult != null) {
			if (mRenderTargetAutoResize.contains(lResult)) {
				mRenderTargetAutoResize.remove(lResult);

			}

			lResult.unbind();
			lResult.unloadGLContent();

			mRenderTargets.remove(lResult);

		}
	}

	public RenderTarget getRenderTarget(String pName) {
		final int RENDER_TARGET_COUNT = mRenderTargets.size();
		for (int i = 0; i < RENDER_TARGET_COUNT; i++) {
			if (mRenderTargets.get(i).targetName.equals(pName)) {
				return mRenderTargets.get(i);

			}

		}

		return null;
	}

	public void reloadRenderTargets(final int pWidth, final int pHeight) {
		final int RENDER_TARGET_COUNT = mRenderTargetAutoResize.size();
		for (int i = 0; i < RENDER_TARGET_COUNT; i++) {
			mRenderTargetAutoResize.get(i).resize(pWidth, pHeight);

		}

	}

}

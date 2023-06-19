package net.lintford.library.core.splitscreen;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.maths.Vector2f;

public class PlayerSessionViewContainer {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final Rectangle mViewport = new Rectangle();

	private ICamera mPlayerCamera;
	private RenderTarget mRenderTarget;

	public final Vector2f desiredPosition = new Vector2f();
	public final Vector2f velocity = new Vector2f();
	public final Vector2f position = new Vector2f();
	public final Vector2f lookAhead = new Vector2f();

	public float cameraZoomFactor;
	public float cameraZoomVelocity;

	private boolean mIsInitialized;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public Rectangle viewport() {
		return mViewport;
	}

	public ICamera playerCamera() {
		return mPlayerCamera;
	}

	public RenderTarget renderTarget() {
		return mRenderTarget;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	public boolean isGlLoaded() {
		return mRenderTarget != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlayerSessionViewContainer() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void init(ICamera camera, RenderTarget renderTarget) {
		mPlayerCamera = camera;
		mRenderTarget = renderTarget;

		mIsInitialized = true;
	}

	public void reset() {
		mPlayerCamera = null;
		mRenderTarget = null;

		mIsInitialized = false;
	}

}

package net.lintfordlib.core.splitscreen;

import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.maths.Vector2f;

public class PlayerSessionViewContainer {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final Rectangle mGameViewport = new Rectangle();
	private final Rectangle mHudViewport = new Rectangle();

	private ICamera mPlayerCamera;
	private RenderTarget mRenderTarget;

	public final Vector2f viewportOffset = new Vector2f();
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

	public Rectangle gameViewport() {
		return mGameViewport;
	}

	public Rectangle hudViewport() {
		return mHudViewport;
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

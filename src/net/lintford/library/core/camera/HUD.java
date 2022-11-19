package net.lintford.library.core.camera;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.options.IResizeListener;

/**
 * Defines a simple HUD Camera which implements the {@link ICamera} interface. The HUD renders objects from -0 (-Z_NEAR) to -10 (-Z_FAR)
 */
public class HUD implements ICamera, IResizeListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/**
	 * The near plane (Z_NEAR) distance for front clipping. This defines the closest distance objects will be rendered from the HUD position.
	 */
	public static final float Z_NEAR = 0.0f;

	/**
	 * The far plane (Z_FAR) distance for rear clipping. This defines the closest distance objects will be rendered from the HUD position.
	 */
	public static final float Z_FAR = 10.0f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayManager mDisplayConfig;
	private Rectangle mBoundingRectangle;
	private int mWindowWidth;
	private int mWindowHeight;
	protected final Vector2f mScaleRatio = new Vector2f();
	private final Vector2f position = new Vector2f();
	private float mScaleFactor;
	private Matrix4f mProjectionMatrix;
	private Matrix4f mViewMatrix;
	private Vector2f mMouseHUDSpace;
	protected float mScaledWindowWidth;
	protected float mScaledWindowHeight;

	@Override
	public Vector2f internalPosition() {
		return position;
	}

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public float getMinX() {
		return -mWindowWidth / 2f;
	}

	@Override
	public float getMaxX() {
		return mWindowWidth / 2f;
	}

	@Override
	public float getMinY() {
		return -mWindowHeight / 2f;
	}

	@Override
	public float getMaxY() {
		return mWindowHeight / 2f;
	}

	@Override
	public float getWidth() {
		return mWindowWidth;
	}

	@Override
	public float getHeight() {
		return mWindowHeight;
	}

	@Override
	public int windowWidth() {
		return mWindowWidth;
	}

	@Override
	public int windowHeight() {
		return mWindowHeight;
	}

	@Override
	public Rectangle boundingRectangle() {
		return mBoundingRectangle;
	}

	// --------------------------------------
	// Constructor(s)
	// --------------------------------------

	public HUD(DisplayManager displayConfig) {
		mDisplayConfig = displayConfig;

		mWindowWidth = displayConfig.windowWidth();
		mWindowHeight = displayConfig.windowHeight();

		mBoundingRectangle = new Rectangle(-mWindowWidth / 2, -mWindowHeight / 2, mWindowWidth, mWindowHeight);

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseHUDSpace = new Vector2f();
		mScaleFactor = 1.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void handleInput(LintfordCore core) {
		float lWindowWidth = mWindowWidth;
		float lWindowHeight = mWindowHeight;

		if (mDisplayConfig.stretchGameScreen()) {
			lWindowWidth = mDisplayConfig.gameResolutionWidth();
			lWindowHeight = mDisplayConfig.gameResolutionHeight();

			mScaleRatio.set(((float) lWindowWidth / (float) mDisplayConfig.windowWidth()), ((float) lWindowHeight / (float) mDisplayConfig.windowHeight()));
		} else {
			mScaleRatio.set(((float) mWindowWidth / (float) mDisplayConfig.windowWidth()), ((float) mWindowHeight / (float) mDisplayConfig.windowHeight()));
		}

		mMouseHUDSpace.x = (float) (-lWindowWidth * .5f + (core.input().mouse().mouseWindowCoords().x) * mScaleRatio.x);
		mMouseHUDSpace.y = (float) (-lWindowHeight * .5f + (core.input().mouse().mouseWindowCoords().y) * mScaleRatio.y);
	}

	@Override
	public void update(LintfordCore core) {
		if (mWindowWidth == 0 || mWindowHeight == 0)
			return;

		mWindowWidth = core.config().display().windowWidth();
		mWindowHeight = core.config().display().windowHeight();

		if ((mWindowWidth % 2) == 1)
			mWindowWidth = mWindowWidth + 1;

		if ((mWindowHeight % 2) == 1)
			mWindowHeight = mWindowHeight + 1;

		mViewMatrix.setIdentity();
		mViewMatrix.translate(position.x, position.y, 0);
		mViewMatrix.scale(mScaleFactor, mScaleFactor, 1.f);

		if (mDisplayConfig.stretchGameScreen()) {
			createOrtho(mDisplayConfig.gameResolutionWidth(), mDisplayConfig.gameResolutionHeight());

			mBoundingRectangle.width(mDisplayConfig.gameResolutionWidth());
			mBoundingRectangle.height(mDisplayConfig.gameResolutionHeight());
			mBoundingRectangle.setCenterPosition(0, 0);

			mWindowWidth = mDisplayConfig.gameResolutionWidth();
			mWindowHeight = mDisplayConfig.gameResolutionHeight();
		} else {
			createOrtho(mWindowWidth, mWindowHeight);

			mBoundingRectangle.width(mWindowWidth);
			mBoundingRectangle.height(mWindowHeight);
			mBoundingRectangle.setCenterPosition(0, 0);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void createOrtho(int gameViewportWidth, int gameViewportHeight) {
		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(-gameViewportWidth * 0.5f, gameViewportWidth * 0.5f, gameViewportHeight * 0.5f, -gameViewportHeight * 0.5f, Z_NEAR, Z_FAR);

		mScaledWindowWidth = gameViewportWidth * getZoomFactorOverOne();
		mScaledWindowHeight = gameViewportHeight * getZoomFactorOverOne();
	}

	@Override
	public Matrix4f projection() {
		return mProjectionMatrix;
	}

	@Override
	public Matrix4f view() {
		return mViewMatrix;
	}

	@Override
	public Vector2f getMouseCameraSpace() {
		return mMouseHUDSpace;
	}

	@Override
	public float getMouseWorldSpaceX() {
		return mMouseHUDSpace.x;
	}

	@Override
	public float getMouseWorldSpaceY() {
		return mMouseHUDSpace.y;
	}

	@Override
	public float getPointCameraSpaceX(float pointX) {
		return (-mScaledWindowWidth * 0.5f + (pointX * mScaleRatio.x));
	}

	@Override
	public float getPointCameraSpaceY(float pointY) {
		return (-mScaledWindowHeight * 0.5f + (pointY * mScaleRatio.y));
	}

	@Override
	public Vector2f getPosition() {
		return position;
	}

	@Override
	public void setPosition(float positionX, float positionY) {
		position.set(positionX, positionY);
	}

	@Override
	public float getZoomFactor() {
		return mScaleFactor;
	}

	@Override
	public void setZoomFactor(float zoomFactor) {
		mScaleFactor = zoomFactor;
	}

	@Override
	public float getZoomFactorOverOne() {
		return 1f;
	}

	@Override
	public void onResize(int width, int height) {
		mWindowWidth = width;

		// Force the window height to be an even number (because it messes up some pixel
		// rendering)
		if ((height % 2) == 1) {
			height = height + 1;
		}

		mWindowHeight = height;
	}

	@Override
	public CameraState getCameraState() {
		return null;
	}

	@Override
	public void setCameraState(CameraState cameraState) {

	}

}

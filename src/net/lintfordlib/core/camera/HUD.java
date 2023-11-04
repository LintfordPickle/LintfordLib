package net.lintfordlib.core.camera;

import net.lintfordlib.ConstantsDisplay;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Matrix4f;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.IResizeListener;

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
	private final Vector2f mScaleRatio = new Vector2f();
	private final Vector2f position = new Vector2f();
	private Matrix4f mProjectionMatrix;
	private Matrix4f mViewMatrix;
	private Vector2f mMouseHUDSpace;
	private int mViewportWidth;
	private int mViewportHeight;
	private float mScaledViewportWidth;
	private float mScaledViewportHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public float getMinX() {
		return -mViewportWidth / 2f;
	}

	@Override
	public float getMaxX() {
		return mViewportWidth / 2f;
	}

	@Override
	public float getMinY() {
		return -mViewportHeight / 2f;
	}

	@Override
	public float getMaxY() {
		return mViewportHeight / 2f;
	}

	@Override
	public float getWidth() {
		return mViewportWidth;
	}

	@Override
	public float getHeight() {
		return mViewportHeight;
	}

	@Override
	public int viewportWidth() {
		return mViewportWidth;
	}

	@Override
	public int viewportHeight() {
		return mViewportHeight;
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

		mViewportWidth = displayConfig.windowWidth();
		mViewportHeight = displayConfig.windowHeight();

		mBoundingRectangle = new Rectangle(-mViewportWidth / 2, -mViewportHeight / 2, mViewportWidth, mViewportHeight);

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseHUDSpace = new Vector2f();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void handleInput(LintfordCore core) {
		float lWindowWidth = mViewportWidth;
		float lWindowHeight = mViewportHeight;

		mScaleRatio.set((mViewportWidth / (float) mDisplayConfig.windowWidth()), (mViewportHeight / (float) mDisplayConfig.windowHeight()));

		mMouseHUDSpace.x = -lWindowWidth * .5f + (core.input().mouse().mouseWindowCoords().x) * mScaleRatio.x;
		mMouseHUDSpace.y = -lWindowHeight * .5f + (core.input().mouse().mouseWindowCoords().y) * mScaleRatio.y;
	}

	@Override
	public void update(LintfordCore core) {
		if (mViewportWidth == 0 || mViewportHeight == 0)
			return;

		// prevent the Hud getting too small on 4k+ resolutions by artificially capping the dimensions for the HudCamera
		mViewportWidth = MathHelper.clampi(core.config().display().windowWidth(), ConstantsDisplay.MIN_UI_HUD_WIDTH, ConstantsDisplay.MAX_UI_HUD_WIDTH);
		mViewportHeight = MathHelper.clampi(core.config().display().windowHeight(), ConstantsDisplay.MIN_UI_HUD_HEIGHT, ConstantsDisplay.MAX_UI_HUD_HEIGHT);

		if ((mViewportWidth % 2) == 1)
			mViewportWidth = mViewportWidth + 1;

		if ((mViewportHeight % 2) == 1)
			mViewportHeight = mViewportHeight + 1;

		mViewMatrix.setIdentity();
		mViewMatrix.translate(position.x, position.y, 0);
		mViewMatrix.scale(1.f, 1.f, 1.f);

		createOrtho(mViewportWidth, mViewportHeight);

		mBoundingRectangle.width(mViewportWidth);
		mBoundingRectangle.height(mViewportHeight);
		mBoundingRectangle.setCenterPosition(0, 0);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void createOrtho(int gameViewportWidth, int gameViewportHeight) {
		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(-gameViewportWidth / 2, gameViewportWidth / 2, gameViewportHeight / 2, -gameViewportHeight / 2, Z_NEAR, Z_FAR);

		mScaledViewportWidth = gameViewportWidth * getZoomFactorOverOne();
		mScaledViewportHeight = gameViewportHeight * getZoomFactorOverOne();
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
		return (-mScaledViewportWidth * 0.5f + (pointX * mScaleRatio.x));
	}

	@Override
	public float getPointCameraSpaceY(float pointY) {
		return (-mScaledViewportHeight * 0.5f + (pointY * mScaleRatio.y));
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
		return 1.f;
	}

	/**
	 * Hud camera has no scaling. ZoomFactor is always 1.f
	 */
	@Override
	public void setZoomFactor(float zoomFactor) {

	}

	@Override
	public float getZoomFactorOverOne() {
		return 1f;
	}

	@Override
	public void onResize(int width, int height) {
		mViewportWidth = width;

		// Force the window height to be an even number (because it messes up some pixel
		// rendering)
		if ((height % 2) == 1) {
			height = height + 1;
		}

		mViewportHeight = height;
	}

	@Override
	public CameraState getCameraState() {
		return null;
	}

	@Override
	public void setCameraState(CameraState cameraState) {

	}

}

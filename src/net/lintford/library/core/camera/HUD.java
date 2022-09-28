package net.lintford.library.core.camera;

import org.lwjgl.opengl.GL11;

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
	private final Vector2f position = new Vector2f();
	private float mScaleFactor;
	private Matrix4f mProjectionMatrix;
	private Matrix4f mViewMatrix;
	private Vector2f mMouseHUDSpace;
	private float mRatioW;
	private float mRatioH;

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

		mRatioW = ((float) mWindowWidth / (float) mDisplayConfig.windowWidth());
		mRatioH = ((float) mWindowHeight / (float) mDisplayConfig.windowHeight());

		if (mDisplayConfig.stretchGameScreen()) {
			//			lWindowWidth = mDisplayConfig.baseGameResolutionWidth();
			//			lWindowHeight = mDisplayConfig.baseGameResolutionHeight();
			//
			//			mRatioW = ((float) lWindowWidth / (float) mDisplayConfig.windowWidth());
			//			mRatioH = ((float) lWindowHeight / (float) mDisplayConfig.windowHeight());
		}

		// FIXME: Remove the mMouseHUDSpace away from this class - it doesn't belong here
		mMouseHUDSpace.x = (float) (-lWindowWidth * 0.5f + (core.input().mouse().mouseWindowCoords().x - 1) * mRatioW);
		mMouseHUDSpace.y = (float) (-lWindowHeight * 0.5f + (core.input().mouse().mouseWindowCoords().y - 1) * mRatioH);
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

		mViewMatrix.setIdentity(); // points at -Z
		mViewMatrix.translate(position.x, position.y, 0);
		mViewMatrix.scale(mScaleFactor, mScaleFactor, 1.f);

		createOrtho(mWindowWidth, mWindowHeight);

		// update the bounding rectangle so we can properly do frustum culling
		mBoundingRectangle.setCenterPosition(0, 0);
		mBoundingRectangle.width(mWindowWidth);
		mBoundingRectangle.height(mWindowHeight);

		applyGameViewport();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void createOrtho(final float gameViewportWidth, final float gameViewportheight) {
		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(-gameViewportWidth * 0.5f, gameViewportWidth * 0.5f, gameViewportheight * 0.5f, -gameViewportheight * 0.5f, Z_NEAR, Z_FAR);
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
		return (-mWindowWidth * 0.5f + (pointX - 1));
	}

	@Override
	public float getPointCameraSpaceY(float pointY) {
		return (-mWindowHeight * 0.5f + (pointY - 1));
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

	public void applyGameViewport() {
		GL11.glViewport(0, 0, mWindowWidth, mWindowHeight);

	}

	@Override
	public float getWorldPositionXInCameraSpace(float pointX) {
		return pointX;
	}

	@Override
	public float getWorldPositionYInCameraSpace(float pointY) {
		return pointY;
	}
}

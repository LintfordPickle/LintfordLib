package net.lintford.library.core.camera;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.options.DisplayConfig;
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

	private DisplayConfig mDisplayConfig;

	private Rectangle mBoundingRectangle;

	/** The width of the window. */
	/** The height of the window. */
	private float mWindowWidth;
	private float mWindowHeight;

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

	public float windowWidth() {
		return mWindowWidth;
	}

	public float windowHeight() {
		return mWindowHeight;
	}

	@Override
	public Rectangle boundingRectangle() {
		return mBoundingRectangle;
	}

	// --------------------------------------
	// Constructor(s)
	// --------------------------------------

	public HUD(DisplayConfig pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;

		mWindowWidth = pDisplayConfig.windowSize().x;
		mWindowHeight = pDisplayConfig.windowSize().y;

		mBoundingRectangle = new Rectangle(-mWindowWidth / 2, -mWindowHeight / 2, mWindowWidth, mWindowHeight);

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseHUDSpace = new Vector2f();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void handleInput(LintfordCore pCore) {
		float lWindowWidth = mWindowWidth;
		float lWindowHeight = mWindowHeight;
		
		mRatioW = ((float) mWindowWidth / (float) mDisplayConfig.windowSize().x);
		mRatioH = ((float) mWindowHeight / (float) mDisplayConfig.windowSize().y);

		if (mDisplayConfig.stretchGameScreen()) {
//			lWindowWidth = mDisplayConfig.baseGameResolutionWidth();
//			lWindowHeight = mDisplayConfig.baseGameResolutionHeight();
//
//			mRatioW = ((float) lWindowWidth / (float) mDisplayConfig.windowSize().x);
//			mRatioH = ((float) lWindowHeight / (float) mDisplayConfig.windowSize().y);
		} else {
			mRatioW = ((float) mWindowWidth / (float) mDisplayConfig.windowSize().x);
			mRatioH = ((float) mWindowHeight / (float) mDisplayConfig.windowSize().y);
		}

		mMouseHUDSpace.x = (float) (-lWindowWidth * 0.5f + (pCore.input().mouseWindowCoords().x - 1) * mRatioW);
		mMouseHUDSpace.y = (float) (-lWindowHeight * 0.5f + (pCore.input().mouseWindowCoords().y - 1) * mRatioH);

	}

	public void update(LintfordCore pCore) {
		if (mWindowWidth == 0 || mWindowHeight == 0)
			return;

		if (mDisplayConfig.stretchGameScreen()) {
			mWindowWidth = mDisplayConfig.baseGameResolutionWidth();
			mWindowHeight = mDisplayConfig.baseGameResolutionHeight();
			
		} else {
			mWindowWidth = pCore.config().display().windowSize().x;
			mWindowHeight = pCore.config().display().windowSize().y;
			
		}

		if ((mWindowWidth % 2) == 1) {
			mWindowWidth = mWindowWidth + 1;
		}

		if ((mWindowHeight % 2) == 1) {
			mWindowHeight = mWindowHeight + 1;
		}

		mViewMatrix.setIdentity(); // points at -Z
		mViewMatrix.translate(0, 0, 0);

		createOrtho(mWindowWidth, mWindowHeight);

		// update the bounding rectangle so we can properly do frustum culling
		mBoundingRectangle.setCenterPosition(0, 0);
		mBoundingRectangle.setWidth(mWindowWidth);
		mBoundingRectangle.setHeight(mWindowHeight);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void createOrtho(final float pGameViewportWidth, final float pGameViewportheight) {
		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(-pGameViewportWidth * 0.5f, pGameViewportWidth * 0.5f, pGameViewportheight * 0.5f, -pGameViewportheight * 0.5f, Z_NEAR, Z_FAR);

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
	public float getPointWorldSpaceX(float pPointX) {
		return (-mWindowWidth * 0.5f + (pPointX - 1));
	}

	@Override
	public float getPointCameraSpaceY(float pPointY) {
		return (-mWindowWidth * 0.5f + (pPointY - 1));
	}

	@Override
	public Vector2f getPosition() {
		return Vector2f.Zero;
	}

	@Override
	public void setPosition(float pX, float pY) {

	}

	@Override
	public float getZoomFactor() {
		return 1f;
	}

	@Override
	public void setZoomFactor(float pNewValue) {

	}

	@Override
	public float getZoomFactorOverOne() {
		return 1f;
	}

	@Override
	public void onResize(int pWidth, int pHeight) {
		mWindowWidth = pWidth;

		// Force the window height to be an even number (because it messes up some pixel
		// rendering)
		if ((pHeight % 2) == 1) {
			pHeight = pHeight + 1;

		}

		mWindowHeight = pHeight;

	}

	@Override
	public CameraState getCameraState() {
		return null;

	}

	@Override
	public void setCameraState(CameraState pCameraState) {

	}

}

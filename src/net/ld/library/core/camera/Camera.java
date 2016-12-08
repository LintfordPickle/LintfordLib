package net.ld.library.core.camera;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

public class Camera implements ICamera {

	// =============================================
	// Constants
	// =============================================

	/** Determines the maximum zoom in amount */
	public static final float ZOOM_LEVEL_MAX = 3.0f;

	/** Determines the maximum zoom out amount */
	public static final float ZOOM_LEVEL_MIN = 0.9f; // 0.8f

	protected static final float ZOOM_ACCELERATE_AMOUNT = 9.0f;
	protected static final float DRAG = 0.9365f;
	protected static final boolean CAMERA_LAG_EFFECT = false;

	// =============================================
	// Variables
	// =============================================

	protected DisplayConfig mDisplayConfig;

	protected Rectangle mBoundingRectangle;
	protected Vector2f mPosition;
	protected Vector2f mAcceleration;
	protected Vector2f mVelocity;
	protected Vector2f mTargetPosition;
	protected Vector2f mOffsetPosition;
	protected Matrix4f mProjectionMatrix;
	protected Matrix4f mViewMatrix;
	protected float mMinX;
	protected float mMaxX;
	protected float mMinY;
	protected float mMaxY;
	protected float mZoomFactor = 1.0f;
	protected float mRotation = 0.0f;
	protected float mZoomAcceleration;
	protected float mZoomVelocity;
	protected boolean mAllowZoom = true;
	protected int mWindowWidth;
	protected int mWindowHeight;
	protected float mScaledWindowWidth;
	protected float mScaledWindowHeight;
	protected float mzNear;
	protected float mzFar;

	protected Vector2f mMouseCameraSpace;

	// =============================================
	// Properties
	// =============================================

	@Override
	public void setPosition(float pX, float pY) {
		mTargetPosition.x = pX;
		mTargetPosition.y = pY;
	}

	public float getScaledCenterX() {
		return mMinX + (mMaxX - mMinX) * 0.5f;
	}

	public float getScaledCenterY() {
		return mMinY + (mMaxY - mMinY) * 0.5f;
	}

	@Override
	public Vector2f getPosition() {
		return mPosition;
	}

	@Override
	public float getMinX() {
		return mMinX;
	}

	@Override
	public float getMaxX() {
		return mMaxX;
	}

	@Override
	public float getMinY() {
		return mMinY;
	}

	@Override
	public float getMaxY() {
		return mMaxY;
	}

	@Override
	public float getWidth() {
		return mMaxX - mMinX;
	}

	@Override
	public float getHeight() {
		return mMaxY - mMinY;
	}

	public float rotation() {
		return mRotation;
	}

	public void rotation(float newValue) {
		this.mRotation = newValue;
	}

	public float zoomFactor() {
		return mZoomFactor;
	}

	public void zoomFactor(float newValue) {
		this.mZoomFactor = newValue;
	}

	public float zNear() {
		return mzNear;
	}

	public void zNear(float newValue) {
		this.mzNear = newValue;
	}

	public float zFar() {
		return mzFar;
	}

	public void zFar(float newValue) {
		this.mzFar = newValue;
	}

	public float scaledWindowWidth() {
		return mScaledWindowWidth;
	}

	public float scaledWindowHeight() {
		return mScaledWindowHeight;
	}

	public int windowWidth() {
		return mWindowWidth;
	}

	public int windowHeight() {
		return mWindowHeight;
	}

	@Override
	public Rectangle boundingRectangle() {
		return mBoundingRectangle;
	}

	// =============================================
	// Constructor(s)
	// =============================================

	public Camera(DisplayConfig pDisplayConfig, final float pX, final float pY, final float pWidth, final float pHeight) {
		mDisplayConfig = pDisplayConfig;

		this.mMinX = pX;
		this.mMaxX = pX + pWidth;
		this.mMinY = pY;
		this.mMaxY = pY + pHeight;

		mBoundingRectangle = new Rectangle(mMinX, mMinY, pWidth, pHeight);

		mPosition = new Vector2f();
		mAcceleration = new Vector2f();
		mVelocity = new Vector2f();

		mOffsetPosition = new Vector2f();
		mTargetPosition = new Vector2f();

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseCameraSpace = new Vector2f();
		mZoomFactor = 1.7f;

		createOrtho(pWidth, pHeight);

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void handleInput(InputState pInputState) {
		// Update the MouseCameraSpace instance
		mMouseCameraSpace.x = pInputState.mouseWindowCoords().x * 1f * getZoomFactorOverOne() + this.getMinX();
		mMouseCameraSpace.y = pInputState.mouseWindowCoords().y * 1f * getZoomFactorOverOne() + this.getMinY();

		// static zoom factor
		if (mAllowZoom) {
			mZoomAcceleration += pInputState.mouseWheelYOffset() * ZOOM_ACCELERATE_AMOUNT * pInputState.gameTime().elapseGameTime() / 1000f * getZoomFactor();
		}

	}

	public void update(GameTime pGameTime) {

		mWindowWidth = (int) DisplayConfig.WINDOW_WIDTH;
		mWindowHeight = (int) DisplayConfig.WINDOW_HEIGHT;

		if (CAMERA_LAG_EFFECT) {
			mAcceleration.x = mTargetPosition.x - mPosition.x;
			mAcceleration.y = mTargetPosition.y - mPosition.y;

			// apply movement //
			mVelocity.x += mAcceleration.x * pGameTime.elapseGameTime() / 1000.0f;
			mVelocity.y += mAcceleration.y * pGameTime.elapseGameTime() / 1000.0f;

			// don't let the camera go miles off course
			if (Math.abs(mPosition.x) > Math.abs(mTargetPosition.x) * 20.5) {
				mVelocity.x *= 0.45f;
			}

			if (Math.abs(mPosition.y) > Math.abs(mTargetPosition.y) * 20.5) {
				mVelocity.y *= 0.45f;
			}

			mPosition.x += mVelocity.x;
			mPosition.y += mVelocity.y;

			mAcceleration.x = 0.0f;
			mAcceleration.y = 0.0f;

			// slow down the vel
			mVelocity.x *= 0.98f;
			mVelocity.y *= 0.98f;
		} else {
			mPosition.x = mTargetPosition.x;
			mPosition.y = mTargetPosition.y;

			mAcceleration.x = 0.0f;
			mAcceleration.y = 0.0f;
		}

		// apply zoom //
		mZoomVelocity += mZoomAcceleration;
		mZoomVelocity *= DRAG;
		mZoomFactor += mZoomVelocity * pGameTime.elapseGameTime() / 1000.0f;
		mZoomAcceleration = 0.0f;

		// Check bounds
		if (mZoomFactor < ZOOM_LEVEL_MIN)
			mZoomFactor = ZOOM_LEVEL_MIN;
		if (mZoomFactor > ZOOM_LEVEL_MAX)
			mZoomFactor = ZOOM_LEVEL_MAX;

		build(mWindowWidth, mWindowHeight);

	}

	public void build(float pW, float pH) {

		// Update the scaled camera position, width and height.
		mScaledWindowWidth = pW * getZoomFactorOverOne();
		mScaledWindowHeight = pH * getZoomFactorOverOne();

		// Update the camera position
		mMinX = -mPosition.x - mScaledWindowWidth / 2.0f;
		mMinY = -mPosition.y - mScaledWindowHeight / 2.0f;

		mMaxX = -mPosition.x + mScaledWindowWidth / 2.0f;
		mMaxY = -mPosition.y + mScaledWindowHeight / 2.0f;

		// update the bounding rectangle so we can properly do frustum culling
		mBoundingRectangle.x = mMinX;
		mBoundingRectangle.y = mMinY;
		mBoundingRectangle.width = mMaxX - mMinX;
		mBoundingRectangle.height = mMaxY - mMinY;

		mViewMatrix = new Matrix4f();
		mViewMatrix.scale(mZoomFactor, mZoomFactor, 1f);
		mViewMatrix.translate(mPosition.x * getZoomFactor(), mPosition.y * getZoomFactor(), 0f);

		createOrtho(pW, pH);

	}

	private void createOrtho(final float pW, final float pH) {

		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(-pW * 0.5f, pW * 0.5f, pH * 0.5f, -pH * 0.5f, -1.0f, 10.0f);

	}

	// =============================================
	// Methods
	// =============================================

	public void changePerspectiveMatrix(float pW, float pH) {
		build(pW, pH);

	}

	protected float getCenterX() {
		return mPosition.x + (mScaledWindowWidth * 0.5f);
	}

	protected float getCenterY() {
		return mPosition.y + (mScaledWindowHeight * 0.5f);
	}

	@Override
	public float getZoomFactor() {
		return mZoomFactor;
	}

	@Override
	public void setZoomFactor(float pNewValue) {
		mZoomFactor = pNewValue;

		// if (mZoomFactor < ZOOM_LEVEL_MIN)
		// mZoomFactor = ZOOM_LEVEL_MIN;
		// if (mZoomFactor > ZOOM_LEVEL_MAX)
		// mZoomFactor = ZOOM_LEVEL_MAX;
	}

	@Override
	public float getZoomFactorOverOne() {
		return 1f / mZoomFactor;
	}

	public void resetState() {
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
	public float getMouseCameraSpaceX() {
		return mMouseCameraSpace.x;

	}

	@Override
	public float getMouseCameraSpaceY() {
		return mMouseCameraSpace.y;

	}

	@Override
	public Vector2f getMouseCameraSpace() {
		return mMouseCameraSpace;

	}

	@Override
	public float getPointCameraSpaceX(float pPointX) {
		return pPointX * getZoomFactorOverOne() + this.getMinX();

	}

	@Override
	public float getPointCameraSpaceY(float pPointY) {
		return pPointY * getZoomFactorOverOne() + this.getMinX();

	}

}

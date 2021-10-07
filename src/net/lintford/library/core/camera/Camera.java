package net.lintford.library.core.camera;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.options.DisplayManager;

/**
 * Defines a simple Game Camera which implements the {@link ICamera} interface. The HUD renders objects from -0 (-Z_NEAR) to -10 (-Z_FAR)
 */
public class Camera implements ICamera {

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

	protected static final float ZOOM_ACCELERATE_AMOUNT = 0.1f;
	protected static final float DRAG = 0.9365f;
	protected static final boolean CAMERA_LAG_EFFECT = true;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected DisplayManager mDisplayConfig;

	protected final Rectangle mBoundingRectangle;
	protected final Vector2f mInternalPosition;
	protected final Vector2f mAcceleration;
	protected final Vector2f mVelocity;
	protected final Vector2f mTargetPosition;
	protected final Vector2f mOffsetPosition;

	protected final Matrix4f mProjectionMatrix;
	protected final Matrix4f mViewMatrix;

	protected float mMinX;
	protected float mMaxX;
	protected float mMinY;
	protected float mMaxY;
	protected float mZoomFactor = 1.0f;
	protected float mRotation = 0.0f;

	/** If true, the zooms the camera using the mouse scroll wheel */
	// TODO: integrate into the Input options menu
	protected boolean mAllowMouseScrollZoom = true;
	protected int mWindowWidth;
	protected int mWindowHeight;
	protected float mScaledWindowWidth;
	protected float mScaledWindowHeight;

	protected boolean mIsCameraChaseMode;

	// A coefficient used to modify how far behind the camera lags from the target position.
	protected float mChaseSpeedAmount;

	protected Vector2f mMouseWorldSpace;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setIsChaseCamera(boolean pEnabled) {
		if (pEnabled) {
			setIsChaseCamera(true, 0.05f);
		} else {
			setIsChaseCamera(false, 1.f);
		}
	}

	public void setIsChaseCamera(boolean pEnabled, float pChaseAmt) {
		mIsCameraChaseMode = true;
		mChaseSpeedAmount = pChaseAmt;
	}

	public boolean getIsChaseMode() {
		return mIsCameraChaseMode;
	}

	public float getChaseModeAmount() {
		return mChaseSpeedAmount;
	}

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
		return mInternalPosition;
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

	// --------------------------------------
	// Constructor(s)
	// --------------------------------------

	public Camera(DisplayManager pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;

		this.mMinX = 0;
		this.mMaxX = 0 + pDisplayConfig.windowWidth();
		this.mMinY = 0;
		this.mMaxY = 0 + pDisplayConfig.windowHeight();

		mBoundingRectangle = new Rectangle(mMinX, mMinY, pDisplayConfig.windowWidth(), pDisplayConfig.windowHeight());

		mInternalPosition = new Vector2f();
		mAcceleration = new Vector2f();
		mVelocity = new Vector2f();

		mOffsetPosition = new Vector2f();
		mTargetPosition = new Vector2f();

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseWorldSpace = new Vector2f();
		mZoomFactor = 1.0f;

		createView();
		createOrtho(mBoundingRectangle.width(), mBoundingRectangle.height());
		updateZoomBounds(mBoundingRectangle.width(), mBoundingRectangle.height());

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void handleInput(LintfordCore pCore) {
		// Update the MouseCameraSpace instance
		// FIXME: Move the mMouseWorldSpace away from the camera - it doesn't belong here
		mMouseWorldSpace.x = pCore.input().mouse().mouseWindowCoords().x * getZoomFactorOverOne() + getMinX();
		mMouseWorldSpace.y = pCore.input().mouse().mouseWindowCoords().y * getZoomFactorOverOne() + getMinY();

	}

	public void update(LintfordCore pCore) {
		mWindowWidth = mDisplayConfig.windowWidth();
		mWindowHeight = mDisplayConfig.windowHeight();

		if (mIsCameraChaseMode) {
			float newX = mTargetPosition.x;
			float newY = mTargetPosition.y;

			mInternalPosition.x += (newX - mInternalPosition.x) * mChaseSpeedAmount;
			mInternalPosition.y += (newY - mInternalPosition.y) * mChaseSpeedAmount;

		} else {
			mInternalPosition.x = mTargetPosition.x + mOffsetPosition.x;
			mInternalPosition.y = mTargetPosition.y + mOffsetPosition.y;

		}

		mAcceleration.x = 0.0f;
		mAcceleration.y = 0.0f;

		createView();
		if (mDisplayConfig.stretchGameScreen()) {
			createOrtho(mDisplayConfig.baseGameResolutionWidth(), mDisplayConfig.baseGameResolutionHeight());
		} else
			createOrtho(mWindowWidth, mWindowHeight);

		updateZoomBounds(mWindowWidth, mWindowHeight);

		applyGameViewport();

	}

	public void createView() {
		mViewMatrix.setIdentity();
		mViewMatrix.scale(mZoomFactor, mZoomFactor, 1f);
		mViewMatrix.translate((int) (-mInternalPosition.x * getZoomFactor()), (int) (-mInternalPosition.y * getZoomFactor()), 0f);

	}

	private void createOrtho(final float pGameViewportWidth, final float pGameViewportheight) {
		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(-pGameViewportWidth * 0.5f, pGameViewportWidth * 0.5f, pGameViewportheight * 0.5f, -pGameViewportheight * 0.5f, Z_NEAR, Z_FAR);

	}

	private void updateZoomBounds(final float pGameViewportWidth, final float pGameViewportheight) {
		// Update the scaled camera position, width and height.
		mScaledWindowWidth = pGameViewportWidth * getZoomFactorOverOne();
		mScaledWindowHeight = pGameViewportheight * getZoomFactorOverOne();

		// Update the camera position
		mMinX = mInternalPosition.x - mScaledWindowWidth / 2.0f;
		mMinY = mInternalPosition.y - mScaledWindowHeight / 2.0f;

		mMaxX = mInternalPosition.x + mScaledWindowWidth / 2.0f;
		mMaxY = mInternalPosition.y + mScaledWindowHeight / 2.0f;

		// update the bounding rectangle so we can properly do frustum culling
		mBoundingRectangle.setCenterPosition(mInternalPosition.x, mInternalPosition.y);
		mBoundingRectangle.width(mScaledWindowWidth);
		mBoundingRectangle.height(mScaledWindowHeight);

	}

	@Override
	public void applyGameViewport() {
		int lNearestW = ((mWindowWidth % 2) == 0) ? mWindowWidth : mWindowWidth + 1;
		int lNearestH = ((mWindowHeight % 2) == 0) ? mWindowHeight : mWindowHeight + 1;

		GL11.glViewport(0, 0, lNearestW, lNearestH);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setCameraOffset(float pOffsetX, float pOffsetY) {
		mOffsetPosition.x = pOffsetX;
		mOffsetPosition.y = pOffsetY;

	}

	@Override
	public CameraState getCameraState() {
		CameraState pReturnState = new CameraState();

		pReturnState.acceleration.x = mAcceleration.x;
		pReturnState.acceleration.y = mAcceleration.y;

		pReturnState.velocity.x = mVelocity.x;
		pReturnState.velocity.y = mTargetPosition.y;

		pReturnState.targetPosition.x = mTargetPosition.x;
		pReturnState.targetPosition.y = mTargetPosition.y;

		pReturnState.offsetPosition.x = mOffsetPosition.x;
		pReturnState.offsetPosition.y = mOffsetPosition.y;

		pReturnState.zoomFactor = mZoomFactor;
		pReturnState.rotation = mRotation;

		return pReturnState;

	}

	@Override
	public void setCameraState(CameraState pCameraState) {
		if (pCameraState == null)
			return;

		mAcceleration.x = pCameraState.acceleration.x;
		mAcceleration.y = pCameraState.acceleration.y;

		mVelocity.x = pCameraState.velocity.x;
		mVelocity.y = pCameraState.velocity.y;

		mTargetPosition.x = pCameraState.targetPosition.x;
		mTargetPosition.y = pCameraState.targetPosition.y;

		mOffsetPosition.x = pCameraState.offsetPosition.x;
		mOffsetPosition.y = pCameraState.offsetPosition.y;

		mZoomFactor = pCameraState.zoomFactor;
		mRotation = pCameraState.rotation;

	}

	public void changePerspectiveMatrix(float pW, float pH) {
		createOrtho(pW, pH);

	}

	protected float getCenterX() {
		return mInternalPosition.x + (mScaledWindowWidth * 0.5f);
	}

	protected float getCenterY() {
		return mInternalPosition.y + (mScaledWindowHeight * 0.5f);
	}

	@Override
	public float getZoomFactor() {
		return mZoomFactor;
	}

	@Override
	public void setZoomFactor(float pNewValue) {
		mZoomFactor = pNewValue;

		createView();
		updateZoomBounds(mWindowWidth, mWindowHeight);

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
	public float getMouseWorldSpaceX() {
		return mMouseWorldSpace.x;

	}

	@Override
	public float getMouseWorldSpaceY() {
		return mMouseWorldSpace.y;

	}

	@Override
	public Vector2f getMouseCameraSpace() {
		return mMouseWorldSpace;

	}

	@Override
	public float getPointCameraSpaceX(float pPointX) {
		return pPointX * getZoomFactorOverOne() + this.getMinX();

	}

	@Override
	public float getPointCameraSpaceY(float pPointY) {
		return pPointY * getZoomFactorOverOne() + this.getMinY();

	}

	@Override
	public float getWorldPositionXInCameraSpace(float pPointX) {
		return (mInternalPosition.x - pPointX) * getZoomFactor();
	}

	@Override
	public float getWorldPositionYInCameraSpace(float pPointY) {
		return (mInternalPosition.y - pPointY) * getZoomFactor();
	}

}

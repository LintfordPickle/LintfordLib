package net.lintfordlib.core.graphics.rendertarget;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.CameraState;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Matrix4f;
import net.lintfordlib.core.maths.Vector2f;

public class RTCamera implements ICamera {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final float Z_NEAR = 0.0f;
	public static final float Z_FAR = 10.0f;

	protected static final float ZOOM_ACCELERATE_AMOUNT = 0.1f;
	protected static final float DRAG = 0.9365f;
	protected static final boolean CAMERA_LAG_EFFECT = true;

	// --------------------------------------
	// Variables
	// --------------------------------------

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

	protected boolean mAllowMouseScrollZoom = true;
	protected int mViewportWidth;
	protected int mViewportHeight;
	protected float mScaledViewportWidth;
	protected float mScaledViewportHeight;

	protected Vector2f mMouseWorldSpace;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public void setPosition(float targetX, float targetY) {
		mTargetPosition.x = targetX;
		mTargetPosition.y = targetY;
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
		return mScaledViewportWidth;
	}

	public float scaledWindowHeight() {
		return mScaledViewportHeight;
	}

	public int viewportWidth() {
		return mViewportWidth;
	}

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

	public RTCamera(int windowWidth, int windowHeight) {
		mViewportWidth = windowWidth;
		mViewportHeight = windowHeight;

		this.mMinX = 0;
		this.mMaxX = 0 + windowWidth;
		this.mMinY = 0;
		this.mMaxY = 0 + windowHeight;

		mBoundingRectangle = new Rectangle(mMinX, mMinY, windowWidth, windowHeight);

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

	public void handleInput(LintfordCore core) {
		final var mouseWindowCoordX = core.input().mouse().mouseWindowCoords().x;
		final var mouseWindowCoordY = core.input().mouse().mouseWindowCoords().y;

		final var displayWidth = core.config().display().windowWidth();
		final var displayHeight = core.config().display().windowHeight();

		final var lx = mouseWindowCoordX / (float) displayWidth;
		final var ly = mouseWindowCoordY / (float) displayHeight;
		final var mouseWindowCoordXScaled = lx * mViewportWidth;
		final var mouseWindowCoordYScaled = ly * mViewportHeight;

		mMouseWorldSpace.x = (mouseWindowCoordXScaled * getZoomFactorOverOne()) + getMinX();
		mMouseWorldSpace.y = (mouseWindowCoordYScaled * getZoomFactorOverOne()) + getMinY();
	}

	public void update(LintfordCore core) {
		mInternalPosition.x = mTargetPosition.x + mOffsetPosition.x;
		mInternalPosition.y = mTargetPosition.y + mOffsetPosition.y;

		mAcceleration.x = 0.0f;
		mAcceleration.y = 0.0f;

		createView();
		createOrtho(mViewportWidth, mViewportHeight);

		updateZoomBounds(mViewportWidth, mViewportHeight);
	}

	public void createView() {
		mViewMatrix.setIdentity();
		mViewMatrix.createLookAt(0.f, 0.f, 0.f, 0.f, 0.f, -1f, 0.f, 1.f, 0.f);
		mViewMatrix.scale(mZoomFactor, mZoomFactor, 1f);
		mViewMatrix.translate((int) (-mInternalPosition.x * getZoomFactor()), (int) (-mInternalPosition.y * getZoomFactor()), 0f);
	}

	private void createOrtho(float gameViewportWidth, float gameViewportheight) {
		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(-gameViewportWidth * 0.5f, gameViewportWidth * 0.5f, gameViewportheight * 0.5f, -gameViewportheight * 0.5f, Z_NEAR, Z_FAR);
	}

	private void updateZoomBounds(float gameViewportWidth, float gameViewportHeight) {
		mScaledViewportWidth = gameViewportWidth * getZoomFactorOverOne();
		mScaledViewportHeight = gameViewportHeight * getZoomFactorOverOne();

		mMinX = mInternalPosition.x - mScaledViewportWidth / 2.0f;
		mMinY = mInternalPosition.y - mScaledViewportHeight / 2.0f;

		mMaxX = mInternalPosition.x + mScaledViewportWidth / 2.0f;
		mMaxY = mInternalPosition.y + mScaledViewportHeight / 2.0f;

		mBoundingRectangle.setCenterPosition(mInternalPosition.x, mInternalPosition.y);
		mBoundingRectangle.width(mScaledViewportWidth);
		mBoundingRectangle.height(mScaledViewportHeight);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setCameraOffset(float offsetX, float offsetY) {
		mOffsetPosition.x = offsetX;
		mOffsetPosition.y = offsetY;
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
	public void setCameraState(CameraState cameraState) {
		if (cameraState == null)
			return;

		mAcceleration.x = cameraState.acceleration.x;
		mAcceleration.y = cameraState.acceleration.y;

		mVelocity.x = cameraState.velocity.x;
		mVelocity.y = cameraState.velocity.y;

		mTargetPosition.x = cameraState.targetPosition.x;
		mTargetPosition.y = cameraState.targetPosition.y;

		mOffsetPosition.x = cameraState.offsetPosition.x;
		mOffsetPosition.y = cameraState.offsetPosition.y;

		mZoomFactor = cameraState.zoomFactor;
		mRotation = cameraState.rotation;
	}

	public void changePerspectiveMatrix(float newWidth, float newHeight) {
		createOrtho(newWidth, newHeight);
	}

	protected float getCenterX() {
		return mInternalPosition.x + (mScaledViewportWidth * 0.5f);
	}

	protected float getCenterY() {
		return mInternalPosition.y + (mScaledViewportHeight * 0.5f);
	}

	@Override
	public float getZoomFactor() {
		return mZoomFactor;
	}

	@Override
	public void setZoomFactor(float newValue) {
		mZoomFactor = newValue;

		createView();
		updateZoomBounds(mViewportWidth, mViewportHeight);
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
	public float getPointCameraSpaceX(float pointX) {
		return pointX * getZoomFactorOverOne() + this.getMinX();
	}

	@Override
	public float getPointCameraSpaceY(float pointY) {
		return pointY * getZoomFactorOverOne() + this.getMinY();
	}
}

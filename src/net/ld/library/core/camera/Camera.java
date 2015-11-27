package net.ld.library.core.camera;

import org.lwjgl.opengl.GL11;

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

	protected static final float DRAG = 0.9365f;

	// =============================================
	// Variables
	// =============================================

	protected DisplayConfig mDisplayConfig;
	protected Rectangle mBoundingRectangle;

	protected Vector2f mPosition;
	protected Vector2f mTargetPosition;
	public boolean mIsTransitioning;
	public Vector2f mStartPosition = new Vector2f();
	protected Matrix4f mProjectionMatrix;
	protected Matrix4f mViewMatrix;
	protected float mMinX;
	protected float mMaxX;
	protected float mMinY;
	protected float mMaxY;
	protected float mRotation = 0.0f;
	protected int mWindowWidth;
	protected int mWindowHeight;
	protected float mScaledWindowWidth;
	protected float mScaledWindowHeight;
	protected float mzNear;
	protected float mzFar;

	// =============================================
	// Properties
	// =============================================

	@Override
	public void setTargetPosition(float pX, float pY) {
		mTargetPosition.x = pX;
		mTargetPosition.y = pY;

	}

	public float getScaledCenterX() {
		return mMinX + (mMaxX - mMinX) * 0.5f;
	}

	public float getScaledCenterY() {
		return mMinY + (mMaxY - mMinY) * 0.5f;
	}

	public void setPosition(float pX, float pY) {
		mPosition.x = pX;
		mPosition.y = pY;
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

	public Camera(DisplayConfig pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;
		mBoundingRectangle = new Rectangle();

		mPosition = new Vector2f();
		mTargetPosition = new Vector2f();

		mProjectionMatrix = new Matrix4f();
		mProjectionMatrix.createOrtho(-mDisplayConfig.windowWidth() * 0.5f, mDisplayConfig.windowWidth() * 0.5f, mDisplayConfig.windowHeight() * 0.5f, -mDisplayConfig.windowHeight() * 0.5f, 1.0f, -1.0f);
		mViewMatrix = new Matrix4f();

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void handleInput(InputState pInputState) {

	}

	@Override
	public void update(GameTime pGameTime) {

		mWindowWidth = mDisplayConfig.windowWidth();
		mWindowHeight = mDisplayConfig.windowHeight();

		if (mIsTransitioning) {
			float transitionDelta = (float) -Math.pow((pGameTime.elapseGameTime() / 5f), 2);

			float xSign = Math.signum(mStartPosition.x - mTargetPosition.x);
			float ySign = Math.signum(mStartPosition.y - mTargetPosition.y);

			mPosition.x += transitionDelta * xSign * pGameTime.elapseGameTime() / 10f;
			mPosition.y += transitionDelta * ySign * pGameTime.elapseGameTime() / 10f;

			if (xSign < 0 && mPosition.x > mTargetPosition.x)
				mPosition.x = mTargetPosition.x;
			if (xSign > 0 && mPosition.x < mTargetPosition.x)
				mPosition.x = mTargetPosition.x;

			if (ySign < 0 && mPosition.y > mTargetPosition.y)
				mPosition.y = mTargetPosition.y;
			if (ySign > 0 && mPosition.y < mTargetPosition.y)
				mPosition.y = mTargetPosition.y;

			if (mPosition.x == mTargetPosition.x && mPosition.y == mTargetPosition.y) {
				mIsTransitioning = false;
			}

		}

		build();

	}

	public void build() {

		// Update the scaled camera position, width and height.
		mScaledWindowWidth = mWindowWidth;
		mScaledWindowHeight = mWindowHeight;

		// Update the camera position
		mMinX = -mPosition.x - mScaledWindowWidth / 2.0f;
		mMinY = -mPosition.y - mScaledWindowHeight / 2.0f;

		mMaxX = -mPosition.x + mScaledWindowWidth / 2.0f;
		mMaxY = -mPosition.y + mScaledWindowHeight / 2.0f;

		// update the bounding rectangle so we can properly do frustum culling
		mBoundingRectangle.mX = mMinX;
		mBoundingRectangle.mY = mMinY;
		mBoundingRectangle.mWidth = mMaxX - mMinX;
		mBoundingRectangle.mHeight = mMaxY - mMinY;

		mViewMatrix = new Matrix4f();
		mViewMatrix.translate(-mPosition.x, -mPosition.y, 0f);

		GL11.glViewport(0, 0, mDisplayConfig.windowWidth(), mDisplayConfig.windowHeight());

		// This is where we can stretch and thin the display
		mProjectionMatrix.createOrtho(-mDisplayConfig.windowWidth() * 0.5f, mDisplayConfig.windowWidth() * 0.5f, mDisplayConfig.windowHeight() * 0.5f, -mDisplayConfig.windowHeight() * 0.5f, -1.0f, 10.0f);

	}

	// =============================================
	// Methods
	// =============================================

	protected float getCenterX() {
		return mPosition.x + (mScaledWindowWidth * 0.5f);
	}

	protected float getCenterY() {
		return mPosition.y + (mScaledWindowHeight * 0.5f);
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
	public Vector2f getMouseCameraSpace(double pMouseX, double pMouseY) {
		float lMouseX = (float) (pMouseX);
		float lMouseY = (float) (mWindowHeight - pMouseY);

		float lWorldX = lMouseX + this.getMinX();
		float lWorldY = lMouseY + this.getMinY();

		// TODO: don't create a new vector here each time
		return new Vector2f(lWorldX, lWorldY);
	}

	public Vector2f convertPointToCameraSpace(Vector2f pInput) {
		return new Vector2f(pInput.x + this.getMinX(), pInput.y + this.getMinY());
	}

	public void startTransition(Vector2f target) {
		mIsTransitioning = true;

		mTargetPosition.x = target.x;
		mTargetPosition.y = target.y;

		mStartPosition.x = mPosition.x;
		mStartPosition.y = mPosition.y;
	}

	public void startTransition(float pX, float pY) {
		mIsTransitioning = true;

		mTargetPosition.x = mPosition.x + pX;
		mTargetPosition.y = mPosition.y + pY;

		mStartPosition.x = mPosition.x;
		mStartPosition.y = mPosition.y;
	}
}

package net.ld.library.core.camera;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.config.IResizeListener;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

public class Camera implements ICamera, IResizeListener {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final float Z_NEAR = -1f;
	public static final float Z_FAR = 10f;

	protected static final float DRAG = 0.987f;
	protected static final boolean CAMERA_PHYSICS = true;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected Rectangle mBoundingRectangle;
	protected Vector2f mPosition;
	protected Vector2f mAcceleration;
	protected Vector2f mVelocity;
	protected Vector2f mTargetPosition;
	protected Vector2f mOffsetPosition;
	protected Matrix4f mProjectionMatrix;
	protected Matrix4f mViewMatrix;
	protected Vector2f mMouseCameraSpace;

	protected int mWindowWidth;
	protected int mWindowHeight;
	protected float mZoomFactor;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	/** Returns this camera's view matrix */
	@Override
	public Matrix4f view() {
		return mViewMatrix;
	}

	/** Returns this camera's projection matrix */
	@Override
	public Matrix4f projection() {
		return mProjectionMatrix;
	}

	/** Returns the view frustum of this camera. */
	@Override
	public Rectangle boundingRectangle() {
		return mBoundingRectangle;
	}

	/** Returns the current target position the camera will move towards. */
	public Vector2f targetPosition() {
		return mTargetPosition;
	}

	/**
	 * Sets the target position of the camera, which the camera will slowly move
	 * towards over time.
	 */
	public void setPosition(float pX, float pY) {
		mTargetPosition.x = pX;
		mTargetPosition.y = pY;
	}

	/**
	 * Sets the position of the camera and kills the velocity and acceleration.
	 */
	public void setAbsPosition(float pX, float pY) {
		mTargetPosition.x = pX;
		mTargetPosition.y = pY;

		mPosition.x = pX;
		mPosition.y = pY;

		mVelocity.x = 0;
		mVelocity.y = 0;

		mAcceleration.x = 0;
		mAcceleration.y = 0;

		updateZoomBounds();

	}

	public float getScaledCenterX() {
		return this.mBoundingRectangle.centerX();
	}

	public float getScaledCenterY() {
		return this.mBoundingRectangle.centerY();
	}

	public Vector2f getPosition() {
		return mPosition;
	}

	public float getMinX() {
		return this.mBoundingRectangle.left();
	}

	public float getMaxX() {
		return this.mBoundingRectangle.right();
	}

	public float getMinY() {
		return this.mBoundingRectangle.top();
	}

	public float getMaxY() {
		return this.mBoundingRectangle.bottom();
	}

	public float getWidth() {
		return this.mBoundingRectangle.width;
	}

	public float getHeight() {
		return this.mBoundingRectangle.height;
	}

	public float zoomFactor() {
		return mZoomFactor;
	}

	public void zoomFactor(float newValue) {
		this.mZoomFactor = newValue;
	}

	public float scaledWindowWidth() {
		return this.mWindowWidth * this.getZoomFactorOverOne();
	}

	public float scaledWindowHeight() {
		return this.mWindowHeight * this.getZoomFactorOverOne();
	}

	public int windowWidth() {
		return mWindowWidth;
	}

	public int windowHeight() {
		return mWindowHeight;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	/**
	 * Camera constructor. Creates a camera and all needed matrices to support
	 * an OpenGL game. The camera matrices will be constructor col-maj, using
	 * the given parameters.
	 */
	public Camera(final float pX, final float pY, final int pWidth, final int pHeight) {
		this.mWindowWidth = pWidth;
		this.mWindowHeight = pHeight;

		this.mBoundingRectangle = new Rectangle(pX - pWidth * 0.5f, pY - pHeight * 0.5f, pWidth, pHeight);

		this.mPosition = new Vector2f(pX, pY);
		this.mOffsetPosition = new Vector2f();
		this.mAcceleration = new Vector2f();
		this.mVelocity = new Vector2f();

		this.mMouseCameraSpace = new Vector2f();
		this.mTargetPosition = new Vector2f();

		this.mProjectionMatrix = new Matrix4f();
		this.mViewMatrix = new Matrix4f();

		this.mZoomFactor = 1.0f;

		createView();
		createOrtho();
		updateZoomBounds();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * handleInput called once per frame. Stores the position of the mouse for
	 * future reference (within camera space). Tracks mouse wheel movement to
	 * change zoom factor.
	 */
	public void handleInput(InputState pInputState) {
		// Update the MouseCameraSpace instance
		// TODO: Add this to a Controller and move
		mMouseCameraSpace.x = this.getMinX() + pInputState.mouseWindowCoords().x * getZoomFactorOverOne();
		mMouseCameraSpace.y = this.getMinY() + pInputState.mouseWindowCoords().y * getZoomFactorOverOne();

	}

	/** Updates the state of the camera */
	public void update(GameTime pGameTime) {
		final float DELTA_TIME = (float) pGameTime.elapseGameTime() / 1000.0f;

		// TODO (John): When moving to MVC pattern, move these physics out of
		// the
		// camera class.
		if (CAMERA_PHYSICS) {
			mAcceleration.x = mTargetPosition.x - mPosition.x;
			mAcceleration.y = mTargetPosition.y - mPosition.y;

			// apply movement //
			mVelocity.x += mAcceleration.x * DELTA_TIME;
			mVelocity.y += mAcceleration.y * DELTA_TIME;

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
		}

		else {
			mPosition.x = mTargetPosition.x;
			mPosition.y = mTargetPosition.y;

			mAcceleration.x = 0.0f;
			mAcceleration.y = 0.0f;
		}

		createView();
		createOrtho();
		updateZoomBounds();

		applyGameViewport();

	}

	/** creates a view matrix from the current camera state (zoom & position) */
	private void createView() {
		this.mViewMatrix.setIdentity();
		this.mViewMatrix.scale(mZoomFactor, mZoomFactor, 1f);
		this.mViewMatrix.translate((-mPosition.x + mOffsetPosition.x) * getZoomFactor(),
				(-mPosition.y + mOffsetPosition.y) * getZoomFactor(), 0f);

	}

	/**
	 * creates a projection matrix from the current camera state (window
	 * dimensions)
	 */
	private void createOrtho() {
		this.mProjectionMatrix.setIdentity();
		this.mProjectionMatrix.createOrtho(-this.mWindowWidth * 0.5f, this.mWindowWidth * 0.5f,
				this.mWindowHeight * 0.5f, -this.mWindowHeight * 0.5f, -1.0f, 10.0f);

	}

	/**
	 * Updates the bounding rectangle properties based on this camera state
	 * (position, size and zoom)
	 */
	private void updateZoomBounds() {
		// Update the scaled camera position, width and height.
		final float lScaledWindowWidth = this.scaledWindowWidth();
		final float lScaledWindowHeight = this.scaledWindowHeight();

		// update the bounding rectangle so we can properly do frustum culling
		this.mBoundingRectangle.x = mPosition.x - lScaledWindowWidth / 2.0f;
		this.mBoundingRectangle.y = mPosition.y - lScaledWindowHeight / 2.0f;
		this.mBoundingRectangle.width = (this.mPosition.x + lScaledWindowWidth / 2.0f) - this.mBoundingRectangle.x;
		this.mBoundingRectangle.height = (this.mPosition.y + lScaledWindowHeight / 2.0f) - this.mBoundingRectangle.y;

	}

	/**
	 * Sets the OpenGL viewport size. Typically will be set to the window or
	 * render-target dimensions.
	 */
	public void applyGameViewport() {
		GL11.glViewport(0, 0, this.mWindowWidth, this.mWindowHeight);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/**
	 * Rebuilds the projection matrix of this camera with the given dimensions.
	 * Throws IllegalArgumentException is the input values are less than or
	 * equal to zero.
	 */
	public void changePerspectiveMatrix(int pNewWidth, int pNewHeight) throws IllegalArgumentException {
		if (pNewWidth <= 0 || pNewHeight <= 0) {
			// Minimized window?
			return;

		}

		mWindowWidth = pNewWidth;
		mWindowHeight = pNewHeight;

		createOrtho();

	}

	/**
	 * Gets the centre X point of this camera (taking into consideration
	 * position, size and zoom).
	 */
	protected float getCenterX() {
		return mPosition.x + (this.scaledWindowWidth() * 0.5f);
	}

	/**
	 * Gets the centre Y point of this camera (taking into consideration
	 * position, size and zoom).
	 */
	protected float getCenterY() {
		return mPosition.y + (this.scaledWindowHeight() * 0.5f);
	}

	/** Gets the current zoom factor of this camera. */
	public float getZoomFactor() {
		return mZoomFactor;
	}

	/** Sets the current zoom factor of this camera. */
	public void setZoomFactor(float pNewValue) {
		mZoomFactor = pNewValue;

	}

	public float getZoomFactorOverOne() {
		return 1f / mZoomFactor;
	}

	public Vector2f getMouseCameraSpace() {
		return mMouseCameraSpace;

	}

	/**
	 * Converts the given point from window space to camera (world) space.
	 * Taking into consideration zoom and camera position.
	 */
	public float getPointCameraSpaceX(float pPointX) {
		return pPointX * getZoomFactorOverOne() + this.getMinX();

	}

	/**
	 * Converts the given point from window space to camera (world) space.
	 * Taking into consideration zoom and camera position.
	 */
	public float getPointCameraSpaceY(float pPointY) {
		return pPointY * getZoomFactorOverOne() + this.getMinY();

	}

	// ---------------------------------------------
	// Inherited Methods
	// ---------------------------------------------

	/** Called (from GLFW) when the window is resized */
	@Override
	public void onResize(int pWidth, int pHeight) {
		changePerspectiveMatrix(pWidth, pHeight);

	}

}
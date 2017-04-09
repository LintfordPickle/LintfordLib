package net.ld.library.core.camera;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.config.IResizeListener;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

public class HUD implements ICamera, IResizeListener {

	private static final float Z_NEAR = -1.0f;
	private static final float Z_FAR = 10.0f;

	// =============================================
	// Variables
	// =============================================

	private Rectangle mHUDRectangle;

	// These are not the dimensions of the window, but rather the dimensions of
	// the canvas to use for the HUD (i.e. menu system)
	private float mWindowWidth;
	private float mWindowHeight;

	private Matrix4f mProjectionMatrix;
	private Matrix4f mViewMatrix;

	private Vector2f mMouseHUDSpace;

	// =============================================
	// Properties
	// =============================================

	public float getMinX() {
		return -mWindowWidth / 2f;
	}

	public float getMaxX() {
		return mWindowWidth / 2f;
	}

	public float getMinY() {
		return -mWindowHeight / 2f;
	}

	public float getMaxY() {
		return mWindowHeight / 2f;
	}

	public float getWidth() {
		return mWindowWidth;
	}

	public float getHeight() {
		return mWindowHeight;
	}

	public float windowWidth() {
		return mWindowWidth;
	}

	public float windowHeight() {
		return mWindowHeight;
	}

	public Rectangle boundingHUDRectange() {
		return mHUDRectangle;
	}

	// =============================================
	// Constructor(s)
	// =============================================

	public HUD(final float pX, final float pY, final float pWidth, final float pHeight) {
		mWindowWidth = pWidth;
		mWindowHeight = pHeight;

		mHUDRectangle = new Rectangle(-pWidth / 2, -pHeight / 2, pWidth, pHeight);

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseHUDSpace = new Vector2f();

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void handleInput(InputState pInputState) {
		mMouseHUDSpace.x = (float) (-DisplayConfig.WINDOW_WIDTH * 0.5f + (pInputState.mouseWindowCoords().x - 1));
		mMouseHUDSpace.y = (float) (-DisplayConfig.WINDOW_HEIGHT * 0.5f + (pInputState.mouseWindowCoords().y - 1));

	}

	public void update(GameTime pGameTime) {
		mWindowWidth = DisplayConfig.WINDOW_WIDTH;
		mWindowHeight = DisplayConfig.WINDOW_HEIGHT;

		mHUDRectangle.set(getMinX(), getMinY(), getWidth(), getHeight());

		if (mWindowWidth == 0 || mWindowHeight == 0)
			return;

		createView();
		createProjection();

	}

	/** recreates the view matrix. */
	private void createView() {
		this.mViewMatrix.setIdentity();
		this.mViewMatrix.scale(1f, 1f, 1f);

	}

	/** Creates a new projection matrix (orthographic projection) */
	private void createProjection() {
		this.mProjectionMatrix.setIdentity();
		this.mProjectionMatrix.createOrtho(getMinX(), getMaxX(), getMaxY(), getMinY(), Z_NEAR, Z_FAR);

	}

	// =============================================
	// Methods
	// =============================================

	public Vector2f getMouseCameraSpace() {
		return mMouseHUDSpace;
	}

	public float getMouseCameraSpaceX() {
		return mMouseHUDSpace.x;
	}

	public float getMouseCameraSpaceY() {
		return mMouseHUDSpace.y;
	}

	public float getPointCameraSpaceX(float pPointX) {
		return (-mWindowWidth * 0.5f + (pPointX - 1));
	}

	public float getPointCameraSpaceY(float pPointY) {
		return (-mWindowWidth * 0.5f + (pPointY - 1));
	}

	public Vector2f getPosition() {
		return new Vector2f();
	}

	public void setPosition(float pX, float pY) {

	}

	public float getZoomFactor() {
		return 1f;
	}

	public void setZoomFactor(float pNewValue) {

	}

	public float getZoomFactorOverOne() {
		return 1f;
	}

	/**
	 * Rebuilds the projection matrix of this camera with the given dimensions.
	 * Throws IllegalArgumentException is the input values are less than or
	 * equal to zero.
	 */
	public void changePerspectiveMatrix(int pNewWidth, int pNewHeight) throws IllegalArgumentException {
		if (pNewWidth <= 0 || pNewHeight <= 0) {
			throw new IllegalArgumentException(
					"You cannot set the new width or new height of the camera to zero or less.");

		}

		mWindowWidth = pNewWidth;
		mWindowHeight = pNewHeight;

		createProjection();

	}

	// =============================================
	// Inherited-Methods
	// =============================================

	@Override
	public Matrix4f projection() {
		return mProjectionMatrix;
	}

	@Override
	public Matrix4f view() {
		return mViewMatrix;
	}

	@Override
	public Rectangle boundingRectangle() {
		return mHUDRectangle;

	}

	/** Called (from GLFW) when the window is resized */
	@Override
	public void onResize(int pWidth, int pHeight) {
		changePerspectiveMatrix(pWidth, pHeight);

	}

}

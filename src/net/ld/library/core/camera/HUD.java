package net.ld.library.core.camera;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

public class HUD implements ICamera {

	private static final float Z_NEAR = -1.0f;
	private static final float Z_FAR = 10.0f;

	// =============================================
	// Variables
	// =============================================

	private Rectangle mHUDRectangle;

	private float mRotation = 0.0f;

	// These are not the dimensions of the window, but rather the dimensions of the canvas to use for the HUD (i.e. menu system)
	private float mWindowWidth;
	private float mWindowHeight;

	private float mzNear;
	private float mzFar;

	private Matrix4f mProjectionMatrix;
	private Matrix4f mViewMatrix;

	private Vector2f mMouseHUDSpace;

	// =============================================
	// Properties
	// =============================================

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

		mViewMatrix.setIdentity();
		mViewMatrix.scale(1f, 1f, 1f);

		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(getMinX(), getMaxX(), getMaxY(), getMinY(), Z_NEAR, Z_FAR);

	}

	// =============================================
	// Methods
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
	public Vector2f getMouseCameraSpace() {
		return mMouseHUDSpace;
	}

	@Override
	public float getMouseCameraSpaceX() {
		return mMouseHUDSpace.x;
	}

	@Override
	public float getMouseCameraSpaceY() {
		return mMouseHUDSpace.y;
	}

	@Override
	public float getPointCameraSpaceX(float pPointX) {
		return (-mWindowWidth * 0.5f + (pPointX - 1));
	}

	@Override
	public float getPointCameraSpaceY(float pPointY) {
		return (-mWindowWidth * 0.5f + (pPointY - 1));
	}

	@Override
	public Vector2f getPosition() {
		return new Vector2f();
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
	public Rectangle boundingRectangle() {
		return mHUDRectangle;

	}

}

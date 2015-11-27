package net.ld.library.core.camera;

import org.lwjgl.opengl.GL11;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Matrix4f;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

public class HUD implements ICamera {

	// =============================================
	// Variables
	// =============================================

	private DisplayConfig mDisplayConfig;

	private Rectangle mBoundingRectangle;

	private float mRotation = 0.0f;

	private int mWindowWidth;
	private int mWindowHeight;

	private float mzNear;
	private float mzFar;

	private Matrix4f mProjectionMatrix;
	private Matrix4f mViewMatrix;

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

	public HUD(DisplayConfig pDisplayConfig, final float pX, final float pY, final float pWidth, final float pHeight) {
		mDisplayConfig = pDisplayConfig;

		mBoundingRectangle = new Rectangle(getMinX(), getMinY(), windowWidth(), windowHeight());

		mProjectionMatrix = new Matrix4f();
		mProjectionMatrix.createOrtho(-mDisplayConfig.windowWidth(), 0.0f, 0.0f, -mDisplayConfig.windowHeight(), 1.0f, -1.0f);
		mViewMatrix = new Matrix4f();

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void handleInput(InputState pInputState) {

		// TODO: Need to reimplement the mouse scroll wheel for zooming

	}

	@Override
	public void update(GameTime pGameTime) {

		GL11.glViewport(0, 0, mDisplayConfig.windowWidth(), mDisplayConfig.windowHeight());

		mProjectionMatrix.createOrtho(-mDisplayConfig.windowWidth(), 0.0f, 0.0f, -mDisplayConfig.windowHeight(), 1.0f, -1.0f);

		mViewMatrix.setIdentity();
		mViewMatrix.scale(1f, 1f, 1f);
		// mViewMatrix.translate(getWidth()/2f, getHeight()/2f, 0f);

		mWindowWidth = mDisplayConfig.windowWidth();
		mWindowHeight = mDisplayConfig.windowHeight();

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
	public Vector2f getMouseCameraSpace(double pMouseXPosition, double pMouseYPosition) {

		Vector2f mMouseScreenCoord = new Vector2f();
		mMouseScreenCoord.x = (float) (pMouseXPosition - 1);
		mMouseScreenCoord.y = (float) (mDisplayConfig.windowHeight() - pMouseYPosition - 1);

		return mMouseScreenCoord;
	}

	@Override
	public Vector2f getPosition() {
		return new Vector2f();
	}

	@Override
	public void setTargetPosition(float pX, float pY) {

	}

}

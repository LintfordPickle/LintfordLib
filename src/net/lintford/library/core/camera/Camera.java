package net.lintford.library.core.camera;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.options.DisplayConfig;

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

	/** If true, the zooms the camera using the mouse scroll wheel */
	// TODO: integrate into the Input options menu
	protected boolean mAllowMouseScrollZoom = true;
	protected int mWindowWidth;
	protected int mWindowHeight;
	protected float mScaledWindowWidth;
	protected float mScaledWindowHeight;

	protected Vector2f mMouseWorldSpace;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public Camera(DisplayConfig pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;

		this.mMinX = 0;
		this.mMaxX = 0 + pDisplayConfig.gameViewportSize().x;
		this.mMinY = 0;
		this.mMaxY = 0 + pDisplayConfig.gameViewportSize().y;

		mBoundingRectangle = new Rectangle(mMinX, mMinY, pDisplayConfig.gameViewportSize().x, pDisplayConfig.gameViewportSize().y);

		mPosition = new Vector2f();
		mAcceleration = new Vector2f();
		mVelocity = new Vector2f();

		mOffsetPosition = new Vector2f();
		mTargetPosition = new Vector2f();

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseWorldSpace = new Vector2f();
		mZoomFactor = 1.0f;

		createView();
		createOrtho(mBoundingRectangle.width, mBoundingRectangle.height);
		updateZoomBounds(mBoundingRectangle.width, mBoundingRectangle.height);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void handleInput(LintfordCore pCore) {
		// Update the MouseCameraSpace instance
		mMouseWorldSpace.x = pCore.input().mouseWindowCoords().x * getZoomFactorOverOne() + this.getMinX();
		mMouseWorldSpace.y = pCore.input().mouseWindowCoords().y * getZoomFactorOverOne() + this.getMinY();

	}

	public void update(LintfordCore pCore) {
		mWindowWidth = mDisplayConfig.windowSize().x;
		mWindowHeight = mDisplayConfig.windowSize().y;

		int lGameViewportWidth = mDisplayConfig.windowSize().x;
		int lGameViewportHeight = mDisplayConfig.windowSize().y;

		if ((lGameViewportWidth % 2) == 1) {
			lGameViewportWidth = lGameViewportWidth + 1;
		}

		if ((lGameViewportHeight % 2) == 1) {
			lGameViewportHeight = lGameViewportHeight + 1;
		}

		mPosition.x = mTargetPosition.x;
		mPosition.y = mTargetPosition.y;

		mAcceleration.x = 0.0f;
		mAcceleration.y = 0.0f;

		createView();
		createOrtho(lGameViewportWidth, lGameViewportHeight);
		updateZoomBounds(lGameViewportWidth, lGameViewportHeight);

		applyGameViewport();

	}

	public void createView() {
		mViewMatrix.setIdentity();
		mViewMatrix.scale(mZoomFactor, mZoomFactor, 1f);
		mViewMatrix.translate((int) (mPosition.x * getZoomFactor()), (int) (mPosition.y * getZoomFactor()), 0f);

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
		mMinX = -mPosition.x - mScaledWindowWidth / 2.0f;
		mMinY = -mPosition.y - mScaledWindowHeight / 2.0f;

		mMaxX = -mPosition.x + mScaledWindowWidth / 2.0f;
		mMaxY = -mPosition.y + mScaledWindowHeight / 2.0f;

		// update the bounding rectangle so we can properly do frustum culling
		mBoundingRectangle.x = mMinX;
		mBoundingRectangle.y = mMinY;
		mBoundingRectangle.width = mMaxX - mMinX;
		mBoundingRectangle.height = mMaxY - mMinY;

	}

	public void applyGameViewport() {
		GL11.glViewport(0, 0, mWindowWidth, mWindowHeight);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void changePerspectiveMatrix(float pW, float pH) {
		createOrtho(pW, pH);

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
	public float getPointWorldSpaceX(float pPointX) {
		return pPointX * getZoomFactorOverOne() + this.getMinX();

	}

	@Override
	public float getPointCameraSpaceY(float pPointY) {
		return pPointY * getZoomFactorOverOne() + this.getMinX();

	}

}

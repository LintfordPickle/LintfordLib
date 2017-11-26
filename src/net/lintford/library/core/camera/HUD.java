package net.lintford.library.core.camera;

import net.lintford.library.core.input.InputState;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.options.IResizeListener;

/**
 * Defines a simple HUD Camera which implements the {@link ICamera} interface.
 * The HUD renders objects from -0 (-Z_NEAR) to -10 (-Z_FAR)
 */
public class HUD implements ICamera, IResizeListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	/**
	 * The near plane (Z_NEAR) distance for front clipping. This defines the closest
	 * distance objects will be rendered from the HUD position.
	 */
	public static final float Z_NEAR = 0.0f;

	/**
	 * The far plane (Z_FAR) distance for rear clipping. This defines the closest
	 * distance objects will be rendered from the HUD position.
	 */
	public static final float Z_FAR = 10.0f;

	public static final int HUD_SIZE_WIDTH = 1024;
	public static final int HUD_SIZE_HEIGHT = 768;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayConfig mDisplayConfig;

	private Rectangle mBoundingRectangle;

	/** The width of the window. */
	private float mWindowWidth;
	/** The height of the window. */
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

		// TODO (John): HUD Bounds needs to change based on the size of the window
		mBoundingRectangle = new Rectangle(-HUD_SIZE_WIDTH / 2, -HUD_SIZE_HEIGHT / 2, HUD_SIZE_WIDTH, HUD_SIZE_HEIGHT);

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();

		mMouseHUDSpace = new Vector2f();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void handleInput(InputState pInputState) {
		mRatioW = ((float) mWindowWidth / (float) mDisplayConfig.windowSize().x);
		mRatioH = ((float) mWindowHeight / (float) mDisplayConfig.windowSize().y);

		mMouseHUDSpace.x = (float) (-mWindowWidth * 0.5f + (pInputState.mouseWindowCoords().x - 1) * mRatioW);
		mMouseHUDSpace.y = (float) (-mWindowHeight * 0.5f + (pInputState.mouseWindowCoords().y - 1) * mRatioH);

	}

	public void update(GameTime pGameTime) {
		if (mWindowWidth == 0 || mWindowHeight == 0)
			return;

		mViewMatrix.setIdentity(); // points at -Z
		mViewMatrix.translate(0, 0, 0);

		mProjectionMatrix.setIdentity();
		mProjectionMatrix.createOrtho(getMinX(), getMaxX(), getMaxY(), getMinY(), Z_NEAR, Z_FAR);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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
	public void onResize(int pWidth, int pHeight) {
		mWindowWidth = pWidth;

		// Force the window height to be an even number (because it messes up some pixel
		// rendering)
		if ((pHeight % 2) == 1) {
			pHeight = pHeight + 1;

		}

		mWindowHeight = pHeight;

	}

}

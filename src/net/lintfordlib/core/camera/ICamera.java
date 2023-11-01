package net.lintfordlib.core.camera;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Matrix4f;
import net.lintfordlib.core.maths.Vector2f;

public interface ICamera {

	public static ICamera EMPTY = new ICamera() {

		// --------------------------------------
		// Variables
		// --------------------------------------

		Matrix4f mView = new Matrix4f();
		Matrix4f mProjection = new Matrix4f();
		Vector2f mPosition = new Vector2f();
		Rectangle mRectangle = new Rectangle();

		// --------------------------------------
		// Properties
		// --------------------------------------

		@Override
		public Matrix4f view() {
			return mView;
		}

		@Override
		public void setZoomFactor(float pNewValue) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set zoom on ICamera.EMPTY");
		}

		@Override
		public void setPosition(float pX, float pY) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set position on ICamera.EMPTY");
		}

		@Override
		public Matrix4f projection() {
			return mProjection;
		}

		@Override
		public float getZoomFactorOverOne() {
			return 1.f;
		}

		@Override
		public float getZoomFactor() {
			return 1.f;
		}

		@Override
		public float getWidth() {
			return 1.f;
		}

		@Override
		public Vector2f getPosition() {
			return mPosition;
		}

		@Override
		public float getPointCameraSpaceX(float pPointX) {
			return 0;
		}

		@Override
		public float getPointCameraSpaceY(float pPointY) {
			return 0;
		}

		@Override
		public float getMouseWorldSpaceY() {
			return 0;
		}

		@Override
		public float getMouseWorldSpaceX() {
			return 0;
		}

		@Override
		public Vector2f getMouseCameraSpace() {
			return mPosition;
		}

		@Override
		public float getMinY() {
			return 0;
		}

		@Override
		public float getMinX() {
			return 0;
		}

		@Override
		public float getMaxY() {
			return 0;
		}

		@Override
		public float getMaxX() {
			return 0;
		}

		@Override
		public float getHeight() {
			return 0;
		}

		@Override
		public Rectangle boundingRectangle() {
			return mRectangle;
		}

		@Override
		public CameraState getCameraState() {
			return null;
		}

		@Override
		public void setCameraState(CameraState pCameraState) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set camera state on ICamera.EMPTY");
		}

		@Override
		public int viewportWidth() {
			return 800;
		}

		@Override
		public int viewportHeight() {
			return 600;
		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		@Override
		public void handleInput(LintfordCore pCore) {

		}

		@Override
		public void update(LintfordCore pCore) {

		}

	};

	// --------------------------------------
	// Properties
	// --------------------------------------

	public abstract Matrix4f projection();

	public abstract Matrix4f view();

	public abstract Rectangle boundingRectangle();

	public abstract float getMinX();

	public abstract float getMaxX();

	public abstract float getMinY();

	public abstract float getMaxY();

	public abstract float getWidth();

	public abstract float getHeight();

	public abstract Vector2f getPosition();

	public abstract float getZoomFactor();

	public abstract void setZoomFactor(float zoomFactor);

	public abstract float getZoomFactorOverOne();

	public abstract int viewportWidth();

	public abstract int viewportHeight();

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public abstract void handleInput(LintfordCore core);

	public abstract void update(LintfordCore core);

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract CameraState getCameraState();

	public abstract void setCameraState(CameraState cameraState);

	public abstract void setPosition(float positionX, float positionY);

	public abstract Vector2f getMouseCameraSpace();

	public abstract float getMouseWorldSpaceX();

	public abstract float getMouseWorldSpaceY();

	public abstract float getPointCameraSpaceX(float pointX);

	public abstract float getPointCameraSpaceY(float pointY);
}

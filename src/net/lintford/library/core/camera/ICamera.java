package net.lintford.library.core.camera;

import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.core.maths.Vector2f;

public interface ICamera {

	public static ICamera EMPTY = new ICamera() {
		Matrix4f mView = new Matrix4f();
		Matrix4f mProjection = new Matrix4f();
		Vector2f mPosition = new Vector2f();
		Rectangle mRectangle = new Rectangle();

		@Override
		public Matrix4f view() {
			return mView;
		}

		@Override
		public void setZoomFactor(float pNewValue) {

		}

		@Override
		public void setPosition(float pX, float pY) {

		}

		@Override
		public Matrix4f projection() {
			return mProjection;
		}

		@Override
		public float getZoomFactorOverOne() {
			return 0;
		}

		@Override
		public float getZoomFactor() {
			return 0;
		}

		@Override
		public float getWidth() {
			return 0;
		}

		@Override
		public Vector2f getPosition() {
			return mPosition;
		}

		@Override
		public float getPointWorldSpaceX(float pPointX) {
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

	public abstract void setZoomFactor(float pNewValue);

	public abstract float getZoomFactorOverOne();

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract void setPosition(float pX, float pY);

	public abstract Vector2f getMouseCameraSpace();

	public abstract float getMouseWorldSpaceX();

	public abstract float getMouseWorldSpaceY();

	public abstract float getPointWorldSpaceX(float pPointX);

	public abstract float getPointCameraSpaceY(float pPointY);

}

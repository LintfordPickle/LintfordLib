package net.ld.library.core.camera;

import org.junit.Test;

import net.ld.library.core.maths.Matrix4f;

public class CameraTest {

	/**
	 * The {@link Camera} should be ready to use as soon as it is instantiated
	 * (i.e. before the update method has been called). This requires a valid
	 * view matrix.
	 */
	@Test
	public void cameraViewMatrixAfterConstructorTest() {

		// Arrange
		final int lX = 0;
		final int lY = 0;
		final int lWidth = 800;
		final int lHeight = 600;

		final Camera lCamera = new Camera(lX, lY, lWidth, lHeight);

		// Act
		// Get the camera projection matrix to be used
		Matrix4f lViewMatrix = lCamera.view();

		// Assert
		assert (lViewMatrix != null) : "View matrix not created during camera instantiation (camera not usable)";

		assert (lViewMatrix.m30 == 0f) : "View matrix position not set correctly (initial)";
		assert (lViewMatrix.m31 == 0f) : "View matrix position not set correctly (initial)";
		assert (lViewMatrix.m32 == 0f) : "View matrix position not set correctly (initial)";

		assert (lViewMatrix.m00 == 1f) : "View matrix scale X not set correctly (initial)";
		assert (lViewMatrix.m11 == 1f) : "View matrix scale Y not set correctly (initial)";
		assert (lViewMatrix.m22 == 1f) : "View matrix scale Z not set correctly (initial)";

	}

	/**
	 * The {@link Camera} should be ready to use as soon as it is instantiated
	 * (i.e. before the update method has been called). This requires a valid
	 * orthographic projection matrix.
	 */
	@Test
	public void cameraProjectionMatrixAfterConstructorTest() {

		// Arrange
		final int lX = 0;
		final int lY = 0;
		final int lWidth = 800;
		final int lHeight = 600;

		float lLeft = -lWidth * 0.5f;
		float lRight = lWidth * 0.5f;

		float lTop = -lHeight * 0.5f;
		float lBottom = lHeight * 0.5f;

		float lNear = Camera.Z_NEAR;
		float lFar = Camera.Z_FAR;

		final Camera lCamera = new Camera(lX, lY, lWidth, lHeight);

		// Act
		// Get the camera projection matrix to be used
		Matrix4f lOrth = lCamera.projection();

		// Assert
		assert (lOrth != null) : "Orthographic matrix not created during camera instantiation (camera not usable)";

		assert (lOrth.m00 == 2.0f / (lRight - lLeft)) : "projection matrix does not match constructor arguements";
		assert (lOrth.m11 == 2.0f / (lTop - lBottom)) : "projection matrix does not match constructor arguements";
		assert (lOrth.m22 == -2.0f / (lFar - lNear)) : "projection matrix does not match constructor arguements";
		assert (lOrth.m33 == 1.0f) : "projection matrix doesn't match constructor arguements.";

	}

	/**
	 * The {@link Camera} matrices are constructed from the actual parameters
	 * supplied in its constructor. The camera should be built around those
	 * input values (meaning the center position is always locally-zero).
	 */
	@Test
	public void cameraInitialPositionZeroTest() {

		// Arrange
		final int lX = 0;
		final int lY = 0;
		final int lWidth = 800;
		final int lHeight = 600;

		final Camera lCamera = new Camera(lX, lY, lWidth, lHeight);

		// Assert
		assert (lCamera.getCenterX() == lWidth * 0.5f) : "Camera was not built around the given center point X";
		assert (lCamera.getCenterY() == lHeight * 0.5f) : "Camera was not built around the given center point Y";

	}

	/** Test the spatial bounds of the camera */
	@Test
	public void cameraInitialPositionTest() {
		// Arrange
		final int CAMERA_POSITION_X = 1000;
		final int CAMERA_POSITION_Y = 0;
		final int CAMERA_WIDTH = 800;
		final int CAMERA_HEIGHT = 600;

		final Camera lCamera = new Camera(CAMERA_POSITION_X, CAMERA_POSITION_Y, CAMERA_WIDTH, CAMERA_HEIGHT);

		// Assert
		assert (lCamera.getCenterX() == CAMERA_POSITION_X
				+ (CAMERA_WIDTH * 0.5f)) : "Camera was not built around the given center point X";
		assert (lCamera.getCenterY() == CAMERA_POSITION_Y
				+ (CAMERA_HEIGHT * 0.5f)) : "Camera was not built around the given center point Y";

	}

	/**
	 * Tests the absolute position movement of the camera (checks internal
	 * integrity of camera after movement)
	 */
	@Test
	public void cameraAbsMovementTest() {
		// Arrange
		final int lX = 0;
		final int lY = 0;
		final int lWidth = 800;
		final int lHeight = 600;

		final Camera lCamera = new Camera(lX, lY, lWidth, lHeight);

		// Act
		lCamera.zoomFactor(1.0f);
		lCamera.setAbsPosition(1000, 1000);

		// Assert
		assert (lCamera.getPosition().x == 1000) : "Camera returned incorrect position X";
		assert (lCamera.getPosition().y == 1000) : "Camera returned incorrect position Y";

		assert (lCamera.getCenterX() == 1000 + lWidth * 0.5f) : "Camera returned incorrect center position X";
		assert (lCamera.getCenterY() == 1000 + lHeight * 0.5f) : "Camera returned incorrect center position Y";

	}

	/** Test the spatial bounds of the camera */
	@Test
	public void cameraBoundsTest() {
		// Arrange
		final float CAMERA_POSITION_X = 1000f;
		final float CAMERA_POSITION_Y = 1000f;
		final int CAMERA_WIDTH = 800;
		final int CAMERA_HEIGHT = 600;

		final Camera lCamera = new Camera(CAMERA_POSITION_X, CAMERA_POSITION_Y, CAMERA_WIDTH, CAMERA_HEIGHT);

		// Act
		lCamera.setAbsPosition(CAMERA_POSITION_X + 500, CAMERA_POSITION_Y + 500);

		// Assert
		assert (lCamera.boundingRectangle().left() == (CAMERA_POSITION_X + 500) - CAMERA_WIDTH * 0.5f);

	}

}

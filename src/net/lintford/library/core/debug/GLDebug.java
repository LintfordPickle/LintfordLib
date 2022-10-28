package net.lintford.library.core.debug;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GLDebug {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final boolean GL_CHECKS_IN_LOADRESOURCES = true;
	public static final boolean GL_CHECKS_IN_UNLOADRESOURCES = true;
	public static final boolean GL_CHECKS_IN_DRAWS = true;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static boolean checkGLErrorsException() {
		checkGLErrors("GLDebug", true);

		return false;
	}

	public static boolean checkGLErrorsException(String customTAG) {
		checkGLErrors(customTAG, true);

		return false;
	}

	public static boolean checkGLErrors() {
		return checkGLErrors("GLDebug");

	}

	public static boolean checkGLErrors(String customTAG) {
		return checkGLErrors(customTAG, false);
	}

	private static boolean checkGLErrors(String customTAG, boolean printStackTrace) {
		final int lGLError = GL11.glGetError();
		if (lGLError == GL11.GL_NO_ERROR)
			return false;

		switch (lGLError) {
		case GL11.GL_INVALID_ENUM:
			Debug.debugManager().logger().e(GLDebug.class.getSimpleName(), String.format("GL_INVALID_ENUM (%d)", GL11.GL_INVALID_ENUM));
			if (printStackTrace) {
				throw new RuntimeException("GL_INVALID_ENUM exception occured: " + customTAG);
			}
			return true;
		case GL11.GL_INVALID_VALUE:
			Debug.debugManager().logger().e(GLDebug.class.getSimpleName(), String.format("GL_INVALID_VALUE (%d)", GL11.GL_INVALID_VALUE));
			if (printStackTrace) {
				throw new RuntimeException("GL_INVALID_VALUE exception occured: " + customTAG);
			}
			return true;
		case GL11.GL_INVALID_OPERATION:
			Debug.debugManager().logger().e(GLDebug.class.getSimpleName(), String.format("GL_INVALID_OPERATION (%d)", GL11.GL_INVALID_OPERATION));
			if (printStackTrace) {
				throw new RuntimeException("GL_INVALID_OPERATION exception occured: " + customTAG);
			}
			return true;
		case GL11.GL_OUT_OF_MEMORY:
			Debug.debugManager().logger().e(GLDebug.class.getSimpleName(), String.format("GL_OUT_OF_MEMORY (%d)", GL11.GL_OUT_OF_MEMORY));
			if (printStackTrace) {
				throw new RuntimeException("GL_OUT_OF_MEMORY exception occured: " + customTAG);
			}
			return true;
		case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
			Debug.debugManager().logger().e(GLDebug.class.getSimpleName(), String.format("GL_INVALID_FRAMEBUFFER_OPERATION (%d)", GL30.GL_INVALID_FRAMEBUFFER_OPERATION));
			if (printStackTrace) {
				throw new RuntimeException("GL_INVALID_FRAMEBUFFER_OPERATION exception occured: " + customTAG);
			}
			return true;
		}

		return false;
	}
}
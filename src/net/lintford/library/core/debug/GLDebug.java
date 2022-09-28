package net.lintford.library.core.debug;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GLDebug {

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
		// FIXME: Need to add some proper handling in all of these cases - there is probably some way to handle each of the
		// cases in a graceful way which SHOULD NOT result in a runtime exception being thrown!

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
package net.lintford.library.core.debug;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GLDebug {

	public static boolean checkGLErrorsException() {
		checkGLErrors("GLDebug");

		return false;
	}

	public static boolean checkGLErrorsException(String pCustomTAG) {
		checkGLErrors(pCustomTAG, true);

		return false;
	}

	public static boolean checkGLErrors() {
		return checkGLErrors("GLDebug");

	}

	public static boolean checkGLErrors(String pCustomTAG) {
		return checkGLErrors(pCustomTAG, false);

	}

	private static boolean checkGLErrors(String pCustomTAG, boolean pPrintStackTrace) {
		final int lGLError = GL11.glGetError();
		if (lGLError == GL11.GL_NO_ERROR)
			return false;
		switch (lGLError) {
		case GL11.GL_INVALID_ENUM:
			DebugManager.DEBUG_MANAGER.logger().e(GLDebug.class.getSimpleName(), "GL_INVALID_ENUM");
			if (pPrintStackTrace) {
				Thread.dumpStack();
				throw new RuntimeException("GL_INVALID_ENUM exception occured");
			}
			return true;
		case GL11.GL_INVALID_VALUE:
			DebugManager.DEBUG_MANAGER.logger().e(GLDebug.class.getSimpleName(), "GL_INVALID_VALUE");
			if (pPrintStackTrace) {
				Thread.dumpStack();
				throw new RuntimeException("GL_INVALID_VALUE exception occured");
			}
			return true;
		case GL11.GL_INVALID_OPERATION:
			DebugManager.DEBUG_MANAGER.logger().e(GLDebug.class.getSimpleName(), "GL_INVALID_OPERATION");
			if (pPrintStackTrace) {
				Thread.dumpStack();
				throw new RuntimeException("GL_INVALID_OPERATION exception occured");
			}

			return true;
		case GL11.GL_OUT_OF_MEMORY:
			DebugManager.DEBUG_MANAGER.logger().e(GLDebug.class.getSimpleName(), "GL_OUT_OF_MEMORY");
			if (pPrintStackTrace) {
				Thread.dumpStack();
				throw new RuntimeException("GL_OUT_OF_MEMORY exception occured");
			}
			return true;
		case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
			DebugManager.DEBUG_MANAGER.logger().e(GLDebug.class.getSimpleName(), "GL_INVALID_FRAMEBUFFER_OPERATION");
			if (pPrintStackTrace) {
				Thread.dumpStack();
				throw new RuntimeException("GL_INVALID_FRAMEBUFFER_OPERATION exception occured");
			}
			return true;
		}

		return false;
	}

}

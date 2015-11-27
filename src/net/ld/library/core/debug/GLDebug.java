package net.ld.library.core.debug;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GLDebug {

	public static boolean checkGLErrorsException() {
		if (checkGLErrors(false)) {
			throw new RuntimeException();
		}

		return false;
	}

	public static boolean checkGLErrors() {
		return checkGLErrors(true);
	}

	private static boolean checkGLErrors(boolean pPrintStackTrace) {
		final int lGLError = GL11.glGetError();
		if (lGLError == GL11.GL_NO_ERROR)
			return false;
		switch (lGLError) {
		case GL11.GL_INVALID_ENUM:
			System.err.println("GL_INVALID_ENUM");
			if (pPrintStackTrace)
				Thread.dumpStack();
			return true;
		case GL11.GL_INVALID_VALUE:
			System.err.println("GL_INVALID_VALUE");
			if (pPrintStackTrace)
				Thread.dumpStack();
			return true;
		case GL11.GL_INVALID_OPERATION:
			System.err.println("GL_INVALID_OPERATION");
			if (pPrintStackTrace)
				Thread.dumpStack();
			return true;
		case GL11.GL_OUT_OF_MEMORY:
			System.err.println("GL_OUT_OF_MEMORY");
			if (pPrintStackTrace)
				Thread.dumpStack();
			return true;
		case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
			System.err.println("GL_INVALID_FRAMEBUFFER_OPERATION");
			if (pPrintStackTrace)
				Thread.dumpStack();
			return true;
		}

		return false;
	}

}

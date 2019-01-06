package net.lintford.library.core.maths;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public class MathUtil {

	private static FloatBuffer int_mat_float_buffer;

	public static void allocResources() {
		if (int_mat_float_buffer == null) {
			int_mat_float_buffer = MemoryUtil.memAllocFloat(16);

		}

	}

	public static void freeResources() {
		if (int_mat_float_buffer != null) {
			MemoryUtil.memFree(int_mat_float_buffer);
			int_mat_float_buffer = null;

		}

	}

	public static FloatBuffer getMatBufferColMaj(Matrix4f pMat) {
		if (int_mat_float_buffer == null)
			allocResources();

		int_mat_float_buffer.clear();

		// Col Major (Col elements are next to each other in memory)
		int_mat_float_buffer.put(pMat.m00).put(pMat.m10).put(pMat.m20).put(pMat.m30);
		int_mat_float_buffer.put(pMat.m01).put(pMat.m11).put(pMat.m21).put(pMat.m31);
		int_mat_float_buffer.put(pMat.m02).put(pMat.m12).put(pMat.m22).put(pMat.m32);
		int_mat_float_buffer.put(pMat.m03).put(pMat.m13).put(pMat.m23).put(pMat.m33);

		int_mat_float_buffer.flip();
		return int_mat_float_buffer;
	}

	/**
	 * According to opengl.org, a col-maj matrix has translation elements at position 15,16 and 17. (http://www.opengl.org/archives/resources/faq/technical/transformations.htm)
	 * 
	 * @return A FloatBuffer containing a col-maj matrix
	 */
	public FloatBuffer copyToBuffer(final FloatBuffer pBuffer, Matrix4f pMat) {
		if (pBuffer == null)
			return null;

		pBuffer.clear();

		pBuffer.put(pMat.m00).put(pMat.m10).put(pMat.m20).put(pMat.m30);
		pBuffer.put(pMat.m01).put(pMat.m11).put(pMat.m21).put(pMat.m31);
		pBuffer.put(pMat.m02).put(pMat.m12).put(pMat.m22).put(pMat.m32);
		pBuffer.put(pMat.m03).put(pMat.m13).put(pMat.m23).put(pMat.m33);

		return pBuffer;
	}

	/** @return A FloatBuffer containing a row-maj matrix */
	public FloatBuffer getBufferTranspose(Matrix4f pMat) {
		int_mat_float_buffer.clear();

		int_mat_float_buffer.put(pMat.m00).put(pMat.m01).put(pMat.m02).put(pMat.m03);
		int_mat_float_buffer.put(pMat.m10).put(pMat.m11).put(pMat.m12).put(pMat.m13);
		int_mat_float_buffer.put(pMat.m20).put(pMat.m21).put(pMat.m22).put(pMat.m23);
		int_mat_float_buffer.put(pMat.m30).put(pMat.m31).put(pMat.m32).put(pMat.m33);

		int_mat_float_buffer.flip();

		return int_mat_float_buffer;
	}

}
